package JNA_SCIP.Examples.LOP;

import java.util.Arrays;

import JNA_SCIP.*;

public class Cons_LOP extends ConstraintData<Cons_LOP, Conshdlr_LOP> {

	/* Member methods and data for per-constraint data. */
	SCIP_VAR[][] vars;
	Cons_LOP(SCIP_VAR[][] _vars, boolean copy) {
		int n = _vars.length;
		if(!copy) {
			this.vars = _vars;
		} else {
			//deep copy
			
			vars = new SCIP_VAR[n][];
			for(int i=0; i<n; i++) {
				vars[i] = Arrays.copyOf(_vars[i], n);
			}
		}
	}
	
	//instead of returning a SCIP_RETCODE, a "cutoff", and an "nGen" like the example, we just
	//return one of SCIP_DIDNOTFIND, SCIP_SEPARATED, or SCIP_CUTOFF.
	SCIP_RESULT LOPseparate(SCIP scip, SCIP_SOL sol) {
		int nGen = 0;
		boolean cutoff = false;
		
		int n = vars.length;
		double[][] vals = scip.getSolVals(sol, vars);
		
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
	    		double valIJ = vals[i][j];;
	    		double valJI = vals[j][i];
	    		
				if ( !scip.isFeasEQ(scip, valIJ + valJI, 1.0) ) {
					String name = "sym#"+i+"#"+j;
					SCIP_ROW row = scip.createEmptyRowConshdlr(getScipHdlr(), name, 1.0, 1.0, false, false, true);
					scip.cacheRowExtensions(row);
					scip.addVarToRow(row, vars[i][j], 1.0);
					scip.addVarToRow(row, vars[j][i], 1.0);
					scip.flushRowExtensions(row);

					SCIP.debug(()->{
						scip.printRow(row, null);
					});
					
					cutoff = scip.addRow(row, false);
					scip.releaseRow(row);
					nGen++;
					
					if(cutoff)
						return SCIP_RESULT.SCIP_CUTOFF;
				}
				
				for(int k=i+1; k<n; k++) {
					if(k==j)
						continue;
					
					double valJK = vals[j][k];
					double valKI = vals[k][i];
					double sum = valIJ + valJK + valKI;
		    		
					if ( scip.isEfficacious(sum - 2.0) ) {
						//build row
						String name = "triangle#"+i+"#"+j+"#"+k;
						SCIP_ROW row = scip.createEmptyRowConshdlr(getScipHdlr(), name, -scip.infinity(), 2.0, false, false, true);
						scip.cacheRowExtensions(row);
						scip.addVarToRow(row, vars[i][j], 1.0);
						scip.addVarToRow(row, vars[j][k], 1.0);
						scip.addVarToRow(row, vars[k][i], 1.0);
						scip.flushRowExtensions(row);

						SCIP.debug(()->{
							scip.printRow(row, null);
						});
						
						cutoff = scip.addRow(row, false);
						scip.releaseRow(row);
						nGen++;
						
						if(cutoff)
							return SCIP_RESULT.SCIP_CUTOFF;
					}
				}
			}
		}
		
		if(nGen > 0) {
			System.out.println("Separated with "+nGen);
			return SCIP_RESULT.SCIP_SEPARATED;
		} else
			return SCIP_RESULT.SCIP_DIDNOTFIND;
	}
	
	boolean isFeasible_Integral(SCIP scip, SCIP_SOL sol, boolean printreason) {
		int n = vars.length;
		double[][] vals = scip.getSolVals(sol, vars);

		/* check triangle inequalities and symmetry equations */
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
				boolean oneIJ = vals[i][j] > 0.5;
				boolean oneJI = vals[j][i] > 0.5;
				
				if ( oneIJ == oneJI ) {
					scip.debugMsg("constraint <"+getName()+"> infeasible (violated equation).\n");
					if( printreason ) {
						scip.infoMessage(null,
						"violation: symmetry equation violated <%s> = %.15g and <%s> = %.15g\n",
						vars[i][j].getName(), scip.getSolVal(sol, vars[i][j]),
						vars[j][i].getName(), scip.getSolVal(sol, vars[j][i]));
					}
					return false;
				}
				
				for(int k=i+1; k<n; k++) {
					if(k==j)
						continue;
					
					boolean oneJK = vals[j][k] > 0.5;
					boolean oneKI = vals[k][i] > 0.5;
					
					if (oneIJ && oneJK && oneKI) {
						scip.debugMsg("constraint <"+getName()+"> infeasible (violated triangle ineq.).\n");
						if( printreason ) {
							scip.infoMessage(null,
							"violation: triangle inequality violated <%s> = %.15g, <%s> = %.15g, <%s> = %.15g\n",
							vars[i][j].getName(), scip.getSolVal(sol, vars[i][j]),
							vars[j][k].getName(), scip.getSolVal(sol, vars[j][k]),
							vars[k][i].getName(), scip.getSolVal(sol, vars[k][i]));
						}
						return false;
					}
				}
			}
		}
		
		return true;
	}

	@Override
	public Cons_LOP copy(SCIP sourcescip, SCIP targetscip,
			SCIP_HASHMAP varmap, SCIP_HASHMAP consmap, boolean global) {
		
		targetscip.debugMsg("Copying method for linear ordering constraint handler.\n");
		
		int n = vars.length;
		SCIP_VAR[][] newvars = new SCIP_VAR[n][n];
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				if(i != j) {
					SCIP_VAR newvar = vars[i][j].getCopy(sourcescip, targetscip, varmap, consmap, global);
					if(newvar == null) {
						//getCopy failed
						throw new RuntimeException("Couldn't copy "+vars[i][j].getName()+" in "+this.getScipCons().getName());
					}
					newvars[i][j] = newvar;
				}
			}
		}
		return new Cons_LOP(newvars, false);//don't need to copy the array, just made it
	}
	
	@Override
	public Cons_LOP transform(SCIP scip) {
		scip.debugMsg("transforming linear ordering constraint <%s>\n", getName());
	
		int n = vars.length;
		SCIP_VAR[][] newvars = new SCIP_VAR[n][n];
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				SCIP_VAR vij = vars[i][j];
				if(vij == null) {
					continue;
				} else {
					//switch to the transformed one
					vij = scip.getTransformedVar(vij);
					newvars[i][j] = vij;
				}
			}
		}
		return new Cons_LOP(newvars, false);//don't need to copy
	}

	@Override
	public void delete(SCIP scip) {
		scip.debugMsg("deleting linear ordering constraint <%s>\n", getName());
		
		//not strictly necessary, but release the array to the garbage collector.
		vars = null;
	}

	@Override
	public void exit(SCIP scip) {
		//We do nothing here, but we do override consexit in Conshdlr_LOP.
		throw new RuntimeException("Shouldn't be called");
	}
}

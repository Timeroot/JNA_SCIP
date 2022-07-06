package JNA_SCIP.Examples.LOP;

import JNA_SCIP.*;

import static JNA_SCIP.SCIP_RETCODE.*;
import static JNA_SCIP.SCIP_RESULT.*;

public class Conshdlr_LOP extends ConstraintHandler<Cons_LOP,Conshdlr_LOP> {

	//Uncomment block below for the equivalent of #define SCIP_DEBUG.
	//But it will hold for all Java code, not just this class.
	
//	static{ SCIP.DEBUG = true; }
	
	/* Constants to define behavior and timing */ 
	static final String CONSHDLR_NAME = "lop";
	static final String CONSHDLR_DESC           = "linear ordering constraint handler";
	static final int CONSHDLR_SEPAPRIORITY      = 100;
	static final int CONSHDLR_ENFOPRIORITY      = -100;
	static final int CONSHDLR_CHECKPRIORITY     = -100;
	static final int CONSHDLR_SEPAFREQ          = 1;
	static final int CONSHDLR_PROPFREQ          = 1;
	static final int CONSHDLR_EAGERFREQ         = 100;
	static final boolean CONSHDLR_DELAYSEPA     = false;
	static final boolean CONSHDLR_DELAYPROP     = false;
	static final boolean CONSHDLR_NEEDSCONS     = true;
	static final SCIP_PROPTIMING CONSHDLR_PROP_TIMING = SCIP_PROPTIMING.BEFORELP;
	
	/* Static methods for including the handler into SCIP or creating constraints. */
	static void includeConshdlrLOP(SCIP scip) {
		//Don't need to save the Java object anywhere --
		//It gets saved in the ConstraintHandler's mapping.
		@SuppressWarnings("unused")
		Conshdlr_LOP hdlr = new Conshdlr_LOP(scip);
	}
	
	static SCIP_CONS createConsLOP(SCIP scip, String name, SCIP_VAR[][] vars,
			boolean initial, boolean separate, boolean enforce, boolean check,
			boolean propagate, boolean local, boolean modifiable, boolean dynamic, boolean removable,
			boolean stickingatnode) {
		
		SCIP_CONSHDLR s_hdlr = scip.findConshdlr(CONSHDLR_NAME); 
		Conshdlr_LOP hdlr = (Conshdlr_LOP)ConstraintHandler.findJavaConshdlr(s_hdlr);
		
		Cons_LOP cons_data = new Cons_LOP(vars, true);
		SCIP_CONS scip_cons = hdlr.instantiate(scip, name,
			initial, separate, enforce, check, propagate, local, modifiable, dynamic, removable, stickingatnode,
			cons_data);
		
		return scip_cons;
	}

	/* Constructor of the Conshdlr */
	public Conshdlr_LOP(SCIP scip) {
		super(scip, Cons_LOP.class,
				CONSHDLR_NAME, CONSHDLR_DESC, CONSHDLR_ENFOPRIORITY, CONSHDLR_CHECKPRIORITY,
				CONSHDLR_EAGERFREQ, CONSHDLR_NEEDSCONS);
		this.enableConsCopy(scip);
		this.enableConsInitlp(scip);
		this.enableConsSepa(scip, CONSHDLR_SEPAFREQ, CONSHDLR_SEPAPRIORITY, CONSHDLR_DELAYSEPA);
		this.enableConsProp(scip, CONSHDLR_PROPFREQ, CONSHDLR_DELAYPROP, CONSHDLR_PROP_TIMING);
		this.enableConsResprop(scip);
	}

	/* Everything below here is callbacks */
	@Override
	public SCIP_RESULT conscheck(SCIP scip, Cons_LOP[] conss, SCIP_SOL sol, boolean checkintegrality,
			boolean checklprows, boolean printreason, boolean completely) {
		for(Cons_LOP cons : conss) {
			scip.debugMsg("checking linear ordering constraint <"+cons.getName()+">.\n");
			SCIP_VAR[][] vars = cons.vars;
			int n = vars.length;

			/* check triangle inequalities and symmetry equations */
		    for(int i=0; i<n; i++) {
		    	for(int j=i+1; j<n; j++) {
		    		boolean oneIJ = scip.getSolVal(sol, vars[i][j]) > 0.5;
		    		boolean oneJI = scip.getSolVal(sol, vars[j][i]) > 0.5;
		    		
					if ( oneIJ == oneJI ) {
						scip.debugMsg("constraint <"+cons.getName()+"> infeasible (violated equation).\n");
						scip.debugMsg("%s == %f, %s == %f\n", vars[i][j].getName(), scip.getSolVal(sol, vars[i][j]),
								vars[j][i].getName(), scip.getSolVal(sol, vars[j][i]));
						if( printreason ) {
							scip.infoMessage(null,
							"violation: symmetry equation violated <%s> = %.15g and <%s> = %.15g\n",
							vars[i][j].getName(), scip.getSolVal(sol, vars[i][j]),
							vars[j][i].getName(), scip.getSolVal(sol, vars[j][i]));
						}
						return SCIP_INFEASIBLE;
					}
					
					for(int k=i+1; k<n; k++) {
						if(k==j)
							continue;
						
						boolean oneJK = scip.getSolVal(sol, vars[j][k]) > 0.5;
			    		boolean oneKI = scip.getSolVal(sol, vars[k][i]) > 0.5;
			    		
			    		if (oneIJ && oneJK && oneKI) {
		    				scip.debugMsg("constraint <"+cons.getName()+"> infeasible (violated triangle ineq.).\n");
							if( printreason ) {
								scip.infoMessage(null,
								"violation: triangle inequality violated <%s> = %.15g, <%s> = %.15g, <%s> = %.15g\n",
								vars[i][j].getName(), scip.getSolVal(sol, vars[i][j]),
								vars[j][k].getName(), scip.getSolVal(sol, vars[j][k]),
								vars[k][i].getName(), scip.getSolVal(sol, vars[k][i]));
							}
							return SCIP_INFEASIBLE;
						}
					}
		    	}
		    }
		}
		scip.debugMsg("all linear ordering constraints are feasible.\n");
		return SCIP_FEASIBLE;
	}

	@Override
	public SCIP_RETCODE conslock(SCIP scip, Cons_LOP cons, SCIP_LOCKTYPE locktype, int nlockspos,
			int nlocksneg) {
		scip.debugMsg("Locking linear ordering constraint <"+cons.getName()+">.\n");
		
		SCIP_VAR[][] vars = cons.vars;
		int n = vars.length;
		
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				if(i==j)
					continue;
				scip.addVarLocksType(vars[i][j], SCIP_LOCKTYPE.MODEL, nlockspos+nlocksneg, nlockspos+nlocksneg);
			}
		}
		
		return SCIP_OKAY;
	}

	@Override
	public SCIP_RESULT consenfops(SCIP scip, Cons_LOP[] conss, int nusefulconss, boolean solinfeasible,
			boolean objinfeasible) {
		for(Cons_LOP cons : conss) {
				scip.debugMsg("enforcing pseudo solution for linear ordering constraint <"+cons.getName()+">.\n");
				
				SCIP_VAR[][] vars = cons.vars;
				int n = vars.length;
				
				/* check triangle inequalities and symmetry equations */
			    for(int i=0; i<n; i++) {
			    	for(int j=i+1; j<n; j++) {
			    		boolean oneIJ = scip.getSolVal(null, vars[i][j]) > 0.5;
			    		boolean oneJI = scip.getSolVal(null, vars[j][i]) > 0.5;
			    		
						if ( oneIJ == oneJI ) {
							scip.debugMsg("constraint <"+cons.getName()+"> infeasible (violated equation).\n");
							return SCIP_INFEASIBLE;
						}
						
						for(int k=i+1; k<n; k++) {
							if(k==j)
								continue;
							
							boolean oneJK = scip.getSolVal(null, vars[j][k]) > 0.5;
				    		boolean oneKI = scip.getSolVal(null, vars[k][i]) > 0.5;
				    		
				    		if (oneIJ && oneJK && oneKI) {
			    				scip.debugMsg("constraint <"+cons.getName()+"> infeasible (violated triangle ineq.).\n");
								return SCIP_INFEASIBLE;
							}
						}
			    	}
			    }
			}
			scip.debugMsg("all linear ordering constraints are feasible.\n");
			return SCIP_FEASIBLE;
	}

	@Override
	public SCIP_RESULT consenfolp(SCIP scip, Cons_LOP[] conss, int nusefulconss, boolean solinfeasible) {
		
		if(conss.length == 0)
			return SCIP_DIDNOTRUN;
		
		for(Cons_LOP cons : conss) {
			scip.debugMsg("enforcing lp solution for linear ordering constraint <"+cons.getName()+">.\n");
			
			SCIP_RESULT separation_res = cons.LOPseparate(scip, null);
			
			//CUTOFF or SEPARATED
			if(separation_res != SCIP_DIDNOTFIND)
				return separation_res;
		}
		
		scip.debugMsg("all linear ordering constraints are feasible.\n");
		return SCIP_FEASIBLE;
	}
	
	public void consexit(SCIP scip, Cons_LOP[] conss) {
		//This alternate implementation skips the default consexit in ConstranitHandler,
		//and won't call cons.exit() on each Cons_LOP.
		scip.debugMsg("exiting linear ordering constraint handler <%s>\n", conshdlr.getName());
		
		if(scip.getSubscipDepth() > 0)
			return;

		SCIP_SOL sol = scip.getBestSol();
		if (sol == null) {
			scip.debugMsg("no best solution\n"); 
			return;
		}
		
		for(Cons_LOP cons : conss) {
			SCIP_VAR[][] vars = cons.vars;
			int n = vars.length;
			
			int[] outdeg = new int[n];
			for(int i=0; i<n; i++) {
				for(int j=0; j<n; j++) {
					if(i==j)
						continue;
					double valIJ = scip.getSolVal(sol, vars[i][j]);
					if(valIJ < 0.5)
						outdeg[i]++;
				}
			}
			
			//outdeg is a permutation and we invert it
			int[] indices = new int[n];
			for(int i=0; i<n; i++) {
				indices[outdeg[i]] = i;
			}
			
			scip.infoMessage(null, "\nFinal order of linear ordering constraint <%s>:\n", cons.getName());
			for(int i=0; i<n; i++)
				scip.infoMessage(null, "%d ", indices[i]);
			scip.infoMessage(null, "\n");
		}
	}
	
	public void copy(SCIP subscip) {
		Conshdlr_LOP.includeConshdlrLOP(subscip);
	}
	
	public SCIP_RESULT consprop(SCIP scip, Cons_LOP[] conss, int nusefulconss, int nmarkedconss, SCIP_PROPTIMING proptiming) {
		if(conss.length == 0)
			return SCIP_DIDNOTRUN;
		
		int nGen = 0;
		
		for(Cons_LOP cons : conss) {
			scip.debugMsg("propagating linear ordering constraint <%s>.\n",cons.getName());
			
			SCIP_VAR[][] vars = cons.vars;
			int n = vars.length;

			/* add all symmetry constraints */
			for(int i=0; i<n; i++) {
				for(int j=i+1; j<n; j++) {
					
					if( vars[i][j].getLbLocal() > 0.5 ) {
						InferVarResult eff = scip.inferBinvarCons(vars[j][i], false, cons.getScipCons(), i*n + j);
						if(eff.infeasible()) {
							scip.debugMsg(" -> node infeasible.\n");
							scip.initConflictAnalysis(SCIP_CONFTYPE.PROPAGATION, false);
							scip.addConflictBinvar(vars[i][j]);
							scip.addConflictBinvar(vars[j][i]);
							scip.analyzeConflictCons(cons.getScipCons());
							return SCIP_CUTOFF;
						}
						if(eff.tightened()) {
							nGen++;
						}
					}

					if( vars[i][j].getUbLocal() < 0.5 ) {
						InferVarResult eff = scip.inferBinvarCons(vars[j][i], true, cons.getScipCons(), i*n + j);
						if(eff.infeasible()) {
							scip.debugMsg(" -> node infeasible.\n");
							scip.initConflictAnalysis(SCIP_CONFTYPE.PROPAGATION, false);
							scip.addConflictBinvar(vars[i][j]);
							scip.addConflictBinvar(vars[j][i]);
							scip.analyzeConflictCons(cons.getScipCons());
							return SCIP_CUTOFF;
						}
						if(eff.tightened()) {
							nGen++;
						}
					}
					
					for(int k=i+1; k<n; k++) {
						if(k==j)
							continue;
						
						if( vars[i][j].getLbLocal() > 0.5 && vars[j][k].getLbLocal() > 0.5 ) {
							InferVarResult eff = scip.inferBinvarCons(vars[k][i], false, cons.getScipCons(), n*n + i*n*n + j*n + k);
							if(eff.infeasible()) {
								scip.debugMsg(" -> node infeasible.\n");
								scip.initConflictAnalysis(SCIP_CONFTYPE.PROPAGATION, false);
								scip.addConflictBinvar(vars[i][j]);
								scip.addConflictBinvar(vars[j][k]);
								scip.addConflictBinvar(vars[k][i]);
								scip.analyzeConflictCons(cons.getScipCons());
								return SCIP_CUTOFF;
							}
							if(eff.tightened()) {
								nGen++;
							}
						}
					}
				}
			}
		}
		
		scip.debugMsg("propagated %d domains.\n", nGen);
		if(nGen > 0)
			return SCIP_REDUCEDDOM;
		return SCIP_DIDNOTFIND;
	}

	//Return "true" if INFEASIBLE.
	public boolean consinitlp(SCIP scip, Cons_LOP[] conss) {
		int nGen = 0;
		for(Cons_LOP cons : conss) {
			scip.debugMsg("adding initial rows for linear ordering constraint <%s>.\n",cons.getName());
		
			SCIP_VAR[][] vars = cons.vars;
			int n = vars.length;
			
			/* add all symmetry constraints */
			for(int i=0; i<n; i++) {
				for(int j=i+1; j<n; j++) {
					
					String name = "sym#"+i+"#"+j;
					SCIP_ROW row = scip.createEmptyRowConshdlr(conshdlr, name, 1.0, 1.0, false, false, false);
					scip.cacheRowExtensions(row);
					scip.addVarToRow(row, vars[i][j], 1.0);
					scip.addVarToRow(row, vars[j][i], 1.0);
					scip.flushRowExtensions(row);
					
					SCIP.debug(()->{
						scip.printRow(row, null);
					});
					
					boolean infeasible = scip.addRow(row, false);
					scip.releaseRow(row);
					nGen++;
					
					if(infeasible)
						return true;//infeasible
				}
			}
		}
		scip.debugMsg("added %d equations.\n", nGen);
		return false;//feasible
	}

	//Return "true" if INFEASIBLE.
	public SCIP_RESULT conssepalp(SCIP scip, Cons_LOP[] conss, int nusefulconss) {
		if(conss.length == 0)
			return SCIP_DIDNOTRUN;
		
		boolean separated = false;
		for(Cons_LOP cons : conss) {
			scip.debugMsg("separating LP solution for linear ordering constraint <%s>.\n",cons.getName());
			
			SCIP_RESULT separation_res = cons.LOPseparate(scip, null);
			
			//CUTOFF or SEPARATED
			if(separation_res == SCIP_CUTOFF)
				return separation_res;
			else if(separation_res == SCIP_SEPARATED)
				separated = true;
		}
		
		if(separated) {
			scip.debugMsg("added separating cuts.\n");
			return SCIP_SEPARATED;
		}		
		return SCIP_DIDNOTFIND;
	}
	
	public SCIP_RESULT conssepasol(SCIP scip, Cons_LOP[] conss, int nusefulconss, SCIP_SOL sol) {
		if(conss.length == 0)
			return SCIP_DIDNOTRUN;
		
		boolean separated = false;
		for(Cons_LOP cons : conss) {
			scip.debugMsg("separating solution for linear ordering constraint <%s>.\n",cons.getName());
			
			SCIP_RESULT separation_res = cons.LOPseparate(scip, sol);
			
			//CUTOFF or SEPARATED
			if(separation_res == SCIP_CUTOFF)
				return separation_res;
			else if(separation_res == SCIP_SEPARATED)
				separated = true;
		}
		
		if(separated) {
			scip.debugMsg("added separating cuts.\n");
			return SCIP_SEPARATED;
		}		
		return SCIP_DIDNOTFIND;
	}
	
	@Override
	public SCIP_RESULT consresprop(SCIP scip, Cons_LOP cons, SCIP_VAR infervar, int inferinfo,
			SCIP_BOUNDTYPE boundtype, SCIP_BDCHGIDX bdchgidx, double relaxedbd) {
		
		scip.debugMsg("Propagation resolution of constraint <%s>.\n",cons.getName());

		SCIP_VAR[][] vars = cons.vars;
		int n = vars.length;
		
		if (inferinfo < n*n) {
			int index1 = inferinfo/n;
			int index2 = inferinfo%n;
			
			if(infervar.getUbAtIndex(bdchgidx, false) > 0.5 && infervar.getUbAtIndex(bdchgidx, true) < 0.5) {
				scip.debugMsg(" -> reason for x[%d][%d] == 0 was x[%d][%d] = 1.\n",
						index2, index1, index1, index2);
				scip.addConflictLb(vars[index1][index2], bdchgidx);
				return SCIP_SUCCESS;
			}
			
			if(infervar.getLbAtIndex(bdchgidx, false) < 0.5 && infervar.getLbAtIndex(bdchgidx, true) > 0.5) {
				scip.debugMsg(" -> reason for x[%d][%d] == 1 was x[%d][%d] = 0.\n",
						index2, index1, index1, index2);
				scip.addConflictUb(vars[index1][index2], bdchgidx);
				return SCIP_SUCCESS;
			}
		} else {
			inferinfo -= n*n;
			int index1 = inferinfo/(n*n);
			inferinfo -= index1*(n*n);
			int index2 = inferinfo/n;
			int index3 = inferinfo%n;
			
			scip.debugMsg(" -> reason for x[%d][%d] == 0 was x[%d][%d] = x[%d][%d] = 1.\n",
					index3, index1, index1, index2, index2, index3);
			scip.addConflictLb(vars[index1][index2], bdchgidx);
			scip.addConflictLb(vars[index2][index3], bdchgidx);
			return SCIP_SUCCESS;
		}
		
		return SCIP_DIDNOTFIND;
	}
}

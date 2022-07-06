package JNA_SCIP.Examples;

import static JNA_SCIP.SCIP_VARTYPE.*;

import static JNA_SCIP.SCIP_RETCODE.*;
import static JNA_SCIP.SCIP_LOCKTYPE.*;

import JNA_SCIP.*;

public class Conshdlr_Obj {

	//Toy example of using a custom constraint handler using JSCIP's convenience methods.
	//The problem we solve is:
	// Maximize x
	// 0 <= x <= 100
	// Integer x
	// with a custom constraint enforcing x <= 67.5.
	//This constraint handler is a bit smarter, so that we don't have search blindly:
	//when given an infeasible solution, it will provide a cutting plane on the nearest
	//multiple of 10. For instance, it will refute the solution 83 by a cutting plane x<=80.
	
	//This version uses JNA_SCIP's "ConstraintHandler" type. For a more manual method,
	//see Conshdlr_Manual in the examples.
	
	public static void main(String[] args) {
		SCIP scip = SCIP.create();
		
		scip.includeDefaultPlugins();

		scip.createProbBasic("conshdlr_example");
		
		scip.setObjsense(SCIP_OBJSENSE.MINIMIZE);
		
		SCIP_VAR x = scip.createVarBasic("x", 0, 100, -1, INTEGER);
		scip.addVar(x);

		MyConshdlr conshdlr = new MyConshdlr(scip);
		
		MyCons cons_data = new MyCons(scip, x);
		SCIP_CONS scip_cons = conshdlr.instantiate(scip, "mycons", cons_data);
		
		//put constraint in the problem
		scip.addCons(scip_cons);
		scip.releaseCons(scip_cons);
		
		//Release the variable now that we're done with it
		scip.releaseVar(x);
		
		scip.solve();
		scip.printBestSol(null, true);
		
		scip.free();
	}
	
	static class MyCons extends ConstraintData<MyCons,MyConshdlr> {
		
		private SCIP_VAR var;
		
		public MyCons(SCIP scip, SCIP_VAR var) {
			if(scip.isTransformed())
				var = scip.getTransformedVar(var);
			
			this.var = var;
		}
		
		public SCIP_VAR var() { return var; }
		
		public String toString() {
			return "MyCons{var="+var.getName()+"}";
		}
		
		@Override
		public MyCons transform(SCIP scip) {
			return new MyCons(scip, var);
		}

		@Override
		public void delete(SCIP scip) {
			scip.releaseVar(var);
		}
		
		@Override
		public MyCons copy(SCIP sourcescip, SCIP targetscip,
				SCIP_HASHMAP varmap, SCIP_HASHMAP consmap, boolean global) {

			SCIP_VAR copyvar = var.getCopy(targetscip, sourcescip, varmap, consmap, global);
			return new MyCons(targetscip, copyvar);
		}

		@Override
		public void exit(SCIP scip) {
			//nothing to clean up
			System.out.println("CONSEXIT");
		}
	}
	
	static class MyConshdlr extends ConstraintHandler<MyCons,MyConshdlr> {
		public MyConshdlr(SCIP scip) {
			super(scip, MyCons.class, "MyConshdlr", "my custom handler", -1, -1, 1, true);
			//Unlikely we'll need copying enabled here -- but just to show how to
			//enable an optional handler. This requires the "copy()" method in MyCons
			//and "copy()" in MyConshdlr below.
			enableConsCopy(scip);
		}
		
		//Tries to find a cut. SCIP_CUTOFF if it rendered problem infeasible,
		//SCIP_SEPARATED if it found an efficacious cut. null if no cut.
		//(This isn't SCIP API, just our own method internal to this callback.)
		public SCIP_RESULT findCut(SCIP scip, SCIP_VAR var, double x_val) {
			double cut_val = Math.floor((x_val-0.1)/10)*10;
			if(cut_val <= 67.5)
				return null;
			
			SCIP_ROW row = scip.createEmptyRowConshdlr(findScipConshdlr(scip), "x10row", -1000, cut_val, false, false, true);
			scip.cacheRowExtensions(row);
			scip.addVarToRow(row, var, 1);
			scip.flushRowExtensions(row);
			System.out.println("Cut with "+cut_val);
			
			boolean is_infeasible = scip.addRow(row, false);
			scip.releaseRow(row);
			
			if(is_infeasible) {
				return SCIP_RESULT.SCIP_CUTOFF;
			} else {
				return SCIP_RESULT.SCIP_SEPARATED;
			}
		}

		@Override
		public SCIP_RESULT conscheck(SCIP scip, MyCons[] conss, SCIP_SOL sol, boolean checkintegrality,
				boolean checklprows, boolean printreason, boolean completely) {
			
			for(MyCons cons : conss) {
				double x_val = scip.getSolVal(sol, cons.var());
				System.out.println("CONSCHECK, x = "+x_val);

				boolean is_ok = x_val <= 67.5;
				if(!is_ok)
					return SCIP_RESULT.SCIP_INFEASIBLE;
			}
			return SCIP_RESULT.SCIP_FEASIBLE;
		}

		@Override
		public SCIP_RETCODE conslock(SCIP scip, MyCons cons, SCIP_LOCKTYPE locktype, int nlockspos, int nlocksneg) {
			System.out.println("CONSLOCK called on "+cons);
			scip.addVarLocksType(cons.var(), MODEL, nlocksneg, nlockspos);
			return SCIP_OKAY;
		}

		@Override
		public SCIP_RESULT consenfops(SCIP scip, MyCons[] conss, int nusefulconss, boolean solinfeasible,
				boolean objinfeasible) {
			System.out.println("CONSENFOPS called");
			//This is often implemented similarly to consenfolp, but on this simple problem it's never actually
			//called -- we never hit a pseudosolution (PS). But if we did, we could just forward it to our consenfolp
			//implementation.
			return consenfolp(scip, conss, nusefulconss, solinfeasible);
		}

		@Override
		public SCIP_RESULT consenfolp(SCIP scip, MyCons[] conss, int nusefulconss, boolean solinfeasible) {
			
			for(MyCons cons : conss) {
				double x_val = scip.getSolVal(null, cons.var());//no sol provided, use "null" here
				System.out.println("CONSENFOLP, x = "+x_val);

				boolean is_ok = x_val <= 67.5;
				if(!is_ok) {
					SCIP_RESULT cut_res = findCut(scip, cons.var(), x_val);
					if(cut_res != null)
						return cut_res;
					else
						return SCIP_RESULT.SCIP_INFEASIBLE;
				}
			}
			return SCIP_RESULT.SCIP_FEASIBLE;
		}

		@Override
		public SCIP_RESULT consprop(SCIP scip, MyCons[] conss, int nusefulconss, int nmarkedconss,
				SCIP_PROPTIMING proptiming) {
			System.out.println("Override conspropr implementation fired, did nothing");
			return SCIP_RESULT.SCIP_DIDNOTRUN;
		}

		@Override
		public void copy(SCIP subscip) {
			//Don't need to save this object, it gets put in the mapping.
			@SuppressWarnings("unused")
			MyConshdlr conshdlr = new MyConshdlr(subscip);
		}
	}
}
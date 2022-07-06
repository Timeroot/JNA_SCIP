package JNA_SCIP.Examples;

import static JNA_SCIP.SCIP_RETCODE.*;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import JNA_SCIP.*;

public class Conshdlr_Manual {

	//Toy example of using a custom constraint handler.
	//The problem we solve is:
	// Maximize x
	// 0 <= x <= 10
	// Integer x
	// with a custom constraint enforcing x <= 6.5.
	
	//This version uses a more "manual" version of the SCIP_DECL's.
	//Conshdlr_Obj shows how to use JNA_SCIP's "ConstraintHandler" type, and
	//adds cuts as it goes.
	
	public static void main(String[] args) {
		SCIP scip = SCIP.create();
		scip.includeDefaultPlugins();
		scip.setIntParam("display/verblevel", 4);

		scip.createProbBasic("conshdlr_example");
		
		SCIP_VAR x = scip.createVarBasic("x", 0, 10, -1, SCIP_VARTYPE.INTEGER);
		scip.addVar(x);
		
		SCIP_DECL_CONSCHECK conscheck_hdlr = new SCIP_DECL_CONSCHECK() {
			@Override
			public SCIP_RETCODE conscheck(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss, SCIP_SOL sol,
					boolean checkintegrality, boolean checklprows, boolean printreason, boolean completely,
					IntByReference scip_result) {
				
				SCIP_CONS cons = new SCIP_CONS(conss.getPointer(0));
				SCIP_CONSDATA consdata = JSCIP.consGetData(cons);
				SCIP_VAR var = new SCIP_VAR(consdata.getPointer());
				
				double x_val = scip.getSolVal(sol, var);
				System.out.println("CONSCHECK, x = "+x_val);

				boolean is_ok = x_val <= 6.5;
				if(is_ok)
					scip_result.setValue(SCIP_RESULT.SCIP_FEASIBLE.ordinal());
				else
					scip_result.setValue(SCIP_RESULT.SCIP_INFEASIBLE.ordinal());
				return SCIP_OKAY;
			}
		};

		SCIP_DECL_CONSENFOLP consenfolp_hdlr = new SCIP_DECL_CONSENFOLP() {
			@Override
			public SCIP_RETCODE consenfolp(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss, int nusefulconss,
					boolean solinfeasible, IntByReference scip_result) {

				SCIP_CONS cons = new SCIP_CONS(conss.getPointer(0));
				SCIP_CONSDATA consdata = JSCIP.consGetData(cons);
				SCIP_VAR var = new SCIP_VAR(consdata.getPointer());
				
				double x_val = scip.getSolVal(null, var);//no sol provided, use "null" here
				System.out.println("CONSENFOLP, x = "+x_val);
				
				boolean is_ok = x_val <= 6.5;
				if(is_ok)
					scip_result.setValue(SCIP_RESULT.SCIP_FEASIBLE.ordinal());
				else
					scip_result.setValue(SCIP_RESULT.SCIP_INFEASIBLE.ordinal());
				return SCIP_OKAY;
			}
		};

		SCIP_DECL_CONSENFOPS consenfops_hdlr = new SCIP_DECL_CONSENFOPS() {
			@Override
			public SCIP_RETCODE consenfops(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss, int nusefulconss,
					boolean solinfeasible, boolean objinfeasible, IntByReference scip_result) {
				System.out.println("CONSENFOPS called");
				
				return SCIP_OKAY;
			}
		};

		SCIP_DECL_CONSLOCK conslock_hdlr = new SCIP_DECL_CONSLOCK() {
			@Override
			public SCIP_RETCODE conslock(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS cons, SCIP_LOCKTYPE locktype,
					int nlockspos, int nlocksneg) {
				//Our constraint could be violated by increasing x, so mark it with a "lock" here
				//If could be violated by decreasing x, swap nlockspos and nlocksneg.
				//If could be violated either way, use nlockpos+nlocksneg
				System.out.println("Conslock on "+cons);
				SCIP_CONSDATA consdata = JSCIP.consGetData(cons);
				SCIP_VAR var = new SCIP_VAR(consdata.getPointer());
				scip.addVarLocksType(var, SCIP_LOCKTYPE.MODEL, nlocksneg, nlockspos);
				
				return SCIP_OKAY;
			}
		};
		
		SCIP_DECL_CONSDELETE consdelete_hdlr = new SCIP_DECL_CONSDELETE() {
			@Override
			public SCIP_RETCODE consdelete(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS cons, PointerByReference consdata) {
				//Release the variable we were holding
				SCIP_VAR var = new SCIP_VAR(consdata.getValue());
				scip.releaseVar(var);
				
				//Set the associated consdata to null (not strictly necessary, but SCIP gives debug warnings otherwise)
				consdata.setValue(null);
				
				return SCIP_OKAY;
			}
		};

		SCIP_CONSHDLR conshdlr = JSCIP.CALL_SCIPincludeConshdlrBasic(scip,
				"myconshdlr", "A custom handler", -1, -1, 1,
				true, consenfolp_hdlr, consenfops_hdlr, conscheck_hdlr, conslock_hdlr, null);
		scip.setConshdlrDelete(conshdlr, consdelete_hdlr);

		SCIP_CONS cons_inst = makeMyCons(scip, conshdlr, x);
		scip.addCons(cons_inst);
		//Release the constraint + variable now that we're done defining them
		scip.releaseCons(cons_inst);
		scip.releaseVar(x);
		
		scip.solve();
		scip.printBestSol(null, true);
		
		scip.free();
	}
	
	static SCIP_CONS makeMyCons(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_VAR x) {
		scip.captureVar(x);
		SCIP_CONS cons_inst = scip.createCons("mycons", conshdlr, x.getPointer(),
				false, true, true, true, true, false, false, false, true, true);
		System.out.println("Created cons "+cons_inst);
		return cons_inst;
	}
}
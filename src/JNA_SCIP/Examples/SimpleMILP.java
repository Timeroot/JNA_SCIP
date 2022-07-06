package JNA_SCIP.Examples;

import JNA_SCIP.*;

public class SimpleMILP {

	public static void main(String[] args) {
		SCIP scip = SCIP.create();
		scip.includeDefaultPlugins();
		
		scip.infoMessage(null, "SCIP Loaded, \"%s\"\n", "Hello world");

		System.out.println("Verblevel = "+scip.getVerbLevel());
		
		scip.setEmphasis(SCIP_PARAMEMPHASIS.OPTIMALITY, false);
		
		scip.createProbBasic("test");
		double inf = scip.infinity();
		
		//obj = 40x + 30y
		//cons_1: x + 3y <= 12
		//cons_2: 3x + y <= 16
		//x >= 0, y >= 0
		//x is an integer
		SCIP_VAR x = scip.createVarBasic("x", 0, inf, -40.0, SCIP_VARTYPE.INTEGER);
		scip.addVar(x);
		
		SCIP_VAR y = scip.createVarBasic("y", 0, inf, -30.0, SCIP_VARTYPE.CONTINUOUS);
		scip.addVar(y);
		
		SCIP_CONS cons_1 = scip.createConsBasicLinear("cons1", new SCIP_VAR[]{x,y}, new double[]{1,3}, -inf, 12);
		scip.addCons(cons_1);

		SCIP_CONS cons_2 = scip.createConsBasicLinear("cons2", new SCIP_VAR[]{x,y}, new double[]{3,1}, -inf, 16);
		scip.addCons(cons_2);

		scip.releaseVar(x);
		scip.releaseVar(y);
		scip.releaseCons(cons_1);
		scip.releaseCons(cons_2);
		
		scip.printOrigProblem(null, "cip", false);
		
		scip.solve();
		
		scip.printSol(scip.getBestSol(), null, false);
		
		scip.free();
	}
}
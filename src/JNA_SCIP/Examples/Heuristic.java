package JNA_SCIP.Examples;

import static JNA_SCIP.SCIP_VARTYPE.INTEGER;

import com.sun.jna.ptr.IntByReference;

import JNA_SCIP.SCIP;
import JNA_SCIP.SCIP_CONS;
import JNA_SCIP.SCIP_DECL_HEUREXEC;
import JNA_SCIP.SCIP_HEUR;
import JNA_SCIP.SCIP_HEURTIMING;
import JNA_SCIP.SCIP_RESULT;
import JNA_SCIP.SCIP_RETCODE;
import JNA_SCIP.SCIP_SOL;
import JNA_SCIP.SCIP_VAR;

public class Heuristic {

	//We have a problem given by
	// x+2y <= 100
	// 3y+4z <= 300
	// 5z+6x <= 700
	//with x, y, z all integral, objective function
	// 6x + 5y + 3z.
	
	//We add a custom heuristic that suggests the following points:
	// (5, 10, 15); (68, 16, 58); and (100, 200, 300).
	//These are points we expect to be solutions with our expert knowledge!
	//The first will not improve the primal objective, the second is useful,
	//and the third is invalid.
	
	static SCIP_HEUR scip_heur;
	
	public static void main(String[] args) {
		SCIP scip = SCIP.create();

		scip.includeDefaultPlugins();
		scip.setIntParam("display/verblevel", 4);

		scip.createProbBasic("heuristic_example");
		
		double inf = scip.infinity();
		SCIP_VAR x = scip.createVarBasic("x", 0, inf, -6, INTEGER);
		scip.addVar(x);
		
		SCIP_VAR y = scip.createVarBasic("y", 0, inf, -5, INTEGER);
		scip.addVar(y);
		
		SCIP_VAR z = scip.createVarBasic("z", 0, inf, -3, INTEGER);
		scip.addVar(z);
		
		SCIP_CONS cons_1 = scip.createConsBasicLinear("cons1", new SCIP_VAR[]{x,y}, new double[]{1,2}, -inf, 100);
		scip.addCons(cons_1);
		scip.releaseCons(cons_1);

		SCIP_CONS cons_2 = scip.createConsBasicLinear("cons2", new SCIP_VAR[]{y,z}, new double[]{3,4}, -inf, 300);
		scip.addCons(cons_2);
		scip.releaseCons(cons_2);

		SCIP_CONS cons_3 = scip.createConsBasicLinear("cons3", new SCIP_VAR[]{z,x}, new double[]{5,6}, -inf, 700);
		scip.addCons(cons_3);
		scip.releaseCons(cons_3);

		scip.releaseVar(x);
		scip.releaseVar(y);
		scip.releaseVar(z);
		
		final int[][] sols_to_try = new int[][]{{5,10,15}, {68,16,58}, {100,200,300}};
		
		SCIP_DECL_HEUREXEC heurexec = new SCIP_DECL_HEUREXEC() {
			@Override
			public SCIP_RETCODE heurexec(SCIP scip, SCIP_HEUR heur, SCIP_HEURTIMING heurtiming, boolean nodeinfeasible,
					IntByReference scip_result) {
				System.out.println("HEUREXEC CALLED");
				
				for(int[] vals : sols_to_try) {
					
					SCIP_SOL scip_sol = scip.createSol(scip_heur);
					scip.setSolVal(scip_sol, x, vals[0]);
					scip.setSolVal(scip_sol, y, vals[1]);
					scip.setSolVal(scip_sol, z, vals[2]);
					
					boolean stored = scip.trySol(scip_sol, true, false, true, false, true);
					System.out.println("Was solsution stored? "+stored);
					
					scip.freeSol(scip_sol);
				}
				
				scip_result.setValue(SCIP_RESULT.SCIP_FOUNDSOL.ordinal());
				return SCIP_RETCODE.SCIP_OKAY;
			}
		};
		scip_heur = scip.includeHeurBasic("myheur", "a custom heuristic", (byte)'d',
				4000, 1, 0, -1, SCIP_HEURTIMING.BEFORENODE, false, heurexec, null);

		scip.solve();
		scip.printBestSol(null, true);
		
		scip.free();
	}
}

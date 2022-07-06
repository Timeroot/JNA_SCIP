package JNA_SCIP.Examples;

import java.io.PrintStream;
import java.util.ArrayList;

import JNA_SCIP.*;

/*
 * Translation of "queens" from the SCIP examples, transformed one-to-one C-to-Java.
 * For an example of a more clean and idiomatic usage, see Queens_Obj.
 * Copyright Alexander Meiburg 2022.
 * Original example copyright Cornelius Schwarz (2007) and University of Bayreuth (2007)
 * 
 * Source code of the original available online at e.g.
 * https://www.scipopt.org/doc-6.0.1/html/queens_8hpp_source.php
 */

//queens_main.cpp
public class Queens_C {
	public static void main(String[] args) {
		System.out.println("*************************************************");
		System.out.println("* n-queens solver based on SCIP                 *");
		System.out.println("*                                               *");
		System.out.println("* (c) Cornelius Schwarz (2007)                  *");
		System.out.println("* Adapted to JNA_SCIP, Alexander Meiburg (2022) *");
		System.out.println("*************************************************");
		
		if(args.length < 1) {
			System.err.println("Requires one argument, <number of queens>");
			System.exit(1);
		}
		
		int n = Integer.valueOf(args[0]);
		
		try {
			QueensSolver solver = new QueensSolver(n);
			
			solver.solve();
			solver.disp(System.out);
			
			solver.dispose();
			
		} catch(ScipException exc) {
			exc.printStackTrace();
			System.exit(1);
		}
	}

	//queens.cpp
	static class QueensSolver {
		private SCIP scip;
		private int n;
		private SCIP_VAR[][] vars;
		private ArrayList<SCIP_CONS> cons;
		
		public QueensSolver(int n) {
			scip = new SCIP();//uninitialized, just a null pointer.
			this.n = n;
			this.vars = new SCIP_VAR[n][n];
			this.cons = new ArrayList<>();
			
			JSCIP.CALL_SCIPcreate(scip);
			JSCIP.CALL_SCIPincludeDefaultPlugins(scip);
			JSCIP.LIB.SCIPmessagehdlrSetQuiet(JSCIP.LIB.SCIPgetMessagehdlr(scip), true);
			JSCIP.LIB.SCIPcreateProb(scip, "queens", null, null, null, null, null, null, null);
			JSCIP.LIB.SCIPsetObjsense(scip, SCIP_OBJSENSE.MAXIMIZE);
			
			for(int i=0; i<n; i++) {
				for(int j=0; j<n; j++) {
					SCIP_VAR var = new SCIP_VAR();//uninitialized, just a null pointer.
					String namebuf = "x#"+i+"#"+j;
					JSCIP.CALL_SCIPcreateVar(scip, var, namebuf, 0, 1, 1, SCIP_VARTYPE.BINARY,
							true, false, null, null, null, null, null);
					
					JSCIP.CALL_SCIPaddVar(scip, var);
					vars[i][j] = var;
				}
			}
			
			for(int i=0; i<n; i++) {
				SCIP_CONS cons = new SCIP_CONS();
				String namebuf = "row_"+i;
				JSCIP.CALL_SCIPcreateConsLinear(scip, cons, namebuf, null, null, 1.0, 1.0,
						  true, true, true, true, true, false, false, false, false, false);
				
				for(int j=0; j<n; j++)
					JSCIP.CALL_SCIPaddCoefLinear(scip, cons, vars[i][j], 1.0);
				
				JSCIP.CALL_SCIPaddCons(scip, cons);
				this.cons.add(cons);
			}
			
			for(int j=0; j<n; j++) {
				SCIP_CONS cons = new SCIP_CONS();
				String namebuf = "col_"+j;
				JSCIP.CALL_SCIPcreateConsLinear(scip, cons, namebuf, null, null, 1.0, 1.0,
						  true, true, true, true, true, false, false, false, false, false);
				
				for(int i=0; i<n; i++)
					JSCIP.CALL_SCIPaddCoefLinear(scip, cons, vars[i][j], 1.0);
				
				JSCIP.CALL_SCIPaddCons(scip, cons);
				this.cons.add(cons);
			}
			
			for(int j=0; j<n; j++) {
				SCIP_CONS cons = new SCIP_CONS();
				String namebuf = "diag_col_down_"+j;
				JSCIP.CALL_SCIPcreateConsLinear(scip, cons, namebuf, null, null, 0.0, 1.0,
						  true, true, true, true, true, false, false, false, false, false);
				
				for(int i=0; i<n-j; i++)
					JSCIP.CALL_SCIPaddCoefLinear(scip, cons, vars[i][j+i], 1.0);
				
				JSCIP.CALL_SCIPaddCons(scip, cons);
				this.cons.add(cons);
			}
			
			for(int i=0; i<n; i++) {
				SCIP_CONS cons = new SCIP_CONS();
				String namebuf = "diag_row_down_"+i;
				JSCIP.CALL_SCIPcreateConsLinear(scip, cons, namebuf, null, null, 0.0, 1.0,
						  true, true, true, true, true, false, false, false, false, false);
				
				for(int j=0; j<n-i; j++)
					JSCIP.CALL_SCIPaddCoefLinear(scip, cons, vars[i+j][j], 1.0);
				
				JSCIP.CALL_SCIPaddCons(scip, cons);
				this.cons.add(cons);
			}

			
			for(int j=0; j<n; j++) {
				SCIP_CONS cons = new SCIP_CONS();
				String namebuf = "diag_col_up_"+j;
				JSCIP.CALL_SCIPcreateConsLinear(scip, cons, namebuf, null, null, 0.0, 1.0,
						  true, true, true, true, true, false, false, false, false, false);
				
				for(int i=0; i<n-j; i++)
					JSCIP.CALL_SCIPaddCoefLinear(scip, cons, vars[i][n-j-i-1], 1.0);
				
				JSCIP.CALL_SCIPaddCons(scip, cons);
				this.cons.add(cons);
			}
			
			for(int i=0; i<n; i++) {
				SCIP_CONS cons = new SCIP_CONS();
				String namebuf = "diag_row_up_"+i;
				JSCIP.CALL_SCIPcreateConsLinear(scip, cons, namebuf, null, null, 0.0, 1.0,
						  true, true, true, true, true, false, false, false, false, false);
				
				for(int j=0; j<n-i; j++)
					JSCIP.CALL_SCIPaddCoefLinear(scip, cons, vars[i+j][n-j-1], 1.0);
				
				JSCIP.CALL_SCIPaddCons(scip, cons);
				this.cons.add(cons);
			}
		}
		
		public void disp(PrintStream out) {
			SCIP_SOL sol = JSCIP.LIB.SCIPgetBestSol(scip);
			out.println("solution for "+this.n+"-queens:\n");
			
			if (sol == null) {
				out.println("no solution found");
				return;
			}
			
			for(int i=0; i<n; i++) {
				for(int j=0; j<n; j++)
					out.print(" ---");
				out.println();
				
				for(int j=0; j<n; j++) {
					out.print("| ");
					if(JSCIP.LIB.SCIPgetSolVal(scip, sol, vars[i][j]) > 0.5)
						out.print("D ");
					else
						out.print("  ");
				}
				out.println("|");
			}
			for(int j=0; j<n; j++)
				out.print(" ---");
			out.println();
		}
		
		public void solve() {
			JSCIP.CALL_SCIPsolve(scip);
		}
		
		//Our equivalent of the C++ example's ~QueensSolver
		public void dispose() {
			try {
				for(int i=0; i<n; i++) {
					for(int j=0; j<n; j++)
						JSCIP.CALL_SCIPreleaseVar(scip, vars[i][j]);
				}
				vars = null;
				
				for(int i=0; i<cons.size(); i++) {
					JSCIP.CALL_SCIPreleaseCons(scip, cons.get(i));
				}
				cons.clear();
				
				JSCIP.CALL_SCIPfree(scip);
			} catch(ScipException re) {
				System.err.println("SCIP Error:");
				re.printStackTrace();
				System.exit(1);
				
			} catch(Exception e) {
				System.err.println("Unknown Error:");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
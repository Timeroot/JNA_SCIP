package JNA_SCIP.Examples;

import java.io.PrintStream;
import java.util.ArrayList;

import JNA_SCIP.*;

/*
 * Translation of "queens" from the SCIP examples, translated to Java.
 * For an example of a more one-to-one mapping from C, see Queens_C.
 * 
 * Copyright Alexander Meiburg 2022.
 * Original example copyright Cornelius Schwarz (2007) and University of Bayreuth (2007)
 * 
 * Source code of the original available online at e.g.
 * https://www.scipopt.org/doc-6.0.1/html/queens_8hpp_source.php
 */

//queens_main.cpp
public class Queens_Obj {
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
			scip = SCIP.create();
			this.n = n;
			this.vars = new SCIP_VAR[n][n];
			this.cons = new ArrayList<>();
			
			scip.includeDefaultPlugins();
			scip.getMessagehdlr().setQuiet(true);
			scip.createProbBasic("queens");
			scip.setObjsense(SCIP_OBJSENSE.MAXIMIZE);
			
			for(int i=0; i<n; i++) {
				for(int j=0; j<n; j++) {
					String name = "x#"+i+"#"+j;
					SCIP_VAR var = scip.createVarBasic(name, 0, 1, 1, SCIP_VARTYPE.BINARY);
					scip.addVar(var);
					vars[i][j] = var;
				}
			}
			
			for(int i=0; i<n; i++) {
				String name = "row_"+i;
				SCIP_CONS cons = scip.createConsBasicLinear(name, null, null, 1.0, 1.0);
				
				for(int j=0; j<n; j++)
					scip.addCoefLinear(cons, vars[i][j], 1.0);
				
				scip.addCons(cons);
				this.cons.add(cons);
			}
			
			for(int j=0; j<n; j++) {
				String name = "col_"+j;
				SCIP_CONS cons = scip.createConsBasicLinear(name, null, null, 1.0, 1.0);
				
				for(int i=0; i<n; i++)
					scip.addCoefLinear(cons, vars[i][j], 1.0);

				scip.addCons(cons);
				this.cons.add(cons);
			}
			
			for(int j=0; j<n; j++) {
				String name = "diag_col_down_"+j;
				SCIP_CONS cons = scip.createConsBasicLinear(name, null, null, 0.0, 1.0);
				
				for(int i=0; i<n-j; i++)
					scip.addCoefLinear(cons, vars[i][j+i], 1.0);

				scip.addCons(cons);
				this.cons.add(cons);
			}
			
			for(int i=0; i<n; i++) {
				String name = "diag_row_down_"+i;
				SCIP_CONS cons = scip.createConsBasicLinear(name, null, null, 0.0, 1.0);
				
				for(int j=0; j<n-i; j++)
					scip.addCoefLinear(cons, vars[i+j][j], 1.0);

				scip.addCons(cons);
				this.cons.add(cons);
			}

			
			for(int j=0; j<n; j++) {
				String name = "diag_col_up_"+j;
				SCIP_CONS cons = scip.createConsBasicLinear(name, null, null, 0.0, 1.0);
				
				for(int i=0; i<n-j; i++)
					scip.addCoefLinear(cons, vars[i][n-j-i-1], 1.0);

				scip.addCons(cons);
				this.cons.add(cons);
			}
			
			for(int i=0; i<n; i++) {
				String name = "diag_row_up_"+i;
				SCIP_CONS cons = scip.createConsBasicLinear(name, null, null, 0.0, 1.0);
				
				for(int j=0; j<n-i; j++)
					scip.addCoefLinear(cons, vars[i+j][n-j-1], 1.0);

				scip.addCons(cons);
				this.cons.add(cons);
			}
		}
		
		public void disp(PrintStream out) {
			SCIP_SOL sol = scip.getBestSol();
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
					boolean solval = scip.getSolVal(sol, vars[i][j]) > 0.5;
					out.print("| "+(solval?'D':' ')+" ");
				}
				out.println("|");
			}
			for(int j=0; j<n; j++)
				out.print(" ---");
			out.println();
		}
		
		public void solve() {
			scip.solve();
		}
		
		//Our equivalent of the C++ example's ~QueensSolver
		public void dispose() {
			try {
				for(SCIP_VAR[] var_row : this.vars)
					for(SCIP_VAR var : var_row)
						scip.releaseVar(var);
				vars = null;
				
				for(SCIP_CONS cons : this.cons)
					scip.releaseCons(cons);
				cons.clear();
				
				scip.free();
				
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

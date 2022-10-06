package JNA_SCIP.Examples.Binpacking;

import java.util.Arrays;

import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;

import JNA_SCIP.*;

public class PricerBinpacking extends Pricer {
	//PricerData object here
	Conshdlr_Samediff conshdlr;
	SCIP_CONS[] conss;
	long[] weights;
	int[] ids;
	int nitems;
	long capacity;
	
	//Pricer implementation
	static final String PRICER_NAME = "binpacking";
	public PricerBinpacking(SCIP scip) {
		super(scip,	PRICER_NAME, "pricer for binpacking tours", 0, true);
	}
	
	void addBranchingDecisionConss(SCIP scip, SCIP subscip, SCIP_VAR[] vars) {
		SCIP_CONS[] conss = conshdlr.getScipConshdlr().getConss();
		
		for(SCIP_CONS scip_cons : conss) {
			if(!scip_cons.isActive())
				continue;
			
			Cons_Samediff cons = conshdlr.getJavaCons(scip_cons);
			int id1 = cons.item1;
			int id2 = cons.item2;
			ConsType type = cons.type;
			
			scip.debugMsg("create varbound for %s(%d,%d)\n", type == ConsType.SAME ? "same" : "diff",
					ProbdataBinpacking.get().ids[id1], ProbdataBinpacking.get().ids[id2]);
			
			double lhs, rhs, vbdcoef;
			if(type == ConsType.SAME) {
				lhs = 0.0;
				rhs = 0.0;
				vbdcoef = -1.0;
				
			} else {
				lhs = -scip.infinity();
				rhs = 1.0;
				vbdcoef = 1.0;
			}
			
			SCIP_VAR var1 = vars[id1], var2 = vars[id2];
			SCIP_CONS newcons = subscip.createConsBasicVarbound(scip_cons.getName(), var1, var2, vbdcoef, lhs, rhs);
			
			//subscip.debugPrintCons(cons, null);
			
			subscip.addCons(newcons);
			subscip.releaseCons(newcons);
		}
	}
	
	void addFixedVarsConss(SCIP scip, SCIP subscip, SCIP_VAR[] vars) {
		SCIP_VAR[] origVars = scip.getVars();
		
		for(SCIP_VAR origvar : origVars) {
			assert origvar.getType() == SCIP_VARTYPE.BINARY;
			
			if(origvar.getUbLocal() < 0.5) {
				scip.debugMsg("variable <%s> glb=[%.15g,%.15g] loc=[%.15g,%.15g] is fixed to zero\n",
						origvar.getName(), origvar.getLbLocal(), origvar.getUbGlobal(), origvar.getLbLocal(), origvar.getLbGlobal());
				
				VardataBinpacking vardata = VardataBinpacking.getData(origvar);
				boolean needed = true;
				int[] consids = vardata.consids;
				int nconsids = consids.length;
				
				SCIP_VAR[] logicorvars = new SCIP_VAR[nitems];
				int nlogicorvars = 0;
				int consid = consids[0];
				int nvars = 0;
				
				for(int c=0, o=0; o < nitems && needed; o++) {
					
					assert o <= consid;
					SCIP_CONS scip_cons = conss[o];
					
					if(scip_cons.isEnabled()) {
						assert scip.getNFixedonesSetppc(scip_cons) == 0;
						
						SCIP_VAR var = vars[nvars++];
						assert var != null;
						
						if(o == consid) {
							var = subscip.getNegatedVar(var);
						}
						
						logicorvars[nlogicorvars++] = var;
					}
		            else if(o == consid)
		                needed = false;
					
					if(o == consid) {
						c++;
						if (c == nconsids) {
							consid = nitems + 100;
						} else {
							assert consid < consids[c];
							consid = consids[c];
						}
					}
				}
				
				if(needed) {
					//Trim to size
					logicorvars = Arrays.copyOf(logicorvars, nlogicorvars);
					
					SCIP_CONS cons = subscip.createConsBasicLogicor(origvar.getName(), logicorvars);
					subscip.setConsInitial(cons, false);
					subscip.addCons(cons);
					subscip.releaseCons(cons);
				}
			}
		}
	}
	
	SCIP_VAR[] initPricing(SCIP scip, SCIP subscip) {
		int nvars = 0;
		SCIP_VAR[] vars = new SCIP_VAR[nitems];
		long[] vals = new long[nitems];
		
		for(int c=0; c<nitems; c++) {
			SCIP_CONS scip_cons = conss[c];
			
			if(!scip_cons.isEnabled())
				continue;
			
			if(scip.getNFixedonesSetppc(scip_cons) == 1) {
				scip.delConsLocal(scip_cons);
				continue;
			}
			
			double dual = scip.getDualsolSetppc(scip_cons);
			
			SCIP_VAR var = subscip.createVarBasic(scip_cons.getName(), 0.0, 1.0, dual, SCIP_VARTYPE.BINARY);
			subscip.addVar(var);
			
			vals[nvars] = weights[c];
			vars[nvars] = var;
			nvars++;
			
			subscip.releaseVar(var);
		}
		
		SCIP_CONS cons = subscip.createConsBasicKnapsack("capacity", nvars, vars, vals, capacity);
		
		subscip.addCons(cons);
		subscip.releaseCons(cons);
		
		addBranchingDecisionConss(scip, subscip, vars);
		
		addFixedVarsConss(scip, subscip, vars);
		
		return vars;
	}

	@Override
	public void pricerfree(SCIP scip) {
		//do nothing
	}
	
	@Override
	public void pricerinit(SCIP scip) {
		for(int c=0; c<nitems; c++) {
			SCIP_CONS cons = conss[c];
			
			scip.releaseCons(cons);
			cons = scip.getTransformedCons(cons);
			scip.captureCons(cons);
			
			conss[c] = cons;
		}
	}
	
	@Override
	public void pricerexitsol(SCIP scip) {
		for(SCIP_CONS cons : conss) {
			scip.releaseCons(cons);
		}
	}

	@Override
	public boolean pricerredcost(SCIP scip, DoubleByReference lowerbound, IntByReference stopearly) {
		//TODO: Original example copies over time and memory limits -- we skip that here for conciseness
		
		SCIP subscip = SCIP.create();
		subscip.includeDefaultPlugins();
		
		subscip.createProbBasic("pricing");
		subscip.setObjsense(SCIP_OBJSENSE.MAXIMIZE);
		
		subscip.setBoolParam("misc/catchctrlc", false);
		subscip.setIntParam("display/verblevel", 0);
		
		SCIP_VAR[] vars = initPricing(scip, subscip);
		
		scip.debugMsg("solver pricer problem\n");
		
		subscip.solve();
		
		SCIP_SOL[] sols = subscip.getSols();
		boolean addvar = false;
		
		for(SCIP_SOL sol : sols) {
			boolean feasible = subscip.checkSolOrig(sol, false, false);
			
			if(!feasible) {
				scip.warningMessage("solution in pricing problem (capacity <%lld>) is infeasible\n", capacity);
				continue;
			}

			/* check if the solution has a value greater than 1.0 */
			if(subscip.isFeasGT(subscip.getSolOrigObj(sol), 1.0)) {
				
				int nconss = 0;
				StringBuilder name = new StringBuilder("items"); //name of newly added variable
				int[] consids = new int[nitems];
				
				SCIP.debug(() -> subscip.printSol(sol, null, false));
				
				for(int o=0, v=0; o<nitems; o++) {
					if(!conss[o].isEnabled())
						continue;
					
					if(subscip.getSolVal(sol, vars[v]) > 0.5) {
						name.append('_').append(ids[o]);
						consids[nconss] = o;
						nconss++;
					}
					
					v++;
				}
				
				//shrink to just the first nconss
				consids = Arrays.copyOf(consids, nconss); 
				
				VardataBinpacking vardata = new VardataBinpacking(consids);
				SCIP_VAR var = VardataBinpacking.createVarBinpacking(scip, name.toString(), 1.0, false, true, vardata);
				
				scip.addPricedVar(var, 1.0);
				addvar = true;
				
				scip.chgVarUbLazy(var, 1.0);
				
				for(int v = 0; v < nconss; v++) {
					scip.addCoefSetppc(conss[consids[v]], var);
				}
				
				SCIP.debug(() -> scip.printVar(var, null));
				scip.releaseVar(var);
			}
			else
				break;
		}
		
		boolean success = (addvar || subscip.getStatus() == SCIP_STATUS.OPTIMAL);
		
		subscip.free();
		
		return success;
	}

	@Override
	public boolean pricerfarkas(SCIP scip) {
		scip.warningMessage("Current master LP is infeasible, but Farkas pricing was not implemented\n");
		System.exit(1);
		return false;
	}

	static PricerBinpacking includePricerBinpacking(SCIP scip) {
		PricerBinpacking pricer = new PricerBinpacking(scip);
		pricer.conshdlr =
				(Conshdlr_Samediff)ConstraintHandler.findJavaConshdlr(scip.findConshdlr("samediff"));
		return pricer;
	}

	static void activate(SCIP scip, SCIP_CONS[] conss, long[] weights, int[] ids, long capacity) {
		SCIP_PRICER scip_pricer = scip.findPricer(PRICER_NAME);
		PricerBinpacking pricer = (PricerBinpacking)Pricer.findJavaPricer(scip_pricer);

		pricer.nitems = conss.length;
		
		assert pricer.nitems > 0;
		assert weights.length == pricer.nitems;
		assert ids.length == pricer.nitems;
		
		pricer.conss = conss.clone();
		pricer.weights = weights.clone();
		pricer.ids = ids.clone();
		pricer.capacity = capacity;
		
		scip.debugMsg("   nitems: %d capacity: %lld  \n", pricer.nitems, capacity);
		scip.debugMsg("      # profits    weights   x  \n");
		
		for(int c=0; c<pricer.nitems; c++) {
			scip.captureCons(conss[c]);
			scip.debugMsgPrint("%4d %3lld\n", c, weights[c]);
		}
		
		scip.activatePricer(scip_pricer);
	}
}

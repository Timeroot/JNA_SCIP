package JNA_SCIP.Examples.Binpacking;

import JNA_SCIP.*;

import java.util.Arrays;
import java.util.List;

import static JNA_SCIP.SCIP_RESULT.*;

public class Conshdlr_Samediff extends ConstraintHandler<Cons_Samediff,Conshdlr_Samediff> {

	/* Constants to define behavior and timing */ 
	static final String CONSHDLR_NAME		= "samediff";
	static final String CONSHDLR_DESC		= "stores the local branching decisions";
	static final int CONSHDLR_ENFOPRIORITY	= 0;
	static final int CONSHDLR_CHECKPRIORITY	= 9999999;
	static final int CONSHDLR_PROPFREQ		= 1;
	static final int CONSHDLR_EAGERFREQ		= 100;
	static final boolean CONSHDLR_DELAYPROP	= false;
	static final boolean CONSHDLR_NEEDSCONS	= true;
	static final SCIP_PROPTIMING CONSHDLR_PROP_TIMING = SCIP_PROPTIMING.BEFORELP;
	
	/* Static methods for including the handler into SCIP or creating constraints. */
	static Conshdlr_Samediff includeConshdlrSamediff(SCIP scip) {
		Conshdlr_Samediff hdlr = new Conshdlr_Samediff(scip);
		return hdlr;
	}
	
	static SCIP_CONS createConsSamediff(SCIP scip, String name,
			int item1, int item2, ConsType type, SCIP_NODE node, boolean local) {
		
		SCIP_CONSHDLR s_hdlr = scip.findConshdlr(CONSHDLR_NAME); 
		Conshdlr_Samediff hdlr = (Conshdlr_Samediff)ConstraintHandler.findJavaConshdlr(s_hdlr);
		
		Cons_Samediff cons_data = new Cons_Samediff(item1, item2, type, node);
		SCIP_CONS scip_cons = hdlr.instantiate(scip, name, cons_data);
		scip.setConsLocal(scip_cons, local);
		
		return scip_cons;
	}

	/* Constructor of the Conshdlr */
	public Conshdlr_Samediff(SCIP scip) {
		super(scip, Cons_Samediff.class,
				CONSHDLR_NAME, CONSHDLR_DESC, CONSHDLR_ENFOPRIORITY, CONSHDLR_CHECKPRIORITY,
				CONSHDLR_EAGERFREQ, CONSHDLR_NEEDSCONS);
		this.enableConsProp(scip, CONSHDLR_PROPFREQ, CONSHDLR_DELAYPROP, CONSHDLR_PROP_TIMING);
		this.enableConsActive(scip);
		this.enableConsDective(scip);
	}

	//Meat of the class: propagating constraint info
	@Override
	public SCIP_RESULT consprop(SCIP scip, Cons_Samediff[] conss, int nusefulconss, int nmarkedconss, SCIP_PROPTIMING proptiming) {
		scip.debugMsg("propagation constraints of constraint handler <"+CONSHDLR_NAME+">\n");

		ProbdataBinpacking probdata = ProbdataBinpacking.get();
		
		boolean reduceddom = false;
		
		for(Cons_Samediff cons : conss) {
			if(!cons.propagated) {
				scip.debugMsg("propagate constraint <%s>\n", cons.getName());
				
				SCIP_RESULT result = consdataFixVariables(scip, cons, probdata.vars);
				cons.npropagations++;
				
				if(result == SCIP_CUTOFF) {
					return SCIP_CUTOFF;
				} else if(result == SCIP_REDUCEDDOM) {
					reduceddom = true;
				}
				
				cons.propagated = true;
				cons.npropagatedvars = probdata.nVars();
			}
		}
		
		if(reduceddom)
			return SCIP_REDUCEDDOM;
		else
			return SCIP_DIDNOTFIND;
	}
	
	SCIP_RESULT consdataFixVariables(SCIP scip, Cons_Samediff cons, List<SCIP_VAR> vars) {
		scip.debugMsg("check variables %d to %d\n", cons.npropagatedvars, vars.size());
		
		boolean cutoff = false;
		int nfixedvars = 0;
		
		for(int v = cons.npropagatedvars; v < vars.size(); v++ ) {
			SCIP_VAR var = vars.get(v);
			SCIP_RESULT result = checkVariable(scip, cons, var);
			
			if(result == SCIP_CUTOFF) {
				cutoff = true;
			} else if(result == SCIP_REDUCEDDOM) {
				nfixedvars++;
			}
		}
		
		scip.debugMsg("fixed %d variables locally\n", nfixedvars);
		
		if(cutoff)
			return SCIP_CUTOFF;
		else if(nfixedvars > 0)
			return SCIP_REDUCEDDOM;
		else
			return SCIP_DIDNOTFIND;
	}
	
	SCIP_RESULT checkVariable(SCIP scip, Cons_Samediff cons, SCIP_VAR var) {
		if(var.getUbLocal() < 0.5) {
			return SCIP_DIDNOTFIND;
		}
		
		VardataBinpacking vardata = VardataBinpacking.getData(var);
		int[] consids = vardata.consids;
		
		int pos1 = Arrays.binarySearch(consids, cons.item1);
		int pos2 = Arrays.binarySearch(consids, cons.item2);
		boolean has1 = pos1 >= 0, has2 = pos2 >= 0;
		ConsType type = cons.type;
		if( (type == ConsType.SAME && (has1 != has2)) || (type == ConsType.DIFFER && has1 && has2)) {
			FixVarResult fixres = scip.fixVar(var, 0.0);
			if(fixres.infeasible()) {
				scip.debugMsg("-> cutoff\n");
				return SCIP_CUTOFF;
			} else {
				return SCIP_REDUCEDDOM;
			}
		}
		return SCIP_DIDNOTFIND;
	}
	
	//activating and deactivating the constraints
	@Override
	protected void consactive(SCIP scip, Cons_Samediff cons) {
		scip.debugMsg("activate constraint <%s> at node <%lld> in depth <%d>: \n",
				cons.getName(), cons.node.getNumber(), cons.node.getDepth());
		
		ProbdataBinpacking probdata = ProbdataBinpacking.get(); 
		if(cons.npropagatedvars != probdata.nVars()) {
			scip.debugMsg("-> mark constraint to be repropagated\n");
			cons.propagated = false;
			scip.repropagateNode(cons.node);
		}
	}
	
	@Override
	protected void consdeactive(SCIP scip, Cons_Samediff cons) {
		scip.debugMsg("deactivate constraint <%s> at node <%lld> in depth <%d>: \n",
				cons.getName(), cons.node.getNumber(), cons.node.getDepth());

		ProbdataBinpacking probdata = ProbdataBinpacking.get();
		cons.npropagatedvars = probdata.nVars();
	}


	/* Everything below here are null callbacks */
	@Override
	public SCIP_RESULT conscheck(SCIP scip, Cons_Samediff[] conss, SCIP_SOL sol, boolean checkintegrality,
			boolean checklprows, boolean printreason, boolean completely) {
		return SCIP_FEASIBLE;
	}

	@Override
	public SCIP_RETCODE conslock(SCIP scip, Cons_Samediff cons, SCIP_LOCKTYPE locktype, int nlockspos,
			int nlocksneg) {
		throw new RuntimeException("Should not be called");
	}

	@Override
	public SCIP_RESULT consenfops(SCIP scip, Cons_Samediff[] conss, int nusefulconss, boolean solinfeasible,
			boolean objinfeasible) {
		return SCIP_FEASIBLE;
	}

	@Override
	public SCIP_RESULT consenfolp(SCIP scip, Cons_Samediff[] conss, int nusefulconss, boolean solinfeasible) {
		return SCIP_FEASIBLE;
	}
}
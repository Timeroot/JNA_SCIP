package JNA_SCIP.Examples.Binpacking;

import java.util.ArrayList;
import java.util.Arrays;

import com.sun.jna.ptr.PointerByReference;

import JNA_SCIP.*;
import static JNA_SCIP.SCIP_RETCODE.*;

public class ProbdataBinpacking {
	//singleton
	private static ProbdataBinpacking inst = null;
	static ProbdataBinpacking get() {
		if(inst == null)
			throw new RuntimeException("Not initialized");
		return inst;
	}
	
	//constants
	static final String EVENTHDLR_NAME = "addedvar";
	static final String EVENTHDLR_DESC = "event handler for catching added variables";
	
	static SCIP_EVENTHDLR eventhdlr;
	
	//Probdata fields
	ArrayList<SCIP_VAR> vars;
	SCIP_CONS[] conss;
	long[] weights;
	int[] ids;
	long capacity;
	
	static final SCIP_DECL_EVENTEXEC eventexec = ProbdataBinpacking::eventExecAddedVar;
	static SCIP_RETCODE eventExecAddedVar(SCIP scip, SCIP_EVENTHDLR eventhdlr, SCIP_EVENT event, SCIP_EVENTDATA eventdata) {
		assert event.getType().equals(SCIP_EVENTTYPE.VARADDED);
		
		scip.debugMsg("exec method of event handler for added variable to probdata\n");
		get().addVar(scip, event.getVar());
		return SCIP_OKAY;
	}
	
	//probdataCreate
	ProbdataBinpacking(SCIP_VAR[] vars, SCIP_CONS[] conss, long[] weights, int[] ids, long capacity){
		int nitems = conss.length;
		assert weights.length == nitems;
		assert ids.length == nitems;
		
		this.vars = (vars==null) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(vars));
		this.conss = conss;
		this.weights = weights;
		this.ids = ids;
		this.capacity = capacity;
	}
	
	void probfree(SCIP scip) {
		for(SCIP_VAR var : vars) {
			scip.releaseVar(var);
		}
	}
	
	void createInitialColumns(SCIP scip) {
		int nitems = conss.length;
		ProbdataBinpacking probdata = ProbdataBinpacking.get();
		
		for(int i=0; i<nitems; i++) {
			String name = "item_"+ids[i];
			scip.debugMsg("create variable for item %d with weight = %lld\n", ids[i], weights[i]);

			VardataBinpacking vardata = new VardataBinpacking(new int[]{i});
			
			SCIP_VAR var = VardataBinpacking.createVarBinpacking(scip, name, 1.0, true, true, vardata);
			scip.addVar(var);
			probdata.addVar(scip, var);
			scip.addCoefSetppc(conss[i], var);
			
			scip.chgVarUbLazy(var, 1.0);
			
			scip.releaseVar(var);
		}
	}

	static final SCIP_DECL_PROBDELORIG probdelorig = ProbdataBinpacking::probdelorig;
	static SCIP_RETCODE probdelorig(SCIP scip, PointerByReference probdata) {
		get().probfree(scip);
		return SCIP_OKAY;
	}
	
	static final SCIP_DECL_PROBTRANS probtrans = ProbdataBinpacking::probtransBinpacking;
	static SCIP_RETCODE probtransBinpacking(SCIP scip, SCIP_PROBDATA sourcedata, PointerByReference targetdata) {
		ProbdataBinpacking probdata = get();
		
		//just transform our own information. Transform variables,
		for(int v=0; v<probdata.vars.size(); v++) {
			SCIP_VAR var = probdata.vars.get(v);
			probdata.vars.set(v, scip.getTransformedVar(var));
			scip.releaseVar(var);
		}
		//transform constraints
		for(int c=0; c<probdata.conss.length; c++) {
			SCIP_CONS cons = probdata.conss[c];
			probdata.conss[c] = scip.getTransformedCons(probdata.conss[c]);
			scip.releaseCons(cons);
		}
		return SCIP_OKAY;
	}

	static final SCIP_DECL_PROBDELTRANS probdeltrans = ProbdataBinpacking::probdeltrans;
	static SCIP_RETCODE probdeltrans(SCIP scip, PointerByReference probdata) {
		get().probfree(scip);
		return SCIP_OKAY;
	}

	static final SCIP_DECL_PROBINITSOL probinitsol = ProbdataBinpacking::probinitsolBinpacking;
	static SCIP_RETCODE probinitsolBinpacking(SCIP scip, SCIP_PROBDATA probdata) {
		scip.catchEvent(SCIP_EVENTTYPE.VARADDED, eventhdlr, null);
		return SCIP_OKAY;
	}

	static final SCIP_DECL_PROBEXITSOL probexitsol = ProbdataBinpacking::probexitsolBinpacking;
	static SCIP_RETCODE probexitsolBinpacking(SCIP scip, SCIP_PROBDATA probdata, boolean restart) {
		scip.dropEvent(SCIP_EVENTTYPE.VARADDED, eventhdlr, null, -1);
		return SCIP_OKAY;
	}
	
	static void create(SCIP scip, String probname, int[] ids, long[] weights, int nitems, long capacity) {
		
		if(eventhdlr == null) {
			eventhdlr = scip.includeEventhdlrBasic(EVENTHDLR_NAME, EVENTHDLR_DESC, eventexec, null);
		}
		
		scip.createProbBasic(probname);
		
		scip.setProbDelorig(probdelorig);
		scip.setProbTrans(probtrans);
		scip.setProbDeltrans(probdeltrans);
		scip.setProbInitsol(probinitsol);
		scip.setProbExitsol(probexitsol);
		
		scip.setObjsense(SCIP_OBJSENSE.MINIMIZE);
		scip.setObjIntegral();
		
		SCIP_CONS[] conss = new SCIP_CONS[nitems];
		
		for(int i=0; i<nitems; i++) {
			String consname = "item_"+ids[i];
			SCIP_CONS cons = scip.createConsBasicSetcover(consname, null);
			conss[i] = cons;
			
			scip.setConsModifiable(cons, true);
			scip.addCons(cons);
		}
		
		ProbdataBinpacking probdata = new ProbdataBinpacking(null, conss, weights, ids, capacity);
		inst = probdata;
		probdata.createInitialColumns(scip);
		
		PricerBinpacking.activate(scip, conss, weights, ids, capacity);
	}

	int nVars() {
		return vars.size();
	}
	
	int nItems() {
		return conss.length;
	}
	
	void addVar(SCIP scip, SCIP_VAR var) {
		vars.add(var);
		scip.captureVar(var);
		scip.debugMsg("added variable to probadata; nvars = %d\n", nVars());
	}
}

package JNA_SCIP.Examples.Binpacking;

import java.util.HashMap;

import com.sun.jna.ptr.PointerByReference;

import JNA_SCIP.*;
import static JNA_SCIP.SCIP_RETCODE.*;

public class VardataBinpacking {
	int[] consids;
	
	VardataBinpacking(int[] consids) {
		this.consids = consids.clone();
	}
	
	//Define a vartrans to associate data to the new variable 
	static final SCIP_DECL_VARTRANS vartrans = VardataBinpacking::vartransBinpacking;
	static SCIP_RETCODE vartransBinpacking(SCIP scip, SCIP_VAR sourcevar, SCIP_VARDATA sourcedata,
			SCIP_VAR targetvar, PointerByReference targetdata) {
		VardataBinpacking origvardata = getData(sourcevar);
		VardataBinpacking newvardata = new VardataBinpacking(origvardata.consids);
		datamap.put(targetvar, newvardata);
		return SCIP_OKAY;
	}
	
	static HashMap<SCIP_VAR, VardataBinpacking> datamap = new HashMap<>();
	
	static SCIP_VAR createVarBinpacking(SCIP scip, String name, double obj, boolean initial,
			boolean removable, VardataBinpacking vardata) {
		
		//Could define deltrans and delorig if we want to clean up objects from our hashmap appropriately
		SCIP_VAR var = scip.createVar(name, 0.0, 1.0, obj, SCIP_VARTYPE.BINARY, initial, removable, null, vartrans, null, null, null);
		
//		var.markDeletable(); //TODO
		
		datamap.put(var, vardata);
		
		return var;
	}
	
	static VardataBinpacking getData(SCIP_VAR var) {
		VardataBinpacking res = datamap.get(var);
		if(res == null)
			throw new RuntimeException("Variable "+var.getPointer()+" not found");
		return res;
	}
}

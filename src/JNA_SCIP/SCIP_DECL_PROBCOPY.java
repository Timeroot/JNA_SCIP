package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;

public interface SCIP_DECL_PROBCOPY extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE probcopy(SCIP scip, SCIP sourcescip, SCIP_PROBDATA probdata, SCIP_HASHMAP varmap,
			SCIP_HASHMAP consmap, PointerByReference targetdata,//SCIP_PROBDATA**
			boolean global, IntByReference result);//SCIP_RESULT*
}
package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;

public interface SCIP_DECL_VARCOPY extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE varcopy(SCIP scip, SCIP sourcescip, SCIP_VAR sourcevar, SCIP_VARDATA sourcedata, SCIP_HASHMAP varmap,
			SCIP_HASHMAP consmap, SCIP_VAR targetvar, PointerByReference targetdata,//SCIP_VARDATA**
			IntByReference result//SCIP_RESULT*
			);
}	
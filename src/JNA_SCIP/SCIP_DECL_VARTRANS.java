package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.PointerByReference;

public interface SCIP_DECL_VARTRANS extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE vartrans(SCIP scip, SCIP_VAR sourcevar, SCIP_VARDATA sourcedata,
			SCIP_VAR targetvar, PointerByReference targetdata//SCIP_VARDATA**
		);
}	
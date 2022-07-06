package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.PointerByReference;

public interface SCIP_DECL_PROBTRANS extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE probtrans(SCIP scip, SCIP_PROBDATA sourcedata, PointerByReference targetdata);//SCIP_PROBDATA**
}	
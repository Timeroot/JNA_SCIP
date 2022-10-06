package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;

public interface SCIP_DECL_BRANCHEXECLP extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE branchexeclp(SCIP scip, SCIP_BRANCHRULE branchrule, boolean allowaddcons,
			IntByReference result);//SCIP_RESULT*
}
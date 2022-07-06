package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.IntByReference;

public interface SCIP_DECL_CONSRESPROP extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
    SCIP_RETCODE consresprop(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS cons,
    		SCIP_VAR infervar, int inferinfo, SCIP_BOUNDTYPE boundtype, SCIP_BDCHGIDX bdchgidx,
    		double relaxedbd, IntByReference scip_result);
}
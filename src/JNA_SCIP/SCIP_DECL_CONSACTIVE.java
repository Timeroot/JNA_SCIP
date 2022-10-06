package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.IntByReference;

public interface SCIP_DECL_CONSACTIVE extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
    SCIP_RETCODE consactive(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS cons);
}
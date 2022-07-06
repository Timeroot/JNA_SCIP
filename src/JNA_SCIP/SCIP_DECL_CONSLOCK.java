package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;

public interface SCIP_DECL_CONSLOCK extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
    SCIP_RETCODE conslock(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS cons,
    		SCIP_LOCKTYPE locktype, int nlockspos, int nlocksneg);
}
package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.Pointer;

public interface SCIP_DECL_CONSEXIT extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
    SCIP_RETCODE consexit(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss//SCIP_CONS**
    	, int nconss);
}

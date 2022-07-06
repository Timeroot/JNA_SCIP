package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public interface SCIP_DECL_CONSSEPASOL  extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE conssepasol(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss,
			int nusefulconss, SCIP_SOL sol, IntByReference scip_result);
}

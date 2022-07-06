package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;

public interface SCIP_DECL_CONSINITLP  extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE consinitlp(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss, ByteByReference infeasible);
}

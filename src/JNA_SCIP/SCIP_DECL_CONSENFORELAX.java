package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.IntByReference;

public interface SCIP_DECL_CONSENFORELAX extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	
    SCIP_RETCODE consenforelax(
    	SCIP scip, SCIP_SOL sol, SCIP_CONSHDLR conshdlr, Pointer conss,//SCIP_CONS**
    	int nconss, int nusefulconss, boolean solinfeasible, IntByReference scip_result//SCIP_RESULT*
    );
}
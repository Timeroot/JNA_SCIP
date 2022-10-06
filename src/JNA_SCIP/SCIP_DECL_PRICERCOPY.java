package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.IntByReference;

public interface SCIP_DECL_PRICERCOPY extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE pricercopy(SCIP scip, SCIP_PRICER pricer, IntByReference valid);
}	
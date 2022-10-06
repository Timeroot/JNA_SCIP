package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;

public interface SCIP_DECL_PRICEREXIT extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE pricerexit(SCIP scip, SCIP_PRICER pricer);
}	
package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;

public interface SCIP_DECL_PRICERREDCOST extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	SCIP_RETCODE pricerredcost(SCIP scip, SCIP_PRICER pricer,
			DoubleByReference lowerbound,//double*
			IntByReference stopearly,//SCIP_Bool*	
			IntByReference result);//SCIP_RESULT*
}
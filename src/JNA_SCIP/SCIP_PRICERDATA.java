package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_PRICERDATA extends PointerType {
	public SCIP_PRICERDATA() {}
	public SCIP_PRICERDATA(Pointer p) {
		super(p);
	}
}
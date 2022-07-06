package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_CONSDATA extends PointerType {
	public SCIP_CONSDATA() {}
	public SCIP_CONSDATA(Pointer p) {
		super(p);
	}
}
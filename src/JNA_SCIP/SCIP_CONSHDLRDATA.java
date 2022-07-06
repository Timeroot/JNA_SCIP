package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_CONSHDLRDATA extends PointerType {
	public SCIP_CONSHDLRDATA() {}
	public SCIP_CONSHDLRDATA(Pointer p) {
		super(p);
	}
}
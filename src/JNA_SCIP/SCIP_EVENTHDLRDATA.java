package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_EVENTHDLRDATA extends PointerType {
	public SCIP_EVENTHDLRDATA() {}
	public SCIP_EVENTHDLRDATA(Pointer p) {
		super(p);
	}
}
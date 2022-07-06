package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_VARDATA extends PointerType {

	public SCIP_VARDATA() {}
	public SCIP_VARDATA(Pointer p) {
		super(p);
	}

}
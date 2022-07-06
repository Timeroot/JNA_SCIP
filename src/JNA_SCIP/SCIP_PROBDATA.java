package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_PROBDATA extends PointerType {

	public SCIP_PROBDATA() {}
	public SCIP_PROBDATA(Pointer p) {
		super(p);
	}

}
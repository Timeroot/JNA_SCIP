package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_BRANCHRULEDATA extends PointerType {
	public SCIP_BRANCHRULEDATA() {}
	public SCIP_BRANCHRULEDATA(Pointer p) {
		super(p);
	}
}
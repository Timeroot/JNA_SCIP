package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_EVENTHDLR extends PointerType {

	public SCIP_EVENTHDLR() {}
	public SCIP_EVENTHDLR(Pointer p) {
		super(p);
	}
	
	//We want to make this effectively immutable.
	@Override
	public void setPointer(Pointer t) {
		if(t == null)
			throw new RuntimeException("Tried to set SCIP pointer to a null");
		if(getPointer() != null)
			throw new RuntimeException("No modifying SCIP pointers");
		super.setPointer(t);
	}
}

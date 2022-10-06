package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_NODE extends PointerType {
	public SCIP_NODE() {}
	public SCIP_NODE(Pointer p) {
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
	
	public long getNumber() { return JSCIP.nodeGetNumber(this); }
	
	public int getDepth() { return JSCIP.nodeGetDepth(this); }
	
	public double getLowerbound() { return JSCIP.nodeGetLowerbound(this); }

}
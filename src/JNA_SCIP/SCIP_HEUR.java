package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public final class SCIP_HEUR extends PointerType {
	public SCIP_HEUR() {}
	public SCIP_HEUR(Pointer p) {
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

	//SCIPheurGetSetupTime
	public double getSetupTime() { return JSCIP.heurGetSetupTime(this); }
	//SCIPheurGetTime
	public double getTime() { return JSCIP.heurGetTime(this); }
}

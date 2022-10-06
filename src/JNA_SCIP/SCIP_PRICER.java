package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public final class SCIP_PRICER extends PointerType {
	public SCIP_PRICER() {}
	public SCIP_PRICER(Pointer p) {
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

	SCIP_PRICERDATA getData() {
		return JSCIP.pricerGetData(this);
	}
	
	void setData(SCIP_PRICERDATA pricerdata) {
		JSCIP.pricerSetData(this, pricerdata);
	}
	
	String getName() {
		return JSCIP.pricerGetName(this);
	}
	
	String getDesc() {
		return JSCIP.pricerGetDesc(this);
	}

	int getPriority() {
		return JSCIP.pricerGetPriority(this);
	}
	
	boolean isInitialized() {
		return JSCIP.pricerIsInitialized(this);
	}
	
}

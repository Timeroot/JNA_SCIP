package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public final class SCIP_BRANCHRULE extends PointerType {
	public SCIP_BRANCHRULE() {}
	public SCIP_BRANCHRULE(Pointer p) {
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

	SCIP_BRANCHRULEDATA getData() {
		return JSCIP.branchruleGetData(this);
	}
	
	void setData(SCIP_BRANCHRULEDATA branchruledata) {
		JSCIP.branchruleSetData(this, branchruledata);
	}
	
	String getName() {
		return JSCIP.branchruleGetName(this);
	}
	
	String getDesc() {
		return JSCIP.branchruleGetDesc(this);
	}

	int getPriority() {
		return JSCIP.branchruleGetPriority(this);
	}
	
	boolean isInitialized() {
		return JSCIP.branchruleIsInitialized(this);
	}
	
}

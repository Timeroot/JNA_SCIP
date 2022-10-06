package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

import static JNA_SCIP.MemoryLayout.*;

public class SCIP_COL extends PointerType {
	public SCIP_COL() {}
	public SCIP_COL(Pointer p) {
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
	
	public void sort() { JSCIP.colSort(this); }
	public double getBestBound() { return JSCIP.colGetBestBound(this); }
	public int getIndex() { return JSCIP.colGetIndex(this); }
	public int getVarProbindex() { return JSCIP.colGetVarProbindex(this); }
	public boolean isIntegral() { return JSCIP.colIsIntegral(this); }
	
	public double getObj() {
		if(DIRECT_MEM)
			return this.getPointer().getDouble(0);
		else
			return JSCIP.colGetObj(this);
	}

	public double getLb() {
		if(DIRECT_MEM)
			return this.getPointer().getDouble(1*d_sz);
		else
			return JSCIP.colGetLb(this);
	}

	public double getUb() {
		if(DIRECT_MEM)
			return this.getPointer().getDouble(2*d_sz);
		else
			return JSCIP.colGetUb(this);
	}

	public SCIP_VAR getVar() {
		if(DIRECT_MEM)
			return new SCIP_VAR(this.getPointer().getPointer(21*d_sz));
		else
			return JSCIP.colGetVar(this);
	}
}

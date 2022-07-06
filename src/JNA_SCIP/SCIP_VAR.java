package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

import static JNA_SCIP.MemoryLayout.*;

public class SCIP_VAR extends PointerType {
	public SCIP_VAR() {}
	public SCIP_VAR(Pointer p) {
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
	
	//SCIPvarGetName
	public String getName() {
		if(DIRECT_MEM)
			return this.getPointer().getPointer(16*d_sz + 2*dom_sz + var_field_data_sz).getString(0);
		else
			return JSCIP.varGetName(this);
	}
	//SCIPvarGetNUses
	public int getNUses() { return JSCIP.varGetNUses(this); }
	//SCIPvarGetType
	public SCIP_VARTYPE getType() { return JSCIP.varGetType(this); }
	//SCIPvarGetStatus
	public SCIP_VARSTATUS getStatus() { return JSCIP.varGetStatus(this); }
	//SCIPvarGetObj
	public double getObj() {
		if(DIRECT_MEM)
			return this.getPointer().getDouble(8*0);
		else
			return JSCIP.varGetObj(this);
	}
	//SCIPvarGetLbLocal
	public double getLbLocal() {
		if(DIRECT_MEM)
			return this.getPointer().getDouble(16*d_sz + dom_sz);
		else
			return JSCIP.varGetLbLocal(this);
	}
	//SCIPvarGetUbLocal
	public double getUbLocal() {
		if(DIRECT_MEM)
			return this.getPointer().getDouble(17*d_sz + dom_sz);
		else
			return JSCIP.varGetUbLocal(this);
	}
	//SCIPvarGetLbGlobal
	public double getLbGlobal() {
		if(DIRECT_MEM)
			return this.getPointer().getDouble(16*d_sz);
		else
			return JSCIP.varGetLbGlobal(this);
	}
	//SCIPvarGetUbGlobal
	public double getUbGlobal() {
		if(DIRECT_MEM)
			return this.getPointer().getDouble(17*d_sz);
		else
			return JSCIP.varGetUbGlobal(this);
	}
	//SCIPvarGetLbOriginal
	public double getLbOriginal() { return JSCIP.varGetLbOriginal(this); }
	//SCIPvarGetUbOriginal
	public double getUbOriginal() { return JSCIP.varGetUbOriginal(this); }
	//SCIPvarGetLbLazy
	public double getLbLazy() { return JSCIP.varGetLbLazy(this); }
	//SCIPvarGetUbLazy
	public double getUbLazy() { return JSCIP.varGetUbLazy(this); }
	//SCIPvarGetLPSol
	public double getLPSol() { return JSCIP.varGetLPSol(this); }
	//SCIPvarGetNLPSol
	public double getNLPSol() { return JSCIP.varGetNLPSol(this); }
	//SCIPvarGetPseudoSol
	public double getPseudoSol() { return JSCIP.varGetPseudoSol(this); }
	//SCIPvarGetSol
	public double getSol(boolean lp) { return JSCIP.varGetSol(this, lp); }
	//SCIPvarGetRootSol
	public double getRootSol() { return JSCIP.varGetRootSol(this); }
	//SCIPvarGetAvgSol
	public double getAvgSol() { return JSCIP.varGetAvgSol(this); }
	//SCIPvarGetLbAtIndex
	public double getLbAtIndex(SCIP_BDCHGIDX bdchgidx, boolean after) {
		return JSCIP.varGetLbAtIndex(this, bdchgidx, after);
	}
	//SCIPvarGetUbAtIndex
	public double getUbAtIndex(SCIP_BDCHGIDX bdchgidx, boolean after) {
		return JSCIP.varGetUbAtIndex(this, bdchgidx, after);
	}
	//SCIPvarGetUbAtIndex
	public double getBdAtIndex(SCIP_BOUNDTYPE bdtype, SCIP_BDCHGIDX bdchgidx, boolean after) {
		return JSCIP.varGetBdAtIndex(this, bdtype, bdchgidx, after);
	}
	
	//There are several notes about why this function is as it is, see
	//JSCIP.CALL_SCIPgetVarCopy for notes.
	//SCIPgetVarCopy
	public SCIP_VAR getCopy(SCIP sourcescip, SCIP targetscip, SCIP_HASHMAP varmap, SCIP_HASHMAP consmap, boolean global) {
		return JSCIP.CALL_SCIPgetVarCopy(sourcescip, targetscip, this, varmap, consmap, global);
	}
}


package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_CONS extends PointerType {
	public SCIP_CONS() {}
	public SCIP_CONS(Pointer p) {
		super(p);
	}
	public String toString() {
		return "S_CONS["+Long.toString(Pointer.nativeValue(getPointer()),16).substring(6)+"]";
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
	
	//SCIPconsGetName
	public String getName(){ return JSCIP.consGetName(this); }
	//SCIPconsGetPos
	public int getPos(){ return JSCIP.consGetPos(this); }
	//SCIPconsGetHdlr
	public SCIP_CONSHDLR getHdlr(){ return JSCIP.consGetHdlr(this); }
	//SCIPconsGetData
	public SCIP_CONSDATA getData(){ return JSCIP.consGetData(this); }
	//SCIPconsGetNUses
	public int getNUses(){ return JSCIP.consGetNUses(this); }
	//SCIPconsIsDeleted
	public boolean isDeleted(){ return JSCIP.consIsDeleted(this); }
	//SCIPconsIsEnabled
	public boolean isEnabled(){ return JSCIP.consIsEnabled(this); }
	//SCIPconsIsAdded
	public boolean isAdded(){ return JSCIP.consIsAdded(this); }
	//SCIPconsIsInitial
	public boolean isInitial(){ return JSCIP.consIsInitial(this); }
	//SCIPconsIsSeparated
	public boolean isSeparated(){ return JSCIP.consIsSeparated(this); }
	//SCIPconsIsEnforced
	public boolean isEnforced(){ return JSCIP.consIsEnforced(this); }
	//SCIPconsIsChecked
	public boolean isChecked(){ return JSCIP.consIsChecked(this); }
	//SCIPconsIsPropagated
	public boolean isPropagated(){ return JSCIP.consIsPropagated(this); }
	//SCIPconsIsLocal
	public boolean isLocal(){ return JSCIP.consIsLocal(this); }
	//SCIPconsIsModifiable
	public boolean isModifiable(){ return JSCIP.consIsModifiable(this); }
	//SCIPconsIsDynamic
	public boolean isDynamic(){ return JSCIP.consIsDynamic(this); }
	//SCIPconsIsRemovable
	public boolean isRemovable(){ return JSCIP.consIsRemovable(this); }
	//SCIPconsIsStickingAtNode
	public boolean isStickingAtNode(){ return JSCIP.consIsStickingAtNode(this); }
	//SCIPconsIsObsolete
	public boolean isObsolete(){ return JSCIP.consIsObsolete(this); }
	//SCIPconsIsConflict
	public boolean isConflict(){ return JSCIP.consIsConflict(this); }
	//SCIPconsIsInProb
	public boolean isInProb(){ return JSCIP.consIsInProb(this); }
}

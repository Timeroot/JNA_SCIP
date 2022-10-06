package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_EVENT extends PointerType {

	public SCIP_EVENT() {}
	public SCIP_EVENT(Pointer p) {
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
	
	//SCIPeventGetType
	public SCIP_EVENTTYPE getType() { return JSCIP.eventGetType(this); }
	//SCIPeventGetVar
	public SCIP_VAR getVar() { return JSCIP.eventGetVar(this); }
	//SCIPeventGetOldobj
	public double getOldobj() { return JSCIP.eventGetOldobj(this); }
	//SCIPeventGetNewobj
	public double getNewobj() { return JSCIP.eventGetNewobj(this); }
	//SCIPeventGetOldbound
	public double getOldbound() { return JSCIP.eventGetOldbound(this); }
	//SCIPeventGetNewbound
	public double getNewbound() { return JSCIP.eventGetNewbound(this); }
	//SCIPeventGetOldtype
	public SCIP_VARTYPE getOldtype() { return JSCIP.eventGetOldtype(this); }
	//SCIPeventGetNewtype
	public SCIP_VARTYPE getNewtype() { return JSCIP.eventGetNewtype(this); }
	//SCIPeventGetNode
	public SCIP_NODE getNode() { return JSCIP.eventGetNode(this); }
	//SCIPeventGetSol
	public SCIP_SOL getSol() { return JSCIP.eventGetSol(this); }
	//SCIPeventGetRow
	public SCIP_ROW getRow() { return JSCIP.eventGetRow(this); }
	//SCIPeventGetRowCol
	public SCIP_COL getRowCol() { return JSCIP.eventGetRowCol(this); }
}

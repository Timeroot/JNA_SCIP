package JNA_SCIP;

public abstract class ConstraintData<Self extends ConstraintData<Self,Hdlr>,Hdlr extends ConstraintHandler<Self,Hdlr>> {
	
	//Callbacks you need to implement:
	//SCIP_DECL_CONSTRANS -- transform a constraint
	public abstract Self transform(SCIP scip);
	//SCIP_DECL_CONSDELETE -- free constraint-specific data
	public abstract void delete(SCIP scip);
	//SCIP_DECL_CONSEXIT -- called before transformed problem is freed.
	public abstract void exit(SCIP scip);
	
	//The reduced version of SCIP_DECL_CONSCOPY. We cut down to the bare minimum arguments.
	//Only needed if you enableConscopy on the ConstraintHandler.
	public Self copy(SCIP sourcescip, SCIP targetscip,
			SCIP_HASHMAP varmap, SCIP_HASHMAP consmap, boolean global) {
		SCIP_CONSHDLR scip_conshdlr = scip_cons.getHdlr();
		String s_conshdlrname = scip_conshdlr.getName();
		String j_conshdlrname = ConstraintHandler.hdlr_mapping.get(scip_conshdlr).getClass().toString();
		String j_thisname = this.getClass().toString();
		throw new RuntimeException("You ran enableConscopy on "+s_conshdlrname+" (Java class "+j_conshdlrname+")"
				+ " but didn't override "+j_thisname+".copy()");
	}
	
	//Keep a reference to our associated SCIP_CONS
	SCIP_CONS scip_cons;
	/*package*/ void setScipCons(SCIP_CONS s_) {
		this.scip_cons = s_;
	}
	public SCIP_CONS getScipCons() {
		return this.scip_cons;
	}
	
	//Some convenience methods
	public String getName() {
		return scip_cons.getName();
	}
	public SCIP_CONSHDLR getScipHdlr() {
		return scip_cons.getHdlr();
	}
	@SuppressWarnings("unchecked")
	public ConstraintHandler<Self,Hdlr> getHdlr() {
		return (ConstraintHandler<Self, Hdlr>) ConstraintHandler.findJavaConshdlr(scip_cons.getHdlr());
	}
}

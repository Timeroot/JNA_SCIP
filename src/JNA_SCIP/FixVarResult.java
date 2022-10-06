package JNA_SCIP;

//This defines our (custom) return type for calls like
// SCIPfixVar, which would otherwise
//otherwise return both an "infeasible" boolean and a "fixed"
//boolean. Since infeasible implies fixed (it can't become infeasible unless
//it was unfixed before) we have three options.
//Compare with InferVarResult.
public enum FixVarResult {
	UNCHANGED, //neither fixed nor feasible
	FIXED, //but still feasible
	INFEASIBLE; //implies tightened
	
	//to compute the two underlying fields
	public boolean fixed() {
		return this != UNCHANGED;
	}
	public boolean infeasible() {
		return this == INFEASIBLE;
	}
}

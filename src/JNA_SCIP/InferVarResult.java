package JNA_SCIP;

//This defines our (custom) return type for calls like
// SCIPinferBinvarProp or SCIPtightenVarLbGlobal, which would
//otherwise return both an "infeasible" boolean and a "tightened"
//boolean. Since infeasible implies tightened (a domain can't
//shrink to empty unless it tightened) we have three options. 
public enum InferVarResult {
	UNCHANGED, //neither tightened nor feasible
	TIGHTENED, //but still feasible
	INFEASIBLE; //implies tightened
	
	//to compute the two underlying fields
	public boolean tightened() {
		return this != UNCHANGED;
	}
	public boolean infeasible() {
		return this == INFEASIBLE;
	}
}

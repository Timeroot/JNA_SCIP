package JNA_SCIP.Examples.Binpacking;

import JNA_SCIP.*;

enum ConsType
{
   DIFFER,
   SAME
};

public class Cons_Samediff extends ConstraintData<Cons_Samediff, Conshdlr_Samediff> {
	int item1;
	int item2;
	ConsType type;
	int npropagatedvars;
	int npropagations;
	boolean propagated;
	SCIP_NODE node;
	
	//consdataCreate
	public Cons_Samediff(int item1, int item2, ConsType type, SCIP_NODE node) {
		this(item1, item2, type, 0, 0, false, node);
	}
	
	private Cons_Samediff(int item1, int item2, ConsType type, int npropagatedvars,
			int npropagations, boolean propagated, SCIP_NODE node) {
		this.item1 = item1;
		this.item2 = item2;
		this.type = type;
		this.npropagatedvars = npropagatedvars;
		this.npropagations = npropagations;
		this.propagated = propagated;
		this.node = node;
	}
	
	@Override
	public Cons_Samediff transform(SCIP scip) {
		return new Cons_Samediff(item1, item2, type, npropagatedvars,
				npropagations, propagated, node);
	}

	@Override
	public void delete(SCIP scip) {
		//nothing
	}

	@Override
	public void exit(SCIP scip) {
		//nothing
	}
	
}

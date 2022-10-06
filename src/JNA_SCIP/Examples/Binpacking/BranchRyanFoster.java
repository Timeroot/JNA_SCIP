package JNA_SCIP.Examples.Binpacking;

import com.sun.jna.ptr.IntByReference;

import JNA_SCIP.*;

public class BranchRyanFoster {
	
	//save lambda permanently, to avoid garbage collection
	static final SCIP_DECL_BRANCHEXECLP branchexeclp = BranchRyanFoster::branchExeclpRyanFoster;
	static SCIP_RETCODE branchExeclpRyanFoster(SCIP scip, SCIP_BRANCHRULE branchrule,
			boolean allowaddcons, IntByReference result) {
		
		scip.debugMsg("start branching at node %lld, depth %d\n", scip.getCurrentNode().getNumber(), scip.getDepth());

		result.setValue(SCIP_RESULT.SCIP_DIDNOTRUN.ordinal());
		
		ProbdataBinpacking probdata = ProbdataBinpacking.get();
		int nitems = probdata.nItems();
		
		double[][] pairweights = new double[nitems][];
		for(int i=0; i<nitems; i++) {
			pairweights[i] = new double[i+1];
		}
		
		LPBranchCands branchCands = scip.getLPBranchCands();
		int nlpcands = branchCands.npriolpcands;
		
		assert nlpcands > 0;
		
		for(int v=0; v<nlpcands; v++) {
			double solval = branchCands.lpcandsfrac[v];
			VardataBinpacking vardata = VardataBinpacking.getData(branchCands.lpcands[v]);
			int[] consids = vardata.consids;
			int nconsids = consids.length;
			
			for(int i=0; i<nconsids; i++) {
				int id1 = consids[i];
				pairweights[id1][id1] += solval;
				
				for(int j=i+1; j<nconsids; j++) {
					int id2 = consids[j];
					pairweights[id2][id1] += solval;
				}
			}
		}
		
		/* select branching */
		double bestvalue = 0.0;
		int id1 = -1, id2 = -1;
		for(int i=0; i<nitems; i++) {
			for(int j=0; j<i; j++) {
				double w = pairweights[i][j];
				double value = Math.min(w, 1-w);
				
				if(bestvalue < value) {
					if(w == pairweights[i][i] & w == pairweights[j][j])
						continue;
					
					bestvalue = value;
					id1 = j;
					id2 = i;
				}
			}
		}
		
		assert bestvalue > 0.0;
		assert id1 >= 0 && id1 < nitems;
		assert id2 >= 0 && id2 < nitems;
		
		scip.debugMsg("branch on order pair <%d,%d> with weight <%g>\n", probdata.ids[id1], probdata.ids[id2], bestvalue);
		
		SCIP_NODE childsame = scip.createChild(0.0, scip.getLocalTransEstimate());
		SCIP_NODE childdiffer = scip.createChild(0.0, scip.getLocalTransEstimate());
		
		SCIP_CONS conssame = Conshdlr_Samediff.createConsSamediff(scip, "same", id1, id2, ConsType.SAME, childsame, true);
		SCIP_CONS consdiffer = Conshdlr_Samediff.createConsSamediff(scip, "differ", id1, id2, ConsType.DIFFER, childdiffer, true);
		
		scip.addConsNode(childsame, conssame, null);
		scip.addConsNode(childdiffer, consdiffer, null);
		
		scip.releaseCons(conssame);
		scip.releaseCons(consdiffer);

		result.setValue(SCIP_RESULT.SCIP_BRANCHED.ordinal());
		
		return SCIP_RETCODE.SCIP_OKAY;
	}
	
	static void includeBranchruleRyanFoster(SCIP scip) {
		SCIP_BRANCHRULE branchrule = scip.includeBranchruleBasic(
				"RyanFoster", "Ryan/Foster branching rule",
				50000, -1, 1.0, null);
		
		scip.setBranchruleExecLp(branchrule, branchexeclp);
	}
}

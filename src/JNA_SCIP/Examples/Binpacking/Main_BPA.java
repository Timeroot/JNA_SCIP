package JNA_SCIP.Examples.Binpacking;

import JNA_SCIP.*;

public class Main_BPA {
	//Run, then type in "read u20_00.bpa optimize display solution quit"
	public static void main(String[] args) {
		SCIP.DEBUG = false;
		
		SCIP scip = SCIP.create();
		
//		scip.enableDebugSol();//TODO
		
		//our four plugins
		Reader_BPA.includeReaderBPA(scip);
		BranchRyanFoster.includeBranchruleRyanFoster(scip);
		Conshdlr_Samediff.includeConshdlrSamediff(scip);
		PricerBinpacking.includePricerBinpacking(scip);
		
		scip.includeDefaultPlugins();
		
		scip.setIntParam("presolving/maxrestarts", 0);
		scip.setSeparating(SCIP_PARAMSETTING.OFF, true);
		
		scip.processShellArguments(args, "scip.set");
		
		scip.free();
	}
}

package JNA_SCIP.Examples.LOP;

import JNA_SCIP.SCIP;

public class Main_LOP {
	//Run, then type in "read ex0.lop optimize display solution quit"
	public static void main(String[] args) {
		SCIP scip = SCIP.create();
		
		scip.infoMessage(null, "Solving the linear ordering problem using SCIP.\n");
		
		scip.includeDefaultPlugins();
		
		Conshdlr_LOP.includeConshdlrLOP(scip);
		
		Reader_LOP.includeReaderLOP(scip);
		
		scip.processShellArguments(args, "scip.set");
		
		scip.free();
	}
}

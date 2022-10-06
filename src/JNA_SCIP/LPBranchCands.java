package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class LPBranchCands {
	//A type specifically for returning the data from SCIPgetLPBranchCands
	
	public SCIP_VAR[] lpcands;
	public double[] lpcandssol;
	public double[] lpcandsfrac;
	
	public int nlpcands;
	public int npriolpcands;
	public int nfracimplvars;
	
	public LPBranchCands(Pointer p_lpcands, Pointer p_lpcandssol, Pointer p_lpcandsfrac,
			IntByReference p_nlpcands, IntByReference p_npriolpcands, IntByReference p_nfracimplvars) {
		
		if(p_nlpcands == null) {
			throw new RuntimeException("Need a non-null p_lpcands");
		}
		if(p_npriolpcands == null) {
			throw new RuntimeException("Need a non-null p_npriolpcands");
		}
		if(p_nfracimplvars == null) {
			throw new RuntimeException("Need a non-null p_nfracimplvars");
		}
		nlpcands = p_nlpcands.getValue();
		npriolpcands = p_npriolpcands.getValue();
		nfracimplvars = p_nfracimplvars.getValue();
		
		if(p_lpcands != null) {
			lpcands = new SCIP_VAR[nlpcands];
			Pointer[] parr = p_lpcands.getPointerArray(0, nlpcands);
			for(int i=0; i<nlpcands; i++) {
				lpcands[i] = new SCIP_VAR(parr[i]);
			}
		}
		
		if(p_lpcandssol != null) {
			lpcandssol = p_lpcandssol.getDoubleArray(0, nlpcands);
		}
		
		if(p_lpcandsfrac != null) {
			lpcandsfrac = p_lpcandsfrac.getDoubleArray(0, nlpcands);
		}
	}
}

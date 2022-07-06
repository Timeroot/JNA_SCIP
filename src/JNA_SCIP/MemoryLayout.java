package JNA_SCIP;

//This class has options and information for accessing memory more directly, avoiding expensive
//redirection and calls through JNA when possible.

class MemoryLayout {
	//setting this flag to true says that JNA_SCIP should pull certain data + fields based
	//on assumed sizes of doubles and pointers and structure layout. This can significantly
	//improve performance on callbacks but is platform-dependent.
	//The functions affected by this are equivalent to the `#ifdef NDEBUG` sections in pub_var.h. 
	static final boolean DIRECT_MEM = true;
	//Used to compute the size of structs in memory layout. By default most compilers will make sure
	//that fields are all (4-)byte-aligned, but if bitfield packing is enabled then the layout
	//will be more compact. Setting this to true (which only matters if DIRECT_MEM is true) will
	//tell JNA_SCIP that the compacted layout was used in the SCIP compilation.
	static final boolean BITFIELD_PACKING = true;
	//what size the compiler rounds up to for alignment
	static final int ALIGN_SIZE = 8;
	
	//Compute sizes of things for alignment
	static final int
		//Primitives
		i_sz=4, //int
		d_sz=8, //pointer or double
		//Structures
		hole_sz = align(2*d_sz),
		hole_list_sz = align(hole_sz + d_sz),
		hole_chg_sz = align(3*d_sz),
		branchingdata_sz = align(d_sz),
		inferencedata_sz = align(d_sz + max(d_sz, d_sz) + i_sz),
		boundchg_sz = align(d_sz + max(branchingdata_sz, inferencedata_sz) + d_sz + (BITFIELD_PACKING ? 1 : 5*i_sz)),
		bdchgidx_sz = align(2*i_sz),
		bdchginfo_sz = align(3*d_sz + inferencedata_sz + bdchgidx_sz + (BITFIELD_PACKING ? 4 : 5*i_sz)),
		dom_sz = align(3*d_sz),
		original_sz = align(dom_sz + d_sz),
		aggregate_sz = align(3*d_sz ),
		multaggr_sz = align(3*d_sz + 2*i_sz),
		negate_sz = align(d_sz),
		//size of the "data" field in a SCIP_VAR
		var_field_data_sz = align(max(original_sz, d_sz, aggregate_sz, multaggr_sz, negate_sz));
	
	//multi-arg max for union sizes
	private static int max(Integer... vals) {
		int v = vals[0];
		for(Integer o : vals)
			v = Math.max(v, o);
		return v;
	}
	private static int align(int sz) {
		return ((sz + ALIGN_SIZE - 1) / ALIGN_SIZE) * ALIGN_SIZE;
	}
	
	public static void main(String[] args) {
		System.out.println("hole size = "+hole_sz);
		System.out.println("hole_list size = "+hole_list_sz);
		System.out.println("hole_chg size = "+hole_chg_sz);
		System.out.println("branchingdata size = "+branchingdata_sz);
		System.out.println("inferencedata size = "+inferencedata_sz);
		System.out.println("boundchg size = "+boundchg_sz);
		System.out.println("bdchgidx size = "+bdchgidx_sz);
		System.out.println("bdchginfo size = "+bdchginfo_sz);
		System.out.println("dom size = "+dom_sz);
		System.out.println("original size = "+original_sz);
		System.out.println("aggregate size = "+aggregate_sz);
		System.out.println("multaggr size = "+multaggr_sz);
		System.out.println("negate size = "+negate_sz);
	}
}

package JNA_SCIP;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public final class SCIP_EVENTTYPE {
	/* This actually an long defined by flags, and there are are 2^63 possible values. This precludes the
	 * possibility of using an enum. Instead, it's a custom type with a long field; we give constants for
	 * checking equality / flags.
	 */
	
	public final long flags;
	private SCIP_EVENTTYPE(long flags) {
		this.flags = flags;
	}
	public static SCIP_EVENTTYPE of(long flags) {
		//could potentially do validation here, or caching of common values
		return new SCIP_EVENTTYPE(flags);
	}
	public SCIP_EVENTTYPE not() {
		return of(~this.flags);
	}
	public SCIP_EVENTTYPE or(SCIP_EVENTTYPE other) {
		return of(this.flags | other.flags);
	}
	public boolean matches(SCIP_EVENTTYPE other) {
		return (this.flags & other.flags) != 0;
	}
	
	@Override
	public boolean equals(Object other) {
		return (other instanceof SCIP_EVENTTYPE) && (this.flags == ((SCIP_EVENTTYPE)other).flags);
	}
	@Override
	public int hashCode() {
		return (int)((0x0110100110010110L * flags) >> 32); 
	}
	public String toString() {return "EVENTTYPE{"+flags+"}";}
	
	public static final SCIP_EVENTTYPE DISABLED = of(0x0);
	public static final SCIP_EVENTTYPE VARADDED = of(0x1);
	public static final SCIP_EVENTTYPE VARDELETED = of(0x2);
	public static final SCIP_EVENTTYPE VARFIXED = of(0x4);
	public static final SCIP_EVENTTYPE VARUNLOCKED = of(0x8);
	public static final SCIP_EVENTTYPE OBJCHANGED = of(0x10);
	public static final SCIP_EVENTTYPE GLBCHANGED = of(0x20);
	public static final SCIP_EVENTTYPE GUBCHANGED = of(0x40);
	public static final SCIP_EVENTTYPE LBTIGHTENED = of(0x80);
	public static final SCIP_EVENTTYPE LBRELAXED = of(0x100);
	public static final SCIP_EVENTTYPE UBTIGHTENED = of(0x200);
	public static final SCIP_EVENTTYPE UBRELAXED  = of(0x400);
	public static final SCIP_EVENTTYPE GHOLEADDED = of(0x800);
	public static final SCIP_EVENTTYPE GHOLEREMOVED = of(0x000001000L);
	public static final SCIP_EVENTTYPE LHOLEADDED = of(0x000002000L);
	public static final SCIP_EVENTTYPE LHOLEREMOVED = of(0x000004000L);
	public static final SCIP_EVENTTYPE IMPLADDED = of(0x000008000L);
	public static final SCIP_EVENTTYPE TYPECHANGED = of(0x000010000L);
	public static final SCIP_EVENTTYPE PRESOLVEROUND = of(0x000020000L);
	public static final SCIP_EVENTTYPE NODEFOCUSED = of(0x000040000L);
	public static final SCIP_EVENTTYPE NODEFEASIBLE = of(0x000080000L);
	public static final SCIP_EVENTTYPE NODEINFEASIBLE = of(0x000100000L);
	public static final SCIP_EVENTTYPE NODEBRANCHED = of(0x000200000L);
	public static final SCIP_EVENTTYPE NODEDELETE = of(0x000400000L);
	public static final SCIP_EVENTTYPE FIRSTLPSOLVED = of(0x000800000L);
	public static final SCIP_EVENTTYPE LPSOLVED = of(0x001000000L);
	public static final SCIP_EVENTTYPE POORSOLFOUND = of(0x002000000L);
	public static final SCIP_EVENTTYPE BESTSOLFOUND = of(0x004000000L);
	public static final SCIP_EVENTTYPE ROWADDEDSEPA = of(0x008000000L);
	public static final SCIP_EVENTTYPE ROWDELETEDSEPA = of(0x010000000L);
	public static final SCIP_EVENTTYPE ROWADDEDLP = of(0x020000000L);
	public static final SCIP_EVENTTYPE ROWDELETEDLP = of(0x040000000L);
	public static final SCIP_EVENTTYPE ROWCOEFCHANGED = of(0x080000000L);
	public static final SCIP_EVENTTYPE ROWCONSTCHANGED = of(0x100000000L);
	public static final SCIP_EVENTTYPE ROWSIDECHANGED = of(0x200000000L);
	public static final SCIP_EVENTTYPE SYNC = of(0x400000000L);
	
	public static final SCIP_EVENTTYPE GBDCHANGED = GLBCHANGED.or(GUBCHANGED);
	public static final SCIP_EVENTTYPE LBCHANGED = LBTIGHTENED.or(LBRELAXED);
	public static final SCIP_EVENTTYPE UBCHANGED = UBTIGHTENED.or(UBRELAXED);
	public static final SCIP_EVENTTYPE BOUNDTIGHTENED = LBTIGHTENED.or(UBTIGHTENED);
	public static final SCIP_EVENTTYPE BOUNDRELAXED = LBRELAXED.or(UBRELAXED);
	public static final SCIP_EVENTTYPE BOUNDCHANGED = LBCHANGED.or(UBCHANGED);

	public static final SCIP_EVENTTYPE GHOLECHANGED = GHOLEADDED.or(GHOLEREMOVED);
	public static final SCIP_EVENTTYPE LHOLECHANGED = LHOLEADDED.or(LHOLEREMOVED);
	public static final SCIP_EVENTTYPE HOLECHANGED = GHOLECHANGED.or(LHOLECHANGED);
	public static final SCIP_EVENTTYPE DOMCHANGED = BOUNDCHANGED.or(HOLECHANGED);
	public static final SCIP_EVENTTYPE VARCHANGED = VARFIXED.or(VARUNLOCKED).or(OBJCHANGED).
			or(GBDCHANGED).or(DOMCHANGED).or(IMPLADDED).or(VARDELETED).or(TYPECHANGED);
	public static final SCIP_EVENTTYPE VAREVENT = VARADDED.or(VARCHANGED).or(TYPECHANGED);
	public static final SCIP_EVENTTYPE NODESOLVED = NODEFEASIBLE.or(NODEINFEASIBLE).or(NODEBRANCHED);
	public static final SCIP_EVENTTYPE NODEEVENT = NODEFOCUSED.or(NODESOLVED);
	public static final SCIP_EVENTTYPE LPEVENT = FIRSTLPSOLVED.or(LPSOLVED);
	public static final SCIP_EVENTTYPE SOLFOUND = POORSOLFOUND.or(BESTSOLFOUND);
	public static final SCIP_EVENTTYPE SOLEVENT = SOLFOUND;
	public static final SCIP_EVENTTYPE ROWCHANGED = ROWCOEFCHANGED.or(ROWCONSTCHANGED).or(ROWSIDECHANGED);
	public static final SCIP_EVENTTYPE ROWEVENT = ROWADDEDSEPA.or(ROWDELETEDSEPA).or(ROWADDEDLP)
			.or(ROWDELETEDLP).or(ROWCHANGED);
	
	public static class EVENTTYPE_Converter implements TypeConverter {
		// Singleton
		public static final EVENTTYPE_Converter inst = new EVENTTYPE_Converter();

		private EVENTTYPE_Converter() {}

		@Override
		public SCIP_EVENTTYPE fromNative(Object input, FromNativeContext context) {
			return SCIP_EVENTTYPE.of((Long)input);
		}

		@Override
		public Long toNative(Object input, ToNativeContext context) {
			return ((SCIP_EVENTTYPE) input).flags;
		}

		@Override
		public Class<Long> nativeType() {
			return long.class;
		}
	}
}

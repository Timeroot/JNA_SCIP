package JNA_SCIP;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;


/* type_prob.h */
public enum SCIP_OBJSENSE {
	MAXIMIZE, MINIMIZE;
	
	public int toNative() {
		return -1 + 2*this.ordinal();
	}
	
	static SCIP_OBJSENSE fromNative(int i) {
		return SCIP_OBJSENSE.values()[(i+1)/2];
	}

	public static class OBJSENSE_Converter implements TypeConverter {
		// Singleton
		public static final OBJSENSE_Converter inst = new OBJSENSE_Converter();

		private OBJSENSE_Converter() {}

		@Override
		public SCIP_OBJSENSE fromNative(Object input, FromNativeContext context) {
			return SCIP_OBJSENSE.fromNative((Integer)input);
		}

		@Override
		public Integer toNative(Object input, ToNativeContext context) {
			return ((SCIP_OBJSENSE) input).toNative();
		}

		@Override
		public Class<Integer> nativeType() {
			return int.class;
		}
	}
}

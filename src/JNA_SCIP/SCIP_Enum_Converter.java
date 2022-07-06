package JNA_SCIP;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

class SCIP_Enum_Converter<T extends Enum<?>> implements TypeConverter {
	
	T[] values;
	
	public SCIP_Enum_Converter(T[] values) {
		this.values = values;
	}

	@Override
	public T fromNative(Object input, FromNativeContext context) {
		return values[(Integer)input];
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer toNative(Object input, ToNativeContext context) {
		return ((T) input).ordinal();
	}

	@Override
	public Class<Integer> nativeType() {
		return int.class;
	}
}

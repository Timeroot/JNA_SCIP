package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ByteByReference;

public interface SCIP_DECL_CONSCOPY extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;

	SCIP_RETCODE conscopy(SCIP scip, PointerByReference cons,//SCIP_CONS**
			String name, SCIP sourcescip, SCIP_CONSHDLR sourceconshdlr, SCIP_CONS sourcecons, SCIP_HASHMAP varmap,
			SCIP_HASHMAP consmap, boolean initial, boolean separate, boolean enforce, boolean check, boolean propagate,
			boolean local, boolean modifiable, boolean dynamic, boolean removable, boolean stickingatnode,
			boolean global, ByteByReference valid);
}

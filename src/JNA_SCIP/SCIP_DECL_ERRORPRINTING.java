package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.TypeMapper;

public interface SCIP_DECL_ERRORPRINTING extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	void errorprinting(Pointer data, FILEPTR file, String msg);
}
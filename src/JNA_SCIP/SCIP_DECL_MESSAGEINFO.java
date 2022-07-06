package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;

public interface SCIP_DECL_MESSAGEINFO extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	void messageinfo(SCIP_MESSAGEHDLR messagehdlr, FILEPTR file, String msg);
}
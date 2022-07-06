package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;

public interface SCIP_DECL_MESSAGEDIALOG extends Callback {	
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
	void messagedialog(SCIP_MESSAGEHDLR data, FILEPTR file, String msg);
}
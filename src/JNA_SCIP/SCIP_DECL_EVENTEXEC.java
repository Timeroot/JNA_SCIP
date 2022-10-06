package JNA_SCIP;

import com.sun.jna.Callback;
import com.sun.jna.TypeMapper;

public interface SCIP_DECL_EVENTEXEC extends Callback {
	public final static TypeMapper TYPE_MAPPER = JSCIP.TYPE_MAPPER;
    SCIP_RETCODE eventexec(SCIP scip, SCIP_EVENTHDLR eventhdlr, SCIP_EVENT event, SCIP_EVENTDATA eventdata);
}
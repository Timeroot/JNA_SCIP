package JNA_SCIP;

public class ScipException extends RuntimeException {
	private static final long serialVersionUID = -5716737424392734016L;
	
	public SCIP_RETCODE retcode;
	public ScipException(SCIP_RETCODE retcode) {
		super("SCIP returned "+retcode);
		this.retcode = retcode;
	}
}

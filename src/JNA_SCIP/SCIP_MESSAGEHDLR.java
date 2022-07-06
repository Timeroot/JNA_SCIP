package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_MESSAGEHDLR extends PointerType {

	public SCIP_MESSAGEHDLR() {}
	public SCIP_MESSAGEHDLR(Pointer p) {
		super(p);
	}
	
	public static SCIP_MESSAGEHDLR create(boolean bufferedoutput, String filename, boolean quiet,
			SCIP_DECL_MESSAGEWARNING messagewarning,
			SCIP_DECL_MESSAGEDIALOG messagedialog,
			SCIP_DECL_MESSAGEINFO messageinfo,
			SCIP_DECL_MESSAGEHDLRFREE messagehdlrfree,
			SCIP_MESSAGEHDLRDATA messagehdlrdata) {
		SCIP_MESSAGEHDLR messagehdlr = new SCIP_MESSAGEHDLR();
		JSCIP.CALL_SCIPmessagehdlrCreate(messagehdlr, bufferedoutput, filename, quiet,
				messagewarning, messagedialog, messageinfo, messagehdlrfree, messagehdlrdata);
		return messagehdlr;
	}
	
	//SCIPmessagehdlrCapture
	public void capture() { JSCIP.messagehdlrCapture(this); }
	//SCIPmessagehdlrRelease
	public void release() { JSCIP.CALL_SCIPmessagehdlrRelease(this); }
	//SCIPmessagehdlrSetData
	public void setData(SCIP_MESSAGEHDLRDATA data) { JSCIP.CALL_SCIPmessagehdlrSetData(this, data); }
	//SCIPmessagehdlrSetLogfile
	public void setLogfile(String filename) { JSCIP.messagehdlrSetLogfile(this, filename); }
	//SCIPmessageSetQuiet
	public void setQuiet(boolean quiet) { JSCIP.messagehdlrSetQuiet(this, quiet); }
	
	//SCIPmessagePrintInfo
	public void printInfo(String fmt, Object... vals) { JSCIP.LIB.SCIPmessagePrintInfo(this, fmt, vals); }
	//SCIPmessageFPrintInfo
	public void fprintInfo(String fmt, FILEPTR file, Object... vals) { JSCIP.LIB.SCIPmessageFPrintInfo(this, file, fmt, vals); }

	//SCIPmessagePrintWarning
	public void printWarning(String fmt, Object... vals) { JSCIP.LIB.SCIPmessagePrintWarning(this, fmt, vals); }
	//SCIPmessageFPrintWarning
	public void fprintWarning(String fmt, FILEPTR file, Object... vals) { JSCIP.LIB.SCIPmessageFPrintWarning(this, file, fmt, vals); }

	//SCIPmessagePrintDialog
	public void printDialog(String fmt, Object... vals) { JSCIP.LIB.SCIPmessagePrintDialog(this, fmt, vals); }
	//SCIPmessageFPrintDialog
	public void fprintDialog(String fmt, FILEPTR file, Object... vals) { JSCIP.LIB.SCIPmessageFPrintDialog(this, file, fmt, vals); }

	//SCIPmessagePrintVerbInfo
	public void printVerbInfo(SCIP_VERBLEVEL verblevel, SCIP_VERBLEVEL msgverblevel, String fmt, Object... vals) {
		JSCIP.LIB.SCIPmessagePrintVerbInfo(this, verblevel, msgverblevel, fmt, vals);
	}
	//SCIPmessageFPrintVerbInfo
	public void fprintVerbInfo(SCIP_VERBLEVEL verblevel, SCIP_VERBLEVEL msgverblevel, FILEPTR file,
			String fmt, Object... vals) {
		JSCIP.LIB.SCIPmessageFPrintVerbInfo(this, verblevel, msgverblevel, file, fmt, vals);
	}
	
	//SCIPmessagehdlrGetData
	SCIP_MESSAGEHDLRDATA getData(){ return JSCIP.messagehdlrGetData(this); }
	//SCIPmessagehdlrGetLogfile
	FILEPTR getLogfile(){ return JSCIP.messagehdlrGetLogfile(this); }
	//SCIPmessagehdlrIsQuiet
	boolean isQuiet() { return JSCIP.messagehdlrIsQuiet(this); }
}
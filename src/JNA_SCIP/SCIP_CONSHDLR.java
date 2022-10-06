package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class SCIP_CONSHDLR extends PointerType {
	public SCIP_CONSHDLR() {}
	public SCIP_CONSHDLR(Pointer p) {
		super(p);
	}
	public String toString() {
		return "S_CONSHDLR["+Long.toString(Pointer.nativeValue(getPointer()),16).substring(6)+"]";
	}
	
	//SCIPconshdlrGetName
	public String getName(){ return JSCIP.conshdlrGetName(this); }
	//SCIPconshdlrGetDesc
	public String getDesc(){ return JSCIP.conshdlrGetDesc(this); }
	//SCIPconshdlrGetData
	public SCIP_CONSHDLRDATA getData(){ return JSCIP.conshdlrGetData(this); }
	//SCIPgetConss
	public SCIP_CONS[] getConss(){ return JSCIP.conshdlrGetConss(this); }
	//SCIPconshdlrGetSetupTime
	public double getSetupTime(){ return JSCIP.conshdlrGetSetupTime(this); }
	//SCIPconshdlrGetPresolTime
	public double getPresolTime(){ return JSCIP.conshdlrGetPresolTime(this); }
	//SCIPconshdlrGetSepaTime
	public double getSepaTime(){ return JSCIP.conshdlrGetSepaTime(this); }
	//SCIPconshdlrGetEnfoLPTime
	public double getEnfoLPTime(){ return JSCIP.conshdlrGetEnfoLPTime(this); }
	//SCIPconshdlrGetEnfoPSTime
	public double getEnfoPSTime(){ return JSCIP.conshdlrGetEnfoPSTime(this); }
	//SCIPconshdlrGetEnfoRelaxTime
	public double getEnfoRelaxTime(){ return JSCIP.conshdlrGetEnfoRelaxTime(this); }
	//SCIPconshdlrGetPropTime
	public double getPropTime(){ return JSCIP.conshdlrGetPropTime(this); }
	//SCIPconshdlrGetCheckTime
	public double getCheckTime(){ return JSCIP.conshdlrGetCheckTime(this); }
	//SCIPconshdlrGetRespropTime
	public double getRespropTime(){ return JSCIP.conshdlrGetRespropTime(this); }
	//SCIPconshdlrGetSepaPriority
	public int getSepaPriority(){ return JSCIP.conshdlrGetSepaPriority(this); } 
	//SCIPconshdlrGetEnfoPriority
	public int getEnfoPriority(){ return JSCIP.conshdlrGetEnfoPriority(this); } 
	//SCIPconshdlrGetCheckPriority
	public int getCheckPriority(){ return JSCIP.conshdlrGetCheckPriority(this); } 
	//SCIPconshdlrGetEnfoFreq
	public int getEnfoFreq(){ return JSCIP.conshdlrGetEnfoFreq(this); } 
	//SCIPconshdlrNeedsCons
	public boolean needsCons(){ return JSCIP.conshdlrNeedsCons(this); } 
}

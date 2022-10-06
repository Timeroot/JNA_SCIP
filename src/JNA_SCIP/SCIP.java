package JNA_SCIP;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public final class SCIP extends PointerType {

	public SCIP() {}
	public SCIP(Pointer p) {
		super(p);
	}
	public String toString() {
		return "SCIP["+Long.toString(Pointer.nativeValue(getPointer()),16).substring(6)+"]";
	}
	
	//Create and initialize a new SCIP instance.
	//SCIPcreate
    public static SCIP create() {
    	SCIP scip = new SCIP();
    	if(scip.getPointer() != null)
    		throw new RuntimeException("Called create() when scip was already nonnull, call free() first.");
    	JSCIP.CALL_SCIPcreate(scip);
    	return scip;
    }

    //"Member" methods -- most of the SCIP library!
    
    /* cons_knapsack.h */
    public SCIP_CONS createConsBasicKnapsack(String name, int nvars, SCIP_VAR[] vars,
			long[] weights, long capacity) {
    	return JSCIP.createConsBasicKnapsack(this, name, nvars, vars, weights, capacity);
    }
    /* END cons_knapsack.h */
    
    /* cons_linear.h */
	//SCIPcreateConsLinear
	public SCIP_CONS createConsLinear(String name, SCIP_VAR[] vars, double[] vals, double lhs, double rhs,
			boolean initial, boolean separate, boolean enforce, boolean check, boolean propagate, boolean local,
			boolean modifiable, boolean dynamic, boolean removable, boolean stickingatnode) {
		return JSCIP.createConsLinear(this, name, vars, vals, lhs, rhs, initial, separate,
				enforce, check, propagate, local, modifiable, dynamic, removable, stickingatnode);
	}
	//SCIPcreateConsBasicLinear
	public SCIP_CONS createConsBasicLinear(String name, SCIP_VAR[] vars, double[] vals, double lhs, double rhs) {
		return JSCIP.createConsBasicLinear(this, name, vars, vals, lhs, rhs);
	}
	//SCIPaddCoefLinear
	public void addCoefLinear(SCIP_CONS cons, SCIP_VAR var, double val) {
		JSCIP.CALL_SCIPaddCoefLinear(this, cons, var, val);
	}
	//SCIPcreateConsBasicLogicor
	public SCIP_CONS createConsBasicLogicor(String name, SCIP_VAR[] vars) {
		return JSCIP.createConsBasicLogicor(this, name, vars);
	}
	
	/* cons_setppc.h */
	//SCIPcreateConsBasicSetcover
	public SCIP_CONS createConsBasicSetcover(String name, SCIP_VAR[] vars) {
		return JSCIP.createConsBasicSetcover(this, name, vars);
	}
	//SCIPaddCoefSetppc
	public void addCoefSetppc(SCIP_CONS cons, SCIP_VAR var) {
		JSCIP.CALL_SCIPaddCoefSetppc(this, cons, var);
	}
	//SCIPgetDualsolSetppc
	public double getDualsolSetppc(SCIP_CONS cons) {
		return JSCIP.getDualsolSetppc(this, cons);
	}
	//SCIPgetNFixedonesSetppc
	public double getNFixedonesSetppc(SCIP_CONS cons) {
		return JSCIP.getNFixedonesSetppc(this, cons);
	}
	/* END cons_setppc.h */
	
	/* cons_varbound.h */
	public SCIP_CONS createConsBasicVarbound(String name, SCIP_VAR var, SCIP_VAR vbdvar, double vbdcoef,
			double lhs, double rhs) {
		return JSCIP.createConsBasicVarbound(this, name, var, vbdvar, vbdcoef, lhs, rhs);
	}
	/* END cons_varbound.h */
	
	//SCIPcreateExprVar
	public SCIP_EXPR createExprVar(SCIP_VAR var, Pointer ownercreate, Pointer ownercreatedata) {
		return JSCIP.createExprVar(this, var, ownercreate, ownercreatedata);
	}
	
	/* scipdefplugins.h */
	//SCIPincludeDefaultPlugins
	public void includeDefaultPlugins() {
		JSCIP.CALL_SCIPincludeDefaultPlugins(this);
	}
	
	/* scipshell.h */
	//SCIPprocessShellArguments
	public void processShellArguments(String[] argv, String settings) {
		JSCIP.CALL_SCIPprocessShellArguments(this, argv, settings);
	}
	/* END scipshell.h */
	
	/* scip_branch.h */
	//SCIPincludeBranchruleBasic
	public SCIP_BRANCHRULE includeBranchruleBasic(String name,
			String desc, int priority, int maxdepth, double maxbounddist,
			SCIP_BRANCHRULEDATA branchruledata) {
		return JSCIP.CALL_SCIPincludeBranchruleBasic(this, name, desc, priority, maxdepth,
				maxbounddist, branchruledata);
	}
	//SCIPsetBranchruleExecLp
	public void setBranchruleExecLp(SCIP_BRANCHRULE branchrule, SCIP_DECL_BRANCHEXECLP branchexeclp) {
		JSCIP.CALL_SCIPsetBranchruleExecLp(this, branchrule, branchexeclp);
	}
	//SCIPcreateChild
	public SCIP_NODE createChild(double nodeselprio, double estimate) {
		return JSCIP.CALL_SCIPcreateChild(this, nodeselprio, estimate);
	}
	//SCIPgetLPBranchCands
	public LPBranchCands getLPBranchCands() { return JSCIP.CALL_SCIPgetLPBranchCands(this); }
	/* END scip_branch.h */
	
	/* scip_conflict.h */
	//SCIPinitConflictAnalysis
	public void initConflictAnalysis(SCIP_CONFTYPE conftype, boolean iscutoffinvolved) {
		JSCIP.CALL_SCIPinitConflictAnalysis(this, conftype, iscutoffinvolved);
	}
	//SCIPaddConflictLb
	public void addConflictLb(SCIP_VAR var, SCIP_BDCHGIDX chgidx) {
		JSCIP.CALL_SCIPaddConflictLb(this, var, chgidx);
	}
	//SCIPaddConflictUb
	public void addConflictUb(SCIP_VAR var, SCIP_BDCHGIDX chgidx) {
		JSCIP.CALL_SCIPaddConflictUb(this, var, chgidx);
	}
	//SCIPaddConflictBinvar
	public void addConflictBinvar(SCIP_VAR var) {
		JSCIP.CALL_SCIPaddConflictBinvar(this, var);
	}
	//SCIPanalyzeConflict
	public boolean analyzeConflictCons(int validdepth) {
		return JSCIP.CALL_SCIPanalyzeConflict(this, validdepth);
	}
	//SCIPanalyzeConflictCons
	public boolean analyzeConflictCons(SCIP_CONS cons) {
		return JSCIP.CALL_SCIPanalyzeConflictCons(this, cons);
	}
	/* END scip_conflict.h */
	
	/* scip_cons.h */
	//SCIPincludeConshdlrBasic
	public SCIP_CONSHDLR includeConshdlrBasic(
			String name, String desc,
			int enfopriority, int chckpriority, int eagerfreq, boolean needscons,
			SCIP_DECL_CONSENFOLP consenfolp,
			SCIP_DECL_CONSENFOPS consenfops,
			SCIP_DECL_CONSCHECK conscheck,
			SCIP_DECL_CONSLOCK conslock,
			SCIP_CONSHDLRDATA conshdlrdata
		) {
		return JSCIP.CALL_SCIPincludeConshdlrBasic(this, name, desc,
				enfopriority, chckpriority, eagerfreq, needscons, consenfolp, consenfops,
				conscheck, conslock, conshdlrdata);
	}
	//SCIPsetConshdlrEnforelax
	public void setConshdlrEnforelax(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSENFORELAX consenforelax) {
		JSCIP.CALL_SCIPsetConshdlrEnforelax(this, conshdlr, consenforelax);
	}
	//SCIPsetConshdlrCopy
	public void setConshdlrCopy(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSHDLRCOPY conshdlrcopy, SCIP_DECL_CONSCOPY conscopy) {
		JSCIP.CALL_SCIPsetConshdlrCopy(this, conshdlr, conshdlrcopy, conscopy);
	}
	//SCIPsetConshdlrTrans
	public void setConshdlrTrans(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSTRANS consTrans) {
		JSCIP.CALL_SCIPsetConshdlrTrans(this, conshdlr, consTrans);
	}
	//SCIPsetConshdlrExit
	public void setConshdlrExit(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSEXIT consExit) {
		JSCIP.CALL_SCIPsetConshdlrExit(this, conshdlr, consExit);
	}
	//SCIPsetConshdlrSepa
	public void setConshdlrSepa(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSSEPALP conssepalp,
			SCIP_DECL_CONSSEPASOL conssepasol, int sepafreq, int sepapriority, boolean delaysepa) {
		JSCIP.CALL_SCIPsetConshdlrSepa(this, conshdlr, conssepalp, conssepasol, sepafreq,
				sepapriority, delaysepa);
	}
	//SCIPsetConshdlrProp
	public void setConshdlrProp(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSPROP consProp,
			int propfreq, boolean delayprop, SCIP_PROPTIMING proptiming) {
		JSCIP.CALL_SCIPsetConshdlrProp(this, conshdlr, consProp, propfreq, delayprop, proptiming);
	}
	//SCIPsetConshdlrResprop
	public void setConshdlrResprop(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSRESPROP consresprop) {
		JSCIP.CALL_SCIPsetConshdlrResprop(this, conshdlr, consresprop);
	}
	//SCIPsetConshdlrActive
	public void setConshdlrActive(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSACTIVE consactive) {
		JSCIP.CALL_SCIPsetConshdlrActive(this, conshdlr, consactive);
	}
	//SCIPsetConshdlrDective
	public void setConshdlrDective(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSDEACTIVE consdeactive) {
		JSCIP.CALL_SCIPsetConshdlrDeactive(this, conshdlr, consdeactive);
	}
	//SCIPsetConshdlrDelete
	public void setConshdlrDelete(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSDELETE consDelete) {
		JSCIP.CALL_SCIPsetConshdlrDelete(this, conshdlr, consDelete);
	}
	//SCIPsetConshdlrInitlp
	public void setConshdlrInitlp(SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSINITLP consinitlp) {
		JSCIP.CALL_SCIPsetConshdlrInitlp(this, conshdlr, consinitlp);
	}
	//SCIPfindConshdlr
	public SCIP_CONSHDLR findConshdlr(String name) {
		return JSCIP.findConshdlr(this, name);
	}
	//SCIPcreateCons
	public SCIP_CONS createCons(String name, SCIP_CONSHDLR conshdlr,
			Pointer consdata, boolean initial, boolean separate, boolean enforce, boolean check,
			boolean propagate, boolean local, boolean modifiable, boolean dynamic, boolean removable,
			boolean stickingatnode) {
		return JSCIP.createCons(this, name, conshdlr, consdata, initial, separate, enforce, check,
				propagate, local, modifiable, dynamic, removable, stickingatnode);
	}
	//SCIPcaptureCons
	public void captureCons(SCIP_CONS cons) { JSCIP.CALL_SCIPcaptureCons(this, cons); }
	//SCIPreleaseCons
	public void releaseCons(SCIP_CONS cons) { JSCIP.CALL_SCIPreleaseCons(this, cons); }
	//SCIPsetConsInitial
	public void setConsInitial(SCIP_CONS cons, boolean bool) { JSCIP.CALL_SCIPsetConsInitial(this, cons, bool); }
	//SCIPsetConsLocal
	public void setConsLocal(SCIP_CONS cons, boolean bool) { JSCIP.CALL_SCIPsetConsLocal(this, cons, bool); }
	//SCIPsetConsModifiable
	public void setConsModifiable(SCIP_CONS cons, boolean bool) { JSCIP.CALL_SCIPsetConsModifiable(this, cons, bool); }
	//SCIPsetConsDynamic
	public void setConsDynamic(SCIP_CONS cons, boolean bool) { JSCIP.CALL_SCIPsetConsDynamic(this, cons, bool); }
	//SCIPsetConsRemovable
	public void setConsRemovable(SCIP_CONS cons, boolean bool) { JSCIP.CALL_SCIPsetConsRemovable(this, cons, bool); }
	//SCIPgetTransformedCons
	public SCIP_CONS getTransformedCons(SCIP_CONS cons) { return JSCIP.CALL_SCIPgetTransformedCons(this, cons); }
	/* END scip_cons.h */
	
	/* scip_copy.h */
	public int getSubscipDepth() { return JSCIP.getSubscipDepth(this); }
	/* END scip_copy.h */
	
	/* scip_cut.h */
	//SCIPaddRow
	public boolean addRow(SCIP_ROW row, boolean forcecut) { return JSCIP.CALL_SCIPaddRow(this, row, forcecut); }
	//SCIPaddPoolCut
	public void addPoolCut(SCIP_ROW row) {  JSCIP.CALL_SCIPaddPoolCut(this, row); }
	//SCIPisCutEfficacious
	public boolean isCutEfficacious(SCIP_SOL sol, SCIP_ROW cut) {
		return JSCIP.isCutEfficacious(this, sol, cut);
	}
	//SCIPisEfficacious
	public boolean isEfficacious(double efficacy) {
		//TODO speed up with direct access, maybe
		return JSCIP.isEfficacious(this, efficacy);
	}
	/* END scip_cut.h */

	/* scip_dialog.h */
	//SCIPstartInteraction
	public void startInteraction() { JSCIP.CALL_SCIPstartInteraction(this); }
	//SCIPfree
	//Note: sets the pointer to null.
	public void free() { JSCIP.CALL_SCIPfree(this); }
	//SCIPgetStage
	public SCIP_STAGE getStage() { return JSCIP.getStage(this); }
	//SCIPgetVersion
	public void printVersion(FILEPTR file) { JSCIP.printVersion(this, file); }
	//SCIPgetStatus
	public SCIP_STATUS getStatus() { return JSCIP.getStatus(this); }
	//SCIPisTransformed
	public boolean isTransformed() { return JSCIP.isTransformed(this); }
	/* END scip_dialog.h */
	
	/* scip_event.h */
	//SCIPincludeEventhdlrBasic
	public SCIP_EVENTHDLR includeEventhdlrBasic(String name,
			String desc, SCIP_DECL_EVENTEXEC eventexec, SCIP_EVENTHDLRDATA data) {
		return JSCIP.includeEventhdlrBasic(this, name, desc, eventexec, data);
	}
	//SCIPfindEventhdlr
	public SCIP_EVENTHDLR findEventhdlr(String name) { return JSCIP.findEventhdlr(this, name); }
	//SCIPcatchEvent
	public int catchEvent(SCIP_EVENTTYPE eventtype, SCIP_EVENTHDLR eventhdlr,
			SCIP_EVENTDATA data) {
		return JSCIP.CALL_SCIPcatchEvent(this, eventtype, eventhdlr, data);
	}
	//SCIPdropEvent
	public void dropEvent(SCIP_EVENTTYPE eventtype, SCIP_EVENTHDLR eventhdlr,
			SCIP_EVENTDATA data, int filterpos) {
		JSCIP.CALL_SCIPdropEvent(this, eventtype, eventhdlr, data, filterpos);
	}
	/* END scip_event.h */
	
	/* scip_lp.h */
	//SCIPhasCurrentNodeLP
	public boolean hasCurrentNodeLP() { return JSCIP.hasCurrentNodeLP(this); }
	//SCIPisLPConstructed
	public boolean isLPConstructed() { return JSCIP.isLPConstructed(this); }
	//SCIPconstructLP
	public boolean constructLP() { return JSCIP.CALL_SCIPconstructLP(this); }
	//SCIPgetLPRows
	public SCIP_ROW[] getLPRows() { return JSCIP.getLPRows(this); }
	//SCIPgetNLPRows
	public int getNLPRows() { return JSCIP.getNLPRows(this); }
	//SCIPcreateEmptyRowConshdlr
	public SCIP_ROW createEmptyRowConshdlr(SCIP_CONSHDLR conshdlr, String name, double lhs,
			double rhs, boolean local, boolean modifiable, boolean removable) {
		return JSCIP.createEmptyRowConshdlr(this, conshdlr, name, lhs, rhs, local, modifiable, removable);
	}
	//SCIPaddVarToRow
	public void addVarToRow(SCIP_ROW row, SCIP_VAR var, double val) {
		JSCIP.CALL_SCIPaddVarToRow(this, row, var, val);
	}
	//SCIPreleaseRow
	public void releaseRow(SCIP_ROW row) { JSCIP.CALL_SCIPreleaseRow(this, row); }
	//SCIPcacheRowExtensions
	public void cacheRowExtensions(SCIP_ROW row) { JSCIP.CALL_SCIPcacheRowExtensions(this, row); }
	//SCIPflushRowExtensions
	public void flushRowExtensions(SCIP_ROW row) { JSCIP.CALL_SCIPflushRowExtensions(this, row); }
	//SCIPflushRowExtensions
	public void printRow(SCIP_ROW row, FILEPTR file) { JSCIP.CALL_SCIPprintRow(this, row, file); }
	//SCIPstartDive
	public void startDive() { JSCIP.CALL_SCIPstartDive(this); }
	//SCIPstartDive
	public void endDive() { JSCIP.CALL_SCIPendDive(this); }
	//SCIPchgCutoffboundDive
	public void chgCutoffboundDive(double newcutoff) { JSCIP.CALL_SCIPchgCutoffboundDive(this, newcutoff); }
	//SCIPinDive
	public boolean inDive() { return JSCIP.inDive(this); }
	
	//SCIPincludeHeurBasic
	public SCIP_HEUR includeHeurBasic(String name, String desc, byte dispchar, int priority, int freq,
			int freqofs, int maxdepth, SCIP_HEURTIMING timingmask, boolean usessubscip,
			SCIP_DECL_HEUREXEC heurexec, SCIP_HEURDATA heurdata) {
		return JSCIP.includeHeurBasic(this, name, desc, dispchar, priority, freq, freqofs, maxdepth,
				timingmask, usessubscip, heurexec, heurdata);
	}
	//SCIPblkmem
	public Pointer blkmem() { return JSCIP.blkmem(this); }
	
	//SCIPsetMessagehdlr
	public void setMessagehdlr(SCIP_MESSAGEHDLR messagehdlr) {
		JSCIP.CALL_SCIPsetMessagehdlr(this, messagehdlr);
	}
	//SCIPgetMessagehdlr
	public SCIP_MESSAGEHDLR getMessagehdlr() {
		return JSCIP.getMessagehdlr(this);
	}
	//SCIPsetMessagehdlrLogfile
	public void setMessagehdlrLogfile(String filename) {
		JSCIP.setMessagehdlrLogfile(this, filename);
	}
	//SCIP setMessagehdlrQuiet
	public void setMessagehdlrQuiet(boolean quiet) {
		JSCIP.setMessagehdlrQuiet(this, quiet);
	}
	//SCIPwarningMessage
	public void warningMessage(String formatstr, Object... vals) {
		JSCIP.warningMessage(this, formatstr, vals);
	}
	//SCIPdebugMessage
	public void debugMessage(String sourcefile, int sourceline,  String formatstr, Object... vals) {
		JSCIP.debugMessage(this, sourcefile, sourceline, formatstr, vals);
	}
	//SCIPprintDebugMessage
	public void printDebugMessage(String file, int line, String formatstr, Object... vals) {
		JSCIP.printDebugMessage(this, file, line, formatstr, vals);
	}
	//SCIPdebugMessagePrint
	public void debugMessagePrint(String formatstr, Object... vals) {
		JSCIP.debugMessagePrint(this, formatstr, vals);
	}
	//SCIPdialogMessage
	public void dialogMessage(FILEPTR file, String formatstr, Object... vals) {
		JSCIP.dialogMessage(this, file, formatstr, vals);
	}
	//SCIPverbMessage
	public void verbMessage(SCIP_VERBLEVEL verblevel, FILEPTR file, String formatstr, Object... vals) {
		JSCIP.verbMessage(this, verblevel, file, formatstr, vals);
	}
	//SCIPinfoMessage
	public void infoMessage(FILEPTR file, String formatstr, Object... vals) {
		JSCIP.infoMessage(this, file, formatstr, vals);
	}
	//SCIPgetVerbLevel
	public SCIP_VERBLEVEL getVerbLevel() { return JSCIP.getVerbLevel(this); };
	
	/* scip_numerics.h */
	//SCIPinfinity
	public double infinity() { return JSCIP.infinity(this); }
	
	//TODO: improve performance on comparison calls with direct memory mapping.
	//SCIPisFeasEQ
	public boolean isFeasEQ(double x, double y) { return JSCIP.isFeasEQ(this, x, y); }
	//SCIPisFeasLT
	public boolean isFeasLT(double x, double y) { return JSCIP.isFeasLT(this, x, y); }
	//SCIPisFeasLE
	public boolean isFeasLE(double x, double y) { return JSCIP.isFeasLE(this, x, y); }
	//SCIPisFeasGT
	public boolean isFeasGT(double x, double y) { return JSCIP.isFeasGT(this, x, y); }
	//SCIPisFeasGE
	public boolean isFeasGE(double x, double y) { return JSCIP.isFeasGE(this, x, y); }
	//SCIPisFeasZero
	public boolean isFeasZero(double x) { return JSCIP.isFeasZero(this, x); }
	//SCIPisFeasPositive
	public boolean isFeasPositive(double x) { return JSCIP.isFeasPositive(this, x); }
	//SCIPisFeasNegative
	public boolean isFeasNegative(double x) { return JSCIP.isFeasNegative(this, x); }
	//SCIPisFeasIntegral
	public boolean isFeasIntegral(double x) { return JSCIP.isFeasIntegral(this, x); }
	/* END scip_numerics.h */
	
	/* scip_param.h */
	//SCIPsetRealParam
	public void setRealParam(String name, double value) { JSCIP.CALL_SCIPsetRealParam(this, name, value); }
	//SCIPsetCharParam
	public void setCharParam(String name, byte value) { JSCIP.CALL_SCIPsetCharParam(this, name, value); }
	//SCIPsetBoolParam
	public void setBoolParam(String name, boolean value) { JSCIP.CALL_SCIPsetBoolParam(this, name, value); }
	//SCIPsetIntParam
	public void setIntParam(String name, int value) { JSCIP.CALL_SCIPsetIntParam(this, name, value); }
	//SCIPsetLongintParam
	public void setLongintParam(String name, long value) { JSCIP.CALL_SCIPsetLongintParam(this, name, value); }
	//SCIPsetStringParam
	public void setStringParam(String name, String value) { JSCIP.CALL_SCIPsetStringParam(this, name, value); }
	//SCIPsetEmphasis
	public void setEmphasis(SCIP_PARAMEMPHASIS emph, boolean quiet) { JSCIP.CALL_SCIPsetEmphasis(this, emph, quiet); }
	//SCIPsetHeuristics
	public void setHeuristics(SCIP_PARAMSETTING emph, boolean quiet) { JSCIP.CALL_SCIPsetHeuristics(this, emph, quiet); }
	//SCIPsetPresolving
	public void setPresolving(SCIP_PARAMSETTING emph, boolean quiet) { JSCIP.CALL_SCIPsetPresolving(this, emph, quiet);	}
	//SCIPsetSeparating
	public void setSeparating(SCIP_PARAMSETTING emph, boolean quiet) { JSCIP.CALL_SCIPsetSeparating(this, emph, quiet);	}
	/* END scip_param.h */
	
	/* scip_pricer.h */
	//SCIPincludePricer
	public void includePricer(
			String name, String desc, int priority, boolean delay,
			   SCIP_DECL_PRICERCOPY  pricercopy,
			   SCIP_DECL_PRICERFREE  pricerfree,
			   SCIP_DECL_PRICERINIT  pricerinit,
			   SCIP_DECL_PRICEREXIT  pricerexit,
			   SCIP_DECL_PRICERINITSOL pricerinitsol,
			   SCIP_DECL_PRICEREXITSOL pricerexitsol,
			   SCIP_DECL_PRICERREDCOST pricerredcost,
			   SCIP_DECL_PRICERFARKAS pricerfarkas,
			   SCIP_PRICERDATA  pricerdata
		) {
		JSCIP.CALL_SCIPincludePricer(this, name, desc, priority, delay, pricercopy, pricerfree,
				pricerinit, pricerexit, pricerinitsol, pricerexitsol, pricerredcost, pricerfarkas,
				pricerdata);
	}
	
	//SCIPincludePricerBasic
	public SCIP_PRICER includePricerBasic(
			String name, String desc,
			int priority, boolean delay,
			   SCIP_DECL_PRICERREDCOST pricerredcost,
			   SCIP_DECL_PRICERFARKAS pricerfarkas,
			   SCIP_PRICERDATA  pricerdata
		) {
		return JSCIP.CALL_SCIPincludePricerBasic(this, name, desc, priority, delay, pricerredcost,
				pricerfarkas, pricerdata);
	}
	
	//SCIPsetPricerCopy
	public void setPricerCopy(SCIP_PRICER pricer, SCIP_DECL_PRICERCOPY pricercopy) {
		JSCIP.CALL_SCIPsetPricerCopy(this, pricer, pricercopy);
	}
	//SCIPsetPricerFree
	public void setPricerFree(SCIP_PRICER pricer, SCIP_DECL_PRICERFREE pricerfree) {
		JSCIP.CALL_SCIPsetPricerFree(this, pricer, pricerfree);
	}
	//SCIPsetPricerInit
	public void setPricerInit(SCIP_PRICER pricer, SCIP_DECL_PRICERINIT pricerinit) {
		JSCIP.CALL_SCIPsetPricerInit(this, pricer, pricerinit);
	}
	//SCIPsetPricerExit
	public void setPricerExit(SCIP_PRICER pricer, SCIP_DECL_PRICEREXIT pricerexit) {
		JSCIP.CALL_SCIPsetPricerExit(this, pricer, pricerexit);
	}
	//SCIPsetPricerInitsol
	public void setPricerInitsol(SCIP_PRICER pricer, SCIP_DECL_PRICERINITSOL pricerinitsol) {
		JSCIP.CALL_SCIPsetPricerInitsol(this, pricer, pricerinitsol);
	}
	//SCIPsetPricerExitsol
	public void setPricerExitsol(SCIP_PRICER pricer, SCIP_DECL_PRICEREXITSOL pricerexitsol) {
		JSCIP.CALL_SCIPsetPricerExitsol(this, pricer, pricerexitsol);
	}
	//SCIPfindPricer
	public SCIP_PRICER findPricer(String name) { return JSCIP.findPricer(this, name); }
	//SCIPactivatePricer
	public void activatePricer(SCIP_PRICER pricer) { JSCIP.CALL_SCIPactivatePricer(this, pricer); }
	/* END scip_pricer.h */
	
	/* scip_prob.h */
	//SCIPcreateProb
	public void createProb(String name,
			SCIP_DECL_PROBDELORIG probdelorig,
			SCIP_DECL_PROBTRANS probtrans,
			SCIP_DECL_PROBDELTRANS probdeltrans,
			SCIP_DECL_PROBINITSOL probinitsol,
			SCIP_DECL_PROBEXITSOL probexitsol,
			SCIP_DECL_PROBCOPY probcopy,
			SCIP_PROBDATA probdata) {
		JSCIP.CALL_SCIPcreateProb(this, name, probdelorig, probtrans, probdeltrans,
				probinitsol, probexitsol, probcopy, probdata);
	}
	//SCIPcreateProbBasic
	public void createProbBasic(String name) {
		JSCIP.CALL_SCIPcreateProbBasic(this, name);
	}
	//SCIPsetProbDelorig
	public void setProbDelorig(SCIP_DECL_PROBDELORIG method) {
		JSCIP.CALL_SCIPsetProbDelorig(this, method);
	}
	//SCIPsetProbTrans
	public void setProbTrans(SCIP_DECL_PROBTRANS method) {
		JSCIP.CALL_SCIPsetProbTrans(this, method);
	}
	//SCIPsetProbDeltrans
	public void setProbDeltrans(SCIP_DECL_PROBDELTRANS method) {
		JSCIP.CALL_SCIPsetProbDeltrans(this, method);
	}
	//SCIPsetProbExitsol
	public void setProbInitsol(SCIP_DECL_PROBINITSOL method) {
		JSCIP.CALL_SCIPsetProbInitsol(this, method);
	}
	//SCIPsetProbExitsol
	public void setProbExitsol(SCIP_DECL_PROBEXITSOL method) {
		JSCIP.CALL_SCIPsetProbExitsol(this, method);
	}
	//SCIPsetProbCopy
	public void setProbCopy(SCIP_DECL_PROBCOPY method) {
		JSCIP.CALL_SCIPsetProbCopy(this, method);
	}
	//SCIPreadProb
	public void readProb(String filename, String ext) {
		JSCIP.CALL_SCIPreadProb(this, filename, ext);
	}
	//SCIPfreeProb
	public void freeProb() { JSCIP.CALL_SCIPfreeProb(this); }
	//SCIPsetObjsense
	public void setObjsense(SCIP_OBJSENSE objsense) { JSCIP.CALL_SCIPsetObjsense(this, objsense); }
	//SCIPsetObjIntegral
	public void setObjIntegral() { JSCIP.CALL_SCIPsetObjIntegral(this); }
	//SCIPisObjIntegral
	public boolean isObjIntegral() { return JSCIP.isObjIntegral(this); }
	//SCIPaddVar
	public void addVar(SCIP_VAR var) { JSCIP.CALL_SCIPaddVar(this, var); }
	//SCIPaddPricedVar
	public void addPricedVar(SCIP_VAR var, double score) { JSCIP.CALL_SCIPaddPricedVar(this, var, score); }
	//SCIPgetVars
	public SCIP_VAR[] getVars() { return JSCIP.CALL_SCIPgetVars(this); }
	//SCIPgetNVars
	public int getNVars() { return JSCIP.getNVars(this); }
	//SCIPaddCons
	public void addCons(SCIP_CONS cons) { JSCIP.CALL_SCIPaddCons(this, cons); }
	//SCIPgetLocalTransEstimate
	public double getLocalTransEstimate() { return JSCIP.getLocalTransEstimate(this); }
	//SCIPaddConsNode
	public void addConsNode(SCIP_NODE node, SCIP_CONS cons, SCIP_NODE validnode) {
		JSCIP.CALL_SCIPaddConsNode(this, node, cons, validnode);
	}
	//SCIPdelConsLocal
	public void delConsLocal(SCIP_CONS cons) {
		JSCIP.CALL_SCIPdelConsLocal(this, cons);
	}
	/* END scip_prob.h */
	
	/* scip_reader.h */
	//SCIPincludeReaderBasic
	public SCIP_READER includeReaderBasic(String name, String desc, String extensions, SCIP_READERDATA data) {
		return JSCIP.CALL_SCIPincludeReaderBasic(this, name, desc, extensions, data);
	}
	//SCIPsetReaderRead
	public void setReaderRead(SCIP_READER reader, SCIP_DECL_READERREAD readerread) {
		JSCIP.CALL_SCIPsetReaderRead(this, reader, readerread);
	}
	
	/* scip_sol.h */
	//SCIPgetBestSol
	public SCIP_SOL getBestSol() { return JSCIP.getBestSol(this); }
	//SCIPgetNSols
	public int getNSols() { return JSCIP.getNSols(this); }
	//SCIPgetSols
	public SCIP_SOL[] getSols() { return JSCIP.getSols(this); } 
	//SCIPprintSol
	public void printSol(SCIP_SOL sol, FILEPTR file, boolean printzeros) {
		JSCIP.CALL_SCIPprintSol(this, sol, file, printzeros);
	}
	//SCIPfreeReoptSolve
	public void freeReoptSolve() { JSCIP.CALL_SCIPfreeReoptSolve(this); }
	//SCIPfreeTransform
	public void freeTransform() { JSCIP.CALL_SCIPfreeTransform(this); }
	//SCIPfreeSolve
	public void freeSolve() { JSCIP.CALL_SCIPfreeSolve(this); }
	//SCIPprintBestSol
	public void printBestSol(FILEPTR file, boolean printzeros) {
		JSCIP.CALL_SCIPprintBestSol(this, file, printzeros);
	}
	//SCIPgetSolVal
	public double getSolVal(SCIP_SOL sol, SCIP_VAR var) { return JSCIP.getSolVal(this, sol, var); }
	//SCIPgetSolVals
	//Offer a few methods -- one where it allocates the array for you, one where it fills in an array
	//And a version of each for 2D arrays as well
	public void getSolVals(SCIP_SOL sol, SCIP_VAR[] vars, double[] vals) {
		JSCIP.getSolVals(this, sol, vars, vals);
	}
	public double[] getSolVals(SCIP_SOL sol, SCIP_VAR[] vars) {
		double[] vals = new double[vars.length];
		JSCIP.getSolVals(this, sol, vars, vals);
		return vals;
	}
	public void getSolVals(SCIP_SOL sol, SCIP_VAR[][] vars, double[][] vals) {
		//if there are null vars, we skip them. Otherwise we can use fast copying
		boolean hasnulls = false;
		
		int n = 0; 
		for(SCIP_VAR[] row : vars)
			for(SCIP_VAR var : row)
				if(var != null)
					n++;
				else
					hasnulls = true;
		
		SCIP_VAR[] flattenedVars = new SCIP_VAR[n];
		double[] flattenedVals = new double[n];

		n = 0;
		for(SCIP_VAR[] row : vars) {
			if(!hasnulls) {
				System.arraycopy(row, 0, flattenedVars, n, row.length);
				n += row.length;
			} else {
				for(SCIP_VAR var : row)
					if(var != null)
						flattenedVars[n++] = var;
			}
		}
		
		JSCIP.getSolVals(this, sol, flattenedVars, flattenedVals);
		
		n = 0;
		for(int r=0; r<vals.length; r++) {
			double[] row = vals[r];
			if(!hasnulls) {
				System.arraycopy(flattenedVals, n, row, 0, row.length);
				n += row.length;
			} else {
				for(int c=0; c<row.length; c++) {
					if(vars[r][c] != null)
						row[c] = flattenedVals[n++];
				}
			}
		}
	}
	public double[][] getSolVals(SCIP_SOL sol, SCIP_VAR[][] vars) {
		double[][] vals = new double[vars.length][];
		for(int r = 0; r < vars.length; r++) {
			int w = vars[r].length;
			vals[r] = new double[w];
		}
		this.getSolVals(sol, vars, vals);
		return vals;
	}
	
	//SCIPgetSolOrigObj
	public double getSolOrigObj(SCIP_SOL sol) {
		return JSCIP.getSolOrigObj(this, sol);
	}
	//SCIPcreateSol
	public SCIP_SOL createSol(SCIP_HEUR heur) {
		return JSCIP.createSol(this, heur);
	}
	//SCIPfreeSol
	public void freeSol(SCIP_SOL sol) {
		JSCIP.CALL_SCIPfreeSol(this, sol);
	}
	//SCIPsetSolVal
	public void setSolVal(SCIP_SOL sol, SCIP_VAR var, double val) {
		JSCIP.CALL_SCIPsetSolVal(this, sol, var, val);	
	}
	//SCIPtrySol
	//Returns true if solution was stored
	public boolean trySol(SCIP_SOL sol, boolean printreason, boolean completely,
			boolean checkbounds, boolean checkintegrality, boolean checklprows) {
		return JSCIP.CALL_SCIPtrySol(this, sol, printreason, completely, checkbounds,
				checkintegrality, checklprows);
	}
	//SCIPcheckSol
	//Returns true if it's feasible
	public boolean checkSol(SCIP_SOL sol, boolean printreason, boolean completely,
			boolean checkbounds, boolean checkintegrality, boolean checklprows) {
		return JSCIP.CALL_SCIPcheckSol(this, sol, printreason, completely, checkbounds,
				checkintegrality, checklprows);
	}
	//SCIPcheckSolOrig
	//Returns true if it's feasible
	public boolean checkSolOrig(SCIP_SOL sol, boolean printreason, boolean completely) {
		return JSCIP.CALL_SCIPcheckSolOrig(this, sol, printreason, completely);
	}
	/* END scip_sol.h */
	
	/* scip_solve.h */
	//SCIPenableReoptimization
	public void enableReoptimization(boolean enable) { JSCIP.CALL_SCIPenableReoptimization(this, enable); }
	//SCIPpresolve
	public void presolve() { JSCIP.CALL_SCIPpresolve(this); }
	//SCIPsolve
	public void solve() { JSCIP.CALL_SCIPsolve(this); }
	//SCIPprintOriginalProblem
	public void printOrigProblem(FILEPTR file, String extension, boolean genericnames) {
		JSCIP.CALL_SCIPprintOrigProblem(this, file, extension, genericnames);
	}
	
	/* scip_solvingstats.h */
	//SCIPprintStatistics
	public void printStatistics(FILEPTR file) { JSCIP.CALL_SCIPprintStatistics(this, file); }
	/* END scip_solvingstats.h */
	
	/* scip_tree.h */
	//SCIPgetCurrentNode
	public SCIP_NODE getCurrentNode() { return JSCIP.getCurrentNode(this); }
	//SCIPgetDepth
	public int getDepth() { return JSCIP.getDepth(this); }
	//SCIPrepropagateNode
	public void repropagateNode(SCIP_NODE node) { JSCIP.CALL_SCIPrepropagateNode(this, node); }
	/* END scip_tree.h */
	
	/* scip_var.h */
	//SCIPcreateVar
	public SCIP_VAR createVar(String name, double lb, double ub, double obj, SCIP_VARTYPE vartype,
			boolean initial, boolean removable,
			SCIP_DECL_VARDELORIG vardelorig,
			SCIP_DECL_VARTRANS vartrans,
			SCIP_DECL_VARDELTRANS vardeltrans,
			SCIP_DECL_VARCOPY varcopy,
			SCIP_VARDATA vardata) {
		return JSCIP.createVar(this, name, lb, ub, obj, vartype, initial,
				removable, vardelorig, vartrans, vardeltrans, varcopy, vardata);
	}
	//SCIPcreateVarBasic
	public SCIP_VAR createVarBasic(String name, double lb, double ub, double obj, SCIP_VARTYPE vartype) {
		return JSCIP.createVarBasic(this, name, lb, ub, obj, vartype);
	}
	//SCIPcaptureVar
	public void captureVar(SCIP_VAR var) { JSCIP.CALL_SCIPcaptureVar(this, var); }
	//SCIPreleaseVar
	public void releaseVar(SCIP_VAR var) { JSCIP.CALL_SCIPreleaseVar(this, var); }
	//SCIPgetTransformedVar
	public SCIP_VAR getTransformedVar(SCIP_VAR var) {
		return JSCIP.CALL_SCIPgetTransformedVar(this, var);
	}
	//SCIPgetNegatedVar
	public SCIP_VAR getNegatedVar(SCIP_VAR var) {
		return JSCIP.CALL_SCIPgetNegatedVar(this, var);
	}
	//SCIPflattenVarAggregationGraph
	public void flattenVarAggregationGraph(SCIP_VAR var) {
		JSCIP.CALL_SCIPflattenVarAggregationGraph(this, var);
	}
	//SCIPaddVarLocksType
	public void addVarLocksType(SCIP_VAR var, SCIP_LOCKTYPE type, int nlocksdown, int nlocksup) {
		JSCIP.CALL_SCIPaddVarLocksType(this, var, type, nlocksdown, nlocksup);
	}
	//SCIPaddVarLocks
	public void addVarLocks(SCIP_VAR var, int nlocksdown, int nlocksup) {
		JSCIP.CALL_SCIPaddVarLocks(this, var, nlocksdown, nlocksup);
	}
	//SCIPchgVarLb
	public void chgVarLb(SCIP_VAR var, double newbound) {
		JSCIP.CALL_SCIPchgVarLb(this, var, newbound);
	}
	//SCIPchgVarUb
	public void chgVarUb(SCIP_VAR var, double newbound) {
		JSCIP.CALL_SCIPchgVarUb(this, var, newbound);
	}
	//SCIPchgVarLb
	public void chgVarLbNode(SCIP_VAR var, double newbound) {
		JSCIP.CALL_SCIPchgVarLbNode(this, var, newbound);
	}
	//SCIPchgVarUb
	public void chgVarUbNode(SCIP_VAR var, double newbound) {
		JSCIP.CALL_SCIPchgVarUbNode(this, var, newbound);
	}
	//SCIPchgVarLb
	public void chgVarLbGlobal(SCIP_VAR var, double newbound) {
		JSCIP.CALL_SCIPchgVarLbGlobal(this, var, newbound);
	}
	//SCIPchgVarUb
	public void chgVarUbGlobal(SCIP_VAR var, double newbound) {
		JSCIP.CALL_SCIPchgVarUbGlobal(this, var, newbound);
	}
	//SCIPchgVarLb
	public void chgVarLbLazy(SCIP_VAR var, double newbound) {
		JSCIP.CALL_SCIPchgVarLbLazy(this, var, newbound);
	}
	//SCIPchgVarUb
	public void chgVarUbLazy(SCIP_VAR var, double newbound) {
		JSCIP.CALL_SCIPchgVarUbLazy(this, var, newbound);
	}
	//SCIPtightenVarLb
	public InferVarResult tightenVarLb(SCIP_VAR var, double newbound, boolean force) {
		return JSCIP.CALL_SCIPtightenVarLb(this, var, newbound, force);
	}
	//SCIPtightenVarUb
	public InferVarResult tightenVarUb(SCIP_VAR var, double newbound, boolean force) {
		return JSCIP.CALL_SCIPtightenVarUb(this, var, newbound, force);
	}
	//SCIPinferVarUbCons
	public InferVarResult inferVarFixCons(SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {
		return JSCIP.CALL_SCIPinferVarFixCons(this, var, fixedval, infercons, inferinfo, force);
	}
	//SCIPinferVarUbCons
	public InferVarResult inferVarLbCons(SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {
		return JSCIP.CALL_SCIPinferVarLbCons(this, var, fixedval, infercons, inferinfo, force);
	}
	//SCIPinferVarUbCons
	public InferVarResult inferVarUbCons(SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {
		return JSCIP.CALL_SCIPinferVarUbCons(this, var, fixedval, infercons, inferinfo, force);
	}
	//SCIPinferBinvarCons
	public InferVarResult inferBinvarCons(SCIP_VAR var, boolean fixedval,
			SCIP_CONS infercons, int inferinfo) {
		return JSCIP.CALL_SCIPinferBinvarCons(this, var, fixedval, infercons, inferinfo);
	}
	//SCIPfixVar
	public FixVarResult fixVar(SCIP_VAR var, double fixedval) {
		return JSCIP.CALL_SCIPfixVar(this, var, fixedval);
	}
	//SCIPprintVar
	public void printVar(SCIP_VAR var, FILEPTR file) { JSCIP.CALL_SCIPprintVar(this, var, file); }
	/* END scip_var.h */
	
	/////////
	
	//We aren't C, we don't have the idea of defining or undefining SCIP_DEBUG at the
	//top of each file to enable magical macros. Instead, we'll define one constant SCIP.DEBUG.
	//When true, calls like SCIPdebugMsg will go through, otherwise they'll be skipped.
	public static boolean DEBUG = false;
	//We emulate the magic of SCIPdebugMsg's (__FILE__, __LINE__) annotation through Java's
	//stack examination.
	public void debugMsg(String formatstr, Object... args) {
		if(!DEBUG)
			return;
		
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		String filename = caller.getFileName();
		int line = caller.getLineNumber();
		this.printDebugMessage(filename, line, formatstr, args);
	}
	//Similar to the above but doesn't print the header.
	public void debugMsgPrint(String formatstr, Object... args) {
		if(!DEBUG)
			return;
		this.debugMessagePrint(formatstr, args);
	}
	//Runs a runnable only if SCIP_DEBUG is true.
	public static void debug(Runnable runnable) {
		if(!DEBUG)
			return;
		runnable.run();
	}
}











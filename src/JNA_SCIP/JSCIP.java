package JNA_SCIP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;

import static JNA_SCIP.SCIP_RETCODE.*;

public interface JSCIP extends Library {
	public static final TypeMapper TYPE_MAPPER = new DefaultTypeMapper() {
        {
        	//Each enum needs its own TypeConverter
        	List<Class<? extends Enum<?>>> enums = List.of(
        			SCIP_BOUNDTYPE.class,
        			SCIP_BOUNDCHGTYPE.class,
        			SCIP_CONFTYPE.class,
        			SCIP_DOMCHGTYPE.class,
        			SCIP_LINCONSTYPE.class,
        			SCIP_LOCKTYPE.class,
        			SCIP_PARAMEMPHASIS.class,
        			SCIP_PARAMSETTING.class,
        			SCIP_PROPTIMING.class,
        			SCIP_RESULT.class,
        			SCIP_STAGE.class,
        			SCIP_STATUS.class,
        			SCIP_VARSTATUS.class,
        			SCIP_VARTYPE.class,
        			SCIP_VERBLEVEL.class
        	);
        	//Oh the things we do in order to get generics to stop complaining at us...
        	class TypeAdder implements Consumer<Class<? extends Enum<?>>> {
        		@Override
				public void accept(Class<? extends Enum<?>> x) {
					accept0(x);
				}
				public <T extends Enum<?>> void accept0(Class<T> x) {
					addTypeConverter(x, new SCIP_Enum_Converter<T>(x.getEnumConstants()));
				}
        		
        	};
        	enums.forEach(new TypeAdder());
        	
            //Weird enums that need a special converter, because they don't go 0...n.
            addTypeConverter(SCIP_OBJSENSE.class, SCIP_OBJSENSE.OBJSENSE_Converter.inst);
            addTypeConverter(SCIP_RETCODE.class, SCIP_RETCODE.RETCODE_Converter.inst);
            addTypeConverter(SCIP_HEURTIMING.class, SCIP_HEURTIMING.HEURTIMING_Converter.inst);
            
            //SCIP also insists on a strict 0/1 convention for booleans
            addToNativeConverter(boolean.class, new ToNativeConverter() {
                @Override
                public Object toNative(Object arg, ToNativeContext ctx) {
                    return Integer.valueOf(Boolean.TRUE.equals(arg) ? 1 : 0);
                }
                @Override
                public Class<?> nativeType() {
                    return int.class;
                }
            });
        }
    };
    
	static JSCIP LIB = Native.load("scip", JSCIP.class,
		new HashMap<String, Object>() {
			private static final long serialVersionUID = -8766823852974891689L;
		{
		    put(Library.OPTION_TYPE_MAPPER, TYPE_MAPPER);
		}
    });
	
	/* cons.h */
	String SCIPconsGetName(SCIP_CONS cons);
	static String consGetName(SCIP_CONS cons){ return LIB.SCIPconsGetName(cons); }

	int SCIPconsGetPos(SCIP_CONS cons);
	static int consGetPos(SCIP_CONS cons){ return LIB.SCIPconsGetPos(cons); }
	
	SCIP_CONSHDLR SCIPconsGetHdlr(SCIP_CONS cons);
	static SCIP_CONSHDLR consGetHdlr(SCIP_CONS cons){ return LIB.SCIPconsGetHdlr(cons); }
	
	SCIP_CONSDATA SCIPconsGetData(SCIP_CONS cons);
	static SCIP_CONSDATA consGetData(SCIP_CONS cons){ return LIB.SCIPconsGetData(cons); }
	
	int SCIPconsGetNUses(SCIP_CONS cons);
	static int consGetNUses(SCIP_CONS cons){ return LIB.SCIPconsGetNUses(cons); }
	
	boolean SCIPconsIsDeleted(SCIP_CONS cons);
	static boolean consIsDeleted(SCIP_CONS cons){ return LIB.SCIPconsIsDeleted(cons); }
	
	boolean SCIPconsIsEnabled(SCIP_CONS cons);
	static boolean consIsEnabled(SCIP_CONS cons){ return LIB.SCIPconsIsEnabled(cons); }
	
	boolean SCIPconsIsAdded(SCIP_CONS cons);
	static boolean consIsAdded(SCIP_CONS cons){ return LIB.SCIPconsIsAdded(cons); }
	
	boolean SCIPconsIsInitial(SCIP_CONS cons);
	static boolean consIsInitial(SCIP_CONS cons){ return LIB.SCIPconsIsInitial(cons); }
	
	boolean SCIPconsIsSeparated(SCIP_CONS cons);
	static boolean consIsSeparated(SCIP_CONS cons){ return LIB.SCIPconsIsSeparated(cons); }
	
	boolean SCIPconsIsEnforced(SCIP_CONS cons);
	static boolean consIsEnforced(SCIP_CONS cons){ return LIB.SCIPconsIsEnforced(cons); }
	
	boolean SCIPconsIsChecked(SCIP_CONS cons);
	static boolean consIsChecked(SCIP_CONS cons){ return LIB.SCIPconsIsChecked(cons); }
	
	boolean SCIPconsIsPropagated(SCIP_CONS cons);
	static boolean consIsPropagated(SCIP_CONS cons){ return LIB.SCIPconsIsPropagated(cons); }
	
	boolean SCIPconsIsLocal(SCIP_CONS cons);
	static boolean consIsLocal(SCIP_CONS cons){ return LIB.SCIPconsIsLocal(cons); }
	
	boolean SCIPconsIsModifiable(SCIP_CONS cons);
	static boolean consIsModifiable(SCIP_CONS cons){ return LIB.SCIPconsIsModifiable(cons); }
	
	boolean SCIPconsIsDynamic(SCIP_CONS cons);
	static boolean consIsDynamic(SCIP_CONS cons){ return LIB.SCIPconsIsDynamic(cons); }
	
	boolean SCIPconsIsRemovable(SCIP_CONS cons);
	static boolean consIsRemovable(SCIP_CONS cons){ return LIB.SCIPconsIsRemovable(cons); }
	
	boolean SCIPconsIsStickingAtNode(SCIP_CONS cons);
	static boolean consIsStickingAtNode(SCIP_CONS cons){ return LIB.SCIPconsIsStickingAtNode(cons); }
	
	boolean SCIPconsIsObsolete(SCIP_CONS cons);
	static boolean consIsObsolete(SCIP_CONS cons){ return LIB.SCIPconsIsObsolete(cons); }
	
	boolean SCIPconsIsConflict(SCIP_CONS cons);
	static boolean consIsConflict(SCIP_CONS cons){ return LIB.SCIPconsIsConflict(cons); }
	
	boolean SCIPconsIsInProb(SCIP_CONS cons);
	static boolean consIsInProb(SCIP_CONS cons){ return LIB.SCIPconsIsInProb(cons); }
	
	String SCIPconshdlrGetName(SCIP_CONSHDLR conshdlr);
	static String conshdlrGetName(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetName(conshdlr); } 
	
	String SCIPconshdlrGetDesc(SCIP_CONSHDLR conshdlr);
	static String conshdlrGetDesc(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetDesc(conshdlr); }
	
	SCIP_CONSHDLRDATA SCIPconshdlrGetData(SCIP_CONSHDLR cons);
	static SCIP_CONSHDLRDATA conshdlrGetData(SCIP_CONSHDLR cons){ return LIB.SCIPconshdlrGetData(cons); }
	
	double SCIPconshdlrGetSetupTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetSetupTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetSetupTime(conshdlr); }
	
	double SCIPconshdlrGetPresolTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetPresolTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetPresolTime(conshdlr); }
	
	double SCIPconshdlrGetSepaTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetSepaTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetSepaTime(conshdlr); }
	
	double SCIPconshdlrGetEnfoLPTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetEnfoLPTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetEnfoLPTime(conshdlr); }
	
	double SCIPconshdlrGetEnfoPSTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetEnfoPSTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetEnfoPSTime(conshdlr); }
	
	double SCIPconshdlrGetEnfoRelaxTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetEnfoRelaxTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetEnfoRelaxTime(conshdlr); }
	
	double SCIPconshdlrGetPropTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetPropTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetPropTime(conshdlr); }
	
	double SCIPconshdlrGetCheckTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetCheckTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetCheckTime(conshdlr); }
	
	double SCIPconshdlrGetRespropTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetRespropTime(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetRespropTime(conshdlr); }
	
	int SCIPconshdlrGetSepaPriority(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetSepaPriority(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetSepaPriority(conshdlr); }
	
	int SCIPconshdlrGetEnfoPriority(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetEnfoPriority(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetEnfoPriority(conshdlr); }
	
	int SCIPconshdlrGetCheckPriority(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetCheckPriority(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetCheckPriority(conshdlr); }
	
	int SCIPconshdlrGetEnfoFreq(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetEnfoFreq(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrGetEnfoFreq(conshdlr); }
	
	boolean SCIPconshdlrNeedsCons(SCIP_CONSHDLR conshdlr);
	static boolean conshdlrNeedsCons(SCIP_CONSHDLR conshdlr){ return LIB.SCIPconshdlrNeedsCons(conshdlr); }
	
	/* cons_linear.h */
	SCIP_RETCODE SCIPcreateConsLinear (SCIP scip, PointerByReference cons, String name,
			int nvars, SCIP_VAR[] vars, double[] vals, double lhs, double rhs, boolean initial,
			boolean separate, boolean enforce, boolean check, boolean propagate, boolean local,
			boolean modifiable, boolean dynamic, boolean removable, boolean stickingatnode);
	static void CALL_SCIPcreateConsLinear(SCIP scip, SCIP_CONS cons, String name,
			SCIP_VAR[] vars, double[] vals, double lhs, double rhs, boolean initial, boolean separate,
			boolean enforce, boolean check, boolean propagate, boolean local, boolean modifiable,
			boolean dynamic, boolean removable, boolean stickingatnode) {
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsLinear(scip, pref, name, vars==null?0:vars.length, vars, vals, lhs, rhs, 
				initial, separate, enforce, check, propagate, local, modifiable, dynamic, removable, stickingatnode);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsLinear(SCIP scip, String name, SCIP_VAR[] vars, double[] vals,
			double lhs, double rhs, boolean initial, boolean separate, boolean enforce, boolean check,
			boolean propagate, boolean local, boolean modifiable, boolean dynamic, boolean removable,
			boolean stickingatnode) {
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsLinear(scip, cons, name, vars, vals, lhs, rhs, initial, separate,
				enforce, check, propagate, local, modifiable, dynamic, removable, stickingatnode);
		return cons;
	}
	
	SCIP_RETCODE SCIPcreateConsBasicLinear (SCIP scip, PointerByReference cons, String name,
			int nvars, SCIP_VAR[] vars, double[] vals, double lhs, double rhs);
	static void CALL_SCIPcreateConsBasicLinear(SCIP scip, SCIP_CONS cons, String name,
			SCIP_VAR[] vars, double[] vals, double lhs, double rhs) {
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsBasicLinear(scip, pref, name, vars==null?0:vars.length, vars, vals, lhs, rhs);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsBasicLinear(SCIP scip, String name, SCIP_VAR[] vars, double[] vals, double lhs, double rhs) {
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsBasicLinear(scip, cons, name, vars, vals, lhs, rhs);
		return cons;
	}
	
	SCIP_RETCODE SCIPaddCoefLinear(SCIP scip, SCIP_CONS cons, SCIP_VAR var, double val);
	static void CALL_SCIPaddCoefLinear(SCIP scip, SCIP_CONS cons, SCIP_VAR var, double val) {
		SCIP_RETCODE ret = LIB.SCIPaddCoefLinear(scip, cons, var, val);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* cons_logicor.h */
	SCIP_RETCODE SCIPcreateConsBasicLogicor(SCIP scip, PointerByReference cons,
			String name, int nvars, SCIP_VAR[] vars);
	static void CALL_SCIPcreateConsBasicLogicor(SCIP scip, SCIP_CONS cons, String name, SCIP_VAR[] vars) {
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsBasicLogicor(scip, pref, name, vars==null?0:vars.length, vars);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsBasicLogicor(SCIP scip, String name, SCIP_VAR[] vars) {
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsBasicLogicor(scip, cons, name, vars);
		return cons;
	}
	
	/* cons_setppc.h */
	SCIP_RETCODE SCIPcreateConsBasicSetcover(SCIP scip, PointerByReference cons,
			String name, int nvars, SCIP_VAR[] vars);
	static void CALL_SCIPcreateConsBasicSetcover(SCIP scip, SCIP_CONS cons, String name,
			SCIP_VAR[] vars) {
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsBasicSetcover(scip, pref, name, vars==null?0:vars.length, vars);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsBasicSetcover(SCIP scip, String name, SCIP_VAR[] vars) {
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsBasicSetcover(scip, cons, name, vars);
		return cons;
	}
	
	/* expr_var.h */
	SCIP_RETCODE SCIPcreateExprVar(SCIP scip, PointerByReference expr, SCIP_VAR var,
			Pointer ownercreate, Pointer ownercreatedata);
	static void CALL_SCIPcreateExprVar(SCIP scip, SCIP_EXPR expr, SCIP_VAR var,
			Pointer ownercreate, Pointer ownercreatedata) {
		PointerByReference pref = pbr.get();
		pref.setValue(expr.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateExprVar(scip, pref, var, ownercreate, ownercreatedata);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		expr.setPointer(pref.getValue());
	}
	static SCIP_EXPR createExprVar(SCIP scip, SCIP_VAR var, Pointer ownercreate, Pointer ownercreatedata) {
		SCIP_EXPR expr = new SCIP_EXPR();
		CALL_SCIPcreateExprVar(scip, expr, var, ownercreate, ownercreatedata);
		return expr;
	}
	
	/* heur.h */
	double SCIPheurGetSetupTime(SCIP_HEUR scip_heur);
	static double heurGetSetupTime(SCIP_HEUR scip_heur) { return LIB.SCIPheurGetSetupTime(scip_heur); }
	
	double SCIPheurGetTime(SCIP_HEUR scip_heur);
	static double heurGetTime(SCIP_HEUR scip_heur) { return LIB.SCIPheurGetTime(scip_heur); }
	
	/* pub_fileio.h */
	FILEPTR SCIPfopen(String path, String mode);
	static FILEPTR fopen(String path, String mode) { return LIB.SCIPfopen(path, mode); }
	
	char[] SCIPfgets(char[] buf, int size, FILEPTR stream);
	//returns true at least one character is written
	static boolean fgets(char[] buf, int size, FILEPTR stream) { return LIB.SCIPfgets(buf, size, stream) != null; }
	
	/* pub_message.h */
	SCIP_RETCODE SCIPmessagehdlrCreate(
			PointerByReference messagehdlr, boolean bufferedoutput, String filename, boolean quiet,
			SCIP_DECL_MESSAGEWARNING messagewarning,
			SCIP_DECL_MESSAGEDIALOG messagedialog,
			SCIP_DECL_MESSAGEINFO messageinfo,
			SCIP_DECL_MESSAGEHDLRFREE messagehdlrfree,
			SCIP_MESSAGEHDLRDATA messagehdlrdata);
	static void CALL_SCIPmessagehdlrCreate(SCIP_MESSAGEHDLR messagehdlr,
			boolean bufferedoutput, String filename, boolean quiet,
			SCIP_DECL_MESSAGEWARNING messagewarning,
			SCIP_DECL_MESSAGEDIALOG messagedialog,
			SCIP_DECL_MESSAGEINFO messageinfo,
			SCIP_DECL_MESSAGEHDLRFREE messagehdlrfree,
			SCIP_MESSAGEHDLRDATA messagehdlrdata) {
		PointerByReference pref = pbr.get();
		pref.setValue(messagehdlr.getPointer());
		SCIP_RETCODE ret = LIB.SCIPmessagehdlrCreate(pref, bufferedoutput, filename, quiet,
			messagewarning, messagedialog, messageinfo, messagehdlrfree, messagehdlrdata);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		messagehdlr.setPointer(pref.getValue());
	}

	void SCIPmessagehdlrCapture(SCIP_MESSAGEHDLR messagehdlr);
	static void messagehdlrCapture(SCIP_MESSAGEHDLR messagehdlr) { LIB.SCIPmessagehdlrCapture(messagehdlr); }

	SCIP_RETCODE SCIPmessagehdlrRelease(SCIP_MESSAGEHDLR messagehdlr);
	static void CALL_SCIPmessagehdlrRelease(SCIP_MESSAGEHDLR messagehdlr) {
		SCIP_RETCODE ret = LIB.SCIPmessagehdlrRelease(messagehdlr);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPmessagehdlrSetData(SCIP_MESSAGEHDLR messagehdlr, SCIP_MESSAGEHDLRDATA messagehdlrdata);
	static void CALL_SCIPmessagehdlrSetData(SCIP_MESSAGEHDLR messagehdlr, SCIP_MESSAGEHDLRDATA messagehdlrdata) {
		SCIP_RETCODE ret = LIB.SCIPmessagehdlrSetData(messagehdlr, messagehdlrdata);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	void SCIPmessagehdlrSetLogfile(SCIP_MESSAGEHDLR messagehdlr, String filename);
	static void messagehdlrSetLogfile(SCIP_MESSAGEHDLR messagehdlr, String filename) {
		LIB.SCIPmessagehdlrSetLogfile(messagehdlr, filename);
	}
	
	void SCIPmessagehdlrSetQuiet(SCIP_MESSAGEHDLR messagehdlr, boolean quiet);
	static void messagehdlrSetQuiet(SCIP_MESSAGEHDLR messagehdlr, boolean quiet) {
		LIB.SCIPmessagehdlrSetQuiet(messagehdlr, quiet);
	}

	void SCIPmessagePrintInfo(SCIP_MESSAGEHDLR messagehdlr, String fmt, Object... vals);
	void SCIPmessageFPrintInfo(SCIP_MESSAGEHDLR messagehdlr, FILEPTR file, String fmt, Object... vals);
	
	void SCIPmessagePrintWarning(SCIP_MESSAGEHDLR messagehdlr, String fmt, Object... vals);
	void SCIPmessageFPrintWarning(SCIP_MESSAGEHDLR messagehdlr, FILEPTR file, String fmt, Object... vals);
	
	void SCIPmessagePrintDialog(SCIP_MESSAGEHDLR messagehdlr, String fmt, Object... vals);
	void SCIPmessageFPrintDialog(SCIP_MESSAGEHDLR messagehdlr, FILEPTR file, String fmt, Object... vals);
	
	void SCIPmessagePrintVerbInfo(SCIP_MESSAGEHDLR messagehdlr, SCIP_VERBLEVEL verblevel,
			SCIP_VERBLEVEL msgverblevel, String fmt, Object... vals);
	void SCIPmessageFPrintVerbInfo(SCIP_MESSAGEHDLR messagehdlr, SCIP_VERBLEVEL verblevel,
			SCIP_VERBLEVEL msgverblevel, FILEPTR file, String fmt, Object... vals);
	
	void SCIPmessagePrintError(String fmt, Object... vals);
	static void messagePrintError(String fmt, Object... vals) { LIB.SCIPmessagePrintError(fmt, vals); }

	void SCIPmessageSetErrorPrinting(SCIP_DECL_ERRORPRINTING errorprinting, Pointer data);
	static void messageSetErrorPrinting(SCIP_DECL_ERRORPRINTING errorprinting, Pointer data) {
		LIB.SCIPmessageSetErrorPrinting(errorprinting, data);
	}
	
	void SCIPmessageSetErrorPrintingDefault();
	static void messageSetErrorPrintingDefault() { LIB.SCIPmessageSetErrorPrintingDefault(); }
	
	SCIP_MESSAGEHDLRDATA SCIPmessagehdlrGetData(SCIP_MESSAGEHDLR messagehdlr);
	static SCIP_MESSAGEHDLRDATA messagehdlrGetData(SCIP_MESSAGEHDLR messagehdlr) {
		return LIB.SCIPmessagehdlrGetData(messagehdlr);
	}
	
	FILEPTR SCIPmessagehdlrGetLogfile(SCIP_MESSAGEHDLR messagehdlr);
	static FILEPTR messagehdlrGetLogfile(SCIP_MESSAGEHDLR messagehdlr) {
		return LIB.SCIPmessagehdlrGetLogfile(messagehdlr);
	}
	
	boolean SCIPmessagehdlrIsQuiet(SCIP_MESSAGEHDLR messagehdlr);
	static boolean messagehdlrIsQuiet(SCIP_MESSAGEHDLR messagehdlr) {
		return LIB.SCIPmessagehdlrIsQuiet(messagehdlr);
	}
	/* END pub_message.h */
	
	/* pub_var.c */
	String SCIPvarGetName(SCIP_VAR var);
	static String varGetName(SCIP_VAR var) { return LIB.SCIPvarGetName(var); }
	
	int SCIPvarGetNUses(SCIP_VAR var);
	static int varGetNUses(SCIP_VAR var) { return LIB.SCIPvarGetNUses(var); }
	
	SCIP_VARTYPE SCIPvarGetType(SCIP_VAR var);
	static SCIP_VARTYPE varGetType(SCIP_VAR var) { return LIB.SCIPvarGetType(var); }
	
	SCIP_VARSTATUS SCIPvarGetStatus(SCIP_VAR var);
	static SCIP_VARSTATUS varGetStatus(SCIP_VAR var) { return LIB.SCIPvarGetStatus(var); }
	
	double SCIPvarGetObj(SCIP_VAR var);
	static double varGetObj(SCIP_VAR var) { return LIB.SCIPvarGetObj(var); }
	
	double SCIPvarGetLbLocal(SCIP_VAR var);
	static double varGetLbLocal(SCIP_VAR var) { return LIB.SCIPvarGetLbLocal(var); }
	
	double SCIPvarGetUbLocal(SCIP_VAR var);
	static double varGetUbLocal(SCIP_VAR var) { return LIB.SCIPvarGetUbLocal(var); }
	
	double SCIPvarGetLbGlobal(SCIP_VAR var);
	static double varGetLbGlobal(SCIP_VAR var) { return LIB.SCIPvarGetLbGlobal(var); }
	
	double SCIPvarGetUbGlobal(SCIP_VAR var);
	static double varGetUbGlobal(SCIP_VAR var) { return LIB.SCIPvarGetUbGlobal(var); }
	
	double SCIPvarGetLbOriginal(SCIP_VAR var);
	static double varGetLbOriginal(SCIP_VAR var) { return LIB.SCIPvarGetLbOriginal(var); }
	
	double SCIPvarGetUbOriginal(SCIP_VAR var);
	static double varGetUbOriginal(SCIP_VAR var) { return LIB.SCIPvarGetUbOriginal(var); }
	
	double SCIPvarGetLbLazy(SCIP_VAR var);
	static double varGetLbLazy(SCIP_VAR var) { return LIB.SCIPvarGetLbLazy(var); }
	
	double SCIPvarGetUbLazy(SCIP_VAR var);
	static double varGetUbLazy(SCIP_VAR var) { return LIB.SCIPvarGetUbLazy(var); }
	
	double SCIPvarGetLPSol(SCIP_VAR var);
	static double varGetLPSol(SCIP_VAR var) { return LIB.SCIPvarGetLPSol(var); }
	
	double SCIPvarGetNLPSol(SCIP_VAR var);
	static double varGetNLPSol(SCIP_VAR var) { return LIB.SCIPvarGetNLPSol(var); }
	
	double SCIPvarGetPseudoSol(SCIP_VAR var);
	static double varGetPseudoSol(SCIP_VAR var) { return LIB.SCIPvarGetPseudoSol(var); }
	
	double SCIPvarGetSol(SCIP_VAR var, boolean lp);
	static double varGetSol(SCIP_VAR var, boolean lp) { return LIB.SCIPvarGetSol(var, lp); }
	
	double SCIPvarGetRootSol(SCIP_VAR var);
	static double varGetRootSol(SCIP_VAR var) { return LIB.SCIPvarGetRootSol(var); }
	
	double SCIPvarGetAvgSol(SCIP_VAR var);
	static double varGetAvgSol(SCIP_VAR var) { return LIB.SCIPvarGetAvgSol(var); }
	
	double SCIPvarGetLbAtIndex(SCIP_VAR var, SCIP_BDCHGIDX bdchgidx, boolean after);
	static double varGetLbAtIndex(SCIP_VAR var, SCIP_BDCHGIDX bdchgidx, boolean after) {
		return LIB.SCIPvarGetLbAtIndex(var, bdchgidx, after);
	}

	double SCIPvarGetUbAtIndex(SCIP_VAR var, SCIP_BDCHGIDX bdchgidx, boolean after);
	static double varGetUbAtIndex(SCIP_VAR var, SCIP_BDCHGIDX bdchgidx, boolean after) {
		return LIB.SCIPvarGetUbAtIndex(var, bdchgidx, after);
	}

	double SCIPvarGetBdAtIndex(SCIP_VAR var, SCIP_BOUNDTYPE bdtype, SCIP_BDCHGIDX bdchgidx, boolean after);
	static double varGetBdAtIndex(SCIP_VAR var, SCIP_BOUNDTYPE bdtype, SCIP_BDCHGIDX bdchgidx, boolean after) {
		return LIB.SCIPvarGetBdAtIndex(var, bdtype, bdchgidx, after);
	}
	

	
	/* scipdefplugins.h */
	SCIP_RETCODE SCIPincludeDefaultPlugins(SCIP scip);
	static void CALL_SCIPincludeDefaultPlugins(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPincludeDefaultPlugins(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scipshell.h */
	//We pad this a +1 to the argc and a dummy "java" to argv.
	//SCIP is written in C and expecting argv[0] to be the program itself, which differs from
	//java's convention.
	SCIP_RETCODE SCIPprocessShellArguments(SCIP scip, int argc, String[] argv, String settings);
	static void CALL_SCIPprocessShellArguments(SCIP scip, String[] argv, String settings) {
		String[] newArgv = new String[argv.length+1];
		System.arraycopy(argv, 0, newArgv, 1, argv.length);
		newArgv[0] = "java";
		int argc = newArgv.length;
		SCIP_RETCODE ret = LIB.SCIPprocessShellArguments(scip, argc, newArgv, settings);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scip_conflict.h */
	SCIP_RETCODE SCIPinitConflictAnalysis(SCIP scip, SCIP_CONFTYPE conftype, boolean iscutoffinvolved);
	static void CALL_SCIPinitConflictAnalysis(SCIP scip, SCIP_CONFTYPE conftype, boolean iscutoffinvolved) {
		SCIP_RETCODE ret = LIB.SCIPinitConflictAnalysis(scip, conftype, iscutoffinvolved);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPaddConflictLb(SCIP scip, SCIP_VAR var, SCIP_BDCHGIDX chgidx);
	static void CALL_SCIPaddConflictLb(SCIP scip, SCIP_VAR var, SCIP_BDCHGIDX chgidx) {
		SCIP_RETCODE ret = LIB.SCIPaddConflictLb(scip, var, chgidx);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPaddConflictUb(SCIP scip, SCIP_VAR var, SCIP_BDCHGIDX chgidx);
	static void CALL_SCIPaddConflictUb(SCIP scip, SCIP_VAR var, SCIP_BDCHGIDX chgidx) {
		SCIP_RETCODE ret = LIB.SCIPaddConflictUb(scip, var, chgidx);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPaddConflictBinvar(SCIP scip, SCIP_VAR var);
	static void CALL_SCIPaddConflictBinvar(SCIP scip, SCIP_VAR var) {
		SCIP_RETCODE ret = LIB.SCIPaddConflictBinvar(scip, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPanalyzeConflict(SCIP scip, int validdepth, ByteByReference success);
	static boolean CALL_SCIPanalyzeConflict(SCIP scip, int validdepth) {
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPanalyzeConflict(scip, validdepth, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	SCIP_RETCODE SCIPanalyzeConflictCons(SCIP scip, SCIP_CONS cons, ByteByReference success);
	static boolean CALL_SCIPanalyzeConflictCons(SCIP scip, SCIP_CONS cons) {
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPanalyzeConflictCons(scip, cons, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	/* scip_cons.h */
	SCIP_RETCODE SCIPincludeConshdlrBasic(SCIP scip,
			PointerByReference conshdlrptr, String name, String desc,
			int enfopriority, int chckpriority, int eagerfreq, boolean needscons,
			SCIP_DECL_CONSENFOLP consenfolp,
			SCIP_DECL_CONSENFOPS consenfops,
			SCIP_DECL_CONSCHECK conscheck,
			SCIP_DECL_CONSLOCK conslock,
			SCIP_CONSHDLRDATA conshdlrdata 
		);
	static SCIP_CONSHDLR CALL_SCIPincludeConshdlrBasic(SCIP scip,
			String name, String desc,
			int enfopriority, int chckpriority, int eagerfreq, boolean needscons,
			SCIP_DECL_CONSENFOLP consenfolp,
			SCIP_DECL_CONSENFOPS consenfops,
			SCIP_DECL_CONSCHECK conscheck,
			SCIP_DECL_CONSLOCK conslock,
			SCIP_CONSHDLRDATA conshdlrdata 
		) {
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPincludeConshdlrBasic(scip, pref, name, desc,
				enfopriority, chckpriority, eagerfreq, needscons, consenfolp, consenfops,
				conscheck, conslock, conshdlrdata);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_CONSHDLR(pref.getValue());
	}
	
	SCIP_RETCODE SCIPsetConshdlrEnforelax(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSENFORELAX consenforelax);
	static void CALL_SCIPsetConshdlrEnforelax(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSENFORELAX consenforelax) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrEnforelax(scip, conshdlr, consenforelax);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	
	SCIP_RETCODE SCIPsetConshdlrCopy(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSHDLRCOPY conshdlrcopy, SCIP_DECL_CONSCOPY conscopy);
	static void CALL_SCIPsetConshdlrCopy(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSHDLRCOPY conshdlrcopy, SCIP_DECL_CONSCOPY conscopy) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrCopy(scip, conshdlr, conshdlrcopy, conscopy);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConshdlrTrans(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSTRANS constrans);
	static void CALL_SCIPsetConshdlrTrans(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSTRANS constrans) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrTrans(scip, conshdlr, constrans);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConshdlrExit(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSEXIT consexit);
	static void CALL_SCIPsetConshdlrExit(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSEXIT consexit) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrExit(scip, conshdlr, consexit);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConshdlrSepa(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSSEPALP conssepalp,
			SCIP_DECL_CONSSEPASOL conssepasol, int sepafreq, int sepapriority, boolean delaysepa);
	static void CALL_SCIPsetConshdlrSepa(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSSEPALP conssepalp,
			SCIP_DECL_CONSSEPASOL conssepasol, int sepafreq, int sepapriority, boolean delaysepa) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrSepa(scip, conshdlr, conssepalp, conssepasol, sepafreq,
				sepapriority, delaysepa);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConshdlrProp(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSPROP consprop,
			int propfreq, boolean delayprop, SCIP_PROPTIMING proptiming);
	static void CALL_SCIPsetConshdlrProp(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSPROP consprop,
			int propfreq, boolean delayprop, SCIP_PROPTIMING proptiming) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrProp(scip, conshdlr, consprop, propfreq, delayprop, proptiming);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPsetConshdlrResprop(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSRESPROP consresprop);
	static void CALL_SCIPsetConshdlrResprop(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSRESPROP consresprop) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrResprop(scip, conshdlr, consresprop);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPsetConshdlrDelete(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSDELETE consDelete);
	static void CALL_SCIPsetConshdlrDelete(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSDELETE consDelete) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrDelete(scip, conshdlr, consDelete);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPsetConshdlrInitlp(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSINITLP consinitlp);
	static void CALL_SCIPsetConshdlrInitlp(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSINITLP consinitlp) {
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrInitlp(scip, conshdlr, consinitlp);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_CONSHDLR SCIPfindConshdlr(SCIP scip, String name);
	static SCIP_CONSHDLR findConshdlr(SCIP scip, String name) { return LIB.SCIPfindConshdlr(scip, name); }

	SCIP_RETCODE SCIPcreateCons(SCIP scip, PointerByReference cons, String name, SCIP_CONSHDLR conshdlr,
			Pointer consdata, boolean initial, boolean separate, boolean enforce, boolean check,
			boolean propagate, boolean local, boolean modifiable, boolean dynamic, boolean removable,
			boolean stickingatnode);
	static void CALL_SCIPcreateCons(SCIP scip, SCIP_CONS cons, String name, SCIP_CONSHDLR conshdlr,
			Pointer consdata, boolean initial, boolean separate, boolean enforce, boolean check,
			boolean propagate, boolean local, boolean modifiable, boolean dynamic, boolean removable,
			boolean stickingatnode) {
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateCons(scip, pref, name, conshdlr, consdata, initial, separate,
				enforce, check, propagate, local, modifiable, dynamic, removable, stickingatnode);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createCons(SCIP scip, String name, SCIP_CONSHDLR conshdlr,
			Pointer consdata, boolean initial, boolean separate, boolean enforce, boolean check,
			boolean propagate, boolean local, boolean modifiable, boolean dynamic, boolean removable,
			boolean stickingatnode) {
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateCons(scip, cons, name, conshdlr, consdata, initial, separate, enforce, check,
				propagate, local, modifiable, dynamic, removable, stickingatnode);
		return cons;
	}
	
	SCIP_RETCODE SCIPcaptureCons(SCIP scip, SCIP_CONS cons);
	static void CALL_SCIPcaptureCons(SCIP scip, SCIP_CONS cons) {
		SCIP_RETCODE ret = LIB.SCIPcaptureCons(scip, cons);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPreleaseCons(SCIP scip, PointerByReference cons);
	static void CALL_SCIPreleaseCons(SCIP scip, SCIP_CONS cons) {
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPreleaseCons(scip, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		//cons.setPointer(pref.getValue()); //always returns null
	}
	
	/* scip_copy.h */
	
	//This has essentially *three* return values. The RETCODE which we check (and throw) as usual, the
	//SCIP_VAR copied, and a boolean indicating success. If there's no success, we indicate this by
	//returning null.
	//We put this as a member function of sourcevar, not sourcescip. This does break our convention but
	//would otherwise be liable to mixing up sourcescip and targetscip. It can be found under
	//SCIP_VAR.getCopy().
	SCIP_RETCODE SCIPgetVarCopy(SCIP sourcescip, SCIP targetscip, SCIP_VAR sourcevar, PointerByReference targetvar,//SCIP_VAR**
		SCIP_HASHMAP varmap, SCIP_HASHMAP consmap, boolean global, ByteByReference success);
	static SCIP_VAR CALL_SCIPgetVarCopy(SCIP sourcescip, SCIP targetscip, SCIP_VAR sourcevar,
			SCIP_HASHMAP varmap, SCIP_HASHMAP consmap, boolean global) {
		PointerByReference pref = pbr.get();
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPgetVarCopy(sourcescip, targetscip, sourcevar, pref, varmap,
				consmap, global, bref);
		pbr.free(pref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		boolean success = (bref.getValue() != 0);
		if(!success)
			return null;
		else
			return new SCIP_VAR(pref.getValue());
	}
	
	int SCIPgetSubscipDepth(SCIP scip);
	static int getSubscipDepth(SCIP scip) {
		return LIB.SCIPgetSubscipDepth(scip);
	}
	
	/* scip_cut.h */
	SCIP_RETCODE SCIPaddRow(SCIP scip, SCIP_ROW row, boolean forcecut, ByteByReference infeasible);
	//Returns true if the row rendered problem infeasible 
	static boolean CALL_SCIPaddRow(SCIP scip, SCIP_ROW row, boolean forcecut) {
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPaddRow(scip, row, forcecut, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	SCIP_RETCODE SCIPaddPoolCut(SCIP scip, SCIP_ROW row);
	static void CALL_SCIPaddPoolCut(SCIP scip, SCIP_ROW row) {
		SCIP_RETCODE ret = LIB.SCIPaddPoolCut(scip, row);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return;
	}
	
	boolean SCIPisCutEfficacious(SCIP scip, SCIP_SOL sol, SCIP_ROW cut);
	static boolean isCutEfficacious(SCIP scip, SCIP_SOL sol, SCIP_ROW cut) {
		return LIB.SCIPisCutEfficacious(scip, sol, cut);
	}
	
	boolean SCIPisEfficacious(SCIP scip, double efficacy);
	static boolean isEfficacious(SCIP scip, double efficacy) {
		return LIB.SCIPisEfficacious(scip, efficacy);
	}
	/* END scip_cut.h */
	
	/* scip_dialog.h */
	SCIP_RETCODE SCIPstartInteraction(SCIP scip);
	static void CALL_SCIPstartInteraction(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPstartInteraction(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	static void startInteraction(SCIP scip) { CALL_SCIPstartInteraction(scip); }
	
	/* scip_general.h */
	SCIP_RETCODE SCIPcreate(PointerByReference scip);//SCIP_RETCODE
    static void CALL_SCIPcreate(SCIP scip) {
		PointerByReference pref = pbr.get();
    	pref.setValue(scip.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreate(pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		scip.setPointer(pref.getValue());
    }
    
    SCIP_RETCODE SCIPfree(PointerByReference scip);//SCIP_RETCODE
    static void CALL_SCIPfree(SCIP scip) {
		PointerByReference pref = pbr.get();
    	pref.setValue(scip.getPointer());
		SCIP_RETCODE ret = LIB.SCIPfree(pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		scip.setPointer(pref.getValue()); //NOTE: always returns null
    }
    static void free(SCIP scip) { CALL_SCIPfree(scip); }
    
    SCIP_STAGE SCIPgetStage(SCIP scip);
    static SCIP_STAGE getStage(SCIP scip) { return LIB.SCIPgetStage(scip); }
    
	void SCIPprintVersion(SCIP scip, FILEPTR file);
	static void printVersion(SCIP scip, FILEPTR file) { LIB.SCIPprintVersion(scip, file); }
	
	SCIP_STATUS SCIPgetStatus(SCIP scip);
	static SCIP_STATUS getStatus(SCIP scip) { return LIB.SCIPgetStatus(scip); }
	
	boolean SCIPisTransformed(SCIP scip);
	static boolean isTransformed(SCIP scip) { return LIB.SCIPisTransformed(scip); }
	
	/* scip_lp.h */
	
	boolean SCIPhasCurrentNodeLP(SCIP scip);
	static boolean hasCurrentNodeLP(SCIP scip) { return LIB.SCIPhasCurrentNodeLP(scip); }

	boolean SCIPisLPConstructed(SCIP scip);
	static boolean isLPConstructed(SCIP scip) { return LIB.SCIPisLPConstructed(scip); }

	SCIP_RETCODE SCIPconstructLP(SCIP scip, ByteByReference cutoff);
	static boolean CALL_SCIPconstructLP(SCIP scip) {
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPconstructLP(scip, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	Pointer SCIPgetLPRows(SCIP scip);
	static SCIP_ROW[] getLPRows(SCIP scip) {
		int n = getNLPRows(scip);
		Pointer scip_row_pp = LIB.SCIPgetLPRows(scip);
		if(scip_row_pp == null)
			return null;
		Pointer[] scip_row_arr = scip_row_pp.getPointerArray(0, n);
		SCIP_ROW[] ret = new SCIP_ROW[n];
		for(int i=0; i<n; i++) {
			ret[i] = new SCIP_ROW(scip_row_arr[i]);
		}
		return ret;
	}
	
	int SCIPgetNLPRows(SCIP scip);
	static int getNLPRows(SCIP scip) {
		return LIB.SCIPgetNLPRows(scip);
	}
	
	SCIP_RETCODE SCIPcreateEmptyRowConshdlr(SCIP scip, PointerByReference row, SCIP_CONSHDLR conshdlr,
			String name, double lhs, double rhs, boolean local, boolean modifiable,
			boolean removable);
	static void CALL_SCIPcreateEmptyRowConshdlr(SCIP scip, SCIP_ROW row, SCIP_CONSHDLR conshdlr,
			String name, double lhs, double rhs, boolean local, boolean modifiable,
			boolean removable) {
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPcreateEmptyRowConshdlr(scip, pref, conshdlr, name, lhs, rhs, local, modifiable, removable);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		row.setPointer(pref.getValue());
	}
	static SCIP_ROW createEmptyRowConshdlr(SCIP scip, SCIP_CONSHDLR conshdlr, String name, double lhs,
			double rhs, boolean local, boolean modifiable, boolean removable) {
		SCIP_ROW row = new SCIP_ROW();
		CALL_SCIPcreateEmptyRowConshdlr(scip, row, conshdlr, name, lhs, rhs, local, modifiable, removable);
		return row;
	}
	
	SCIP_RETCODE SCIPaddVarToRow(SCIP scip, SCIP_ROW row, SCIP_VAR var, double val);
	static void CALL_SCIPaddVarToRow(SCIP scip, SCIP_ROW row, SCIP_VAR var, double val) {
		SCIP_RETCODE ret = LIB.SCIPaddVarToRow(scip, row, var, val);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPreleaseRow(SCIP scip, PointerByReference row);
	static void CALL_SCIPreleaseRow(SCIP scip, SCIP_ROW row) {
		PointerByReference pref = pbr.get();
		pref.setValue(row.getPointer());
		SCIP_RETCODE ret = LIB.SCIPreleaseRow(scip, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
//		row.setPointer(pref.getValue());//always returns null
	}
	
	SCIP_RETCODE SCIPcacheRowExtensions(SCIP scip, SCIP_ROW row);
	static void CALL_SCIPcacheRowExtensions(SCIP scip, SCIP_ROW row) {
		SCIP_RETCODE ret = LIB.SCIPcacheRowExtensions(scip, row);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPflushRowExtensions(SCIP scip, SCIP_ROW row);
	static void CALL_SCIPflushRowExtensions(SCIP scip, SCIP_ROW row) {
		SCIP_RETCODE ret = LIB.SCIPflushRowExtensions(scip, row);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPprintRow(SCIP scip, SCIP_ROW row, FILEPTR file);
	static void CALL_SCIPprintRow(SCIP scip, SCIP_ROW row, FILEPTR file) {
		SCIP_RETCODE ret = LIB.SCIPprintRow(scip, row, file);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPstartDive(SCIP scip);
	static void CALL_SCIPstartDive(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPstartDive(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPendDive(SCIP scip);
	static void CALL_SCIPendDive(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPendDive(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgCutoffboundDive(SCIP scip, double newcutoffbound);
	static void CALL_SCIPchgCutoffboundDive(SCIP scip, double newcutoffbound) {
		SCIP_RETCODE ret = LIB.SCIPchgCutoffboundDive(scip, newcutoffbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	boolean SCIPinDive(SCIP scip);
	static boolean inDive(SCIP scip) { return LIB.SCIPinDive(scip); }
	
	
	/* scip_heur.h */
	SCIP_RETCODE SCIPincludeHeurBasic(SCIP scip, PointerByReference scip_heur, String name, String desc,
			byte dispchar, int priority, int freq, int freqofs, int maxdepth, SCIP_HEURTIMING timingmask,
			boolean usessubscip, SCIP_DECL_HEUREXEC heurexec, SCIP_HEURDATA heurdata);
	static void CALL_SCIPincludeHeurBasic(SCIP scip, SCIP_HEUR scip_heur, String name, String desc,
			byte dispchar, int priority, int freq, int freqofs, int maxdepth, SCIP_HEURTIMING timingmask,
			boolean usessubscip, SCIP_DECL_HEUREXEC heurexec, SCIP_HEURDATA heurdata) {
		PointerByReference pref = pbr.get();
		pref.setValue(scip_heur.getPointer());
		SCIP_RETCODE ret = LIB.SCIPincludeHeurBasic(scip, pref, name, desc, dispchar, priority, freq,
				freqofs, maxdepth, timingmask, usessubscip, heurexec, heurdata);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		scip_heur.setPointer(pref.getValue());
	}
	static SCIP_HEUR includeHeurBasic(SCIP scip, String name, String desc, byte dispchar, int priority,
			int freq, int freqofs, int maxdepth, SCIP_HEURTIMING timingmask, boolean usessubscip,
			SCIP_DECL_HEUREXEC heurexec, SCIP_HEURDATA heurdata) {
		SCIP_HEUR scip_heur = new SCIP_HEUR();
		CALL_SCIPincludeHeurBasic(scip, scip_heur, name, desc, dispchar, priority, freq, freqofs, maxdepth,
				timingmask, usessubscip, heurexec, heurdata);
		return scip_heur;
	}
	
	//TODO make a wrapper class for these
	SCIP_RETCODE SCIPsetHeurCopy(SCIP scip, SCIP_HEUR heur, SCIP_DECL_HEURCOPY heurcopy);
	SCIP_RETCODE SCIPsetHeurFree(SCIP scip, SCIP_HEUR heur, SCIP_DECL_HEURFREE heurfree);
	SCIP_RETCODE SCIPsetHeurInit(SCIP scip, SCIP_HEUR heur, SCIP_DECL_HEURINIT heurinit);
	SCIP_RETCODE SCIPsetHeurExit(SCIP scip, SCIP_HEUR heur, SCIP_DECL_HEUREXIT heurexit);
	SCIP_RETCODE SCIPsetHeurInitsol(SCIP scip, SCIP_HEUR heur, SCIP_DECL_HEURINITSOL heurinitsol);
	SCIP_RETCODE SCIPsetHeurExitsol(SCIP scip, SCIP_HEUR heur, SCIP_DECL_HEUREXITSOL heurexitsol);
	
	/* scip_mem.h */
	Pointer SCIPblkmem(SCIP scip);
	static Pointer blkmem(SCIP scip) { return LIB.SCIPblkmem(scip); }
	
	/* scip_message.h */
	SCIP_RETCODE SCIPsetMessagehdlr(SCIP scip, SCIP_MESSAGEHDLR messagehdlr);
	static void CALL_SCIPsetMessagehdlr(SCIP scip, SCIP_MESSAGEHDLR messagehdlr) {
		SCIP_RETCODE ret = LIB.SCIPsetMessagehdlr(scip, messagehdlr);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_MESSAGEHDLR SCIPgetMessagehdlr(SCIP scip);
	static SCIP_MESSAGEHDLR getMessagehdlr(SCIP scip) {
		return LIB.SCIPgetMessagehdlr(scip);
	}
	
	void SCIPsetMessagehdlrLogfile(SCIP scip, String filename);
	static void setMessagehdlrLogfile(SCIP scip, String filename) {
		LIB.SCIPsetMessagehdlrLogfile(scip, filename);
	}
	
	void SCIPsetMessagehdlrQuiet(SCIP scip, boolean quiet);
	static void setMessagehdlrQuiet(SCIP scip, boolean quiet) {
		LIB.SCIPsetMessagehdlrQuiet(scip, quiet);
	}

	void SCIPwarningMessage(SCIP scip, String formatstr, Object... vals);
	static void warningMessage(SCIP scip, String formatstr, Object... vals) {
		LIB.SCIPwarningMessage(scip, formatstr, vals);
	}

	void SCIPdebugMessage(SCIP scip, String sourcefile, int sourceline, String formatstr, Object... vals);
	static void debugMessage(SCIP scip, String sourcefile, int sourceline,  String formatstr, Object... vals) {
		LIB.SCIPdebugMessage(scip, sourcefile, sourceline, formatstr, vals);
	}
	
	void SCIPprintDebugMessage(SCIP scip, String file, int line, String formatstr, Object... vals);
	static void printDebugMessage(SCIP scip, String file, int line, String formatstr, Object... vals) {
		LIB.SCIPprintDebugMessage(scip, file, line, formatstr, vals);
	}

	void SCIPdebugMessagePrint(SCIP scip, String formatstr, Object... vals);
	static void debugMessagePrint(SCIP scip, String formatstr, Object... vals) {
		LIB.SCIPdebugMessagePrint(scip, formatstr, vals);
	}
	
	void SCIPdialogMessage(SCIP scip, FILEPTR file, String formatstr, Object... vals);
	static void dialogMessage(SCIP scip, FILEPTR file, String formatstr, Object... vals) {
		LIB.SCIPdialogMessage(scip, file, formatstr, vals);
	}
	
	void SCIPinfoMessage(SCIP scip, FILEPTR file, String formatstr, Object... vals);
	static void infoMessage(SCIP scip, FILEPTR file, String formatstr, Object... vals) {
		LIB.SCIPinfoMessage(scip, file, formatstr, vals);
	}
	
	void SCIPverbMessage(SCIP scip, SCIP_VERBLEVEL verblevel, FILEPTR file, String formatstr, Object... vals);
	static void verbMessage(SCIP scip, SCIP_VERBLEVEL verblevel, FILEPTR file, String formatstr, Object... vals) {
		LIB.SCIPverbMessage(scip, verblevel, file, formatstr, vals);
	}

	SCIP_VERBLEVEL SCIPgetVerbLevel(SCIP scip);//SCIP_VERBLEVEL
	static SCIP_VERBLEVEL getVerbLevel(SCIP scip) { return LIB.SCIPgetVerbLevel(scip); };
	/* END scip_message.h */
	
	/* scip_numerics.h */
	double SCIPinfinity(SCIP scip);
	static double infinity(SCIP scip) { return LIB.SCIPinfinity(scip); }
	
	boolean SCIPisFeasEQ(SCIP scip, double x, double y);
	static boolean isFeasEQ(SCIP scip, double x, double y) {
		return LIB.SCIPisFeasEQ(scip, x, y);
	}

	boolean SCIPisFeasLT(SCIP scip, double x, double y);
	static boolean isFeasLT(SCIP scip, double x, double y) {
		return LIB.SCIPisFeasLT(scip, x, y);
	}

	boolean SCIPisFeasLE(SCIP scip, double x, double y);
	static boolean isFeasLE(SCIP scip, double x, double y) {
		return LIB.SCIPisFeasLE(scip, x, y);
	}

	boolean SCIPisFeasGT(SCIP scip, double x, double y);
	static boolean isFeasGT(SCIP scip, double x, double y) {
		return LIB.SCIPisFeasGT(scip, x, y);
	}

	boolean SCIPisFeasGE(SCIP scip, double x, double y);
	static boolean isFeasGE(SCIP scip, double x, double y) {
		return LIB.SCIPisFeasGE(scip, x, y);
	}

	boolean SCIPisFeasZero(SCIP scip, double x);
	static boolean isFeasZero(SCIP scip, double x) {
		return LIB.SCIPisFeasZero(scip, x);
	}

	boolean SCIPisFeasPositive(SCIP scip, double x);
	static boolean isFeasPositive(SCIP scip, double x) {
		return LIB.SCIPisFeasPositive(scip, x);
	}

	boolean SCIPisFeasNegative(SCIP scip, double x);
	static boolean isFeasNegative(SCIP scip, double x) {
		return LIB.SCIPisFeasNegative(scip, x);
	}

	boolean SCIPisFeasIntegral(SCIP scip, double x);
	static boolean isFeasIntegral(SCIP scip, double x) {
		return LIB.SCIPisFeasIntegral(scip, x);
	}
	/* END scip_numerics.h */
	
	/* scip_param.h */
	SCIP_RETCODE SCIPsetRealParam(SCIP scip, String name, double value);
	static void CALL_SCIPsetRealParam(SCIP scip, String name, double value) {
		SCIP_RETCODE ret = LIB.SCIPsetRealParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetCharParam(SCIP scip, String name, byte value);
	static void CALL_SCIPsetCharParam(SCIP scip, String name, byte value) {
		SCIP_RETCODE ret = LIB.SCIPsetCharParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};

	//this is actually a bool value, but SCIP is picky about bools being 0 or 1
	SCIP_RETCODE SCIPsetBoolParam(SCIP scip, String name, int value);
	static void CALL_SCIPsetBoolParam(SCIP scip, String name, boolean value) {
		SCIP_RETCODE ret = LIB.SCIPsetBoolParam(scip, name, value?1:0);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};

	SCIP_RETCODE SCIPsetIntParam(SCIP scip, String name, int value);
	static void CALL_SCIPsetIntParam(SCIP scip, String name, int value) {
		SCIP_RETCODE ret = LIB.SCIPsetIntParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};

	SCIP_RETCODE SCIPsetLongintParam(SCIP scip, String name, long value);
	static void CALL_SCIPsetLongintParam(SCIP scip, String name, long value) {
		SCIP_RETCODE ret = LIB.SCIPsetLongintParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetStringParam(SCIP scip, String name, String value);
	static void CALL_SCIPsetStringParam(SCIP scip, String name, String value) {
		SCIP_RETCODE ret = LIB.SCIPsetStringParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetEmphasis(SCIP scip, SCIP_PARAMEMPHASIS emph, boolean quiet);
	static void CALL_SCIPsetEmphasis(SCIP scip, SCIP_PARAMEMPHASIS emph, boolean quiet) {
		SCIP_RETCODE ret = LIB.SCIPsetEmphasis(scip, emph, quiet);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetPresolving(SCIP scip, SCIP_PARAMSETTING emph, boolean quiet);
	static void CALL_SCIPsetPresolving(SCIP scip, SCIP_PARAMSETTING emph, boolean quiet) {
		SCIP_RETCODE ret = LIB.SCIPsetPresolving(scip, emph, quiet);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	/* scip_prob.h */
	SCIP_RETCODE SCIPcreateProb(SCIP scip, String name,
			SCIP_DECL_PROBDELORIG probdelorig,
			SCIP_DECL_PROBTRANS probtrans,
			SCIP_DECL_PROBDELTRANS probdeltrans,
			SCIP_DECL_PROBINITSOL probinitsol,
			SCIP_DECL_PROBEXITSOL probexitsol,
			SCIP_DECL_PROBCOPY probcopy,
			SCIP_PROBDATA probdata
			);
	static void CALL_SCIPcreateProb(SCIP scip, String name,
			SCIP_DECL_PROBDELORIG probdelorig,
			SCIP_DECL_PROBTRANS probtrans,
			SCIP_DECL_PROBDELTRANS probdeltrans,
			SCIP_DECL_PROBINITSOL probinitsol,
			SCIP_DECL_PROBEXITSOL probexitsol,
			SCIP_DECL_PROBCOPY probcopy,
			SCIP_PROBDATA probdata
			) {
		SCIP_RETCODE ret = LIB.SCIPcreateProb(scip, name, probdelorig, probtrans, probdeltrans,
				probinitsol, probexitsol, probcopy, probdata);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPcreateProbBasic(SCIP scip, String name);
	static void CALL_SCIPcreateProbBasic(SCIP scip, String name) {
		SCIP_RETCODE ret = LIB.SCIPcreateProbBasic(scip, name);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPreadProb(SCIP scip, String filename, String ext);
	static void CALL_SCIPreadProb(SCIP scip, String filename, String ext) {
		SCIP_RETCODE ret = LIB.SCIPreadProb(scip, filename, ext);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPfreeProb(SCIP scip);
	static void CALL_SCIPfreeProb(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPfreeProb(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetObjsense(SCIP scip, SCIP_OBJSENSE objsense);
	static void CALL_SCIPsetObjsense(SCIP scip, SCIP_OBJSENSE objsense) {
		SCIP_RETCODE ret = LIB.SCIPsetObjsense(scip, objsense);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPaddVar(SCIP scip, SCIP_VAR var);
	static void CALL_SCIPaddVar(SCIP scip, SCIP_VAR var) {
		SCIP_RETCODE ret = LIB.SCIPaddVar(scip, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPaddCons(SCIP scip, SCIP_CONS cons);
	static void CALL_SCIPaddCons(SCIP scip, SCIP_CONS cons) {
		SCIP_RETCODE ret = LIB.SCIPaddCons(scip, cons);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scip_reader.h */
	public SCIP_RETCODE SCIPincludeReaderBasic(SCIP scip, PointerByReference reader,
			String name, String desc, String extensions, SCIP_READERDATA data);
	static SCIP_READER CALL_SCIPincludeReaderBasic(SCIP scip, String name, String desc,
			String extensions, SCIP_READERDATA data) {
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPincludeReaderBasic(scip, pref, name, desc, extensions, data);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_READER(pref.getValue());
	}
	
	public SCIP_RETCODE SCIPsetReaderRead(SCIP scip, SCIP_READER reader, SCIP_DECL_READERREAD readerread);
	static void CALL_SCIPsetReaderRead(SCIP scip, SCIP_READER reader, SCIP_DECL_READERREAD readerread) {
		SCIP_RETCODE ret = LIB.SCIPsetReaderRead(scip, reader, readerread);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scip_sol.h */
	SCIP_SOL SCIPgetBestSol(SCIP scip);
	static SCIP_SOL getBestSol(SCIP scip) { return LIB.SCIPgetBestSol(scip); }

	int SCIPgetNSols(SCIP scip);
	static int getNSols(SCIP scip) { return LIB.SCIPgetNSols(scip); }

	Pointer SCIPgetSols(SCIP scip);
	static SCIP_SOL[] getSols(SCIP scip) { 
		int n = getNSols(scip);
		Pointer scip_sol_pp = LIB.SCIPgetSols(scip);
		Pointer[] scip_sol_arr = scip_sol_pp.getPointerArray(0, n);
		SCIP_SOL[] ret = new SCIP_SOL[n];
		for(int i=0; i<n; i++) {
			ret[i] = new SCIP_SOL(scip_sol_arr[i]);
		}
		return ret;
	}
	
	SCIP_RETCODE SCIPprintSol(SCIP scip, SCIP_SOL sol, FILEPTR file, boolean printzeros);
	static void CALL_SCIPprintSol(SCIP scip, SCIP_SOL sol, FILEPTR file, boolean printzeros) {
		SCIP_RETCODE ret = LIB.SCIPprintSol(scip, sol, file, printzeros);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPfreeReoptSolve(SCIP scip);
	static void CALL_SCIPfreeReoptSolve(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPfreeReoptSolve(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPfreeTransform(SCIP scip);
	static void CALL_SCIPfreeTransform(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPfreeTransform(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPfreeSolve(SCIP scip);
	static void CALL_SCIPfreeSolve(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPfreeSolve(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPprintBestSol(SCIP scip, FILEPTR file, boolean printzeros);
	static void CALL_SCIPprintBestSol(SCIP scip, FILEPTR file, boolean printzeros) {
		SCIP_RETCODE ret = LIB.SCIPprintBestSol(scip, file, printzeros);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	double SCIPgetSolVal(SCIP scip, SCIP_SOL sol, SCIP_VAR var);
	static double getSolVal(SCIP scip, SCIP_SOL sol, SCIP_VAR var) {
		return LIB.SCIPgetSolVal(scip, sol, var);
	}
	
	SCIP_RETCODE SCIPgetSolVals(SCIP scip, SCIP_SOL sol, int nvar, SCIP_VAR[] vars, double[] vals);
	static void getSolVals(SCIP scip, SCIP_SOL sol, SCIP_VAR[] vars, double[] vals) {
		SCIP_RETCODE ret = LIB.SCIPgetSolVals(scip, sol, vars.length, vars, vals);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	double SCIPgetSolOrigObj(SCIP scip, SCIP_SOL sol);
	static double getSolOrigObj(SCIP scip, SCIP_SOL sol) {
		return LIB.SCIPgetSolOrigObj(scip, sol);
	}
	
	SCIP_RETCODE SCIPcreateSol(SCIP scip, PointerByReference sol, SCIP_HEUR heur);
	static void CALL_SCIPcreateSol(SCIP scip, SCIP_SOL sol, SCIP_HEUR heur) {
		PointerByReference pref = pbr.get();
		pref.setValue(sol.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateSol(scip, pref, heur);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		sol.setPointer(pref.getValue());
	}
	static SCIP_SOL createSol(SCIP scip, SCIP_HEUR heur) {
		SCIP_SOL scip_sol = new SCIP_SOL();
		CALL_SCIPcreateSol(scip, scip_sol, heur);
		return scip_sol;
	}

	SCIP_RETCODE SCIPfreeSol(SCIP scip, PointerByReference sol);
	static void CALL_SCIPfreeSol(SCIP scip, SCIP_SOL sol) {
		PointerByReference pref = pbr.get();
		pref.setValue(sol.getPointer());
		SCIP_RETCODE ret = LIB.SCIPfreeSol(scip, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
//		sol.setPointer(pref.getValue());//always returns null
	}
	
	SCIP_RETCODE SCIPsetSolVal(SCIP scip, SCIP_SOL sol, SCIP_VAR var, double val);
	static void CALL_SCIPsetSolVal(SCIP scip, SCIP_SOL sol, SCIP_VAR var, double val) {
		SCIP_RETCODE ret = LIB.SCIPsetSolVal(scip, sol, var, val);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPtrySol(SCIP scip, SCIP_SOL sol, boolean printreason, boolean completely,
			boolean checkbounds, boolean checkintegrality, boolean checklprows,
			ByteByReference stored);
	//Returns true if solution was stored
	static boolean CALL_SCIPtrySol(SCIP scip, SCIP_SOL sol, boolean printreason, boolean completely,
			boolean checkbounds, boolean checkintegrality, boolean checklprows) {
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPtrySol(scip, sol, printreason, completely, checkbounds,
				checkintegrality, checklprows, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	/* scip_solve.h */
	SCIP_RETCODE SCIPenableReoptimization(SCIP scip, int enable);
	static void CALL_SCIPenableReoptimization(SCIP scip, boolean enable) {
		SCIP_RETCODE ret = LIB.SCIPenableReoptimization(scip, enable?1:0);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPpresolve(SCIP scip);
	static void CALL_SCIPpresolve(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPpresolve(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsolve(SCIP scip);
	static void CALL_SCIPsolve(SCIP scip) {
		SCIP_RETCODE ret = LIB.SCIPsolve(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scip_solvingstats.h */
	SCIP_RETCODE SCIPprintOrigProblem(SCIP scip, FILEPTR file, String extension, boolean genericnames);
	static void CALL_SCIPprintOrigProblem(SCIP scip, FILEPTR file, String extension, boolean genericnames) {
		SCIP_RETCODE ret = LIB.SCIPprintOrigProblem(scip, file, extension, genericnames);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPprintStatistics(SCIP scip, FILEPTR file);
	static void CALL_SCIPprintStatistics(SCIP scip, FILEPTR file) {
		SCIP_RETCODE ret = LIB.SCIPprintStatistics(scip, file);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scip_var.h */
	SCIP_RETCODE SCIPcreateVar(SCIP scip, PointerByReference var, String name, double lb,
			double ub, double obj, SCIP_VARTYPE vartype, boolean initial, boolean removable,
			SCIP_DECL_VARDELORIG vardelorig,
			SCIP_DECL_VARTRANS vartrans,
			SCIP_DECL_VARDELTRANS vardeltrans,
			SCIP_DECL_VARCOPY varcopy,
			SCIP_VARDATA vardata
			);
	static void CALL_SCIPcreateVar(SCIP scip, SCIP_VAR var, String name, double lb,
			double ub, double obj, SCIP_VARTYPE vartype, boolean initial, boolean removable,
			SCIP_DECL_VARDELORIG vardelorig,
			SCIP_DECL_VARTRANS vartrans,
			SCIP_DECL_VARDELTRANS vardeltrans,
			SCIP_DECL_VARCOPY varcopy,
			SCIP_VARDATA vardata) {
		PointerByReference pref = pbr.get();
		pref.setValue(var.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateVar(scip, pref, name, lb, ub, obj, vartype, initial,
				removable, vardelorig, vartrans, vardeltrans, varcopy, vardata);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		var.setPointer(pref.getValue());
	}
	static SCIP_VAR createVar(SCIP scip, String name, double lb, double ub,
			double obj, SCIP_VARTYPE vartype, boolean initial, boolean removable,
			SCIP_DECL_VARDELORIG vardelorig,
			SCIP_DECL_VARTRANS vartrans,
			SCIP_DECL_VARDELTRANS vardeltrans,
			SCIP_DECL_VARCOPY varcopy,
			SCIP_VARDATA vardata) {
		SCIP_VAR var = new SCIP_VAR();
		CALL_SCIPcreateVar(scip, var, name, lb, ub, obj, vartype, initial,
				removable, vardelorig, vartrans, vardeltrans, varcopy, vardata);
		return var; 
	}
	
	SCIP_RETCODE SCIPcreateVarBasic(SCIP scip, PointerByReference var, String name, 
			double lb, double ub, double obj, SCIP_VARTYPE vartype);
	static void CALL_SCIPcreateVarBasic(SCIP scip, SCIP_VAR var,
			String name, double lb, double ub, double obj, SCIP_VARTYPE vartype) {
		PointerByReference pref = pbr.get();
		pref.setValue(var.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateVarBasic(scip, pref, name, lb, ub, obj, vartype);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		var.setPointer(pref.getValue());
	}
	static SCIP_VAR createVarBasic(SCIP scip, String name, double lb, double ub,
			double obj, SCIP_VARTYPE vartype) {
		SCIP_VAR var = new SCIP_VAR();
		CALL_SCIPcreateVarBasic(scip, var, name, lb, ub, obj, vartype);
		return var; 
	}
		
	SCIP_RETCODE SCIPcaptureVar(SCIP scip, SCIP_VAR var);
	static void CALL_SCIPcaptureVar(SCIP scip, SCIP_VAR var) {
		SCIP_RETCODE ret = LIB.SCIPcaptureVar(scip, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPreleaseVar(SCIP scip, PointerByReference var);
	static void CALL_SCIPreleaseVar(SCIP scip, SCIP_VAR var) {
		PointerByReference pref = pbr.get();
		pref.setValue(var.getPointer());
		SCIP_RETCODE ret = LIB.SCIPreleaseVar(scip, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
//		var.setPointer(pref.getValue()); //always return null
	}
	
	SCIP_RETCODE SCIPgetTransformedVar(SCIP scip, SCIP_VAR var, PointerByReference transvar);
	static SCIP_VAR CALL_SCIPgetTransformedVar(SCIP scip, SCIP_VAR var) {
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPgetTransformedVar(scip, var, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_VAR(pref.getValue());
	}
	
	SCIP_RETCODE SCIPgetNegatedVar(SCIP scip, SCIP_VAR var, PointerByReference transvar);
	static SCIP_VAR CALL_SCIPgetNegatedVar(SCIP scip, SCIP_VAR var) {
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPgetTransformedVar(scip, var, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_VAR(pref.getValue());
	}
	
	SCIP_RETCODE SCIPflattenVarAggregationGraph(SCIP scip, SCIP_VAR var);
	static void CALL_SCIPflattenVarAggregationGraph(SCIP scip, SCIP_VAR var) {
		SCIP_RETCODE ret = LIB.SCIPflattenVarAggregationGraph(scip, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPaddVarLocksType(SCIP scip, SCIP_VAR var, SCIP_LOCKTYPE type, int nlocksdown, int nlocksup);
	static void CALL_SCIPaddVarLocksType(SCIP scip, SCIP_VAR var, SCIP_LOCKTYPE type, int nlocksdown, int nlocksup) {
		SCIP_RETCODE ret = LIB.SCIPaddVarLocksType(scip, var, null, nlocksdown, nlocksup);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPaddVarLocks(SCIP scip, SCIP_VAR var, int nlocksdown, int nlocksup);
	static void CALL_SCIPaddVarLocks(SCIP scip, SCIP_VAR var, int nlocksdown, int nlocksup) {
		SCIP_RETCODE ret = LIB.SCIPaddVarLocks(scip, var, nlocksdown, nlocksup);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarLb(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarLb(SCIP scip, SCIP_VAR var, double newbound) {
		SCIP_RETCODE ret = LIB.SCIPchgVarLb(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarUb(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarUb(SCIP scip, SCIP_VAR var, double newbound) {
		SCIP_RETCODE ret = LIB.SCIPchgVarUb(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarLbNode(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarLbNode(SCIP scip, SCIP_VAR var, double newbound) {
		SCIP_RETCODE ret = LIB.SCIPchgVarLbNode(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarUbNode(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarUbNode(SCIP scip, SCIP_VAR var, double newbound) {
		SCIP_RETCODE ret = LIB.SCIPchgVarUbNode(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarLbGlobal(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarLbGlobal(SCIP scip, SCIP_VAR var, double newbound) {
		SCIP_RETCODE ret = LIB.SCIPchgVarLbGlobal(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarUbGlobal(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarUbGlobal(SCIP scip, SCIP_VAR var, double newbound) {
		SCIP_RETCODE ret = LIB.SCIPchgVarUbGlobal(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarLbLazy(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarLbLazy(SCIP scip, SCIP_VAR var, double newbound) {
		SCIP_RETCODE ret = LIB.SCIPchgVarLbLazy(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarUbLazy(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarUbLazy(SCIP scip, SCIP_VAR var, double newbound) {
		SCIP_RETCODE ret = LIB.SCIPchgVarUbLazy(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPtightenVarLb(SCIP scip, SCIP_VAR var, double newbound,
			boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPtightenVarLb(SCIP scip, SCIP_VAR var, double newbound,
			boolean force) {
		ByteByReference inf_ref = bbr.get();
		ByteByReference tight_ref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPtightenVarLb(scip, var, newbound, force, inf_ref, tight_ref);
		bbr.free(inf_ref);
		bbr.free(tight_ref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		if(inf_ref.getValue() != 0)
			return InferVarResult.INFEASIBLE;
		if(tight_ref.getValue() != 0)
			return InferVarResult.TIGHTENED;
		return InferVarResult.UNCHNAGED;
	}
	
	SCIP_RETCODE SCIPtightenVarUb(SCIP scip, SCIP_VAR var, double newbound,
			boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPtightenVarUb(SCIP scip, SCIP_VAR var, double newbound,
			boolean force) {
		ByteByReference inf_ref = bbr.get();
		ByteByReference tight_ref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPtightenVarUb(scip, var, newbound, force, inf_ref, tight_ref);
		bbr.free(inf_ref);
		bbr.free(tight_ref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		if(inf_ref.getValue() != 0)
			return InferVarResult.INFEASIBLE;
		if(tight_ref.getValue() != 0)
			return InferVarResult.TIGHTENED;
		return InferVarResult.UNCHNAGED;
	}
	
	SCIP_RETCODE SCIPinferVarFixCons(SCIP scip, SCIP_VAR var, double fixedval, SCIP_CONS infercons,
			int inferinfo, boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPinferVarFixCons(SCIP scip, SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {
		ByteByReference inf_ref = bbr.get();
		ByteByReference tight_ref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPinferVarFixCons(scip, var, fixedval, infercons,
				inferinfo, force, inf_ref, tight_ref);
		bbr.free(inf_ref);
		bbr.free(tight_ref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		if(inf_ref.getValue() != 0)
			return InferVarResult.INFEASIBLE;
		if(tight_ref.getValue() != 0)
			return InferVarResult.TIGHTENED;
		return InferVarResult.UNCHNAGED;
	}
	
	SCIP_RETCODE SCIPinferVarLbCons(SCIP scip, SCIP_VAR var, double fixedval, SCIP_CONS infercons,
			int inferinfo, boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPinferVarLbCons(SCIP scip, SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {
		ByteByReference inf_ref = bbr.get();
		ByteByReference tight_ref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPinferVarLbCons(scip, var, fixedval, infercons,
				inferinfo, force, inf_ref, tight_ref);
		bbr.free(inf_ref);
		bbr.free(tight_ref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		if(inf_ref.getValue() != 0)
			return InferVarResult.INFEASIBLE;
		if(tight_ref.getValue() != 0)
			return InferVarResult.TIGHTENED;
		return InferVarResult.UNCHNAGED;
	}
	
	SCIP_RETCODE SCIPinferVarUbCons(SCIP scip, SCIP_VAR var, double fixedval, SCIP_CONS infercons,
			int inferinfo, boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPinferVarUbCons(SCIP scip, SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {
		ByteByReference inf_ref = bbr.get();
		ByteByReference tight_ref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPinferVarUbCons(scip, var, fixedval, infercons,
				inferinfo, force, inf_ref, tight_ref);
		bbr.free(inf_ref);
		bbr.free(tight_ref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		if(inf_ref.getValue() != 0)
			return InferVarResult.INFEASIBLE;
		if(tight_ref.getValue() != 0)
			return InferVarResult.TIGHTENED;
		return InferVarResult.UNCHNAGED;
	}
	
	SCIP_RETCODE SCIPinferBinvarCons(SCIP scip, SCIP_VAR var, boolean fixedval, SCIP_CONS infercons,
			int inferinfo, ByteByReference infeasible, ByteByReference tightened);
	
	static InferVarResult CALL_SCIPinferBinvarCons(SCIP scip, SCIP_VAR var, boolean fixedval,
			SCIP_CONS infercons, int inferinfo) {
		ByteByReference inf_ref = bbr.get();
		ByteByReference tight_ref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPinferBinvarCons(scip, var, fixedval, infercons,
				inferinfo, inf_ref, tight_ref);
		bbr.free(inf_ref);
		bbr.free(tight_ref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		if(inf_ref.getValue() != 0) {
			return InferVarResult.INFEASIBLE;
		}
		if(tight_ref.getValue() != 0) {
			return InferVarResult.TIGHTENED;
		}
		return InferVarResult.UNCHNAGED;
	}
	
	SCIP_RETCODE SCIPprintVar(SCIP scip, SCIP_VAR var, FILEPTR file);
	static void CALL_SCIPprintVar(SCIP scip, SCIP_VAR var, FILEPTR file) {
		SCIP_RETCODE ret = LIB.SCIPprintVar(scip, var, file);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	//Below we might have some sketchy memory-messing methods of our own.
	//These are not part of the SCIP public API and likely rely on memory ordering.
	
	//Gets the SCIP_SET* field
	static SCIP_SET SCIPset(SCIP scip) {
		return new SCIP_SET(scip.getPointer().getPointer(8));
	}
	
	//Since these calls regularly require allocating new ByteByReference and PointerByReference
	//objects, this can actually make simple things substantially more expensive. We can't keep a
	//fixed one per method, because Java might call SCIP might call a callback ... and use the same
	//object. It's rare, but happens. So instead we keep a little pool of them for use, since it's rare
	//that we'll need more than 3 or 4 at a time.
	class SimplePool<T> {
		private ArrayList<T> free = new ArrayList<>();
		private Supplier<T> constructor;

		//useful for debugging
		private static final boolean TRACK_USED = true;
		private ArrayList<T> used = new ArrayList<>();
		
		public SimplePool(Supplier<T> supp){
			constructor = supp;
		}
		T get() {
			T t;
			if(free.size() > 0)
				t = free.remove(free.size()-1);
			else
				t = constructor.get();
			if(TRACK_USED) {
				used.add(t);
			}
			return t;
		}
		void free(T t) {
			if(TRACK_USED) {
				if(!used.remove(t))
					throw new RuntimeException("Freed when not used");
				if(free.contains(t))
					throw new RuntimeException("Doubly freed");
			}
			free.add(t);
		}
		@Override
		public String toString() {
			String t = free.size() == 0 ? "?" : free.get(0).getClass().toString();
			String used_str = TRACK_USED ? ",used="+used.size() : "";
			return "SimplePool[n="+free.size()+used_str+",t="+t+"]";
		}
		@Override
		public void finalize() {
			if(used.size() > 0) {
				throw new RuntimeException("Warning: Pool closed while pointers in use");
			}
		}
	}
	static SimplePool<ByteByReference> bbr = new SimplePool<>(ByteByReference::new);
	static SimplePool<PointerByReference> pbr = new SimplePool<>(PointerByReference::new);
}
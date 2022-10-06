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
import com.sun.jna.ptr.IntByReference;

import static JNA_SCIP.SCIP_RETCODE.*;

public interface JSCIP extends Library {
	public static final TypeMapper TYPE_MAPPER = new DefaultTypeMapper() {
        {log();
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
            addTypeConverter(SCIP_EVENTTYPE.class, SCIP_EVENTTYPE.EVENTTYPE_Converter.inst);
            
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
	
	//option to turn verbose logging of all calls into SCIP.
	static final boolean LOG_CALLS = false; 
	static void log() {
		if(LOG_CALLS)
			System.err.println(Thread.currentThread().getStackTrace()[2]+"\n  from "+Thread.currentThread().getStackTrace()[4]);
	}
	
	//Begin listing of methods
	
	/* cons_knapsack.h*/
	SCIP_RETCODE SCIPcreateConsBasicKnapsack(SCIP scip, PointerByReference pref, String name, int nvars,
			SCIP_VAR[] vars, long[] weights, long capacity);
	static void CALL_SCIPcreateConsBasicKnapsack(SCIP scip, SCIP_CONS cons, String name, int nvars,
			SCIP_VAR[] vars, long[] weights, long capacity) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsBasicKnapsack(scip, pref, name, nvars, vars, weights, capacity);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsBasicKnapsack(SCIP scip, String name, int nvars, SCIP_VAR[] vars,
			long[] weights, long capacity) {log();
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsBasicKnapsack(scip, cons, name, nvars, vars, weights, capacity);
		return cons;
	}
    /* END cons_knapsack.h */
	
	/* cons_linear.h */
	SCIP_RETCODE SCIPcreateConsLinear (SCIP scip, PointerByReference cons, String name,
			int nvars, SCIP_VAR[] vars, double[] vals, double lhs, double rhs, boolean initial,
			boolean separate, boolean enforce, boolean check, boolean propagate, boolean local,
			boolean modifiable, boolean dynamic, boolean removable, boolean stickingatnode);
	static void CALL_SCIPcreateConsLinear(SCIP scip, SCIP_CONS cons, String name,
			SCIP_VAR[] vars, double[] vals, double lhs, double rhs, boolean initial, boolean separate,
			boolean enforce, boolean check, boolean propagate, boolean local, boolean modifiable,
			boolean dynamic, boolean removable, boolean stickingatnode) {log();
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
			boolean stickingatnode) {log();
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsLinear(scip, cons, name, vars, vals, lhs, rhs, initial, separate,
				enforce, check, propagate, local, modifiable, dynamic, removable, stickingatnode);
		return cons;
	}
	
	SCIP_RETCODE SCIPcreateConsBasicLinear (SCIP scip, PointerByReference cons, String name,
			int nvars, SCIP_VAR[] vars, double[] vals, double lhs, double rhs);
	static void CALL_SCIPcreateConsBasicLinear(SCIP scip, SCIP_CONS cons, String name,
			SCIP_VAR[] vars, double[] vals, double lhs, double rhs) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsBasicLinear(scip, pref, name, vars==null?0:vars.length, vars, vals, lhs, rhs);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsBasicLinear(SCIP scip, String name, SCIP_VAR[] vars, double[] vals, double lhs, double rhs) {log();
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsBasicLinear(scip, cons, name, vars, vals, lhs, rhs);
		return cons;
	}
	
	SCIP_RETCODE SCIPaddCoefLinear(SCIP scip, SCIP_CONS cons, SCIP_VAR var, double val);
	static void CALL_SCIPaddCoefLinear(SCIP scip, SCIP_CONS cons, SCIP_VAR var, double val) {log();
		SCIP_RETCODE ret = LIB.SCIPaddCoefLinear(scip, cons, var, val);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* cons_logicor.h */
	SCIP_RETCODE SCIPcreateConsBasicLogicor(SCIP scip, PointerByReference cons,
			String name, int nvars, SCIP_VAR[] vars);
	static void CALL_SCIPcreateConsBasicLogicor(SCIP scip, SCIP_CONS cons, String name, SCIP_VAR[] vars) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsBasicLogicor(scip, pref, name, vars==null?0:vars.length, vars);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsBasicLogicor(SCIP scip, String name, SCIP_VAR[] vars) {log();
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsBasicLogicor(scip, cons, name, vars);
		return cons;
	}
	
	/* cons_setppc.h */
	SCIP_RETCODE SCIPcreateConsBasicSetcover(SCIP scip, PointerByReference cons,
			String name, int nvars, SCIP_VAR[] vars);
	static void CALL_SCIPcreateConsBasicSetcover(SCIP scip, SCIP_CONS cons, String name,
			SCIP_VAR[] vars) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsBasicSetcover(scip, pref, name, vars==null?0:vars.length, vars);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsBasicSetcover(SCIP scip, String name, SCIP_VAR[] vars) {log();
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsBasicSetcover(scip, cons, name, vars);
		return cons;
	}
	
	SCIP_RETCODE SCIPaddCoefSetppc(SCIP scip, SCIP_CONS cons, SCIP_VAR var);
	static void CALL_SCIPaddCoefSetppc(SCIP scip, SCIP_CONS cons, SCIP_VAR var) {log();
		SCIP_RETCODE ret = LIB.SCIPaddCoefSetppc(scip, cons, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	double SCIPgetDualsolSetppc(SCIP scip, SCIP_CONS cons);
	static double getDualsolSetppc(SCIP scip, SCIP_CONS cons) {log();
		return LIB.SCIPgetDualsolSetppc(scip, cons);
	}
	
	int SCIPgetNFixedonesSetppc(SCIP scip, SCIP_CONS cons);
	static int getNFixedonesSetppc(SCIP scip, SCIP_CONS cons) {log();
		return LIB.SCIPgetNFixedonesSetppc(scip, cons);
	}
	/* END cons_setppc.h */

	/* cons_varbound.h */
	SCIP_RETCODE SCIPcreateConsBasicVarbound(SCIP scip, PointerByReference cons, String name,
			SCIP_VAR var, SCIP_VAR vbdvar, double vbdcoef, double lhs, double rhs);
	static void CALL_SCIPcreateConsBasicVarbound(SCIP scip, SCIP_CONS cons, String name,
			SCIP_VAR var, SCIP_VAR vbdvar, double vbdcoef, double lhs, double rhs) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateConsBasicVarbound(scip, pref, name, var, vbdvar, vbdcoef, lhs, rhs);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		cons.setPointer(pref.getValue());
	}
	static SCIP_CONS createConsBasicVarbound(SCIP scip, String name,
			SCIP_VAR var, SCIP_VAR vbdvar, double vbdcoef, double lhs, double rhs) {log();
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateConsBasicVarbound(scip, cons, name, var, vbdvar, vbdcoef, lhs, rhs);
		return cons;
	}
	
	/* expr_var.h */
	SCIP_RETCODE SCIPcreateExprVar(SCIP scip, PointerByReference expr, SCIP_VAR var,
			Pointer ownercreate, Pointer ownercreatedata);
	static void CALL_SCIPcreateExprVar(SCIP scip, SCIP_EXPR expr, SCIP_VAR var,
			Pointer ownercreate, Pointer ownercreatedata) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(expr.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateExprVar(scip, pref, var, ownercreate, ownercreatedata);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		expr.setPointer(pref.getValue());
	}
	static SCIP_EXPR createExprVar(SCIP scip, SCIP_VAR var, Pointer ownercreate, Pointer ownercreatedata) {log();
		SCIP_EXPR expr = new SCIP_EXPR();
		CALL_SCIPcreateExprVar(scip, expr, var, ownercreate, ownercreatedata);
		return expr;
	}
	/* END expr_var.h */

	/* pub_branch.h */
	SCIP_BRANCHRULEDATA SCIPbranchruleGetData(SCIP_BRANCHRULE branchrule);
	static SCIP_BRANCHRULEDATA branchruleGetData(SCIP_BRANCHRULE branchrule) {log(); return LIB.SCIPbranchruleGetData(branchrule); }
	
	void SCIPbranchruleSetData(SCIP_BRANCHRULE branchrule, SCIP_BRANCHRULEDATA branchruledata);
	static void branchruleSetData(SCIP_BRANCHRULE branchrule, SCIP_BRANCHRULEDATA branchruledata) {log();
		LIB.SCIPbranchruleSetData(branchrule, branchruledata);
	}
	
	String SCIPbranchruleGetName(SCIP_BRANCHRULE branchrule);
	static String branchruleGetName(SCIP_BRANCHRULE branchrule) {log(); return LIB.SCIPbranchruleGetName(branchrule); }
	
	String SCIPbranchruleGetDesc(SCIP_BRANCHRULE branchrule);
	static String branchruleGetDesc(SCIP_BRANCHRULE branchrule) {log(); return LIB.SCIPbranchruleGetDesc(branchrule); }

	int SCIPbranchruleGetPriority(SCIP_BRANCHRULE branchrule);
	static int branchruleGetPriority(SCIP_BRANCHRULE branchrule) {log(); return LIB.SCIPbranchruleGetPriority(branchrule); }
	
	boolean SCIPbranchruleIsInitialized(SCIP_BRANCHRULE branchrule);
	static boolean branchruleIsInitialized(SCIP_BRANCHRULE branchrule) {log(); return LIB.SCIPbranchruleIsInitialized(branchrule); }
	/* END pub_branch.h */
	

	/* pub_cons.h */
	String SCIPconsGetName(SCIP_CONS cons);
	static String consGetName(SCIP_CONS cons){log(); return LIB.SCIPconsGetName(cons); }

	int SCIPconsGetPos(SCIP_CONS cons);
	static int consGetPos(SCIP_CONS cons){log(); return LIB.SCIPconsGetPos(cons); }
	
	SCIP_CONSHDLR SCIPconsGetHdlr(SCIP_CONS cons);
	static SCIP_CONSHDLR consGetHdlr(SCIP_CONS cons){log(); return LIB.SCIPconsGetHdlr(cons); }
	
	SCIP_CONSDATA SCIPconsGetData(SCIP_CONS cons);
	static SCIP_CONSDATA consGetData(SCIP_CONS cons){log(); return LIB.SCIPconsGetData(cons); }
	
	int SCIPconsGetNUses(SCIP_CONS cons);
	static int consGetNUses(SCIP_CONS cons){log(); return LIB.SCIPconsGetNUses(cons); }
	
	boolean SCIPconsIsDeleted(SCIP_CONS cons);
	static boolean consIsDeleted(SCIP_CONS cons){log(); return LIB.SCIPconsIsDeleted(cons); }
	
	boolean SCIPconsIsActive(SCIP_CONS cons);
	static boolean consIsActive(SCIP_CONS cons){log(); return LIB.SCIPconsIsActive(cons); }
	
	boolean SCIPconsIsEnabled(SCIP_CONS cons);
	static boolean consIsEnabled(SCIP_CONS cons){log(); return LIB.SCIPconsIsEnabled(cons); }
	
	boolean SCIPconsIsAdded(SCIP_CONS cons);
	static boolean consIsAdded(SCIP_CONS cons){log(); return LIB.SCIPconsIsAdded(cons); }
	
	boolean SCIPconsIsInitial(SCIP_CONS cons);
	static boolean consIsInitial(SCIP_CONS cons){log(); return LIB.SCIPconsIsInitial(cons); }
	
	boolean SCIPconsIsSeparated(SCIP_CONS cons);
	static boolean consIsSeparated(SCIP_CONS cons){log(); return LIB.SCIPconsIsSeparated(cons); }
	
	boolean SCIPconsIsEnforced(SCIP_CONS cons);
	static boolean consIsEnforced(SCIP_CONS cons){log(); return LIB.SCIPconsIsEnforced(cons); }
	
	boolean SCIPconsIsChecked(SCIP_CONS cons);
	static boolean consIsChecked(SCIP_CONS cons){log(); return LIB.SCIPconsIsChecked(cons); }
	
	boolean SCIPconsIsPropagated(SCIP_CONS cons);
	static boolean consIsPropagated(SCIP_CONS cons){log(); return LIB.SCIPconsIsPropagated(cons); }
	
	boolean SCIPconsIsLocal(SCIP_CONS cons);
	static boolean consIsLocal(SCIP_CONS cons){log(); return LIB.SCIPconsIsLocal(cons); }
	
	boolean SCIPconsIsModifiable(SCIP_CONS cons);
	static boolean consIsModifiable(SCIP_CONS cons){log(); return LIB.SCIPconsIsModifiable(cons); }
	
	boolean SCIPconsIsDynamic(SCIP_CONS cons);
	static boolean consIsDynamic(SCIP_CONS cons){log(); return LIB.SCIPconsIsDynamic(cons); }
	
	boolean SCIPconsIsRemovable(SCIP_CONS cons);
	static boolean consIsRemovable(SCIP_CONS cons){log(); return LIB.SCIPconsIsRemovable(cons); }
	
	boolean SCIPconsIsStickingAtNode(SCIP_CONS cons);
	static boolean consIsStickingAtNode(SCIP_CONS cons){log(); return LIB.SCIPconsIsStickingAtNode(cons); }
	
	boolean SCIPconsIsObsolete(SCIP_CONS cons);
	static boolean consIsObsolete(SCIP_CONS cons){log(); return LIB.SCIPconsIsObsolete(cons); }
	
	boolean SCIPconsIsConflict(SCIP_CONS cons);
	static boolean consIsConflict(SCIP_CONS cons){log(); return LIB.SCIPconsIsConflict(cons); }
	
	boolean SCIPconsIsInProb(SCIP_CONS cons);
	static boolean consIsInProb(SCIP_CONS cons){log(); return LIB.SCIPconsIsInProb(cons); }
	
	String SCIPconshdlrGetName(SCIP_CONSHDLR conshdlr);
	static String conshdlrGetName(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetName(conshdlr); } 
	
	String SCIPconshdlrGetDesc(SCIP_CONSHDLR conshdlr);
	static String conshdlrGetDesc(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetDesc(conshdlr); }
	
	SCIP_CONSHDLRDATA SCIPconshdlrGetData(SCIP_CONSHDLR cons);
	static SCIP_CONSHDLRDATA conshdlrGetData(SCIP_CONSHDLR cons){log(); return LIB.SCIPconshdlrGetData(cons); }
	
	Pointer SCIPconshdlrGetConss(SCIP_CONSHDLR conshdlr);//SCIP_CONS**
	static SCIP_CONS[] conshdlrGetConss(SCIP_CONSHDLR conshdlr) {log();
		int n = conshdlrGetNConss(conshdlr);
		if(n == 0)
			return new SCIP_CONS[0];
		Pointer scip_cons_pp = LIB.SCIPconshdlrGetConss(conshdlr);
		Pointer[] scip_cons_arr = scip_cons_pp.getPointerArray(0, n);
		SCIP_CONS[] ret = new SCIP_CONS[n];
		for(int i=0; i<n; i++) {log();
			ret[i] = new SCIP_CONS(scip_cons_arr[i]);
		}
		return ret;
	}
	
	int SCIPconshdlrGetNConss(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetNConss(SCIP_CONSHDLR conshdlr) {log(); return LIB.SCIPconshdlrGetNConss(conshdlr); }
	
	double SCIPconshdlrGetSetupTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetSetupTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetSetupTime(conshdlr); }
	
	double SCIPconshdlrGetPresolTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetPresolTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetPresolTime(conshdlr); }
	
	double SCIPconshdlrGetSepaTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetSepaTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetSepaTime(conshdlr); }
	
	double SCIPconshdlrGetEnfoLPTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetEnfoLPTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetEnfoLPTime(conshdlr); }
	
	double SCIPconshdlrGetEnfoPSTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetEnfoPSTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetEnfoPSTime(conshdlr); }
	
	double SCIPconshdlrGetEnfoRelaxTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetEnfoRelaxTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetEnfoRelaxTime(conshdlr); }
	
	double SCIPconshdlrGetPropTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetPropTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetPropTime(conshdlr); }
	
	double SCIPconshdlrGetCheckTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetCheckTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetCheckTime(conshdlr); }
	
	double SCIPconshdlrGetRespropTime(SCIP_CONSHDLR conshdlr);
	static double conshdlrGetRespropTime(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetRespropTime(conshdlr); }
	
	int SCIPconshdlrGetSepaPriority(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetSepaPriority(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetSepaPriority(conshdlr); }
	
	int SCIPconshdlrGetEnfoPriority(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetEnfoPriority(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetEnfoPriority(conshdlr); }
	
	int SCIPconshdlrGetCheckPriority(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetCheckPriority(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetCheckPriority(conshdlr); }
	
	int SCIPconshdlrGetEnfoFreq(SCIP_CONSHDLR conshdlr);
	static int conshdlrGetEnfoFreq(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrGetEnfoFreq(conshdlr); }
	
	boolean SCIPconshdlrNeedsCons(SCIP_CONSHDLR conshdlr);
	static boolean conshdlrNeedsCons(SCIP_CONSHDLR conshdlr){log(); return LIB.SCIPconshdlrNeedsCons(conshdlr); }
	/* END pub_cons.h */
	
	/* pub_event.h */
	SCIP_EVENTTYPE SCIPeventGetType(SCIP_EVENT event);
	static SCIP_EVENTTYPE eventGetType(SCIP_EVENT event) {log(); return LIB.SCIPeventGetType(event); }
	
	SCIP_VAR SCIPeventGetVar(SCIP_EVENT event);
	static SCIP_VAR eventGetVar(SCIP_EVENT event) {log(); return LIB.SCIPeventGetVar(event); }
	
	double SCIPeventGetOldobj(SCIP_EVENT event);
	static double eventGetOldobj(SCIP_EVENT event) {log(); return LIB.SCIPeventGetOldobj(event); }
	
	double SCIPeventGetNewobj(SCIP_EVENT event);
	static double eventGetNewobj(SCIP_EVENT event) {log(); return LIB.SCIPeventGetNewobj(event); }
	
	double SCIPeventGetOldbound(SCIP_EVENT event);
	static double eventGetOldbound(SCIP_EVENT event) {log(); return LIB.SCIPeventGetOldbound(event); }
	
	double SCIPeventGetNewbound(SCIP_EVENT event);
	static double eventGetNewbound(SCIP_EVENT event) {log(); return LIB.SCIPeventGetNewbound(event); }
	
	SCIP_VARTYPE SCIPeventGetOldtype(SCIP_EVENT event);
	static SCIP_VARTYPE eventGetOldtype(SCIP_EVENT event) {log(); return LIB.SCIPeventGetOldtype(event); }
	
	SCIP_VARTYPE SCIPeventGetNewtype(SCIP_EVENT event);
	static SCIP_VARTYPE eventGetNewtype(SCIP_EVENT event) {log(); return LIB.SCIPeventGetNewtype(event); }
	
	SCIP_NODE SCIPeventGetNode(SCIP_EVENT event);
	static SCIP_NODE eventGetNode(SCIP_EVENT event) {log(); return LIB.SCIPeventGetNode(event); }
	
	SCIP_SOL SCIPeventGetSol(SCIP_EVENT event);
	static SCIP_SOL eventGetSol(SCIP_EVENT event) {log(); return LIB.SCIPeventGetSol(event); }
	
	SCIP_ROW SCIPeventGetRow(SCIP_EVENT event);
	static SCIP_ROW eventGetRow(SCIP_EVENT event) {log(); return LIB.SCIPeventGetRow(event); }
	
	SCIP_COL SCIPeventGetRowCol(SCIP_EVENT event);
	static SCIP_COL eventGetRowCol(SCIP_EVENT event) {log(); return LIB.SCIPeventGetRowCol(event); }
	/* END pub_event.h */
	
	/* pub_fileio.h */
	FILEPTR SCIPfopen(String path, String mode);
	static FILEPTR fopen(String path, String mode) {log(); return LIB.SCIPfopen(path, mode); }
	
	char[] SCIPfgets(char[] buf, int size, FILEPTR stream);
	//returns true at least one character is written
	static boolean fgets(char[] buf, int size, FILEPTR stream) {log(); return LIB.SCIPfgets(buf, size, stream) != null; }
	/* END pub_fileio.h */
	
	/* pub_heur.h */
	double SCIPheurGetSetupTime(SCIP_HEUR scip_heur);
	static double heurGetSetupTime(SCIP_HEUR scip_heur) {log(); return LIB.SCIPheurGetSetupTime(scip_heur); }
	
	double SCIPheurGetTime(SCIP_HEUR scip_heur);
	static double heurGetTime(SCIP_HEUR scip_heur) {log(); return LIB.SCIPheurGetTime(scip_heur); }
	/* END pub_heur.h */
	
	/* pub_lp.h */
	void SCIPcolSort(SCIP_COL col);
	static void colSort(SCIP_COL col) {log(); LIB.SCIPcolSort(col); }
	
	double SCIPcolGetObj(SCIP_COL col);
	static double colGetObj(SCIP_COL col) {log(); return LIB.SCIPcolGetObj(col); }
	
	double SCIPcolGetUb(SCIP_COL col);
	static double colGetUb(SCIP_COL col) {log(); return LIB.SCIPcolGetUb(col); }
	
	double SCIPcolGetLb(SCIP_COL col);
	static double colGetLb(SCIP_COL col) {log(); return LIB.SCIPcolGetLb(col); } 
	
	double SCIPcolGetBestBound(SCIP_COL col);
	static double colGetBestBound(SCIP_COL col) {log(); return LIB.SCIPcolGetBestBound(col); } 
	
	double SCIPcolGetPrimsol(SCIP_COL col);
	static double colGetPrimsol(SCIP_COL col) {log(); return LIB.SCIPcolGetPrimsol(col); } 
	
	double SCIPcolGetMinPrimsol(SCIP_COL col);
	static double colGetMinPrimsol(SCIP_COL col) {log(); return LIB.SCIPcolGetMinPrimsol(col); } 
	
	double SCIPcolGetMaxPrimsol(SCIP_COL col);
	static double colGetMaxPrimsol(SCIP_COL col) {log(); return LIB.SCIPcolGetMaxPrimsol(col); } 
	
	SCIP_VAR SCIPcolGetVar(SCIP_COL col);
	static SCIP_VAR colGetVar(SCIP_COL col) {log(); return LIB.SCIPcolGetVar(col); } 
	
	int SCIPcolGetIndex(SCIP_COL col);
	static int colGetIndex(SCIP_COL col) {log(); return LIB.SCIPcolGetIndex(col); } 
	
	int SCIPcolGetVarProbindex(SCIP_COL col);
	static int colGetVarProbindex(SCIP_COL col) {log(); return LIB.SCIPcolGetVarProbindex(col); } 
	
	boolean SCIPcolIsIntegral(SCIP_COL col);
	static boolean colIsIntegral(SCIP_COL col) {log(); return LIB.SCIPcolIsIntegral(col); } 
	
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
			SCIP_MESSAGEHDLRDATA messagehdlrdata) {log();
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
	static void messagehdlrCapture(SCIP_MESSAGEHDLR messagehdlr) {log(); LIB.SCIPmessagehdlrCapture(messagehdlr); }

	SCIP_RETCODE SCIPmessagehdlrRelease(SCIP_MESSAGEHDLR messagehdlr);
	static void CALL_SCIPmessagehdlrRelease(SCIP_MESSAGEHDLR messagehdlr) {log();
		SCIP_RETCODE ret = LIB.SCIPmessagehdlrRelease(messagehdlr);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPmessagehdlrSetData(SCIP_MESSAGEHDLR messagehdlr, SCIP_MESSAGEHDLRDATA messagehdlrdata);
	static void CALL_SCIPmessagehdlrSetData(SCIP_MESSAGEHDLR messagehdlr, SCIP_MESSAGEHDLRDATA messagehdlrdata) {log();
		SCIP_RETCODE ret = LIB.SCIPmessagehdlrSetData(messagehdlr, messagehdlrdata);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	void SCIPmessagehdlrSetLogfile(SCIP_MESSAGEHDLR messagehdlr, String filename);
	static void messagehdlrSetLogfile(SCIP_MESSAGEHDLR messagehdlr, String filename) {log();
		LIB.SCIPmessagehdlrSetLogfile(messagehdlr, filename);
	}
	
	void SCIPmessagehdlrSetQuiet(SCIP_MESSAGEHDLR messagehdlr, boolean quiet);
	static void messagehdlrSetQuiet(SCIP_MESSAGEHDLR messagehdlr, boolean quiet) {log();
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
	static void messagePrintError(String fmt, Object... vals) {log(); LIB.SCIPmessagePrintError(fmt, vals); }

	void SCIPmessageSetErrorPrinting(SCIP_DECL_ERRORPRINTING errorprinting, Pointer data);
	static void messageSetErrorPrinting(SCIP_DECL_ERRORPRINTING errorprinting, Pointer data) {log();
		LIB.SCIPmessageSetErrorPrinting(errorprinting, data);
	}
	
	void SCIPmessageSetErrorPrintingDefault();
	static void messageSetErrorPrintingDefault() {log(); LIB.SCIPmessageSetErrorPrintingDefault(); }
	
	SCIP_MESSAGEHDLRDATA SCIPmessagehdlrGetData(SCIP_MESSAGEHDLR messagehdlr);
	static SCIP_MESSAGEHDLRDATA messagehdlrGetData(SCIP_MESSAGEHDLR messagehdlr) {log();
		return LIB.SCIPmessagehdlrGetData(messagehdlr);
	}
	
	FILEPTR SCIPmessagehdlrGetLogfile(SCIP_MESSAGEHDLR messagehdlr);
	static FILEPTR messagehdlrGetLogfile(SCIP_MESSAGEHDLR messagehdlr) {log();
		return LIB.SCIPmessagehdlrGetLogfile(messagehdlr);
	}
	
	boolean SCIPmessagehdlrIsQuiet(SCIP_MESSAGEHDLR messagehdlr);
	static boolean messagehdlrIsQuiet(SCIP_MESSAGEHDLR messagehdlr) {log();
		return LIB.SCIPmessagehdlrIsQuiet(messagehdlr);
	}
	/* END pub_message.h */

	/* pub_pricer.h */
	SCIP_PRICERDATA SCIPpricerGetData(SCIP_PRICER pricer);
	static SCIP_PRICERDATA pricerGetData(SCIP_PRICER pricer) {log(); return LIB.SCIPpricerGetData(pricer); }
	
	void SCIPpricerSetData(SCIP_PRICER pricer, SCIP_PRICERDATA pricerdata);
	static void pricerSetData(SCIP_PRICER pricer, SCIP_PRICERDATA pricerdata) {log();
		LIB.SCIPpricerSetData(pricer, pricerdata);
	}
	
	String SCIPpricerGetName(SCIP_PRICER pricer);
	static String pricerGetName(SCIP_PRICER pricer) {log(); return LIB.SCIPpricerGetName(pricer); }
	
	String SCIPpricerGetDesc(SCIP_PRICER pricer);
	static String pricerGetDesc(SCIP_PRICER pricer) {log(); return LIB.SCIPpricerGetDesc(pricer); }

	int SCIPpricerGetPriority(SCIP_PRICER pricer);
	static int pricerGetPriority(SCIP_PRICER pricer) {log(); return LIB.SCIPpricerGetPriority(pricer); }
	
	boolean SCIPpricerIsInitialized(SCIP_PRICER pricer);
	static boolean pricerIsInitialized(SCIP_PRICER pricer) {log(); return LIB.SCIPpricerIsInitialized(pricer); }
	/* END pub_pricer.h */
	
	/* pub_tree.h */
	long SCIPnodeGetNumber(SCIP_NODE node);
	static long nodeGetNumber(SCIP_NODE node) {log(); return LIB.SCIPnodeGetNumber(node); }

	int SCIPnodeGetDepth(SCIP_NODE node);
	static int nodeGetDepth(SCIP_NODE node) {log(); return LIB.SCIPnodeGetDepth(node); }
	
	int SCIPnodeGetLowerbound(SCIP_NODE node);
	static double nodeGetLowerbound(SCIP_NODE node) {log(); return LIB.SCIPnodeGetLowerbound(node); }
	/* END pub_tree.h */
	
	/* pub_var.h */
	String SCIPvarGetName(SCIP_VAR var);
	static String varGetName(SCIP_VAR var) {log(); return LIB.SCIPvarGetName(var); }
	
	int SCIPvarGetNUses(SCIP_VAR var);
	static int varGetNUses(SCIP_VAR var) {log(); return LIB.SCIPvarGetNUses(var); }
	
	void SCIPvarSetTransData(SCIP_VAR var, SCIP_DECL_VARTRANS vartrans);
	static void varSetTransData(SCIP_VAR var, SCIP_DECL_VARTRANS vartrans) {log();
		LIB.SCIPvarSetTransData(var, vartrans);
	}
	
	SCIP_VARTYPE SCIPvarGetType(SCIP_VAR var);
	static SCIP_VARTYPE varGetType(SCIP_VAR var) {log(); return LIB.SCIPvarGetType(var); }
	
	SCIP_VARSTATUS SCIPvarGetStatus(SCIP_VAR var);
	static SCIP_VARSTATUS varGetStatus(SCIP_VAR var) {log(); return LIB.SCIPvarGetStatus(var); }
	
	double SCIPvarGetObj(SCIP_VAR var);
	static double varGetObj(SCIP_VAR var) {log(); return LIB.SCIPvarGetObj(var); }
	
	double SCIPvarGetLbLocal(SCIP_VAR var);
	static double varGetLbLocal(SCIP_VAR var) {log(); return LIB.SCIPvarGetLbLocal(var); }
	
	double SCIPvarGetUbLocal(SCIP_VAR var);
	static double varGetUbLocal(SCIP_VAR var) {log(); return LIB.SCIPvarGetUbLocal(var); }
	
	double SCIPvarGetLbGlobal(SCIP_VAR var);
	static double varGetLbGlobal(SCIP_VAR var) {log(); return LIB.SCIPvarGetLbGlobal(var); }
	
	double SCIPvarGetUbGlobal(SCIP_VAR var);
	static double varGetUbGlobal(SCIP_VAR var) {log(); return LIB.SCIPvarGetUbGlobal(var); }
	
	double SCIPvarGetLbOriginal(SCIP_VAR var);
	static double varGetLbOriginal(SCIP_VAR var) {log(); return LIB.SCIPvarGetLbOriginal(var); }
	
	double SCIPvarGetUbOriginal(SCIP_VAR var);
	static double varGetUbOriginal(SCIP_VAR var) {log(); return LIB.SCIPvarGetUbOriginal(var); }
	
	double SCIPvarGetLbLazy(SCIP_VAR var);
	static double varGetLbLazy(SCIP_VAR var) {log(); return LIB.SCIPvarGetLbLazy(var); }
	
	double SCIPvarGetUbLazy(SCIP_VAR var);
	static double varGetUbLazy(SCIP_VAR var) {log(); return LIB.SCIPvarGetUbLazy(var); }
	
	double SCIPvarGetLPSol(SCIP_VAR var);
	static double varGetLPSol(SCIP_VAR var) {log(); return LIB.SCIPvarGetLPSol(var); }
	
	double SCIPvarGetNLPSol(SCIP_VAR var);
	static double varGetNLPSol(SCIP_VAR var) {log(); return LIB.SCIPvarGetNLPSol(var); }
	
	double SCIPvarGetPseudoSol(SCIP_VAR var);
	static double varGetPseudoSol(SCIP_VAR var) {log(); return LIB.SCIPvarGetPseudoSol(var); }
	
	double SCIPvarGetSol(SCIP_VAR var, boolean lp);
	static double varGetSol(SCIP_VAR var, boolean lp) {log(); return LIB.SCIPvarGetSol(var, lp); }
	
	double SCIPvarGetRootSol(SCIP_VAR var);
	static double varGetRootSol(SCIP_VAR var) {log(); return LIB.SCIPvarGetRootSol(var); }
	
	double SCIPvarGetAvgSol(SCIP_VAR var);
	static double varGetAvgSol(SCIP_VAR var) {log(); return LIB.SCIPvarGetAvgSol(var); }
	
	double SCIPvarGetLbAtIndex(SCIP_VAR var, SCIP_BDCHGIDX bdchgidx, boolean after);
	static double varGetLbAtIndex(SCIP_VAR var, SCIP_BDCHGIDX bdchgidx, boolean after) {log();
		return LIB.SCIPvarGetLbAtIndex(var, bdchgidx, after);
	}

	double SCIPvarGetUbAtIndex(SCIP_VAR var, SCIP_BDCHGIDX bdchgidx, boolean after);
	static double varGetUbAtIndex(SCIP_VAR var, SCIP_BDCHGIDX bdchgidx, boolean after) {log();
		return LIB.SCIPvarGetUbAtIndex(var, bdchgidx, after);
	}

	double SCIPvarGetBdAtIndex(SCIP_VAR var, SCIP_BOUNDTYPE bdtype, SCIP_BDCHGIDX bdchgidx, boolean after);
	static double varGetBdAtIndex(SCIP_VAR var, SCIP_BOUNDTYPE bdtype, SCIP_BDCHGIDX bdchgidx, boolean after) {log();
		return LIB.SCIPvarGetBdAtIndex(var, bdtype, bdchgidx, after);
	}
	/* END pub_var.h */
	
	/* scipdefplugins.h */
	SCIP_RETCODE SCIPincludeDefaultPlugins(SCIP scip);
	static void CALL_SCIPincludeDefaultPlugins(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPincludeDefaultPlugins(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scipshell.h */
	//We pad this a +1 to the argc and a dummy "java" to argv.
	//SCIP is written in C and expecting argv[0] to be the program itself, which differs from
	//java's convention.
	SCIP_RETCODE SCIPprocessShellArguments(SCIP scip, int argc, String[] argv, String settings);
	static void CALL_SCIPprocessShellArguments(SCIP scip, String[] argv, String settings) {log();
		String[] newArgv = new String[argv.length+1];
		System.arraycopy(argv, 0, newArgv, 1, argv.length);
		newArgv[0] = "java";
		int argc = newArgv.length;
		SCIP_RETCODE ret = LIB.SCIPprocessShellArguments(scip, argc, newArgv, settings);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	/* scip_branch.h */
	SCIP_RETCODE SCIPincludeBranchruleBasic(SCIP scip, PointerByReference branchruleptr, String name,
			String desc, int priority, int maxdepth, double maxbounddist,
			SCIP_BRANCHRULEDATA branchruledata);
	
	static SCIP_BRANCHRULE CALL_SCIPincludeBranchruleBasic(SCIP scip, String name,
			String desc, int priority, int maxdepth, double maxbounddist,
			SCIP_BRANCHRULEDATA branchruledata) {log();
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPincludeBranchruleBasic(scip, pref, name, desc, priority, maxdepth, maxbounddist, branchruledata);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_BRANCHRULE(pref.getValue());
	}
	
	SCIP_RETCODE SCIPsetBranchruleExecLp(SCIP scip, SCIP_BRANCHRULE branchrule, SCIP_DECL_BRANCHEXECLP branchexeclp);
	static void CALL_SCIPsetBranchruleExecLp(SCIP scip, SCIP_BRANCHRULE branchrule, SCIP_DECL_BRANCHEXECLP branchexeclp) {log();
		SCIP_RETCODE ret = LIB.SCIPsetBranchruleExecLp(scip, branchrule, branchexeclp);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPcreateChild(SCIP scip, PointerByReference pref, double nodeselprio, double estimate);
	static SCIP_NODE CALL_SCIPcreateChild(SCIP scip, double nodeselprio, double estimate) {log();
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPcreateChild(scip, pref, nodeselprio, estimate);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_NODE(pref.getValue());
	}
	
	SCIP_RETCODE SCIPgetLPBranchCands(SCIP scip, PointerByReference lpcands, PointerByReference lpcandssol,
			PointerByReference lpcandsfrac, IntByReference nlpcands, IntByReference npriolpcands, IntByReference nfracimplvars);
	static LPBranchCands CALL_SCIPgetLPBranchCands(SCIP scip) {log();
		PointerByReference lpcands = pbr.get();
		PointerByReference lpcandssol = pbr.get();
		PointerByReference lpcandsfrac = pbr.get();
		IntByReference nlpcands = ibr.get();
		IntByReference npriolpcands = ibr.get();
		IntByReference nfracimplvars = ibr.get();
		SCIP_RETCODE ret = LIB.SCIPgetLPBranchCands(scip, lpcands, lpcandssol, lpcandsfrac, nlpcands, npriolpcands, nfracimplvars);
		pbr.free(lpcands);
		pbr.free(lpcandssol);
		pbr.free(lpcandsfrac);
		ibr.free(nlpcands);
		ibr.free(npriolpcands);
		ibr.free(nfracimplvars);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new LPBranchCands(lpcands.getValue(), lpcandssol.getValue(), lpcandsfrac.getValue(),
				nlpcands, npriolpcands, nfracimplvars);
	}
	/* END scip_branch.h */
	
	/* scip_conflict.h */
	SCIP_RETCODE SCIPinitConflictAnalysis(SCIP scip, SCIP_CONFTYPE conftype, boolean iscutoffinvolved);
	static void CALL_SCIPinitConflictAnalysis(SCIP scip, SCIP_CONFTYPE conftype, boolean iscutoffinvolved) {log();
		SCIP_RETCODE ret = LIB.SCIPinitConflictAnalysis(scip, conftype, iscutoffinvolved);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPaddConflictLb(SCIP scip, SCIP_VAR var, SCIP_BDCHGIDX chgidx);
	static void CALL_SCIPaddConflictLb(SCIP scip, SCIP_VAR var, SCIP_BDCHGIDX chgidx) {log();
		SCIP_RETCODE ret = LIB.SCIPaddConflictLb(scip, var, chgidx);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPaddConflictUb(SCIP scip, SCIP_VAR var, SCIP_BDCHGIDX chgidx);
	static void CALL_SCIPaddConflictUb(SCIP scip, SCIP_VAR var, SCIP_BDCHGIDX chgidx) {log();
		SCIP_RETCODE ret = LIB.SCIPaddConflictUb(scip, var, chgidx);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPaddConflictBinvar(SCIP scip, SCIP_VAR var);
	static void CALL_SCIPaddConflictBinvar(SCIP scip, SCIP_VAR var) {log();
		SCIP_RETCODE ret = LIB.SCIPaddConflictBinvar(scip, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPanalyzeConflict(SCIP scip, int validdepth, ByteByReference success);
	static boolean CALL_SCIPanalyzeConflict(SCIP scip, int validdepth) {log();
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPanalyzeConflict(scip, validdepth, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	SCIP_RETCODE SCIPanalyzeConflictCons(SCIP scip, SCIP_CONS cons, ByteByReference success);
	static boolean CALL_SCIPanalyzeConflictCons(SCIP scip, SCIP_CONS cons) {log();
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
		) {log();
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
	static void CALL_SCIPsetConshdlrEnforelax(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSENFORELAX consenforelax) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrEnforelax(scip, conshdlr, consenforelax);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	
	SCIP_RETCODE SCIPsetConshdlrCopy(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSHDLRCOPY conshdlrcopy, SCIP_DECL_CONSCOPY conscopy);
	static void CALL_SCIPsetConshdlrCopy(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSHDLRCOPY conshdlrcopy, SCIP_DECL_CONSCOPY conscopy) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrCopy(scip, conshdlr, conshdlrcopy, conscopy);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConshdlrTrans(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSTRANS constrans);
	static void CALL_SCIPsetConshdlrTrans(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSTRANS constrans) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrTrans(scip, conshdlr, constrans);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConshdlrExit(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSEXIT consexit);
	static void CALL_SCIPsetConshdlrExit(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSEXIT consexit) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrExit(scip, conshdlr, consexit);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConshdlrSepa(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSSEPALP conssepalp,
			SCIP_DECL_CONSSEPASOL conssepasol, int sepafreq, int sepapriority, boolean delaysepa);
	static void CALL_SCIPsetConshdlrSepa(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSSEPALP conssepalp,
			SCIP_DECL_CONSSEPASOL conssepasol, int sepafreq, int sepapriority, boolean delaysepa) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrSepa(scip, conshdlr, conssepalp, conssepasol, sepafreq,
				sepapriority, delaysepa);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConshdlrProp(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSPROP consprop,
			int propfreq, boolean delayprop, SCIP_PROPTIMING proptiming);
	static void CALL_SCIPsetConshdlrProp(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSPROP consprop,
			int propfreq, boolean delayprop, SCIP_PROPTIMING proptiming) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrProp(scip, conshdlr, consprop, propfreq, delayprop, proptiming);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPsetConshdlrResprop(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSRESPROP consresprop);
	static void CALL_SCIPsetConshdlrResprop(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSRESPROP consresprop) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrResprop(scip, conshdlr, consresprop);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPsetConshdlrActive(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSACTIVE consactive);
	static void CALL_SCIPsetConshdlrActive(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSACTIVE consactive) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrActive(scip, conshdlr, consactive);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPsetConshdlrDeactive(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSDEACTIVE consdeactive);
	static void CALL_SCIPsetConshdlrDeactive(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSDEACTIVE consdeactive) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrDeactive(scip, conshdlr, consdeactive);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPsetConshdlrDelete(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSDELETE consDelete);
	static void CALL_SCIPsetConshdlrDelete(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSDELETE consDelete) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrDelete(scip, conshdlr, consDelete);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPsetConshdlrInitlp(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSINITLP consinitlp);
	static void CALL_SCIPsetConshdlrInitlp(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_DECL_CONSINITLP consinitlp) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConshdlrInitlp(scip, conshdlr, consinitlp);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_CONSHDLR SCIPfindConshdlr(SCIP scip, String name);
	static SCIP_CONSHDLR findConshdlr(SCIP scip, String name) {log(); return LIB.SCIPfindConshdlr(scip, name); }

	SCIP_RETCODE SCIPcreateCons(SCIP scip, PointerByReference cons, String name, SCIP_CONSHDLR conshdlr,
			Pointer consdata, boolean initial, boolean separate, boolean enforce, boolean check,
			boolean propagate, boolean local, boolean modifiable, boolean dynamic, boolean removable,
			boolean stickingatnode);
	static void CALL_SCIPcreateCons(SCIP scip, SCIP_CONS cons, String name, SCIP_CONSHDLR conshdlr,
			Pointer consdata, boolean initial, boolean separate, boolean enforce, boolean check,
			boolean propagate, boolean local, boolean modifiable, boolean dynamic, boolean removable,
			boolean stickingatnode) {log();
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
			boolean stickingatnode) {log();
		SCIP_CONS cons = new SCIP_CONS();
		CALL_SCIPcreateCons(scip, cons, name, conshdlr, consdata, initial, separate, enforce, check,
				propagate, local, modifiable, dynamic, removable, stickingatnode);
		return cons;
	}
	
	SCIP_RETCODE SCIPcaptureCons(SCIP scip, SCIP_CONS cons);
	static void CALL_SCIPcaptureCons(SCIP scip, SCIP_CONS cons) {log();
		SCIP_RETCODE ret = LIB.SCIPcaptureCons(scip, cons);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPreleaseCons(SCIP scip, PointerByReference cons);
	static void CALL_SCIPreleaseCons(SCIP scip, SCIP_CONS cons) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(cons.getPointer());
		SCIP_RETCODE ret = LIB.SCIPreleaseCons(scip, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		//cons.setPointer(pref.getValue()); //always returns null
	}
	
	SCIP_RETCODE SCIPsetConsInitial(SCIP scip, SCIP_CONS cons, boolean initial);
	static void CALL_SCIPsetConsInitial(SCIP scip, SCIP_CONS cons, boolean initial) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConsInitial(scip, cons, initial);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		
	}
	
	SCIP_RETCODE SCIPsetConsLocal(SCIP scip, SCIP_CONS cons, boolean bool);
	static void CALL_SCIPsetConsLocal(SCIP scip, SCIP_CONS cons, boolean bool) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConsLocal(scip, cons, bool);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetConsModifiable(SCIP scip, SCIP_CONS cons, boolean modifiable);
	static void CALL_SCIPsetConsModifiable(SCIP scip, SCIP_CONS cons, boolean modifiable) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConsModifiable(scip, cons, modifiable);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		
	}
	
	SCIP_RETCODE SCIPsetConsDynamic(SCIP scip, SCIP_CONS cons, boolean dynamic);
	static void CALL_SCIPsetConsDynamic(SCIP scip, SCIP_CONS cons, boolean dynamic) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConsDynamic(scip, cons, dynamic);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		
	}
	
	SCIP_RETCODE SCIPsetConsRemovable(SCIP scip, SCIP_CONS cons, boolean removable);
	static void CALL_SCIPsetConsRemovable(SCIP scip, SCIP_CONS cons, boolean removable) {log();
		SCIP_RETCODE ret = LIB.SCIPsetConsRemovable(scip, cons, removable);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		
	}
	
	SCIP_RETCODE SCIPgetTransformedCons(SCIP scip, SCIP_CONS cons, PointerByReference pref);
	static SCIP_CONS CALL_SCIPgetTransformedCons(SCIP scip, SCIP_CONS cons) {log();
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPgetTransformedCons(scip, cons, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_CONS(pref.getValue());
	}
	/* END scip_cons.h */
	
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
			SCIP_HASHMAP varmap, SCIP_HASHMAP consmap, boolean global) {log();
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
	static int getSubscipDepth(SCIP scip) {log();
		return LIB.SCIPgetSubscipDepth(scip);
	}
	
	/* scip_cut.h */
	SCIP_RETCODE SCIPaddRow(SCIP scip, SCIP_ROW row, boolean forcecut, ByteByReference infeasible);
	//Returns true if the row rendered problem infeasible 
	static boolean CALL_SCIPaddRow(SCIP scip, SCIP_ROW row, boolean forcecut) {log();
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPaddRow(scip, row, forcecut, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	SCIP_RETCODE SCIPaddPoolCut(SCIP scip, SCIP_ROW row);
	static void CALL_SCIPaddPoolCut(SCIP scip, SCIP_ROW row) {log();
		SCIP_RETCODE ret = LIB.SCIPaddPoolCut(scip, row);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return;
	}
	
	boolean SCIPisCutEfficacious(SCIP scip, SCIP_SOL sol, SCIP_ROW cut);
	static boolean isCutEfficacious(SCIP scip, SCIP_SOL sol, SCIP_ROW cut) {log();
		return LIB.SCIPisCutEfficacious(scip, sol, cut);
	}
	
	boolean SCIPisEfficacious(SCIP scip, double efficacy);
	static boolean isEfficacious(SCIP scip, double efficacy) {log();
		return LIB.SCIPisEfficacious(scip, efficacy);
	}
	/* END scip_cut.h */
	
	/* scip_dialog.h */
	SCIP_RETCODE SCIPstartInteraction(SCIP scip);
	static void CALL_SCIPstartInteraction(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPstartInteraction(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	static void startInteraction(SCIP scip) {log(); CALL_SCIPstartInteraction(scip); }
	/* END scip_dialog.h */
	
	/* scip_event.h */
	SCIP_RETCODE SCIPincludeEventhdlrBasic(SCIP scip, PointerByReference pref, String name,
			String desc, SCIP_DECL_EVENTEXEC eventexec, SCIP_EVENTHDLRDATA data);
	static void CALL_SCIPincludeEventhdlrBasic(SCIP scip, SCIP_EVENTHDLR scip_ehdlr, String name,
			String desc, SCIP_DECL_EVENTEXEC eventexec, SCIP_EVENTHDLRDATA data) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(scip_ehdlr.getPointer());
		SCIP_RETCODE ret = LIB.SCIPincludeEventhdlrBasic(scip, pref, name, desc, eventexec, data);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		scip_ehdlr.setPointer(pref.getValue());
	}
	static SCIP_EVENTHDLR includeEventhdlrBasic(SCIP scip, String name,
			String desc, SCIP_DECL_EVENTEXEC eventexec, SCIP_EVENTHDLRDATA data) {log();
		SCIP_EVENTHDLR scip_ehdlr = new SCIP_EVENTHDLR();
		CALL_SCIPincludeEventhdlrBasic(scip, scip_ehdlr, name, desc, eventexec, data);
		return scip_ehdlr;
	}
	
	SCIP_EVENTHDLR SCIPfindEventhdlr(SCIP scip, String name);
	static SCIP_EVENTHDLR findEventhdlr(SCIP scip, String name) {log();
		return LIB.SCIPfindEventhdlr(scip, name);
	}
	
	SCIP_RETCODE SCIPcatchEvent(SCIP scip, SCIP_EVENTTYPE eventtype, SCIP_EVENTHDLR eventhdlr,
			SCIP_EVENTDATA data, IntByReference filterpos);
	static int CALL_SCIPcatchEvent(SCIP scip, SCIP_EVENTTYPE eventtype, SCIP_EVENTHDLR eventhdlr,
			SCIP_EVENTDATA data) {log();
		IntByReference iref = ibr.get();
		SCIP_RETCODE ret = LIB.SCIPcatchEvent(scip, eventtype, eventhdlr, data, iref);
		int filterpos = iref.getValue();
		ibr.free(iref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return filterpos;
	}
	
	SCIP_RETCODE SCIPdropEvent(SCIP scip, SCIP_EVENTTYPE eventtype, SCIP_EVENTHDLR eventhdlr,
			SCIP_EVENTDATA data, int filterpos);
	static void CALL_SCIPdropEvent(SCIP scip, SCIP_EVENTTYPE eventtype, SCIP_EVENTHDLR eventhdlr,
			SCIP_EVENTDATA data, int filterpos) {log();
		SCIP_RETCODE ret = LIB.SCIPdropEvent(scip, eventtype, eventhdlr, data, filterpos);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	/* END scip_event.h */
	
	/* scip_general.h */
	SCIP_RETCODE SCIPcreate(PointerByReference scip);//SCIP_RETCODE
    static void CALL_SCIPcreate(SCIP scip) {log();
		PointerByReference pref = pbr.get();
    	pref.setValue(scip.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreate(pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		scip.setPointer(pref.getValue());
    }
    
    SCIP_RETCODE SCIPfree(PointerByReference scip);//SCIP_RETCODE
    static void CALL_SCIPfree(SCIP scip) {log();
		PointerByReference pref = pbr.get();
    	pref.setValue(scip.getPointer());
		SCIP_RETCODE ret = LIB.SCIPfree(pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		scip.setPointer(pref.getValue()); //NOTE: always returns null
    }
    static void free(SCIP scip) {log(); CALL_SCIPfree(scip); }
    
    SCIP_STAGE SCIPgetStage(SCIP scip);
    static SCIP_STAGE getStage(SCIP scip) {log(); return LIB.SCIPgetStage(scip); }
    
	void SCIPprintVersion(SCIP scip, FILEPTR file);
	static void printVersion(SCIP scip, FILEPTR file) {log(); LIB.SCIPprintVersion(scip, file); }
	
	SCIP_STATUS SCIPgetStatus(SCIP scip);
	static SCIP_STATUS getStatus(SCIP scip) {log(); return LIB.SCIPgetStatus(scip); }
	
	boolean SCIPisTransformed(SCIP scip);
	static boolean isTransformed(SCIP scip) {log(); return LIB.SCIPisTransformed(scip); }
	/* END scip_general.h*/
	
	/* scip_lp.h */
	Pointer SCIPgetLPCols(SCIP scip);
	static SCIP_COL[] getLPCols(SCIP scip) {log();
		int n = getNLPCols(scip);
		Pointer scip_col_pp = LIB.SCIPgetLPCols(scip);
		if(scip_col_pp == null)
			return null;
		Pointer[] scip_col_arr = scip_col_pp.getPointerArray(0, n);
		SCIP_COL[] ret = new SCIP_COL[n];
		for(int i=0; i<n; i++) {log();
			ret[i] = new SCIP_COL(scip_col_arr[i]);
		}
		return ret;
	}
	
	int SCIPgetNLPCols(SCIP scip);
	static int getNLPCols(SCIP scip) {log(); return LIB.SCIPgetNLPCols(scip); }
	
	int SCIPgetNUnfixedLPCols(SCIP scip);
	static int getNUnfixedLPCols(SCIP scip) {log(); return LIB.SCIPgetNUnfixedLPCols(scip); }
	
	boolean SCIPhasCurrentNodeLP(SCIP scip);
	static boolean hasCurrentNodeLP(SCIP scip) {log(); return LIB.SCIPhasCurrentNodeLP(scip); }

	boolean SCIPisLPConstructed(SCIP scip);
	static boolean isLPConstructed(SCIP scip) {log(); return LIB.SCIPisLPConstructed(scip); }

	SCIP_RETCODE SCIPconstructLP(SCIP scip, ByteByReference cutoff);
	static boolean CALL_SCIPconstructLP(SCIP scip) {log();
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPconstructLP(scip, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	Pointer SCIPgetLPRows(SCIP scip);
	static SCIP_ROW[] getLPRows(SCIP scip) {log();
		int n = getNLPRows(scip);
		Pointer scip_row_pp = LIB.SCIPgetLPRows(scip);
		if(scip_row_pp == null)
			return null;
		Pointer[] scip_row_arr = scip_row_pp.getPointerArray(0, n);
		SCIP_ROW[] ret = new SCIP_ROW[n];
		for(int i=0; i<n; i++) {log();
			ret[i] = new SCIP_ROW(scip_row_arr[i]);
		}
		return ret;
	}
	
	int SCIPgetNLPRows(SCIP scip);
	static int getNLPRows(SCIP scip) {log(); return LIB.SCIPgetNLPRows(scip);	}
	
	boolean SCIPallColsInLP(SCIP scip);
	static boolean allColsInLP(SCIP scip) {log(); return LIB.SCIPallColsInLP(scip);	}
	
	SCIP_RETCODE SCIPcreateEmptyRowConshdlr(SCIP scip, PointerByReference row, SCIP_CONSHDLR conshdlr,
			String name, double lhs, double rhs, boolean local, boolean modifiable,
			boolean removable);
	static void CALL_SCIPcreateEmptyRowConshdlr(SCIP scip, SCIP_ROW row, SCIP_CONSHDLR conshdlr,
			String name, double lhs, double rhs, boolean local, boolean modifiable,
			boolean removable) {log();
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPcreateEmptyRowConshdlr(scip, pref, conshdlr, name, lhs, rhs, local, modifiable, removable);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		row.setPointer(pref.getValue());
	}
	static SCIP_ROW createEmptyRowConshdlr(SCIP scip, SCIP_CONSHDLR conshdlr, String name, double lhs,
			double rhs, boolean local, boolean modifiable, boolean removable) {log();
		SCIP_ROW row = new SCIP_ROW();
		CALL_SCIPcreateEmptyRowConshdlr(scip, row, conshdlr, name, lhs, rhs, local, modifiable, removable);
		return row;
	}
	
	SCIP_RETCODE SCIPaddVarToRow(SCIP scip, SCIP_ROW row, SCIP_VAR var, double val);
	static void CALL_SCIPaddVarToRow(SCIP scip, SCIP_ROW row, SCIP_VAR var, double val) {log();
		SCIP_RETCODE ret = LIB.SCIPaddVarToRow(scip, row, var, val);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPreleaseRow(SCIP scip, PointerByReference row);
	static void CALL_SCIPreleaseRow(SCIP scip, SCIP_ROW row) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(row.getPointer());
		SCIP_RETCODE ret = LIB.SCIPreleaseRow(scip, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
//		row.setPointer(pref.getValue());//always returns null
	}
	
	SCIP_RETCODE SCIPcacheRowExtensions(SCIP scip, SCIP_ROW row);
	static void CALL_SCIPcacheRowExtensions(SCIP scip, SCIP_ROW row) {log();
		SCIP_RETCODE ret = LIB.SCIPcacheRowExtensions(scip, row);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPflushRowExtensions(SCIP scip, SCIP_ROW row);
	static void CALL_SCIPflushRowExtensions(SCIP scip, SCIP_ROW row) {log();
		SCIP_RETCODE ret = LIB.SCIPflushRowExtensions(scip, row);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPprintRow(SCIP scip, SCIP_ROW row, FILEPTR file);
	static void CALL_SCIPprintRow(SCIP scip, SCIP_ROW row, FILEPTR file) {log();
		SCIP_RETCODE ret = LIB.SCIPprintRow(scip, row, file);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPstartDive(SCIP scip);
	static void CALL_SCIPstartDive(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPstartDive(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPendDive(SCIP scip);
	static void CALL_SCIPendDive(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPendDive(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgCutoffboundDive(SCIP scip, double newcutoffbound);
	static void CALL_SCIPchgCutoffboundDive(SCIP scip, double newcutoffbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgCutoffboundDive(scip, newcutoffbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	boolean SCIPinDive(SCIP scip);
	static boolean inDive(SCIP scip) {log(); return LIB.SCIPinDive(scip); }
	
	
	/* scip_heur.h */
	SCIP_RETCODE SCIPincludeHeurBasic(SCIP scip, PointerByReference scip_heur, String name, String desc,
			byte dispchar, int priority, int freq, int freqofs, int maxdepth, SCIP_HEURTIMING timingmask,
			boolean usessubscip, SCIP_DECL_HEUREXEC heurexec, SCIP_HEURDATA heurdata);
	static void CALL_SCIPincludeHeurBasic(SCIP scip, SCIP_HEUR scip_heur, String name, String desc,
			byte dispchar, int priority, int freq, int freqofs, int maxdepth, SCIP_HEURTIMING timingmask,
			boolean usessubscip, SCIP_DECL_HEUREXEC heurexec, SCIP_HEURDATA heurdata) {log();
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
			SCIP_DECL_HEUREXEC heurexec, SCIP_HEURDATA heurdata) {log();
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
	static Pointer blkmem(SCIP scip) {log(); return LIB.SCIPblkmem(scip); }
	/* END scip_mem.h */
	
	/* scip_message.h */
	SCIP_RETCODE SCIPsetMessagehdlr(SCIP scip, SCIP_MESSAGEHDLR messagehdlr);
	static void CALL_SCIPsetMessagehdlr(SCIP scip, SCIP_MESSAGEHDLR messagehdlr) {log();
		SCIP_RETCODE ret = LIB.SCIPsetMessagehdlr(scip, messagehdlr);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_MESSAGEHDLR SCIPgetMessagehdlr(SCIP scip);
	static SCIP_MESSAGEHDLR getMessagehdlr(SCIP scip) {log();
		return LIB.SCIPgetMessagehdlr(scip);
	}
	
	void SCIPsetMessagehdlrLogfile(SCIP scip, String filename);
	static void setMessagehdlrLogfile(SCIP scip, String filename) {log();
		LIB.SCIPsetMessagehdlrLogfile(scip, filename);
	}
	
	void SCIPsetMessagehdlrQuiet(SCIP scip, boolean quiet);
	static void setMessagehdlrQuiet(SCIP scip, boolean quiet) {log();
		LIB.SCIPsetMessagehdlrQuiet(scip, quiet);
	}

	void SCIPwarningMessage(SCIP scip, String formatstr, Object... vals);
	static void warningMessage(SCIP scip, String formatstr, Object... vals) {log();
		LIB.SCIPwarningMessage(scip, formatstr, vals);
	}

	void SCIPdebugMessage(SCIP scip, String sourcefile, int sourceline, String formatstr, Object... vals);
	static void debugMessage(SCIP scip, String sourcefile, int sourceline,  String formatstr, Object... vals) {log();
		LIB.SCIPdebugMessage(scip, sourcefile, sourceline, formatstr, vals);
	}
	
	void SCIPprintDebugMessage(SCIP scip, String file, int line, String formatstr, Object... vals);
	static void printDebugMessage(SCIP scip, String file, int line, String formatstr, Object... vals) {log();
		LIB.SCIPprintDebugMessage(scip, file, line, formatstr, vals);
	}

	void SCIPdebugMessagePrint(SCIP scip, String formatstr, Object... vals);
	static void debugMessagePrint(SCIP scip, String formatstr, Object... vals) {log();
		LIB.SCIPdebugMessagePrint(scip, formatstr, vals);
	}
	
	void SCIPdialogMessage(SCIP scip, FILEPTR file, String formatstr, Object... vals);
	static void dialogMessage(SCIP scip, FILEPTR file, String formatstr, Object... vals) {log();
		LIB.SCIPdialogMessage(scip, file, formatstr, vals);
	}
	
	void SCIPinfoMessage(SCIP scip, FILEPTR file, String formatstr, Object... vals);
	static void infoMessage(SCIP scip, FILEPTR file, String formatstr, Object... vals) {log();
		LIB.SCIPinfoMessage(scip, file, formatstr, vals);
	}
	
	void SCIPverbMessage(SCIP scip, SCIP_VERBLEVEL verblevel, FILEPTR file, String formatstr, Object... vals);
	static void verbMessage(SCIP scip, SCIP_VERBLEVEL verblevel, FILEPTR file, String formatstr, Object... vals) {log();
		LIB.SCIPverbMessage(scip, verblevel, file, formatstr, vals);
	}

	SCIP_VERBLEVEL SCIPgetVerbLevel(SCIP scip);//SCIP_VERBLEVEL
	static SCIP_VERBLEVEL getVerbLevel(SCIP scip) {log(); return LIB.SCIPgetVerbLevel(scip); };
	/* END scip_message.h */
	
	/* scip_numerics.h */
	double SCIPinfinity(SCIP scip);
	static double infinity(SCIP scip) {log(); return LIB.SCIPinfinity(scip); }
	
	boolean SCIPisFeasEQ(SCIP scip, double x, double y);
	static boolean isFeasEQ(SCIP scip, double x, double y) {log();
		return LIB.SCIPisFeasEQ(scip, x, y);
	}

	boolean SCIPisFeasLT(SCIP scip, double x, double y);
	static boolean isFeasLT(SCIP scip, double x, double y) {log();
		return LIB.SCIPisFeasLT(scip, x, y);
	}

	boolean SCIPisFeasLE(SCIP scip, double x, double y);
	static boolean isFeasLE(SCIP scip, double x, double y) {log();
		return LIB.SCIPisFeasLE(scip, x, y);
	}

	boolean SCIPisFeasGT(SCIP scip, double x, double y);
	static boolean isFeasGT(SCIP scip, double x, double y) {log();
		return LIB.SCIPisFeasGT(scip, x, y);
	}

	boolean SCIPisFeasGE(SCIP scip, double x, double y);
	static boolean isFeasGE(SCIP scip, double x, double y) {log();
		return LIB.SCIPisFeasGE(scip, x, y);
	}

	boolean SCIPisFeasZero(SCIP scip, double x);
	static boolean isFeasZero(SCIP scip, double x) {log();
		return LIB.SCIPisFeasZero(scip, x);
	}

	boolean SCIPisFeasPositive(SCIP scip, double x);
	static boolean isFeasPositive(SCIP scip, double x) {log();
		return LIB.SCIPisFeasPositive(scip, x);
	}

	boolean SCIPisFeasNegative(SCIP scip, double x);
	static boolean isFeasNegative(SCIP scip, double x) {log();
		return LIB.SCIPisFeasNegative(scip, x);
	}

	boolean SCIPisFeasIntegral(SCIP scip, double x);
	static boolean isFeasIntegral(SCIP scip, double x) {log();
		return LIB.SCIPisFeasIntegral(scip, x);
	}
	/* END scip_numerics.h */
	
	/* scip_param.h */
	SCIP_RETCODE SCIPsetRealParam(SCIP scip, String name, double value);
	static void CALL_SCIPsetRealParam(SCIP scip, String name, double value) {log();
		SCIP_RETCODE ret = LIB.SCIPsetRealParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetCharParam(SCIP scip, String name, byte value);
	static void CALL_SCIPsetCharParam(SCIP scip, String name, byte value) {log();
		SCIP_RETCODE ret = LIB.SCIPsetCharParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};

	//this is actually a bool value, but SCIP is picky about bools being 0 or 1
	SCIP_RETCODE SCIPsetBoolParam(SCIP scip, String name, int value);
	static void CALL_SCIPsetBoolParam(SCIP scip, String name, boolean value) {log();
		SCIP_RETCODE ret = LIB.SCIPsetBoolParam(scip, name, value?1:0);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};

	SCIP_RETCODE SCIPsetIntParam(SCIP scip, String name, int value);
	static void CALL_SCIPsetIntParam(SCIP scip, String name, int value) {log();
		SCIP_RETCODE ret = LIB.SCIPsetIntParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};

	SCIP_RETCODE SCIPsetLongintParam(SCIP scip, String name, long value);
	static void CALL_SCIPsetLongintParam(SCIP scip, String name, long value) {log();
		SCIP_RETCODE ret = LIB.SCIPsetLongintParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetStringParam(SCIP scip, String name, String value);
	static void CALL_SCIPsetStringParam(SCIP scip, String name, String value) {log();
		SCIP_RETCODE ret = LIB.SCIPsetStringParam(scip, name, value);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetEmphasis(SCIP scip, SCIP_PARAMEMPHASIS emph, boolean quiet);
	static void CALL_SCIPsetEmphasis(SCIP scip, SCIP_PARAMEMPHASIS emph, boolean quiet) {log();
		SCIP_RETCODE ret = LIB.SCIPsetEmphasis(scip, emph, quiet);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetHeuristics(SCIP scip, SCIP_PARAMSETTING emph, boolean quiet);
	static void CALL_SCIPsetHeuristics(SCIP scip, SCIP_PARAMSETTING emph, boolean quiet) {log();
		SCIP_RETCODE ret = LIB.SCIPsetHeuristics(scip, emph, quiet);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetPresolving(SCIP scip, SCIP_PARAMSETTING emph, boolean quiet);
	static void CALL_SCIPsetPresolving(SCIP scip, SCIP_PARAMSETTING emph, boolean quiet) {log();
		SCIP_RETCODE ret = LIB.SCIPsetPresolving(scip, emph, quiet);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetSeparating(SCIP scip, SCIP_PARAMSETTING emph, boolean quiet);
	static void CALL_SCIPsetSeparating(SCIP scip, SCIP_PARAMSETTING emph, boolean quiet) {log();
		SCIP_RETCODE ret = LIB.SCIPsetSeparating(scip, emph, quiet);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	/* END scip_param.h */
	
	/* scip_pricer.h */
	SCIP_RETCODE SCIPincludePricer(
		SCIP scip, String name, String desc, int priority, boolean delay,
		   SCIP_DECL_PRICERCOPY  pricercopy,
		   SCIP_DECL_PRICERFREE  pricerfree,
		   SCIP_DECL_PRICERINIT  pricerinit,
		   SCIP_DECL_PRICEREXIT  pricerexit,
		   SCIP_DECL_PRICERINITSOL pricerinitsol,
		   SCIP_DECL_PRICEREXITSOL pricerexitsol,
		   SCIP_DECL_PRICERREDCOST pricerredcost,
		   SCIP_DECL_PRICERFARKAS pricerfarkas,
		   SCIP_PRICERDATA  pricerdata
	);
	static void CALL_SCIPincludePricer(
			SCIP scip, String name, String desc, int priority, boolean delay,
			   SCIP_DECL_PRICERCOPY  pricercopy,
			   SCIP_DECL_PRICERFREE  pricerfree,
			   SCIP_DECL_PRICERINIT  pricerinit,
			   SCIP_DECL_PRICEREXIT  pricerexit,
			   SCIP_DECL_PRICERINITSOL pricerinitsol,
			   SCIP_DECL_PRICEREXITSOL pricerexitsol,
			   SCIP_DECL_PRICERREDCOST pricerredcost,
			   SCIP_DECL_PRICERFARKAS pricerfarkas,
			   SCIP_PRICERDATA  pricerdata
		) {log();
		SCIP_RETCODE ret = LIB.SCIPincludePricer(scip, name, desc, priority, delay, pricercopy,
				pricerfree, pricerinit, pricerexit, pricerinitsol, pricerexitsol, pricerredcost,
				pricerfarkas, pricerdata);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPincludePricerBasic(
			SCIP scip, PointerByReference pricer, String name, String desc,
			int priority, boolean delay,
			   SCIP_DECL_PRICERREDCOST pricerredcost,
			   SCIP_DECL_PRICERFARKAS pricerfarkas,
			   SCIP_PRICERDATA  pricerdata
		);
	static SCIP_PRICER CALL_SCIPincludePricerBasic(
			SCIP scip, String name, String desc,
			int priority, boolean delay,
			   SCIP_DECL_PRICERREDCOST pricerredcost,
			   SCIP_DECL_PRICERFARKAS pricerfarkas,
			   SCIP_PRICERDATA  pricerdata
		) {log();
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPincludePricerBasic(scip, pref, name, desc, priority, delay,
				pricerredcost, pricerfarkas, pricerdata);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_PRICER(pref.getValue());
	}
	
	SCIP_RETCODE SCIPsetPricerCopy(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICERCOPY pricercopy);
	static void CALL_SCIPsetPricerCopy(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICERCOPY pricercopy) {log();
		SCIP_RETCODE ret = LIB.SCIPsetPricerCopy(scip, pricer, pricercopy);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetPricerFree(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICERFREE pricerfree);
	static void CALL_SCIPsetPricerFree(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICERFREE pricerfree) {log();
		SCIP_RETCODE ret = LIB.SCIPsetPricerFree(scip, pricer, pricerfree);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetPricerInit(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICERINIT pricerinit);
	static void CALL_SCIPsetPricerInit(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICERINIT pricerinit) {log();
		SCIP_RETCODE ret = LIB.SCIPsetPricerInit(scip, pricer, pricerinit);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetPricerExit(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICEREXIT pricerexit);
	static void CALL_SCIPsetPricerExit(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICEREXIT pricerexit) {log();
		SCIP_RETCODE ret = LIB.SCIPsetPricerExit(scip, pricer, pricerexit);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetPricerInitsol(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICERINITSOL pricerinitsol);
	static void CALL_SCIPsetPricerInitsol(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICERINITSOL pricerinitsol) {log();
		SCIP_RETCODE ret = LIB.SCIPsetPricerInitsol(scip, pricer, pricerinitsol);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetPricerExitsol(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICEREXITSOL pricerexitsol);
	static void CALL_SCIPsetPricerExitsol(SCIP scip, SCIP_PRICER pricer, SCIP_DECL_PRICEREXITSOL pricerexitsol) {log();
		SCIP_RETCODE ret = LIB.SCIPsetPricerExitsol(scip, pricer, pricerexitsol);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_PRICER SCIPfindPricer(SCIP scip, String name);
	static SCIP_PRICER findPricer(SCIP scip, String name) {log(); return LIB.SCIPfindPricer(scip, name); }
	
	SCIP_RETCODE SCIPactivatePricer(SCIP scip, SCIP_PRICER pricer);
	static void CALL_SCIPactivatePricer(SCIP scip, SCIP_PRICER pricer) {log();
		SCIP_RETCODE ret = LIB.SCIPactivatePricer(scip, pricer);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* END scip_pricer.h */
	
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
			) {log();
		SCIP_RETCODE ret = LIB.SCIPcreateProb(scip, name, probdelorig, probtrans, probdeltrans,
				probinitsol, probexitsol, probcopy, probdata);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPcreateProbBasic(SCIP scip, String name);
	static void CALL_SCIPcreateProbBasic(SCIP scip, String name) {log();
		SCIP_RETCODE ret = LIB.SCIPcreateProbBasic(scip, name);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetProbDelorig(SCIP scip, SCIP_DECL_PROBDELORIG method);
	static void CALL_SCIPsetProbDelorig(SCIP scip, SCIP_DECL_PROBDELORIG method) {log();
		SCIP_RETCODE ret = LIB.SCIPsetProbDelorig(scip, method);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetProbTrans(SCIP scip, SCIP_DECL_PROBTRANS method);
	static void CALL_SCIPsetProbTrans(SCIP scip, SCIP_DECL_PROBTRANS method) {log();
		SCIP_RETCODE ret = LIB.SCIPsetProbTrans(scip, method);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetProbDeltrans(SCIP scip, SCIP_DECL_PROBDELTRANS method);
	static void CALL_SCIPsetProbDeltrans(SCIP scip, SCIP_DECL_PROBDELTRANS method) {log();
		SCIP_RETCODE ret = LIB.SCIPsetProbDeltrans(scip, method);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetProbInitsol(SCIP scip, SCIP_DECL_PROBINITSOL method);
	static void CALL_SCIPsetProbInitsol(SCIP scip, SCIP_DECL_PROBINITSOL method) {log();
		SCIP_RETCODE ret = LIB.SCIPsetProbInitsol(scip, method);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetProbExitsol(SCIP scip, SCIP_DECL_PROBEXITSOL method);
	static void CALL_SCIPsetProbExitsol(SCIP scip, SCIP_DECL_PROBEXITSOL method) {log();
		SCIP_RETCODE ret = LIB.SCIPsetProbExitsol(scip, method);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsetProbCopy(SCIP scip, SCIP_DECL_PROBCOPY method);
	static void CALL_SCIPsetProbCopy(SCIP scip, SCIP_DECL_PROBCOPY method) {log();
		SCIP_RETCODE ret = LIB.SCIPsetProbCopy(scip, method);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPreadProb(SCIP scip, String filename, String ext);
	static void CALL_SCIPreadProb(SCIP scip, String filename, String ext) {log();
		SCIP_RETCODE ret = LIB.SCIPreadProb(scip, filename, ext);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPfreeProb(SCIP scip);
	static void CALL_SCIPfreeProb(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPfreeProb(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetObjsense(SCIP scip, SCIP_OBJSENSE objsense);
	static void CALL_SCIPsetObjsense(SCIP scip, SCIP_OBJSENSE objsense) {log();
		SCIP_RETCODE ret = LIB.SCIPsetObjsense(scip, objsense);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	SCIP_RETCODE SCIPsetObjIntegral(SCIP scip);
	static void CALL_SCIPsetObjIntegral(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPsetObjIntegral(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	};
	
	boolean SCIPisObjIntegral(SCIP scip);
	static boolean isObjIntegral(SCIP scip) {log();
		return LIB.SCIPisObjIntegral(scip);
	};
	
	SCIP_RETCODE SCIPaddVar(SCIP scip, SCIP_VAR var);
	static void CALL_SCIPaddVar(SCIP scip, SCIP_VAR var) {log();
		SCIP_RETCODE ret = LIB.SCIPaddVar(scip, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPaddPricedVar(SCIP scip, SCIP_VAR var, double score);
	static void CALL_SCIPaddPricedVar(SCIP scip, SCIP_VAR var, double score) {log();
		SCIP_RETCODE ret = LIB.SCIPaddPricedVar(scip, var, score);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	Pointer SCIPgetVars(SCIP scip);
	static SCIP_VAR[] CALL_SCIPgetVars(SCIP scip) {log();
		int n = getNVars(scip);
		Pointer scip_var_pp = LIB.SCIPgetVars(scip);
		Pointer[] scip_var_arr = scip_var_pp.getPointerArray(0, n);
		SCIP_VAR[] ret = new SCIP_VAR[n];
		for(int i=0; i<n; i++) {log();
			ret[i] = new SCIP_VAR(scip_var_arr[i]);
		}
		return ret;
	}
	
	int SCIPgetNVars(SCIP scip);
	static int getNVars(SCIP scip) {log(); return LIB.SCIPgetNVars(scip); }
	
	SCIP_RETCODE SCIPaddCons(SCIP scip, SCIP_CONS cons);
	static void CALL_SCIPaddCons(SCIP scip, SCIP_CONS cons) {log();
		SCIP_RETCODE ret = LIB.SCIPaddCons(scip, cons);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	double SCIPgetLocalTransEstimate(SCIP scip);
	static double getLocalTransEstimate(SCIP scip) {log(); return LIB.SCIPgetLocalTransEstimate(scip); }
	
	SCIP_RETCODE SCIPaddConsNode(SCIP scip, SCIP_NODE node, SCIP_CONS cons, SCIP_NODE validnode);
	static void CALL_SCIPaddConsNode(SCIP scip, SCIP_NODE node, SCIP_CONS cons, SCIP_NODE validnode) {log();
		SCIP_RETCODE ret = LIB.SCIPaddConsNode(scip, node, cons, validnode);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPdelConsLocal(SCIP scip, SCIP_CONS cons);
	static void CALL_SCIPdelConsLocal(SCIP scip, SCIP_CONS cons) {log();
		SCIP_RETCODE ret = LIB.SCIPdelConsLocal(scip, cons);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	/* END scip_prob.h */
	
	/* scip_reader.h */
	public SCIP_RETCODE SCIPincludeReaderBasic(SCIP scip, PointerByReference reader,
			String name, String desc, String extensions, SCIP_READERDATA data);
	static SCIP_READER CALL_SCIPincludeReaderBasic(SCIP scip, String name, String desc,
			String extensions, SCIP_READERDATA data) {log();
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPincludeReaderBasic(scip, pref, name, desc, extensions, data);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_READER(pref.getValue());
	}
	
	public SCIP_RETCODE SCIPsetReaderRead(SCIP scip, SCIP_READER reader, SCIP_DECL_READERREAD readerread);
	static void CALL_SCIPsetReaderRead(SCIP scip, SCIP_READER reader, SCIP_DECL_READERREAD readerread) {log();
		SCIP_RETCODE ret = LIB.SCIPsetReaderRead(scip, reader, readerread);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scip_sol.h */
	SCIP_SOL SCIPgetBestSol(SCIP scip);
	static SCIP_SOL getBestSol(SCIP scip) {log(); return LIB.SCIPgetBestSol(scip); }

	int SCIPgetNSols(SCIP scip);
	static int getNSols(SCIP scip) {log(); return LIB.SCIPgetNSols(scip); }

	Pointer SCIPgetSols(SCIP scip);
	static SCIP_SOL[] getSols(SCIP scip) {log();
		int n = getNSols(scip);
		Pointer scip_sol_pp = LIB.SCIPgetSols(scip);
		Pointer[] scip_sol_arr = scip_sol_pp.getPointerArray(0, n);
		SCIP_SOL[] ret = new SCIP_SOL[n];
		for(int i=0; i<n; i++) {log();
			ret[i] = new SCIP_SOL(scip_sol_arr[i]);
		}
		return ret;
	}
	
	SCIP_RETCODE SCIPprintSol(SCIP scip, SCIP_SOL sol, FILEPTR file, boolean printzeros);
	static void CALL_SCIPprintSol(SCIP scip, SCIP_SOL sol, FILEPTR file, boolean printzeros) {log();
		SCIP_RETCODE ret = LIB.SCIPprintSol(scip, sol, file, printzeros);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPfreeReoptSolve(SCIP scip);
	static void CALL_SCIPfreeReoptSolve(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPfreeReoptSolve(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPfreeTransform(SCIP scip);
	static void CALL_SCIPfreeTransform(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPfreeTransform(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPfreeSolve(SCIP scip);
	static void CALL_SCIPfreeSolve(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPfreeSolve(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPprintBestSol(SCIP scip, FILEPTR file, boolean printzeros);
	static void CALL_SCIPprintBestSol(SCIP scip, FILEPTR file, boolean printzeros) {log();
		SCIP_RETCODE ret = LIB.SCIPprintBestSol(scip, file, printzeros);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	double SCIPgetSolVal(SCIP scip, SCIP_SOL sol, SCIP_VAR var);
	static double getSolVal(SCIP scip, SCIP_SOL sol, SCIP_VAR var) {log();
		return LIB.SCIPgetSolVal(scip, sol, var);
	}
	
	SCIP_RETCODE SCIPgetSolVals(SCIP scip, SCIP_SOL sol, int nvar, SCIP_VAR[] vars, double[] vals);
	static void getSolVals(SCIP scip, SCIP_SOL sol, SCIP_VAR[] vars, double[] vals) {log();
		SCIP_RETCODE ret = LIB.SCIPgetSolVals(scip, sol, vars.length, vars, vals);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	double SCIPgetSolOrigObj(SCIP scip, SCIP_SOL sol);
	static double getSolOrigObj(SCIP scip, SCIP_SOL sol) {log();
		return LIB.SCIPgetSolOrigObj(scip, sol);
	}
	
	SCIP_RETCODE SCIPcreateSol(SCIP scip, PointerByReference sol, SCIP_HEUR heur);
	static void CALL_SCIPcreateSol(SCIP scip, SCIP_SOL sol, SCIP_HEUR heur) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(sol.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateSol(scip, pref, heur);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		sol.setPointer(pref.getValue());
	}
	static SCIP_SOL createSol(SCIP scip, SCIP_HEUR heur) {log();
		SCIP_SOL scip_sol = new SCIP_SOL();
		CALL_SCIPcreateSol(scip, scip_sol, heur);
		return scip_sol;
	}

	SCIP_RETCODE SCIPfreeSol(SCIP scip, PointerByReference sol);
	static void CALL_SCIPfreeSol(SCIP scip, SCIP_SOL sol) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(sol.getPointer());
		SCIP_RETCODE ret = LIB.SCIPfreeSol(scip, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
//		sol.setPointer(pref.getValue());//always returns null
	}
	
	SCIP_RETCODE SCIPsetSolVal(SCIP scip, SCIP_SOL sol, SCIP_VAR var, double val);
	static void CALL_SCIPsetSolVal(SCIP scip, SCIP_SOL sol, SCIP_VAR var, double val) {log();
		SCIP_RETCODE ret = LIB.SCIPsetSolVal(scip, sol, var, val);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPtrySol(SCIP scip, SCIP_SOL sol, boolean printreason, boolean completely,
			boolean checkbounds, boolean checkintegrality, boolean checklprows,
			ByteByReference stored);
	//Returns true if solution was stored
	static boolean CALL_SCIPtrySol(SCIP scip, SCIP_SOL sol, boolean printreason, boolean completely,
			boolean checkbounds, boolean checkintegrality, boolean checklprows) {log();
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPtrySol(scip, sol, printreason, completely, checkbounds,
				checkintegrality, checklprows, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	SCIP_RETCODE SCIPcheckSol(SCIP scip, SCIP_SOL sol, boolean printreason, boolean completely,
			boolean checkbounds, boolean checkintegrality, boolean checklprows, ByteByReference feasible);
	//Returns true if it was feasible
	static boolean CALL_SCIPcheckSol(SCIP scip, SCIP_SOL sol, boolean printreason, boolean completely,
			boolean checkbounds, boolean checkintegrality, boolean checklprows) {log();
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPcheckSol(scip, sol, printreason, completely, checkbounds,
				checkintegrality, checklprows, bref);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
	
	SCIP_RETCODE SCIPcheckSolOrig(SCIP scip, SCIP_SOL sol, ByteByReference feasible, boolean printreason, boolean completely);
	//Returns true if it was feasible
	static boolean CALL_SCIPcheckSolOrig(SCIP scip, SCIP_SOL sol, boolean printreason, boolean completely) {log();
		ByteByReference bref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPcheckSolOrig(scip, sol, bref, printreason, completely);
		bbr.free(bref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return bref.getValue() != 0;
	}
			
	/* END scip_sol.h */
	
	/* scip_solve.h */
	SCIP_RETCODE SCIPenableReoptimization(SCIP scip, int enable);
	static void CALL_SCIPenableReoptimization(SCIP scip, boolean enable) {log();
		SCIP_RETCODE ret = LIB.SCIPenableReoptimization(scip, enable?1:0);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPpresolve(SCIP scip);
	static void CALL_SCIPpresolve(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPpresolve(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPsolve(SCIP scip);
	static void CALL_SCIPsolve(SCIP scip) {log();
		SCIP_RETCODE ret = LIB.SCIPsolve(scip);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	/* scip_solvingstats.h */
	SCIP_RETCODE SCIPprintOrigProblem(SCIP scip, FILEPTR file, String extension, boolean genericnames);
	static void CALL_SCIPprintOrigProblem(SCIP scip, FILEPTR file, String extension, boolean genericnames) {log();
		SCIP_RETCODE ret = LIB.SCIPprintOrigProblem(scip, file, extension, genericnames);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPprintStatistics(SCIP scip, FILEPTR file);
	static void CALL_SCIPprintStatistics(SCIP scip, FILEPTR file) {log();
		SCIP_RETCODE ret = LIB.SCIPprintStatistics(scip, file);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	/* END scip_solvingstats.h */
	
	/* scip_tree.h */
	SCIP_NODE SCIPgetCurrentNode(SCIP scip);
	static SCIP_NODE getCurrentNode(SCIP scip) {log(); return LIB.SCIPgetCurrentNode(scip); }
	
	int SCIPgetDepth(SCIP scip);
	static int getDepth(SCIP scip) {log(); return LIB.SCIPgetDepth(scip); }
	
	SCIP_RETCODE SCIPrepropagateNode(SCIP scip, SCIP_NODE node);
	static void CALL_SCIPrepropagateNode(SCIP scip, SCIP_NODE node) {log();
		SCIP_RETCODE ret = LIB.SCIPrepropagateNode(scip, node);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	/* END scip_tree.h */
	
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
			SCIP_VARDATA vardata) {log();
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
			SCIP_VARDATA vardata) {log();
		SCIP_VAR var = new SCIP_VAR();
		CALL_SCIPcreateVar(scip, var, name, lb, ub, obj, vartype, initial,
				removable, vardelorig, vartrans, vardeltrans, varcopy, vardata);
		return var; 
	}
	
	SCIP_RETCODE SCIPcreateVarBasic(SCIP scip, PointerByReference var, String name, 
			double lb, double ub, double obj, SCIP_VARTYPE vartype);
	static void CALL_SCIPcreateVarBasic(SCIP scip, SCIP_VAR var,
			String name, double lb, double ub, double obj, SCIP_VARTYPE vartype) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(var.getPointer());
		SCIP_RETCODE ret = LIB.SCIPcreateVarBasic(scip, pref, name, lb, ub, obj, vartype);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		var.setPointer(pref.getValue());
	}
	static SCIP_VAR createVarBasic(SCIP scip, String name, double lb, double ub,
			double obj, SCIP_VARTYPE vartype) {log();
		SCIP_VAR var = new SCIP_VAR();
		CALL_SCIPcreateVarBasic(scip, var, name, lb, ub, obj, vartype);
		return var; 
	}
		
	SCIP_RETCODE SCIPcaptureVar(SCIP scip, SCIP_VAR var);
	static void CALL_SCIPcaptureVar(SCIP scip, SCIP_VAR var) {log();
		SCIP_RETCODE ret = LIB.SCIPcaptureVar(scip, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPreleaseVar(SCIP scip, PointerByReference var);
	static void CALL_SCIPreleaseVar(SCIP scip, SCIP_VAR var) {log();
		PointerByReference pref = pbr.get();
		pref.setValue(var.getPointer());
		SCIP_RETCODE ret = LIB.SCIPreleaseVar(scip, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
//		var.setPointer(pref.getValue()); //always return null
	}
	
	SCIP_RETCODE SCIPgetTransformedVar(SCIP scip, SCIP_VAR var, PointerByReference transvar);
	static SCIP_VAR CALL_SCIPgetTransformedVar(SCIP scip, SCIP_VAR var) {log();
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPgetTransformedVar(scip, var, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_VAR(pref.getValue());
	}
	
	SCIP_RETCODE SCIPgetNegatedVar(SCIP scip, SCIP_VAR var, PointerByReference transvar);
	static SCIP_VAR CALL_SCIPgetNegatedVar(SCIP scip, SCIP_VAR var) {log();
		PointerByReference pref = pbr.get();
		SCIP_RETCODE ret = LIB.SCIPgetNegatedVar(scip, var, pref);
		pbr.free(pref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		return new SCIP_VAR(pref.getValue());
	}
	
	SCIP_RETCODE SCIPflattenVarAggregationGraph(SCIP scip, SCIP_VAR var);
	static void CALL_SCIPflattenVarAggregationGraph(SCIP scip, SCIP_VAR var) {log();
		SCIP_RETCODE ret = LIB.SCIPflattenVarAggregationGraph(scip, var);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPaddVarLocksType(SCIP scip, SCIP_VAR var, SCIP_LOCKTYPE type, int nlocksdown, int nlocksup);
	static void CALL_SCIPaddVarLocksType(SCIP scip, SCIP_VAR var, SCIP_LOCKTYPE type, int nlocksdown, int nlocksup) {log();
		SCIP_RETCODE ret = LIB.SCIPaddVarLocksType(scip, var, null, nlocksdown, nlocksup);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}

	SCIP_RETCODE SCIPaddVarLocks(SCIP scip, SCIP_VAR var, int nlocksdown, int nlocksup);
	static void CALL_SCIPaddVarLocks(SCIP scip, SCIP_VAR var, int nlocksdown, int nlocksup) {log();
		SCIP_RETCODE ret = LIB.SCIPaddVarLocks(scip, var, nlocksdown, nlocksup);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarLb(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarLb(SCIP scip, SCIP_VAR var, double newbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgVarLb(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarUb(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarUb(SCIP scip, SCIP_VAR var, double newbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgVarUb(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarLbNode(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarLbNode(SCIP scip, SCIP_VAR var, double newbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgVarLbNode(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarUbNode(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarUbNode(SCIP scip, SCIP_VAR var, double newbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgVarUbNode(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarLbGlobal(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarLbGlobal(SCIP scip, SCIP_VAR var, double newbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgVarLbGlobal(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarUbGlobal(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarUbGlobal(SCIP scip, SCIP_VAR var, double newbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgVarUbGlobal(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarLbLazy(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarLbLazy(SCIP scip, SCIP_VAR var, double newbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgVarLbLazy(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPchgVarUbLazy(SCIP scip, SCIP_VAR var, double newbound);
	static void CALL_SCIPchgVarUbLazy(SCIP scip, SCIP_VAR var, double newbound) {log();
		SCIP_RETCODE ret = LIB.SCIPchgVarUbLazy(scip, var, newbound);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	
	SCIP_RETCODE SCIPtightenVarLb(SCIP scip, SCIP_VAR var, double newbound,
			boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPtightenVarLb(SCIP scip, SCIP_VAR var, double newbound,
			boolean force) {log();
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
		return InferVarResult.UNCHANGED;
	}
	
	SCIP_RETCODE SCIPtightenVarUb(SCIP scip, SCIP_VAR var, double newbound,
			boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPtightenVarUb(SCIP scip, SCIP_VAR var, double newbound,
			boolean force) {log();
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
		return InferVarResult.UNCHANGED;
	}
	
	SCIP_RETCODE SCIPinferVarFixCons(SCIP scip, SCIP_VAR var, double fixedval, SCIP_CONS infercons,
			int inferinfo, boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPinferVarFixCons(SCIP scip, SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {log();
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
		return InferVarResult.UNCHANGED;
	}
	
	SCIP_RETCODE SCIPinferVarLbCons(SCIP scip, SCIP_VAR var, double fixedval, SCIP_CONS infercons,
			int inferinfo, boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPinferVarLbCons(SCIP scip, SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {log();
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
		return InferVarResult.UNCHANGED;
	}
	
	SCIP_RETCODE SCIPinferVarUbCons(SCIP scip, SCIP_VAR var, double fixedval, SCIP_CONS infercons,
			int inferinfo, boolean force, ByteByReference infeasible, ByteByReference tightened);
	static InferVarResult CALL_SCIPinferVarUbCons(SCIP scip, SCIP_VAR var, double fixedval,
			SCIP_CONS infercons, int inferinfo, boolean force) {log();
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
		return InferVarResult.UNCHANGED;
	}
	
	SCIP_RETCODE SCIPinferBinvarCons(SCIP scip, SCIP_VAR var, boolean fixedval, SCIP_CONS infercons,
			int inferinfo, ByteByReference infeasible, ByteByReference tightened);
	
	static InferVarResult CALL_SCIPinferBinvarCons(SCIP scip, SCIP_VAR var, boolean fixedval,
			SCIP_CONS infercons, int inferinfo) {log();
		ByteByReference inf_ref = bbr.get();
		ByteByReference tight_ref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPinferBinvarCons(scip, var, fixedval, infercons,
				inferinfo, inf_ref, tight_ref);
		bbr.free(inf_ref);
		bbr.free(tight_ref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		if(inf_ref.getValue() != 0) {log();
			return InferVarResult.INFEASIBLE;
		}
		if(tight_ref.getValue() != 0) {log();
			return InferVarResult.TIGHTENED;
		}
		return InferVarResult.UNCHANGED;
	}
	
	SCIP_RETCODE SCIPfixVar(SCIP scip, SCIP_VAR var, double fixedval, ByteByReference infeasible, ByteByReference fixed);
	
	static FixVarResult CALL_SCIPfixVar(SCIP scip, SCIP_VAR var, double fixedval) {log();
		ByteByReference inf_ref = bbr.get();
		ByteByReference fix_ref = bbr.get();
		SCIP_RETCODE ret = LIB.SCIPfixVar(scip, var, fixedval, inf_ref, fix_ref);
		bbr.free(inf_ref);
		bbr.free(fix_ref);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
		if(inf_ref.getValue() != 0) {log();
			return FixVarResult.INFEASIBLE;
		}
		if(fix_ref.getValue() != 0) {log();
			return FixVarResult.FIXED;
		}
		return FixVarResult.UNCHANGED;
	}
	
	SCIP_RETCODE SCIPprintVar(SCIP scip, SCIP_VAR var, FILEPTR file);
	static void CALL_SCIPprintVar(SCIP scip, SCIP_VAR var, FILEPTR file) {log();
		SCIP_RETCODE ret = LIB.SCIPprintVar(scip, var, file);
		if(ret != SCIP_OKAY)
			throw new ScipException(ret);
	}
	/* END scip_var.h */
	
	//Below we might have some sketchy memory-messing methods of our own.
	//These are not part of the SCIP public API and likely rely on memory ordering.
	
	//Gets the SCIP_SET* field
	static SCIP_SET SCIPset(SCIP scip) {log();
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
		private static final boolean TRACK_USED = false;
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
	static SimplePool<IntByReference> ibr = new SimplePool<>(IntByReference::new);
	static SimplePool<ByteByReference> bbr = new SimplePool<>(ByteByReference::new);
	static SimplePool<PointerByReference> pbr = new SimplePool<>(PointerByReference::new);
}
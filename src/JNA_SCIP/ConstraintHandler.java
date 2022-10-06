package JNA_SCIP;

import java.lang.reflect.Array;
import java.util.HashMap;

import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import static JNA_SCIP.SCIP_RETCODE.*;
import static JNA_SCIP.SCIP_RESULT.*;

public abstract class ConstraintHandler<Data extends ConstraintData<Data,Self>,Self extends ConstraintHandler<Data,Self>> {
	//Map SCIP's internal CONSHDLRs to our Java objects.
	static HashMap<SCIP_CONSHDLR, ConstraintHandler<?,?>> hdlr_mapping = new HashMap<>();
	
	//And for each CONSHDLR, map the CONS's to the Java objects.
	HashMap<SCIP_CONS, Data> cons_mapping;
	
	//A class reference for allocating a correctly-typed array, because type-safety is nice.
	Class<Data> d_class;
	
	//Needs to know its name for purposes of finding its CONSHDLR object in subSCIPs.
	public final SCIP_CONSHDLR conshdlr;
	public final String name;
	
	//We need to keep hard references here to keep the callbacks from being garbage collected
	private SCIP_DECL_CONSENFOLP	consenfolp = this::consenfolp;
	private SCIP_DECL_CONSENFOPS	consenfops = this::consenfops;
	private SCIP_DECL_CONSCHECK		conscheck = this::conscheck;
	private SCIP_DECL_CONSLOCK		conslock = this::conslock;
	private SCIP_DECL_CONSDELETE	consdelete = this::consdelete;
	private SCIP_DECL_CONSTRANS		constrans = this::constrans;
	private SCIP_DECL_CONSEXIT		consexit = this::consexit;
	private SCIP_DECL_CONSPROP		consprop = this::consprop;
	private SCIP_DECL_CONSRESPROP	consresprop = this::consresprop;
	private SCIP_DECL_CONSACTIVE	consactive = this::consactive;
	private SCIP_DECL_CONSDEACTIVE	consdeactive = this::consdeactive;
	private SCIP_DECL_CONSENFORELAX	consenforelax = this::consenforelax;
	private SCIP_DECL_CONSCOPY 		conscopy = this::conscopy;
	private SCIP_DECL_CONSHDLRCOPY	conshdlrcopy = this::conshdlrcopy;
	private SCIP_DECL_CONSSEPALP	conssepalp = this::conssepalp;
	private SCIP_DECL_CONSSEPASOL	conssepasol = this::conssepasol;
	private SCIP_DECL_CONSINITLP	consinitlp = this::consinitlp;

	/* Define a constraint handler. Needs:
	 * @param builder	Constructor for constraints
	 * @param copier	Copy-constructor for transformed problems
	 * @param t_clazz	A reference to the Cons (T) type class
	 * @param name		Name of the constraint handler
	 * @param desc		Description
	 * @param enfopriority	Enforcement priority
	 * @param chckpriority	Check priority
	 * @param eagerfreq		Frequency of eager checks
	 * @param needscons		Does this constraint handler need constraints to run?
	 */
	public ConstraintHandler(SCIP scip, Class<Data> d_class, String name, String desc,
			int enfopriority, int chckpriority, int eagerfreq, boolean needscons) {
		this.d_class = d_class;
		this.name = name;
		cons_mapping = new HashMap<>();
		
		SCIP_CONSHDLR conshdlr = scip.includeConshdlrBasic(name, desc,
				enfopriority, chckpriority, eagerfreq, needscons,
				consenfolp, consenfops, conscheck, conslock, null);
		scip.setConshdlrDelete(conshdlr, consdelete);
		scip.setConshdlrTrans(conshdlr, constrans);
		scip.setConshdlrExit(conshdlr, consexit);
		
		this.conshdlr = conshdlr;
		
		hdlr_mapping.put(conshdlr, this);
	}
	
	public static ConstraintHandler<?,?> findJavaConshdlr(SCIP_CONSHDLR conshdlr) {
		return hdlr_mapping.get(conshdlr);
	}
	
	public SCIP_CONSHDLR findScipConshdlr(SCIP scip) {
		return scip.findConshdlr(this.name);
	}
	public Data getJavaCons(SCIP_CONS cons) {
		return cons_mapping.get(cons);
	}
	public SCIP_CONSHDLR getScipConshdlr() {
		return conshdlr;
	}
	
	//If we're in a subscip, go the right one to work in
	@SuppressWarnings("unchecked")
	public Self findTrueHdlr(SCIP scip) {
		return (Self)findJavaConshdlr(findScipConshdlr(scip));
	}

	//We manage the data associated to the constraints on the Java side (in `mapping`) but
	//consdelete isn't called -- and we'll never know when a constraint gets deleted --
	//unless we pass a nonnull value for consdata. We keep this single byte allocated and
	//all constraints share it as their consdata. When we 'delete' it, we just stop pointing
	//to it.
	static final Pointer dummyConsdata = new Memory(1);
	
	/* Make a constraint with given name and data, and given flags. */
	public SCIP_CONS instantiate(SCIP scip, String name, boolean initial, boolean separate,
			boolean enforce, boolean check, boolean propagate, boolean local, boolean modifiable,
			boolean dynamic, boolean removable, boolean stickingatnode, Data data) {
		
		Self truehdlr = findTrueHdlr(scip);
		if(truehdlr != this) {
			System.err.println("Wrong handler called instantiate at ");
			Thread.dumpStack();
			return truehdlr.instantiate(scip, name, initial, separate,
				enforce, check, propagate, local, modifiable,
				dynamic, removable, stickingatnode, data);
		}
		
		SCIP_CONS scip_cons = scip.createCons(name, conshdlr, dummyConsdata, initial, separate, enforce, check,
				propagate, local, modifiable, dynamic, removable, stickingatnode);
		
		cons_mapping.put(scip_cons, data);
		data.setScipCons(scip_cons);

		return scip_cons;
	}
	
	/* Make a constraint with given name and data, and default flags. */
	public SCIP_CONS instantiate(SCIP scip, String name, Data con_args) {
		return instantiate(scip, name, false, true, true, true, true, false, false, false, true, true, con_args);
	}
	
	/* Enable optional methods. Consprop, Conscopy, ConsInitLp, ConsResProp, ConsEnforelax */
	public void enableConsProp(SCIP scip, int propfreq, boolean delayprop, SCIP_PROPTIMING proptiming) {
		scip.setConshdlrProp(conshdlr, consprop, propfreq, delayprop, proptiming);
	}
	public void disableConsProp(SCIP scip) {
		//propfreq == -1 means "disable".
		scip.setConshdlrProp(conshdlr, null, -1, true, SCIP_PROPTIMING.NEVER);
	}
	public void enableConsCopy(SCIP scip) {
		scip.setConshdlrCopy(conshdlr, conshdlrcopy, conscopy);
	}
	public void disableConsCopy(SCIP scip) {
		//null methods means "disable".
		scip.setConshdlrCopy(conshdlr, null, null);
	}
	public void enableConsSepa(SCIP scip, int sepafreq, int sepapriority, boolean delaysepa) {
		scip.setConshdlrSepa(conshdlr, conssepalp, conssepasol, sepafreq, sepapriority, delaysepa);
	}
	public void disableConsSepa(SCIP scip) {
		//sepafreq == -1 means "disable".
		scip.setConshdlrSepa(conshdlr, null, null, -1, -1, true);
	}
	public void enableConsInitlp(SCIP scip) {
		scip.setConshdlrInitlp(conshdlr, consinitlp);
	}
	public void disableConsInitlp(SCIP scip) {
		//null methods means "disable".
		scip.setConshdlrInitlp(conshdlr, null);
	}
	public void enableConsResprop(SCIP scip) {
		scip.setConshdlrResprop(conshdlr, consresprop);
	}
	public void disableConsResprop(SCIP scip) {
		//null methods means "disable".
		scip.setConshdlrResprop(conshdlr, null);
	}
	public void enableConsActive(SCIP scip) {
		scip.setConshdlrActive(conshdlr, consactive);
	}
	public void disableConsActive(SCIP scip) {
		//null methods means "disable".
		scip.setConshdlrActive(conshdlr, null);
	}
	public void enableConsDective(SCIP scip) {
		scip.setConshdlrDective(conshdlr, consdeactive);
	}
	public void disableConsDective(SCIP scip) {
		//null methods means "disable".
		scip.setConshdlrDective(conshdlr, null);
	}
	//Can't be disabled afterwards.
	public void enableConsEnforelax(SCIP scip) {
		scip.setConshdlrEnforelax(conshdlr, consenforelax);
	}
	
	/* Define a series of handler that simplify types and do sanity checking */
	
	//CONSCHECK
	public abstract SCIP_RESULT conscheck(SCIP scip, Data[] conss, SCIP_SOL sol, boolean checkintegrality,
			boolean checklprows, boolean printreason, boolean completely);
	
	public SCIP_RETCODE conscheck(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss,//SCIP_CONS**
	    	int nconss,
	    	SCIP_SOL sol, boolean checkintegrality, boolean checklprows, boolean printreason, boolean completely,
	    	IntByReference scip_result//SCIP_RESULT*
    ) {
		//Sanity check on conshdlr
		if(!conshdlr.getName().equals(name)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			SCIP_RESULT res = conscheck(scip, t_arr, sol, checkintegrality, checklprows, printreason, completely);
			scip_result.setValue(res.ordinal());
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	
	//CONSLOCK
	public abstract SCIP_RETCODE conslock(SCIP scip, Data cons, SCIP_LOCKTYPE locktype, int nlockspos, int nlocksneg);

	public SCIP_RETCODE conslock(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS scip_cons, SCIP_LOCKTYPE locktype,
			int nlockspos, int nlocksneg) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation
		return conslock(scip, cons_mapping.get(scip_cons), locktype, nlockspos, nlocksneg);
	}

	//CONSENFOPS
	public abstract SCIP_RESULT consenfops(SCIP scip, Data[] conss, int nusefulconss, boolean solinfeasible,
			boolean objinfeasible);

	public SCIP_RETCODE consenfops(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss, int nusefulconss,
			boolean solinfeasible, boolean objinfeasible, IntByReference scip_result) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {	
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			SCIP_RESULT res = consenfops(scip, t_arr, nusefulconss, solinfeasible, objinfeasible);
			scip_result.setValue(res.ordinal());
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	
	//CONSENFOLP
	public abstract SCIP_RESULT consenfolp(SCIP scip, Data[] conss, int nusefulconss, boolean solinfeasible);

	public SCIP_RETCODE consenfolp(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss, int nusefulconss,
			boolean solinfeasible, IntByReference scip_result) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {	
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			SCIP_RESULT res = consenfolp(scip, t_arr, nusefulconss, solinfeasible);
			scip_result.setValue(res.ordinal());
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	
	//CONSDELETE
	public SCIP_RETCODE consdelete(SCIP scip, Data cons) {
		cons.delete(scip);
		return SCIP_OKAY;
	}
	
	public SCIP_RETCODE consdelete(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS scip_cons, PointerByReference consdata) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		consdata.setValue(null);
		//Call implementation
		return consdelete(scip, cons_mapping.remove(scip_cons));
	}
	
	//CONSTRANS
	public SCIP_RETCODE constrans(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS sourcecons, PointerByReference targetcons) {
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Default implementation: create new constraint with identical flags, and copy the data. 
		String name = "t_"+sourcecons.getName();
		
		boolean initial = sourcecons.isInitial(),
				separate = sourcecons.isSeparated(),
				enforce = sourcecons.isEnforced(),
				check = sourcecons.isChecked(),
				propagate = sourcecons.isPropagated(),
				local = sourcecons.isLocal(),
				modifiable = sourcecons.isModifiable(),
				dynamic = sourcecons.isDynamic(),
				removable = sourcecons.isRemovable(),
				stickingatnode = sourcecons.isStickingAtNode();
		
		SCIP_CONSHDLR orig_s_hdlr = sourcecons.getHdlr();
		
		@SuppressWarnings("unchecked")
		Self orig_j_hdlr = (Self)hdlr_mapping.get(orig_s_hdlr);
		
		Data orig_cons = orig_j_hdlr.cons_mapping.get(sourcecons);
		Data trans_cons = orig_cons.transform(scip);
		
		SCIP_CONS trans_scip_cons = instantiate(scip,
				name, initial, separate, enforce, check,
				propagate, local, modifiable, dynamic, removable,
				stickingatnode, trans_cons);
		
		targetcons.setValue(trans_scip_cons.getPointer());
		return SCIP_OKAY;
	}
	
	//CONSEXIT for exiting trans
	//Default implementation: call exit on each cons individually.
	public void consexit(SCIP scip, Data[] conss) {
		for(Data j_cons : conss) {
			j_cons.exit(scip);
		}
	}
	
	public SCIP_RETCODE consexit(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			consexit(scip, t_arr);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	
	//CONSPROP
	//default do-nothing implementation. Override if you're going to enable it
	public SCIP_RESULT consprop(SCIP scip, Data[] conss, int nusefulconss, int nmarkedconss, SCIP_PROPTIMING proptiming) {
		throw new RuntimeException("Consprop was enabled but you didn't override the default implementation.");
//		return SCIP_DIDNOTRUN;
	}
	
	public SCIP_RETCODE consprop(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss, int nusefulconss,
		      int nmarkedconss, SCIP_PROPTIMING proptiming, IntByReference scip_result) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			SCIP_RESULT res = consprop(scip, t_arr, nusefulconss, nmarkedconss, proptiming);
			scip_result.setValue(res.ordinal());
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	
	//CONSENFORELAX
	//default do-nothing implementation. Override if you're going to enable it
	public SCIP_RESULT consenforelax(SCIP scip, Data[] conss, int nusefulconss, boolean solinfeasible) {
		return SCIP_DIDNOTRUN;
	}
	
	public SCIP_RETCODE consenforelax(SCIP scip, SCIP_SOL sol, SCIP_CONSHDLR conshdlr, Pointer conss,
	    	int nconss, int nusefulconss, boolean solinfeasible, IntByReference scip_result) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {	
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			SCIP_RESULT res = consenforelax(scip, t_arr, nusefulconss, solinfeasible);
			scip_result.setValue(res.ordinal());
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	
	//CONSCOPY
	public SCIP_RETCODE conscopy(SCIP scip, PointerByReference cons,//SCIP_CONS**
			String name, SCIP sourcescip, SCIP_CONSHDLR sourceconshdlr, SCIP_CONS sourcecons, SCIP_HASHMAP varmap,
			SCIP_HASHMAP consmap, boolean initial, boolean separate, boolean enforce, boolean check, boolean propagate,
			boolean local, boolean modifiable, boolean dynamic, boolean removable, boolean stickingatnode,
			boolean global, ByteByReference valid) {
		//fallback name
		if(name == null)
			name = sourcecons.getName();
		
		//copy the data 
		Data orig_cons = cons_mapping.get(sourcecons);
		Data copy_cons = orig_cons.copy(sourcescip, scip, varmap, consmap, global);
		
		//make new cons
		SCIP_CONS scip_cons = findTrueHdlr(scip).
				instantiate(scip, name, initial, separate, enforce, check,
				propagate, local, modifiable, dynamic, removable,
				stickingatnode, copy_cons);
		
		valid.setValue((byte) 1);
		cons.setValue(scip_cons.getPointer());
		return SCIP_OKAY;
	}
	
	//CONSHDLRCOPY
	//Disabled by default, call enableConscopy() to enable. Requires you to implement the `copy`
	//below, which is essentially offering a route to your constructor (and thus including the
	//plugin).
	public void copy(SCIP subscip) {
		throw new RuntimeException("You enabled CONSHDLRCOPY and CONSCOPY but didn't override "+
				"ConstraintHandler.copy on "+this.getClass().toString());
	}
	
	public SCIP_RETCODE conshdlrcopy(SCIP scip, SCIP_CONSHDLR conshdlr, ByteByReference valid) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		try {
			copy(scip);
			valid.setValue((byte)1);
			return SCIP_OKAY;
		} catch(RuntimeException e) {
			e.printStackTrace();
			return SCIP_NOTIMPLEMENTED;
		}
	}
	
	//CONSSEPALP
	//Disabled by default, call enableConssepa to enable. Also enables conssepasol.
	//Return "true" if INFEASIBLE.
	public SCIP_RESULT conssepalp(SCIP scip, Data[] conss, int nusefulconss) {
		throw new RuntimeException("conssepalp was enabled but you didn't override the default implementation.");
	}
	
	public SCIP_RETCODE conssepalp(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss,
			int nusefulconss, IntByReference scip_result) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {	
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			SCIP_RESULT res = conssepalp(scip, t_arr, nusefulconss);
			scip_result.setValue(res.ordinal());
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	

	
	//CONSSEPASOL
	//Disabled by default, call enableConssepa to enable. Also enables conssepalp
	public SCIP_RESULT conssepasol(SCIP scip, Data[] conss, int nusefulconss, SCIP_SOL sol) {
		throw new RuntimeException("conssepasol was enabled but you didn't override the default implementation.");
	}
	
	public SCIP_RETCODE conssepasol(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss,
			int nusefulconss, SCIP_SOL sol, IntByReference scip_result) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {	
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			SCIP_RESULT res = conssepasol(scip, t_arr, nusefulconss, sol);
			scip_result.setValue(res.ordinal());
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	
	//CONSINITLP
	//Disabled by default, call enableConsinitlp to enable.
	//Return "true" if the problem is INFEASIBLE.
	public boolean consinitlp(SCIP scip, Data[] conss) {
		throw new RuntimeException("consinitlp was enabled but you didn't override the default implementation.");
	}
	
	public SCIP_RETCODE consinitlp(SCIP scip, SCIP_CONSHDLR conshdlr, Pointer conss, int nconss,
			ByteByReference infeasible) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {	
			@SuppressWarnings("unchecked")
			Data[] t_arr = (Data[])Array.newInstance(d_class, nconss);
			for(int i=0; i<nconss; i++) {
				SCIP_CONS scip_cons = new SCIP_CONS(conss.getPointer(8 * i));
				t_arr[i] = cons_mapping.get(scip_cons);
			}
			boolean infeas_res = consinitlp(scip, t_arr);
			infeasible.setValue(infeas_res?(byte)1:0);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIP_ERROR;
		}
		return SCIP_OKAY;
	}
	
	//CONSRESPROP
	public SCIP_RESULT consresprop(SCIP scip, Data cons, SCIP_VAR infervar, int inferinfo,
			SCIP_BOUNDTYPE boundtype, SCIP_BDCHGIDX bdchgidx, double relaxedbd) {
		throw new RuntimeException("consresprop was enabled but you didn't override the default implementation.");
	}
	
	public SCIP_RETCODE consresprop(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS scip_cons,
    		SCIP_VAR infervar, int inferinfo, SCIP_BOUNDTYPE boundtype, SCIP_BDCHGIDX bdchgidx,
    		double relaxedbd, IntByReference scip_result) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		//Call implementation with try{} wrapping
		try {	
			Data cons = cons_mapping.get(scip_cons);
			SCIP_RESULT res = consresprop(scip, cons, infervar, inferinfo, boundtype, bdchgidx, relaxedbd);
			scip_result.setValue(res.ordinal());
		} catch (RuntimeException e) {
			return SCIP_ERROR;
		}	
		return SCIP_OKAY;
	}
	
	//CONSACTIVE
	protected void consactive(SCIP scip, Data cons) {
		throw new RuntimeException("consactive was enabled but you didn't override the default implementation.");
	}
	
	public SCIP_RETCODE consactive(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS scip_cons) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		try {
			Data cons = cons_mapping.get(scip_cons);
			consactive(scip, cons);
			return SCIP_OKAY;
		} catch(RuntimeException e) {
			e.printStackTrace();
			return SCIP_NOTIMPLEMENTED;
		}
	}

	//CONSDEACTIVE
	protected void consdeactive(SCIP scip, Data cons) {
		throw new RuntimeException("consdeactive was enabled but you didn't override the default implementation.");
	}
	
	public SCIP_RETCODE consdeactive(SCIP scip, SCIP_CONSHDLR conshdlr, SCIP_CONS scip_cons) {
		//Sanity check on conshdlr
		if(!conshdlr.equals(this.conshdlr)) {
			System.err.println("Unexpected conshdlr "+conshdlr);
			return SCIP_INVALIDDATA;
		}
		try {
			Data cons = cons_mapping.get(scip_cons);
			consdeactive(scip, cons);
			return SCIP_OKAY;
		} catch(RuntimeException e) {
			e.printStackTrace();
			return SCIP_NOTIMPLEMENTED;
		}
	}
}

package JNA_SCIP;

import java.util.HashMap;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.DoubleByReference;

import static JNA_SCIP.SCIP_RETCODE.*;
import static JNA_SCIP.SCIP_RESULT.*;

public abstract class Pricer {
	//Map SCIP's internal PRICERs to our Java objects.
	static HashMap<SCIP_PRICER, Pricer> pricer_mapping = new HashMap<>();
	
	//Needs to know its name for purposes of finding its PRICER object in subSCIPs.
	public final SCIP_PRICER pricer;
	public final String name;
	
	//We need to keep hard references here to keep the callbacks from being garbage collected
	private SCIP_DECL_PRICERCOPY	pricercopy = this::pricercopy;
	private SCIP_DECL_PRICERFREE	pricerfree = this::pricerfree;
	private SCIP_DECL_PRICERINIT	pricerinit = this::pricerinit;
	private SCIP_DECL_PRICEREXIT	pricerexit = this::pricerexit;
	private SCIP_DECL_PRICERINITSOL	pricerinitsol = this::pricerinitsol;
	private SCIP_DECL_PRICEREXITSOL	pricerexitsol = this::pricerexitsol;
	private SCIP_DECL_PRICERREDCOST	pricerredcost = this::pricerredcost;
	private SCIP_DECL_PRICERFARKAS	pricerfarkas = this::pricerfarkas;

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
	public Pricer(SCIP scip, String name, String desc, int priority, boolean delay) {
		this.name = name;
		pricer_mapping = new HashMap<>();
		
		SCIP_PRICER pricer = scip.includePricerBasic(name, desc, priority, delay,
				pricerredcost, pricerfarkas, null);
		scip.setPricerCopy(pricer, pricercopy);
		scip.setPricerFree(pricer, pricerfree);
		scip.setPricerInit(pricer, pricerinit);
		scip.setPricerExit(pricer, pricerexit);
		scip.setPricerInitsol(pricer, pricerinitsol);
		scip.setPricerExitsol(pricer, pricerexitsol);
		
		this.pricer = pricer;
		
		pricer_mapping.put(pricer, this);
	}
	
	public static Pricer findJavaPricer(SCIP_PRICER pricer) {
		return pricer_mapping.get(pricer);
	}
	public SCIP_PRICER findScipPricer(SCIP scip) {
		return scip.findPricer(this.name);
	}
	
	//If we're in a subscip, go the right one to work in
	public Pricer findTrueHdlr(SCIP scip) {
		return (Pricer)findJavaPricer(findScipPricer(scip));
	}
	
	/// Methods to override ///
	
	//Required methods
	//return "true" for success (gets mapped to SCIP_SUCCESS) or "false" (SCIP_DIDNOTRUN)
	//lowerbound and stopearly can be used, or ignored more likely
	public abstract boolean pricerredcost(SCIP scip,
			DoubleByReference lowerbound,//double*
			IntByReference stopearly//bool
		);
	
	//return "true" for success (gets mapped to SCIP_SUCCESS) or "false" (SCIP_DIDNOTRUN)
	public abstract boolean pricerfarkas(SCIP scip);
	
	//Optional methods to override
	
	//Pricercopy: return "true" if it carried over
	public boolean pricercopy(SCIP scip) {
		return false;
	}
	
	public void pricerfree(SCIP scip) {
		//do nothing
	}
	public void pricerinit(SCIP scip) {
		//do nothing
	}
	public void pricerexit(SCIP scip) {
		//do nothing
	}
	public void pricerinitsol(SCIP scip) {
		//do nothing
	}
	public void pricerexitsol(SCIP scip) {
		//do nothing
	}

	//Methods below are wrappers for user-supplied functions
	
	//wrappers
	public SCIP_RETCODE pricerredcost(SCIP scip, SCIP_PRICER pricer,
			DoubleByReference lowerbound,//double*
			IntByReference stopearly,//SCIP_Bool*	
			IntByReference scip_result) {
		if(!pricer.equals(this.pricer)) {
			System.err.println("Unexpected pricer "+pricer);
			scip_result.setValue(SCIP_DIDNOTRUN.ordinal());
			return SCIP_INVALIDDATA;
		}

		boolean success = pricerredcost(scip, lowerbound, stopearly);
		scip_result.setValue(success ? SCIP_SUCCESS.ordinal() : SCIP_DIDNOTRUN.ordinal());
		return SCIP_OKAY;
	}
	
	public SCIP_RETCODE pricerfarkas(SCIP scip, SCIP_PRICER pricer, IntByReference scip_result) {
		if(!pricer.equals(this.pricer)) {
			System.err.println("Unexpected pricer "+pricer);
			scip_result.setValue(SCIP_DIDNOTRUN.ordinal());
			return SCIP_INVALIDDATA;
		}

		boolean success = pricerfarkas(scip);
		scip_result.setValue(success ? SCIP_SUCCESS.ordinal() : SCIP_DIDNOTRUN.ordinal());
		return SCIP_OKAY;
	}
	
	public SCIP_RETCODE pricercopy(SCIP scip, SCIP_PRICER pricer, IntByReference valid_ptr) {
		if(!pricer.equals(this.pricer)) {
			System.err.println("Unexpected pricer "+pricer);
			valid_ptr.setValue(0);
			return SCIP_INVALIDDATA;
		}
		
		boolean valid = pricercopy(scip);
		valid_ptr.setValue((byte) (valid ? 1 : 0));
		return SCIP_OKAY;
	}
	
	public SCIP_RETCODE pricerfree(SCIP scip, SCIP_PRICER pricer) {
		if(!pricer.equals(this.pricer)) {
			System.err.println("Unexpected pricer "+pricer);
			return SCIP_INVALIDDATA;
		}
		try {
			pricerfree(scip);
			return SCIP_OKAY;
		} catch (RuntimeException e) {
			System.err.println("Pricer error: "+e);
			return SCIP_ERROR;
		}
	}
	
	public SCIP_RETCODE pricerinit(SCIP scip, SCIP_PRICER pricer) {
		if(!pricer.equals(this.pricer)) {
			System.err.println("Unexpected pricer "+pricer);
			return SCIP_INVALIDDATA;
		}
		try {
			pricerinit(scip);
			return SCIP_OKAY;
		} catch (RuntimeException e) {
			System.err.println("Pricer error: "+e);
			return SCIP_ERROR;
		}
	}
	
	public SCIP_RETCODE pricerexit(SCIP scip, SCIP_PRICER pricer) {
		if(!pricer.equals(this.pricer)) {
			System.err.println("Unexpected pricer "+pricer);
			return SCIP_INVALIDDATA;
		}
		try {
			pricerexit(scip);
			return SCIP_OKAY;
		} catch (RuntimeException e) {
			System.err.println("Pricer error: "+e);
			return SCIP_ERROR;
		}
	}
	
	public SCIP_RETCODE pricerinitsol(SCIP scip, SCIP_PRICER pricer) {
		if(!pricer.equals(this.pricer)) {
			System.err.println("Unexpected pricer "+pricer);
			return SCIP_INVALIDDATA;
		}
		try {
			pricerinitsol(scip);
			return SCIP_OKAY;
		} catch (RuntimeException e) {
			System.err.println("Pricer error: "+e);
			return SCIP_ERROR;
		}
	}
	
	public SCIP_RETCODE pricerexitsol(SCIP scip, SCIP_PRICER pricer) {
		if(!pricer.equals(this.pricer)) {
			System.err.println("Unexpected pricer "+pricer);
			return SCIP_INVALIDDATA;
		}
		try {
			pricerexitsol(scip);
			return SCIP_OKAY;
		} catch (RuntimeException e) {
			System.err.println("Pricer error: "+e);
			return SCIP_ERROR;
		}
	}
}

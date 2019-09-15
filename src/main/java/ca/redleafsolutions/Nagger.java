package ca.redleafsolutions;

public class Nagger extends Thread {
	protected NaggerVictim victim = null;

	/** Creates a new instance of Nagger */
	public Nagger (NaggerVictim victim) {
		this (victim, "Nagger of " + victim.getClass ().getSimpleName ());
	}

	public Nagger (NaggerVictim victim, String name) {
		super (name);
		this.victim = victim;
	}

	public Nagger execute () {
		victim.again ();
		return this;
	}
	
	@Override
	public void interrupt () {
		synchronized (victim) {
			victim.notifyAll ();
		}
	}

	/**
	 * If this thread was constructed using a separate <code>Runnable</code> run
	 * object, then that <code>Runnable</code> object's <code>run</code> method
	 * is called; otherwise, this method does nothing and returns.
	 * <p>
	 * Subclasses of <code>Thread</code> should override this method.
	 * 
	 * @see java.lang.Thread#start()
	 * @see java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable,
	 *      java.lang.String)
	 * @see java.lang.Runnable#run()
	 * 
	 */
	public void run () {
		while (!victim.done ()) {
			try {
				victim.again ();
				victim.waitCondition ();
			} catch (Throwable e) {
				victim.handleNaggerException (e);
			}
		}
	}
}

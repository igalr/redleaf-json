package ca.redleafsolutions;

public interface NaggerVictim {
	void again ();
	void waitCondition ();
	boolean done ();
	void handleNaggerException (Throwable e);
}

package ca.redleafsolutions;

import java.util.Collection;
import java.util.LinkedList;

public class UniqueList<T> extends LinkedList<T> {
	private static final long serialVersionUID = -1851899212372812308L;

	@Override
	public boolean add (T element) {
		if (this.contains (element))
			return false;
		return super.add (element);

	}

	@Override
	public void add (int index, T element) {
		if (this.contains (element))
			return;
		super.add (index, element);
	}
	
	@Override
	public boolean addAll (Collection<? extends T> c) {
		boolean changed = false;
		for (T element:c) {
			changed |= add (element);
		}
		return changed;
	}
	
	@Override
	public boolean addAll (int index, Collection<? extends T> c) {
		throw new RuntimeException ("Method not supported for class " + this.getClass ().getSimpleName ());
	}
	
	@Override
	public void addFirst (T element) {
		if (this.contains (element))
			return;
		super.addFirst (element);
	}
	
	@Override
	public void addLast (T element) {
		if (this.contains (element))
			return;
		super.addLast (element);
	}
}

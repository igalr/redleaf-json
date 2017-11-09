package ca.redleafsolutions.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class _ObjectList implements MapOrList {
	private List<Object> list;
	
	public _ObjectList () {
		list = new LinkedList<> ();
	}

	public _ObjectList (Collection<? extends Object> collection) {
		list = new LinkedList<> (collection);
	}
	
	public String join (String string) {
		String s = "";
		for (Object item:list) {
			if (s.length () > 0) s += ",";
			s += item;
		}
		return s;
	}
}

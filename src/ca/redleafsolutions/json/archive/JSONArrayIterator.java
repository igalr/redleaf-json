package ca.redleafsolutions.json.archive;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;

@Deprecated
public class JSONArrayIterator implements Iterator<Object> {
	private JSONArray array;
	private int index;

	public JSONArrayIterator (JSONArray json) {
		this.array = json;
		this.index = 0;
	}

	@Override
	public boolean hasNext () {
		return index < array.length ();
	}

	@Override
	public Object next () {
		try {
			return array.get (index++);
		} catch (JSONException e) {
//			iLogger.severe ("JSON array has " + array.length () + " elements and still errored at index " + index);
			return null;
		}
	}

	@Override
	public void remove () {
		array.remove (index-1);
	}
}

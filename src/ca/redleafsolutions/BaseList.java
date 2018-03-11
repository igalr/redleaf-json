package ca.redleafsolutions;

import java.util.Collection;
import java.util.LinkedList;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable;

@SuppressWarnings ("serial")
public class BaseList<T> extends LinkedList<T> implements JSONWritable {
	public BaseList () {
		super ();
	}

	public BaseList (Collection<? extends T> olist) {
		this.addAll (olist);
	}

	public BaseList (T... functions) {
		for (int i=0; i<functions.length; ++i) {
			this.add (functions[i]);
		}
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newArray ();
		for (int i=0; i<size (); ++i) {
			Object value = this.get (i);
			if (!value.getClass ().isPrimitive ()) {
				if (value instanceof String) {
				} else if (value instanceof JSONWritable) {
					value = ((JSONWritable)value).toJSON ();
				} else {
					value = JSONUtils.toJSON (value);
				}
			}
			json.put (value);
		}
		return json;
	}
}

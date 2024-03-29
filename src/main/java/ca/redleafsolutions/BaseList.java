package ca.redleafsolutions;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable;

public class BaseList<T> extends LinkedList<T> implements JSONWritable {
	public BaseList () {
		super ();
	}

	public BaseList (Collection<? extends T> olist) {
		this.addAll (olist);
	}

	@SafeVarargs
	public BaseList (T... items) {
		this.addAll (Arrays.asList (items));
	}

	public BaseList<T> addAnd (T item) {
		this.add (item);
		return this;
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
	
	public String join (String separator) {
		StringBuffer sb = new StringBuffer ();
		for (T item:this) {
			if (sb.length () > 0)
				sb.append (separator);
			sb.append (item.toString ());
		}
		return sb.toString ();
	}
}

package ca.redleafsolutions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable;

public class BaseSet<T> extends HashSet<T> implements JSONWritable {
	public BaseSet () {
		super ();
	}

	public BaseSet (Set<? extends T> set) {
		super.addAll (set);
	}

	public BaseSet (Collection<? extends T> set) {
		super.addAll (set);
	}

	@SafeVarargs
	public BaseSet (T... items) {
		super.addAll (Arrays.asList (items));
	}

	public BaseSet<T> addAnd (T item) {
		super.add (item);
		return this;
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newArray ();
		for (Object value:this) {
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

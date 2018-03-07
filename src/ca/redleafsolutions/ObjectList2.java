package ca.redleafsolutions;

import java.util.Collection;
import java.util.LinkedList;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadWritable2;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable2;

@SuppressWarnings ("serial")
public class ObjectList2 extends LinkedList<Object> implements JSONReadWritable2 {
	public ObjectList2 () {
		super ();
	}

	public ObjectList2 (Collection<? extends Object> collection) {
		super (collection);
	}

	public ObjectList2 (JSONItem.Array json) throws JSONValidationException {
		fromJSON (json);
	}

	@Override
	public String toString () {
		try {
			return toJSON ().toString ();
		} catch (JSONValidationException e) {
			return super.toString ();
		}
	}
	
	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newArray ();
		for (Object value: this) {
			if (value instanceof JSONWritable2) {
				value = ((JSONWritable2)value).toJSON ();
			} else {
				value = JSONUtils.toJSON (value);
			}
			json.put (value);
		}
		return json;
	}

	@Override
	public void fromJSON (JSONItem json) throws JSONValidationException {
		if (!json.isArray ())
			throw new JSONValidationException.TypeMismatch ("JSONItem.Object", "JSONItem.Array");
		
		for (int i=0; i<json.length (); ++i) {
			Object value = json.get (i);
			if (value instanceof JSONItem) {
				if (((JSONItem)value).isArray ()) {
					ObjectList2 list = new ObjectList2 ();
					list.fromJSON ((JSONItem)value);
					this.add (list);
				} else {
					ObjectMap2 map = new ObjectMap2 ();
					map.fromJSON ((JSONItem)value);
					this.add (map);
				}
			} else {
				this.add (value);
			}
		}
	}

	public String join (String string) {
		String s = "";
		for (Object item:this) {
			if (s.length () > 0) s += ",";
			s += item;
		}
		return s;
	}
}

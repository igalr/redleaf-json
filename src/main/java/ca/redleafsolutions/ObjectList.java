package ca.redleafsolutions;

import java.util.Collection;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadWritable;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable;

public class ObjectList extends BaseList<Object> implements JSONReadWritable {
	public ObjectList () {
		super ();
	}

	public ObjectList (Collection<? extends Object> collection) {
		super (collection);
	}

	public ObjectList (JSONItem.Array json) throws JSONValidationException {
		fromJSON (json);
	}

	public ObjectList (Object[] list) {
		super (list);
	}
	
	@SuppressWarnings ("unchecked")
	public <T> T get (int index, Class<T> cls) {
		return (T)get(index);
	}

	public <T> T tryGet (int index, T defaultValue) {
		try {
			@SuppressWarnings ("unchecked")
			T value = (T)get(index);
			if (value == null) {
				value = defaultValue;
			}
			return value;
		} catch (ClassCastException | IndexOutOfBoundsException e) {
			return  defaultValue;
		}
	}

	public String join (String delimiter) {
		String s = "";
		for (Object item:this) {
			if (s.length () > 0)
				s += delimiter;
			if (item instanceof Number) {
				s += item.toString ();
			} else if (item instanceof Boolean) {
				s += item.toString ();
			} else {
				s += "\"" + item.toString () + "\"";
			}
		}
		return s;
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
			if (value instanceof JSONWritable) {
				value = ((JSONWritable)value).toJSON ();
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
					ObjectList list = new ObjectList ();
					list.fromJSON ((JSONItem)value);
					this.add (list);
				} else {
					ObjectMap map = new ObjectMap ();
					map.fromJSON ((JSONItem)value);
					this.add (map);
				}
			} else {
				this.add (value);
			}
		}
	}
}

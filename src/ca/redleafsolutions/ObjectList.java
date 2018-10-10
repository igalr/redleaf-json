package ca.redleafsolutions;

import java.util.Collection;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadWritable;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable;

@SuppressWarnings ("serial")
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

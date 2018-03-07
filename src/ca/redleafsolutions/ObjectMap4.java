package ca.redleafsolutions;

import java.util.Map;
import java.util.TreeMap;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable2;

@SuppressWarnings ("serial")
public class ObjectMap4<T> extends TreeMap<String, T> implements JSONWritable2 {
	public ObjectMap4 () {
		super ();
	}

	public ObjectMap4 (Map<String, ? extends T> omap) {
		this.putAll (omap);
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newObject ();
		for (String key:this.keySet ()) {
			Object value = this.get (key);
			if (value instanceof JSONWritable2) {
				value = ((JSONWritable2)value).toJSON ();
			}
			json.put (key, value);
		}
		return json;
	}
}

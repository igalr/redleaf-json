package ca.redleafsolutions;

import java.util.Map;
import java.util.TreeMap;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable;

@SuppressWarnings ("serial")
public class BaseMap<T> extends TreeMap<String, T> implements JSONWritable {
	public BaseMap () {
		super ();
	}

	public BaseMap (Map<String, ? extends T> omap) {
		this.putAll (omap);
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newObject ();
		for (String key:this.keySet ()) {
			Object value = this.get (key);
			if (!value.getClass ().isPrimitive ()) {
				if (value instanceof String) {
				} else if (value instanceof JSONWritable) {
					value = ((JSONWritable)value).toJSON ();
				} else {
					value = JSONUtils.toJSON (value);
				}
			}
			json.put (key, value);
		}
		return json;
	}
}

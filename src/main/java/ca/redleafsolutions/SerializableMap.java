package ca.redleafsolutions;

import java.util.TreeMap;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadWritable;
import ca.redleafsolutions.json.JSONValidationException;

public abstract class SerializableMap<T extends JSONReadWritable> extends TreeMap<String, T> implements JSONReadWritable {
	public SerializableMap () throws JSONValidationException {
	}
	public SerializableMap (JSONItem json) throws JSONValidationException {
		fromJSON (json);
	}

	@Override
	public void fromJSON (JSONItem json) throws JSONValidationException {
		for (Object okey:json.listKeys ()) { 
			String key = (String)okey;
			put (key, newInstance (json.getJSON (key)));
		}
	}
	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newObject ();
		for (String key:keySet ()) {
			json.put (key, get (key).toJSON ());
		}
		return json;
	}

	protected abstract T newInstance (JSONItem json) throws JSONValidationException;
}

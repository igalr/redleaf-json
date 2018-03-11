package ca.redleafsolutions;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadWritable;
import ca.redleafsolutions.json.JSONValidationException;

public class KeyValuePair<T> implements JSONReadWritable {
	private String key;
	private T value;

	public KeyValuePair (String key, T value) {
		this.key = key;
		this.value = value;
	}

	public String getKey () {
		return key;
	}

	public T getValue () {
		return value;
	}

	@Override
	public String toString () {
		try {
			return toJSON ().toString ();
		} catch (JSONValidationException e) {
			return "{ " + key + ", " + value + " }";
		}
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		return JSONItem.newObject ().put (key, value);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void fromJSON (JSONItem json) throws JSONValidationException {
		if (json.isArray ()) {
			throw new JSONValidationException.TypeMismatch ("json array", "json object");
		}
		if (json.length () != 1) {
			throw new JSONValidationException.TypeMismatch ("multi key", "single key");
		}
		for (Object okey:json.listKeys ()) {
			key = okey.toString ();
			try {
				value = (T)json.get (key);
			} catch (ClassCastException e) {
				throw new JSONValidationException ("Type mismatch while parsing JSON to KeyValuePair");
			}
		}
	}
}

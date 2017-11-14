package ca.redleafsolutions.json;

import ca.redleafsolutions.json.archive.JSONValidationException;

public class KeyValue<T> implements JSONWritable2 {
	private String key;
	private T value;

	public KeyValue (String key, T value) {
		this.key = key;
		this.value = value;
	}

	public String getOne () {
		return key;
	}

	public T getTwo () {
		return value;
	}

	@Override
	public String toString () {
		return "[" + key + "," + value + "]";
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		return JSONItem.newObject ().put (key, value);
	}
}

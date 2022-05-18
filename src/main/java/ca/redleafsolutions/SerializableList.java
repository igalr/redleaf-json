package ca.redleafsolutions;

import java.util.LinkedList;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadWritable;
import ca.redleafsolutions.json.JSONValidationException;

public abstract class SerializableList<T extends JSONReadWritable> extends LinkedList<T> implements JSONReadWritable {
	public SerializableList () throws JSONValidationException {}

	public SerializableList (JSONItem json) throws JSONValidationException {
		fromJSON (json);
	}

	@Override
	public void fromJSON (JSONItem json) throws JSONValidationException {
		json = JSONItem.forceJSONArray (json);
		for (int i = 0; i < json.length (); ++i) {
			add (newInstance (json.getJSON (i)));
		}
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newArray ();
		for (T item:this) {
			json.put (item.toJSON ());
		}
		return json;
	}

	protected abstract T newInstance (JSONItem json) throws JSONValidationException;
}

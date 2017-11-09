package ca.redleafsolutions.json.archive;

import org.json.JSONArray;

public interface JSONListReadable {
	void fromJSON (JSONArray json) throws JSONValidationException;
}

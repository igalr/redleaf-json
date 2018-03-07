package ca.redleafsolutions.json.archive;

import org.json.JSONArray;

import ca.redleafsolutions.json.JSONValidationException;

public interface JSONListReadable {
	void fromJSON (JSONArray json) throws JSONValidationException;
}

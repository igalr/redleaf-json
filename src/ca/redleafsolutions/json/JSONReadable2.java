package ca.redleafsolutions.json;

import ca.redleafsolutions.json.archive.JSONValidationException;

public interface JSONReadable2 {
	void fromJSON (JSONItem json) throws JSONValidationException;
}

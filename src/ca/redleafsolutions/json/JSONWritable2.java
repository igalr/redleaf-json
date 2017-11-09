package ca.redleafsolutions.json;

import ca.redleafsolutions.json.archive.JSONValidationException;

public interface JSONWritable2 {
	JSONItem toJSON () throws JSONValidationException;
}

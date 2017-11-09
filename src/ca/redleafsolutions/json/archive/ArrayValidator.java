package ca.redleafsolutions.json.archive;

import org.json.JSONArray;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONItem.Array;

public class ArrayValidator<T> extends JSONElementValidator {
	@Override
	boolean validate (Object object) {
		return (object instanceof JSONArray) || (object instanceof JSONItem.Array);
	}
}

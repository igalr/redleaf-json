package ca.redleafsolutions.json;

import org.json.JSONArray;

public class ArrayValidator<T> extends JSONElementValidator {
	@Override
	boolean validate (Object object) {
		return (object instanceof JSONArray) || (object instanceof JSONItem.Array);
	}
}

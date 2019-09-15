package ca.redleafsolutions.json.archive;

import java.util.HashMap;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONValidationException;

public class JSONValidator extends HashMap<String, JSONElementValidator> {
	private static final long serialVersionUID = 482870210185825384L;

	public void validate (JSONItem obj) throws JSONValidationException {
		for (String key:keySet ()) {
			JSONElementValidator fieldvalidator = get (key);

			try {
				Object value = obj.get (key);
				if (!fieldvalidator.validate (value)) {
					throw new JSONValidationException.IllegalValue (key, value);
				}
			} catch (JSONValidationException e) {
				if (!fieldvalidator.isOptional ()) {
					throw new JSONValidationException.MissingKey (key);
				}
			}
		}
	}
}

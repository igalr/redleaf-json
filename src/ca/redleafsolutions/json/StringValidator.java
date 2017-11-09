package ca.redleafsolutions.json;

public class StringValidator extends JSONElementValidator {
	@Override
	boolean validate (Object object) {
		return object instanceof String;
	}
}

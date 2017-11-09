package ca.redleafsolutions.json;

public class BooleanValidator extends JSONElementValidator {
	@Override
	boolean validate (Object object) {
		return object instanceof Boolean;
	}
}

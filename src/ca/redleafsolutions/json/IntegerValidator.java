package ca.redleafsolutions.json;

public class IntegerValidator extends JSONElementValidator {
	@Override
	boolean validate (Object object) {
		return object instanceof Integer;
	}
}

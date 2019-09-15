package ca.redleafsolutions.json.archive;

public class IntegerValidator extends JSONElementValidator {
	@Override
	boolean validate (Object object) {
		return object instanceof Integer;
	}
}

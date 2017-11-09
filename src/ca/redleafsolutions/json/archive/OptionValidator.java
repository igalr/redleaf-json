package ca.redleafsolutions.json.archive;

public class OptionValidator<T> extends JSONElementValidator {
	private T[] options;

	@SafeVarargs
	public OptionValidator (T... options) {
		this.options = options;
	}

	@Override
	boolean validate (Object object) {
		for (T option:options) {
			if (option.equals (object))
				return true;
		}
		return false;
	}
}

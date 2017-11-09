package ca.redleafsolutions.json;

abstract public class JSONElementValidator {
	protected boolean optional = true;

	public JSONElementValidator optional (boolean b) {
		this.optional = b;
		return this;
	}

	public boolean isOptional () {
		return optional;
	}

	abstract boolean validate (Object object);
}

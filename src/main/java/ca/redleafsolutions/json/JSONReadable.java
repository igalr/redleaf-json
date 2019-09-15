package ca.redleafsolutions.json;

public interface JSONReadable {
	void fromJSON (JSONItem json) throws JSONValidationException;
}

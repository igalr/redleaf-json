package ca.redleafsolutions.json;

public interface JSONWritable {
	JSONItem toJSON () throws JSONValidationException;
}

package ca.redleafsolutions.json;

public class ObjectDiff implements JSONWritable {
	private Object o1;
	private Object o2;

	public ObjectDiff (Object o1, Object o2) {
		this.o1 = o1;
		this.o2 = o2;
	}
	
	public boolean isIdentical () {
		return this.o1.equals (this.o2);
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newObject ();
		json.put ("1", o1);
		json.put ("2", o2);
		return json;
	}
}

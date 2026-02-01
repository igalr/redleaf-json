package ca.redleafsolutions;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable;

public class Tuple<T1, T2, T3> implements JSONWritable {
	private T1 one;
	private T2 two;
	private T3 three;

	public Tuple (T1 one, T2 two, T3 three) {
		this.one = one;
		this.two = two;
		this.three = three;
	}

	public T1 getOne () {
		return one;
	}

	public T2 getTwo () {
		return two;
	}

	public T3 getThree () {
		return three;
	}

	@Override
	public String toString () {
		return "[" + one + "," + two + "," + three + "]";
	}

	@Override
	public JSONItem toJSON() throws JSONValidationException {
		JSONItem json = JSONItem.newArray();
		json.put(one);
		json.put(two);
		json.put(three);
		return json;
	}
}

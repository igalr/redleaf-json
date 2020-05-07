package ca.redleafsolutions.json;

import java.util.List;

import ca.redleafsolutions.BaseList;
import ca.redleafsolutions.BaseMap;

public class JSONDiff extends ObjectDiff implements JSONWritable {
	BaseList<String> only1;
	BaseList<String> only2;
	BaseMap<ObjectDiff> diff;

	private JSONItem json1;
	private JSONItem json2;
	private boolean alreadyRun;

	public JSONDiff (JSONItem json1, JSONItem json2) {
		super (json1, json2);

		alreadyRun = false;
		this.json1 = json1;
		this.json2 = json2;

		only1 = new BaseList<String> ();
		only2 = new BaseList<String> ();
		diff = new BaseMap<ObjectDiff> ();
	}

	public JSONDiff diff () throws JSONValidationException {
		if (!alreadyRun) {
			List<? extends Object> keys1 = json1.listKeys ();
			List<? extends Object> keys2 = json2.listKeys ();

			for (Object okey:keys1) {
				String key = okey.toString ();
				if (!json2.has (key)) {
					only1.add (key);
				} else {
					diff (key, json1.get (key), json2.get (key));
				}
			}
			for (Object okey:keys2) {
				String key = okey.toString ();
				if (!json1.has (key)) {
					only1.add (key);
				}
			}

			alreadyRun = true;
		}
		return this;
	}

	private void diff (String key, Object o1, Object o2) throws JSONValidationException {
		if (!o1.getClass ().equals (o2.getClass ())) {
			diff.put (key, new ObjectDiff (o1, o2));
			return;
		}

		if (o1 instanceof JSONItem) {
			JSONDiff jsondiff = new JSONDiff ((JSONItem)o1, (JSONItem)o2);
			if (!jsondiff.isIdentical ()) {
				diff.put (key, jsondiff);
			} else {
			}
		} else if (!o1.equals (o2)) {
			diff.put (key, new ObjectDiff (o1, o2));
		}
	}

	@Override
	public boolean isIdentical () {
		if (!alreadyRun) {
			try {
				diff ();
			} catch (JSONValidationException e) {
				return false;
			}
		}
		return only1.size () + only2.size () + diff.size () == 0;
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newObject ();
		if (!alreadyRun)
			diff ();
		if (only1.size () > 0)
			json.put ("only1", only1.toJSON ());
		if (only2.size () > 0)
			json.put ("only2", only2.toJSON ());
		if (diff.size () > 0)
			json.put ("diff", diff.toJSON ());
		return json;
	}
}

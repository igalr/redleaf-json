package ca.redleafsolutions.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Iterator;
import org.junit.jupiter.api.Test;

public class AppTest {
	private JSONItem createObject () throws JSONValidationException {
		JSONItem jobj = JSONItem.newObject ().put ("int", 27).put ("double", 27.1).put ("string", "Hello World!")
				.put ("boolean", true);
		JSONItem jarr = JSONItem.newArray ().put (27).put (27.1).put ("Hello World!").put (true);
		return JSONItem.newObject ().put ("object", jobj).put ("array", jarr);
	}

	@Test
	public void JSONTypes () throws JSONValidationException {
		JSONItem json = createObject ();

		JSONItem jobj = json.getJSON ("object");
		assertEquals (jobj.isArray (), false);
		assertEquals (jobj.isObject (), true);

		assertEquals (jobj.getInt ("int"), 27);
		assertEquals (jobj.getDouble ("double"), 27.1, 0);
		assertEquals (jobj.getString ("string"), "Hello World!");
		assertEquals (jobj.getBoolean ("boolean"), true);

		JSONItem jarr = json.getJSON ("array");
		assertEquals (jarr.isArray (), true);
		assertEquals (jarr.isObject (), false);

		assertEquals (jarr.getInt (0), 27);
		assertEquals (jarr.getDouble (1), 27.1, 0);
		assertEquals (jarr.getString (2), "Hello World!");
		assertEquals (jarr.getBoolean (3), true);
	}

	@Test
	public void isEmpty () throws JSONValidationException {
		JSONItem json = createObject ();
		assertEquals (json.isEmpty (), false);
		assertEquals (JSONItem.newArray ().isEmpty (), true);
		assertEquals (JSONItem.newObject ().isEmpty (), true);
	}

	@Test
	public void cascade () throws JSONValidationException {
		JSONItem json = createObject ();
		JSONItem jobj = json.getJSON ("object");
		JSONItem json2 = json.cascade (jobj);

		for (Iterator<?> it = jobj.keys (); it.hasNext ();) {
			String key = (String)it.next ();
			assertEquals (json.get (key), json2.get (key));
		}
	}

	@Test
	public void equalCheck () throws JSONValidationException {
		JSONItem obj1 = createObject ();
		JSONItem obj2 = JSONItem.clone (obj1);
		assert (obj1.equals (obj2));
		assert (!obj1.equals (JSONItem.newObject ().put ("key", "value")));
	}

	@Test
	public void booleanTest () throws JSONValidationException {
		JSONItem obj1 = JSONItem.newObject ().put ("bool-true", true).put ("int-true", 1).put ("int-false", 0);
		assert ((Boolean)obj1.get ("bool-true") == true);
		assert (obj1.getBoolean ("int-true") == true);
		assert (obj1.getBoolean ("int-false") == false);
	}

	@Test
	public void diff () throws JSONValidationException {
		JSONItem obj1 = createObject ();
		JSONItem obj2 = JSONItem.clone (obj1);
		obj1.put ("extra1", "Extra item");
		obj2.put ("extra2", 123);
		obj2.getJSON ("object").remove ("string");
		obj2.getJSON ("object").put ("boolean", false);
		JSONItem jdiff = JSONUtils.diff (obj1, obj2);
		assert ("Hello World!".equals (jdiff.getJSON ("diff").getJSON ("object").getJSON ("only1").getString ("string")));
		assert (jdiff.getJSON ("diff").getJSON ("object").getJSON ("diff").getJSON ("boolean").getBoolean (1));
		assert (!jdiff.getJSON ("diff").getJSON ("object").getJSON ("diff").getJSON ("boolean").getBoolean (2));
		assert ("Extra item".equals (jdiff.getJSON ("only1").get ("extra1")));
		assert (123 == jdiff.getJSON ("only2").getInt ("extra2"));
	}
}

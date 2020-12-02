package ca.redleafsolutions.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import ca.redleafsolutions.ObjectList;

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
	public void parse () throws JSONValidationException, IOException {
		JSONItem json;
		try {
			json = JSONItem.parse (null);
			fail ("Should fail parsing a null string");
		} catch (JSONValidationException e) {
		}
		try {
			json = JSONItem.parse ("");
			fail ("Should fail parsing empty string");
		} catch (JSONValidationException e) {
		}
		
		json = JSONItem.parse ("{}");
		assert (json.length () == 0);
		
		json = JSONItem.parse ("[]");
		assert (json.length () == 0);
		
		try {
			json = JSONItem.parse ("X");
			fail ("Should fail parsing non-json string");
		} catch (JSONValidationException e) {
		}
		
		InputStream is = new ByteArrayInputStream ("{}".getBytes ());
		json = JSONItem.fromStream (is);
		is.close ();
		assert (json.length () == 0);
		
		File file = File.createTempFile ("temp.", ".json");
		json = JSONItem.parse ("{\"var1\": 1, \"var2\": \"ABC\", \"var3\": {\"v1\":null, \"array\":[1, 2, 3]}}");
		FileUtils.writeByteArrayToFile (file, json.toString (3).getBytes ());
		JSONItem json1 = JSONItem.fromFile (file);
		assert (json1.equals (json));
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
	
	@Test
	public void typesBoolean () throws JSONValidationException {
		JSONItem legaltrue = JSONItem.fromList (new ObjectList (new Object[] { true, 1, "true", "1", "yes", "on" }));
		JSONItem legalfalse = JSONItem.fromList (new ObjectList (new Object[] { false, 0, "false", "0", "no", "off" }));
		JSONItem ilegal = JSONItem.fromList (new ObjectList (new Object[] { "X", 2, null }));

		for (int i=0; i<legaltrue.length (); ++i) {
			boolean b = legaltrue.getBoolean (i);
			assert (b);
		}
		for (int i=0; i<legalfalse.length (); ++i) {
			boolean b = legalfalse.getBoolean (i);
			assert (!b);
		}
		for (int i=0; i<ilegal.length (); ++i) {
			try {
				ilegal.getBoolean (i);
				fail ("call should not succeed");
			} catch (JSONValidationException.IllegalValue e) {}
		}
	}
}

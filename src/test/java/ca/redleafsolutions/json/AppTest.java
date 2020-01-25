package ca.redleafsolutions.json;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;

public class AppTest {
    private JSONItem createObject() throws JSONValidationException {
        JSONItem jobj = JSONItem.newObject().put("int", 27).put("double", 27.1).put("string", "Hello World!")
                .put("boolean", true);
        JSONItem jarr = JSONItem.newArray().put(27).put(27.1).put("Hello World!").put(true);
        return JSONItem.newObject().put("object", jobj).put("array", jarr);
    }

    @Test
    public void JSONTypes() throws JSONValidationException {
        JSONItem json = createObject();

        JSONItem jobj = json.getJSON("object");
        assertEquals(jobj.isArray(), false);
        assertEquals(jobj.isObject(), true);

        assertEquals(jobj.getInt("int"), 27);
        assertEquals(jobj.getDouble("double"), 27.1, 0);
        assertEquals(jobj.getString("string"), "Hello World!");
        assertEquals(jobj.getBoolean("boolean"), true);

        JSONItem jarr = json.getJSON("array");
        assertEquals(jarr.isArray(), true);
        assertEquals(jarr.isObject(), false);

        assertEquals(jarr.getInt(0), 27);
        assertEquals(jarr.getDouble(1), 27.1, 0);
        assertEquals(jarr.getString(2), "Hello World!");
        assertEquals(jarr.getBoolean(3), true);
    }

    @Test
    public void isEmpty() throws JSONValidationException {
        JSONItem json = createObject();
        assertEquals(json.isEmpty(), false);
        assertEquals(JSONItem.newArray().isEmpty(), true);
        assertEquals(JSONItem.newObject().isEmpty(), true);
    }

    @Test
    public void cascade () throws JSONValidationException {
        JSONItem json = createObject();
        JSONItem jobj = json.getJSON("object");
        JSONItem json2 = json.cascade(jobj);

        for (Iterator<?> it=jobj.keys(); it.hasNext(); ) {
            String key = (String) it.next();
            assertEquals(json.get (key), json2.get (key));
        }
    }

    @Test
    public void equalCheck () throws JSONValidationException {
        JSONItem obj1 = createObject();
        JSONItem obj2 = JSONItem.clone(obj1);
        assert (obj1.equals(obj2));
        assert (!obj1.equals(JSONItem.newObject().put ("key", "value")));
    }
}

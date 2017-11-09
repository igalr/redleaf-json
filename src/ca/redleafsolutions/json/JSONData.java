package ca.redleafsolutions.json;

import java.util.Collection;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONData extends JSONObject {
	private JSONObject jsonobj;
	private boolean strict;

	public JSONData (String validator) throws JSONException {
		this (validator, new JSONObject ());
	}

	public JSONData (String validator, String jsonstr) throws JSONException {
		this (validator, new JSONObject (jsonstr));
	}

	public JSONData (String validator, JSONObject obj) throws JSONException {
		super (validator);
		try {
			strict = getBoolean ("strict");
		} catch (JSONException e) {
			strict = false;
		}
		jsonobj = obj;
	}

	@Override
	public JSONObject put (String key, boolean value) throws JSONException {
		validate (key, Boolean.class);
		return super.put (key, value);
	}

	@Override
	public JSONObject put (String key, double value) throws JSONException {
		validate (key, Double.class);
		return super.put (key, value);
	}

	@Override
	public JSONObject put (String key, int value) throws JSONException {
		validate (key, Integer.class);
		return super.put (key, value);
	}

	@Override
	public JSONObject put (String key, long value) throws JSONException {
		validate (key, Long.class);
		return super.put (key, value);
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public JSONObject put (String key, Collection value) throws JSONException {
		// TODO Auto-generated method stub
		return super.put (key, value);
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public JSONObject put (String key, Map value) throws JSONException {
		// TODO Auto-generated method stub
		return super.put (key, value);
	}

	@Override
	public JSONObject put (String key, Object value) throws JSONException {
		// TODO Auto-generated method stub
		return super.put (key, value);
	}
	
	@Override
	public String toString () {
		return jsonobj.toString ();
	}

	@Override
	public String toString (int indentFactor) throws JSONException {
		return jsonobj.toString (indentFactor);
	}

	private boolean isStrict () {
		return strict;
	}

	private void validate (String key, Class<?> classType) throws JSONException {
		try {
			Class<?> type = getValidatorType (key);
			if (type.isAssignableFrom (classType))
				throw new JSONException ("Mismatched key type. Expected " + type.getName () + " but tried to assign " + classType.getName ());
		} catch (JSONException e) {
			if (isStrict ())
				throw new JSONException ("key type is not defined");
		} catch (ClassNotFoundException e) {
			throw new JSONException ("Invalid class " + e.getCause ());
		}
	}

	private Class<?> getValidatorType (String key) throws JSONException, ClassNotFoundException {
		JSONObject v = this.getJSONObject ("key::" + key);
		String type = v.getString ("type");
		if ("boolean".equalsIgnoreCase (type)) {
			return Boolean.class;
		} else if ("int".equalsIgnoreCase (type)) {
			return Integer.class;
		} else if ("long".equalsIgnoreCase (type)) {
			return Long.class;
		} else if ("float".equalsIgnoreCase (type)) {
			return Double.class;
		} else if ("double".equalsIgnoreCase (type)) {
			return Double.class;
		} else if ("number".equalsIgnoreCase (type)) {
			return Double.class;
		} else if ("string".equalsIgnoreCase (type)) {
			return String.class;
		}
		return Class.forName (type);
	}
}

package ca.redleafsolutions.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public abstract class JSONItem implements Iterable<Object>, JSONWritable {
	public static final JSONItem.NULL NULL = new JSONItem.NULL();

	public static JSONItem parse(String str) throws JSONValidationException {
		if (str == null)
			throw new JSONValidationException("Not a JSON format");

		str = str.trim();
		if (str.length() <= 0)
			throw new JSONValidationException("Not a JSON format");

		try {
			if ('{' == str.charAt(0)) {
				JSONObject json = new JSONObject(str);
				return new JSONItem.Object(json);
			} else if ('[' == str.charAt(0)) {
				JSONArray json = new JSONArray(str);
				return new JSONItem.Array(json);
			}
			throw new JSONValidationException("JSON string must start with either '{' or '[' character");
		} catch (JSONException e) {
			throw new JSONValidationException(e);
		}
	}

	public static JSONItem cast(java.lang.Object obj) throws JSONValidationException {
		if (obj instanceof JSONItem) {
			return (JSONItem) obj;
		} else if (obj instanceof JSONWritable) {
			return ((JSONWritable) obj).toJSON();
		}

		if (obj instanceof Map) {
			return new Object((Map<?, ?>) obj);
		} else if (obj instanceof List) {
			return new Array((List<?>) obj);
		}

		if (obj.getClass().isArray()) {
			try {
				return new JSONItem.Array(new JSONArray(obj));
			} catch (JSONException e) {
				// let it try to convert before throwing an exception
			}
		}

		if (obj instanceof JSONObject) {
			return new JSONItem.Object((JSONObject) obj);
		} else if (obj instanceof JSONArray) {
			return new JSONItem.Array((JSONArray) obj);
		}
		throw new ClassCastException("Can't cast " + obj.getClass() + " to JSONItem");
	}

	public static JSONItem fromStream(InputStream is) throws JSONValidationException, IOException {
		StringBuffer sb = new StringBuffer();

		try {
			byte[] buff = new byte[1024];
			int len;
			while ((len = is.read(buff)) > 0) {
				sb.append(new String(buff, 0, len));
			}
		} catch (IOException e) {
		}

		return parse(sb.toString());
	}

	public static JSONItem newObject() {
		return new JSONItem.Object();
	}

	public static JSONItem newArray() {
		return new JSONItem.Array();
	}

	public static JSONItem fromFile(File file) throws JSONValidationException, IOException {
		return parse(new String(Files.readAllBytes(Paths.get(file.toURI()))));
	}

	public static JSONItem fromXML(String xmlstr) throws JSONValidationException {
		try {
			return new JSONItem.Object(XML.toJSONObject(xmlstr));
		} catch (JSONException e) {
			throw new JSONValidationException(e);
		}
	}

	public static JSONItem.Array forceJSONArray(JSONItem json) {
		if (json.isArray())
			return (JSONItem.Array) json;

		JSONItem.Array jarr = new JSONItem.Array();
		jarr.put(json);
		return jarr;
	}

	public static JSONItem fromMap(Map<String, ? extends java.lang.Object> map) {
		return new JSONItem.Object(map);
	}

	public static JSONItem fromList(List<? extends java.lang.Object> list) {
		return new JSONItem.Array(list);
	}

	public boolean isObject() {
		return false;
	}

	public boolean isArray() {
		return false;
	}

	public static JSONItem clone(JSONItem json) throws JSONValidationException {
		return JSONItem.parse(json.toString());
	}

	public abstract JSONItem put(String key, java.lang.Object o) throws JSONValidationException;

	public abstract JSONItem put(java.lang.Object o) throws JSONValidationException;

	public abstract void remove(String string) throws JSONValidationException;

	public abstract void remove(int string) throws JSONValidationException;

	public abstract java.lang.Object get(String key) throws JSONValidationException;

	public abstract java.lang.Object get(int index) throws JSONValidationException;

	public abstract List<? extends java.lang.Object> listKeys();

	public abstract Iterator<? extends java.lang.Object> keys();

	public abstract boolean has(String key);

	/**
	 * Try and get a value from JSON object. If it fails, return the @defaultValue
	 * The type of the value is determined by the type of the defaultValue provided
	 * 
	 * @param <T>          type of value
	 * @param key          the key to the value to retrieve
	 * @param defaultValue the default value to return in case key value is not
	 *                     valid (missing or type mismatched)
	 * @return The value
	 */
	public <T> T tryGet(String key, T defaultValue) {
		try {
			@SuppressWarnings("unchecked")
			T value = (T) get(key);
			if (value == null) {
				value = defaultValue;
			}
			return value;
		} catch (ClassCastException | JSONValidationException e) {
			return defaultValue;
		}
	}

	public <T> T tryGet(int index, T defaultValue) {
		return tryGet("" + index, defaultValue);
	}

	private JSONItem _getJSONItem(String key, java.lang.Object o) throws JSONValidationException {
		if (o == null) {
			return JSONItem.NULL;
		}

		if (o instanceof JSONItem) {
			return (JSONItem) o;
		} else if (o instanceof JSONObject) {
			return new JSONItem.Object((JSONObject) o);
		} else if (o instanceof JSONArray) {
			return new JSONItem.Array((JSONArray) o);
		} else if (o instanceof JSONWritable) {
			return ((JSONWritable) o).toJSON();
		}
		throw new JSONValidationException.IllegalValue(key, o.getClass().getSimpleName());
	}

	public boolean isEmpty() {
		return length() <= 0;
	}

	public boolean isNULL() {
		return false;
	}

	public JSONItem getJSON(String key) throws JSONValidationException {
		java.lang.Object o = _getJSONItem(key, get(key));
		return (JSONItem) o;
	}

	public JSONItem getJSON(int index) throws JSONValidationException {
		return _getJSONItem("" + index, get(index));
	}

	private String _getString(String key, java.lang.Object o) throws JSONValidationException {
		if (o == null) {
			return "";
		} else if (o instanceof JSONItem) {
			throw new JSONValidationException.IllegalValue(key, o);
		} else {
			return o.toString();
		}
	}

	public String getString(String key) throws JSONValidationException {
		return _getString(key, get(key));
	}

	public String getString(int index) throws JSONValidationException {
		return _getString("" + index, get(index));
	}

	private int _getInt(String key, java.lang.Object o) throws JSONValidationException {
		try {
			if (o instanceof Integer) {
				return (int) o;
			} else if (o instanceof Long) {
				return ((Long) o).intValue();
			} else if (o instanceof Double) {
				return (int) Math.round((double) o);
			}
			return Integer.parseInt(o.toString());
		} catch (Throwable e) {
		}
		throw new JSONValidationException.IllegalValue(key, o.toString());
	}

	public int getInt(String key) throws JSONValidationException {
		return _getInt(key, get(key));
	}

	public int getInt(int index) throws JSONValidationException {
		return _getInt("" + index, get(index));
	}

	private long _getLong(String key, java.lang.Object o) throws JSONValidationException {
		try {
			if (o instanceof Integer) {
				return ((Integer) o).longValue();
			} else if (o instanceof Long) {
				return ((Long) o).longValue();
			} else if (o instanceof Double) {
				return ((Double) o).longValue();
			}
			return Long.parseLong(o.toString());
		} catch (Throwable e) {
		}
		throw new JSONValidationException.IllegalValue(key, o.toString());
	}

	public long getLong(String key) throws JSONValidationException {
		return _getLong(key, get(key));
	}

	public long getLong(int index) throws JSONValidationException {
		return _getLong("" + index, get(index));
	}

	private double _getDouble(String key, java.lang.Object o) throws JSONValidationException {
		if (o instanceof Integer) {
			return (int) o;
		} else if (o instanceof Double) {
			return (double) o;
		} else if (o instanceof Float) {
			double d = (float) o;
			return d;
		}
		try {
			return Double.parseDouble(o.toString());
		} catch (NumberFormatException e) {
			throw new JSONValidationException.IllegalValue(key, o.toString());
		}
	}

	public double getDouble(String key) throws JSONValidationException {
		return _getDouble(key, get(key));
	}

	public double getDouble(int index) throws JSONValidationException {
		return _getDouble("" + index, get(index));
	}

	private boolean _getBoolean(String key, java.lang.Object o) throws JSONValidationException {
		try {
			if (o instanceof Boolean) {
				return (boolean) o;
			} else if (o instanceof String) {
				if ("true".equalsIgnoreCase((String) o) || "1".equalsIgnoreCase((String) o)
						|| "yes".equalsIgnoreCase((String) o) || "on".equalsIgnoreCase((String) o)) {
					return true;
				} else if ("false".equalsIgnoreCase((String) o) || "0".equalsIgnoreCase((String) o)
						|| "no".equalsIgnoreCase((String) o) || "off".equalsIgnoreCase((String) o)) {
					return false;
				}
			} else if (o instanceof Number) {
				int i = ((Number) o).intValue();
				if (i == 1) {
					return true;
				} else if (i == 0) {
					return false;
				}
			}
			throw new JSONValidationException.IllegalValue("boolean", o);
		} catch (ClassCastException e) {
			throw new JSONValidationException.IllegalValue(key, o.toString());
		}
	}

	public boolean getBoolean(String key) throws JSONValidationException {
		return _getBoolean(key, get(key));
	}

	public boolean getBoolean(int index) throws JSONValidationException {
		return _getBoolean("" + index, get(index));
	}

	public abstract int length();

	public abstract String toString();

	public abstract String toString(int indent);

	@Override
	public JSONItem toJSON() throws JSONValidationException {
		return this;
	}

	public void save(File file) throws IOException {
		Files.write(Paths.get(file.toURI()), toString().getBytes());
	}

	public void savePretty(File file) throws IOException {
		Files.write(Paths.get(file.toURI()), toString(3).getBytes());
	}

	protected java.lang.Object _convert(java.lang.Object o) {
		if (o instanceof JSONObject)
			return new JSONItem.Object((JSONObject) o);

		if (JSONObject.NULL.equals(o))
			return null;

		if (o instanceof JSONArray)
			return new JSONItem.Array((JSONArray) o);

		return o;
	}

	@Override
	public boolean equals(java.lang.Object other) {
		if (!(other instanceof JSONItem)) {
			return false;
		}
		JSONItem that = (JSONItem) other;
		List<? extends java.lang.Object> keys = this.listKeys();
		List<? extends java.lang.Object> otherkeys = that.listKeys();
		if (!keys.equals(otherkeys)) {
			return false;
		}

		for (int i = 0; i < keys.size(); ++i) {
			String key = keys.get(i).toString();
			try {
				java.lang.Object v1 = this.get(key);
				java.lang.Object v2 = that.get(key);
				if (v1 == null) {
					if ((v2 != null) && !(v2 instanceof JSONItem.NULL) && !"null".equals(v2)) {
						return false;
					}
				} else if (!v1.equals(v2)) {
					return false;
				}
			} catch (JSONValidationException e) {
				return false;
			}
		}
		return true;
	}

	public static class Object extends JSONItem {
		private Map<String, java.lang.Object> map;

		public Object() {
			this.map = new TreeMap<>();
		}

		private Object(Map<?, ?> map) {
			this.map = new TreeMap<>();
			for (java.lang.Object okey : map.keySet()) {
				String key = (String) okey;
				java.lang.Object value = map.get(okey);
				this.map.put(key, denormalize(value));
			}
		}

		Object(JSONObject json) {
			this();
			for (Iterator<?> it = json.keys(); it.hasNext();) {
				String key = (String) it.next();
				try {
					put(key, _convert(json.get(key)));
				} catch (JSONException e) {
					// this is not reasonable exception for this case the keys are queried
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		public boolean isObject() {
			return true;
		}

		public JSONItem put(java.lang.Object value) throws JSONValidationException {
			throw new JSONValidationException("Must be an Array");
		}

		public JSONItem put(String key, java.lang.Object value) {
			map.put(key, normalize(value));
			return this;
		}

		@Override
		public void remove(String key) throws JSONValidationException {
			map.remove(key);
		}

		@Override
		public void remove(int index) throws JSONValidationException {
			String key = Integer.toString(index);
			if (!map.containsKey(key))
				throw new JSONValidationException.MissingKey(key);
			remove(key);
		}

		@Override
		public java.lang.Object get(String key) throws JSONValidationException {
			if (!map.containsKey(key))
				throw new JSONValidationException.MissingKey(key);
			return map.get(key);
		}

		@Override
		public java.lang.Object get(int index) throws JSONValidationException {
			String key = Integer.toString(index);
			if (!map.containsKey(key))
				throw new JSONValidationException.MissingKey(key);
			return map.get(key);
		}

		private JSONObject toJSONObject() throws JSONException {
			JSONObject json = new JSONObject();
			for (String key : map.keySet()) {
				java.lang.Object value = map.get(key);
				if (value instanceof Object) {
					value = ((Object) value).toJSONObject();
				} else if (value instanceof Array) {
					value = ((Array) value).toJSONArray();
				} else if (value instanceof JSONWritable) {
					try {
						JSONItem j = ((JSONWritable) value).toJSON();
						if (j instanceof JSONItem.Object) {
							value = ((JSONItem.Object) j).toJSONObject();
						} else if (j instanceof JSONItem.Array) {
							value = ((JSONItem.Array) j).toJSONArray();
						}
					} catch (JSONValidationException e) {
					}
				} else if (value == null) {
					value = JSONItem.NULL;
				}
				json.put(key, value);
			}
			return json;
		}

		public String _toString() {
			try {
				JSONObject obj = toJSONObject();
				return obj.toString();
			} catch (JSONException e) {
				return "{}";
			}
		}

		@Override
		public String toString() {
			try {
				JSONObject obj = toJSONObject();
				return obj.toString();
			} catch (JSONException e) {
				return "{}";
			}
		}

		public String toString(int indent) {
			try {
				return toJSONObject().toString(indent);
			} catch (JSONException e) {
				return "{}";
			}
		}

		@Override
		public int length() {
			return map.size();
		}

		@Override
		public List<? extends java.lang.Object> listKeys() {
			return new LinkedList<String>(map.keySet());
		}

		@Override
		public Iterator<?> keys() {
			return map.keySet().iterator();
		}

		@Override
		public boolean has(String key) {
			return map.containsKey(key);
		}

		@Override
		public Iterator<java.lang.Object> iterator() {
			return map.values().iterator();
		}

		public Map<String, java.lang.Object> toMap() {
			Map<String, java.lang.Object> map = new TreeMap<>();
			// System.out.println (this.toString ());
			for (String key : this.map.keySet()) {
				java.lang.Object value = this.map.get(key);
				if (value instanceof JSONItem.Object) {
					value = ((JSONItem.Object) value).toMap();
				} else if (value instanceof JSONItem.Array) {
					value = ((JSONItem.Array) value).toList();
				}
				map.put(key, value);
			}
			return map;
		}
	}

	public static class Array extends JSONItem {
		private List<java.lang.Object> list;

		public Array() {
			list = new LinkedList<java.lang.Object>();
		}

		private Array(List<?> list) {
			this();
			for (java.lang.Object value : list) {
				this.list.add(denormalize(value));
			}
		}

		Array(JSONArray json) {
			this();
			for (int i = 0; i < json.length(); ++i) {
				try {
					put(_convert(json.get(i)));
				} catch (JSONException e) {
					// ignore this error
				}
			}
		}

		@Override
		public boolean isArray() {
			return true;
		}

		@Override
		public JSONItem put(java.lang.Object value) {
			list.add(normalize(value));
			return this;
		}

		@Override
		public JSONItem put(String key, java.lang.Object value) throws JSONValidationException {
			try {
				list.add(numericIndex(key), normalize(value));
			} catch (NumberFormatException e) {
				throw new JSONValidationException("Array must have numeric keys only. Received " + key);
			}
			return this;
		}

		@Override
		public void remove(String key) throws JSONValidationException {
			try {
				remove(numericIndex(key));
			} catch (NumberFormatException e) {
				throw new JSONValidationException("Array must have numeric keys only. Received " + key);
			}
		}

		@Override
		public void remove(int index) throws JSONValidationException {
			if (list.size() <= index)
				throw new JSONValidationException.MissingKey("" + index);
			list.remove(index);
		}

		private JSONArray toJSONArray() throws JSONException {
			JSONArray json = new JSONArray();
			for (java.lang.Object value : list) {
				if (value instanceof Object) {
					value = ((Object) value).toJSONObject();
				} else if (value instanceof Array) {
					value = ((Array) value).toJSONArray();
				} else if (value instanceof JSONWritable) {
					try {
						JSONItem j = ((JSONWritable) value).toJSON();
						if (j instanceof JSONItem.Object) {
							value = ((JSONItem.Object) j).toJSONObject();
						} else if (j instanceof JSONItem.Array) {
							value = ((JSONItem.Array) j).toJSONArray();
						}
					} catch (JSONValidationException e) {
					}
				}
				json.put(value);
			}
			return json;
		}

		@Override
		public String toString() {
			try {
				return toJSONArray().toString();
			} catch (JSONException e) {
				return "[]";
			}
		}

		public String toString(int indent) {
			try {
				return toJSONArray().toString(3);
			} catch (JSONException e) {
				return "[]";
			}
		}

		@Override
		public int length() {
			return list.size();
		}

		@Override
		public java.lang.Object get(int index) throws JSONValidationException {
			if (list.size() <= index)
				throw new JSONValidationException.MissingKey("" + index);
			return list.get(index);
		}

		@Override
		public java.lang.Object get(String key) throws JSONValidationException {
			return get(numericIndex(key));
		}

		@Override
		public List<?> listKeys() {
			List<Integer> list = new LinkedList<Integer>();
			for (int i = 0; i < this.list.size(); ++i) {
				list.add(i);
			}
			return list;
		}

		@Override
		public Iterator<?> keys() {
			return listKeys().iterator();
		}

		@Override
		public boolean has(String key) {
			try {
				return numericIndex(key) < length();
			} catch (NumberFormatException e) {
				return false;
			}
		}

		@Override
		public Iterator<java.lang.Object> iterator() {
			return list.iterator();
		}

		public List<java.lang.Object> toList() {
			List<java.lang.Object> list = new LinkedList<>();
			for (java.lang.Object value : this.list) {
				if (value instanceof JSONItem.Object) {
					value = ((JSONItem.Object) value).toMap();
				} else if (value instanceof JSONItem.Array) {
					value = ((JSONItem.Array) value).toList();
				}
				list.add(value);
			}
			return list;
		}

	}

	protected int numericIndex(String key) throws NumberFormatException {
		try {
			return Integer.parseInt(key);
		} catch (NumberFormatException e) {
			throw new NumberFormatException();
		}
	}

	protected java.lang.Object normalize(java.lang.Object o) {
		if (o instanceof JSONObject) {
			o = new JSONItem.Object((JSONObject) o);
		} else if (o instanceof JSONArray) {
			o = new JSONItem.Array((JSONArray) o);
		}
		return o;
	}

	protected java.lang.Object denormalize(java.lang.Object o) {
		if (o instanceof Map) {
			return new Object((Map<?, ?>) o);
		} else if (o instanceof List) {
			return new Array((List<?>) o);
		} else {
			return o;
		}
	}

	public JSONItem cascade(JSONItem other) throws JSONValidationException {
		for (Iterator<?> it = other.keys(); it.hasNext();) {
			String key = (String) it.next();
			java.lang.Object value = other.get(key);
			try {
				java.lang.Object localvalue = this.get(key);
				if (value instanceof JSONItem.Object) {
					if (localvalue instanceof JSONItem.Object) {
						value = ((JSONItem) localvalue).cascade((JSONItem) value);
					}
				}
				this.put(key, value);
			} catch (JSONValidationException.MissingKey e) {
				this.put(key, value);
			}
		}
		return this;
	}

	static public class NULL extends JSONItem.Object {
		@Override
		public boolean isEmpty() {
			return true;
		}

		public boolean isNULL() {
			return true;
		}

		@Override
		public String toString() {
			return "null";
		}

		@Override
		public boolean equals(java.lang.Object obj) {
			if (obj == null) {
				return true;
			}
			if (obj instanceof JSONItem.NULL) {
				return true;
			}
			if (obj instanceof String) {
				if ("null".equalsIgnoreCase(obj.toString())) {
					return true;
				}
			}
			return false;
		}
	}
}

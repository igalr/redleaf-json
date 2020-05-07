package ca.redleafsolutions.json;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.redleafsolutions.Pair;
import ca.redleafsolutions.json.JSONItem.Array;
import ca.redleafsolutions.json.JSONValidationException.MissingKey;

public class JSONUtils {
	public static final Object NULL = JSONObject.NULL;

	/** Convert a JSONItem to and array (if it is not already)
	 * 
	 * @param json input json item (possible object or array)
	 * @return a json item which is guaranteed to be an array
	 * @throws JSONValidationException */
	public static JSONItem.Array asArray (JSONItem json) throws JSONValidationException {
		if (json.isArray ())
			return (Array)json;
		JSONItem jarr = JSONItem.newArray ();
		jarr.put (json);
		return (Array)jarr;
	}

	public static Object toJSON (Object o) throws JSONValidationException {
		return toJSONValue (o, 0);
	}

	public static Object tryGet_old (JSONObject json, String key, Object defaultValue) {
		try {
			return get_old (json, key);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	public static Object tryGet (JSONItem json, String key, Object defaultValue) {
		try {
			return get (json, key);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	public static Object get (JSONItem json, String key) throws JSONValidationException {
		String[] keys = key.split ("/");
		LinkedList<String> list = new LinkedList<String> ();
		list.addAll (Arrays.asList (keys));
		return recurseItem (list, json);
	}

	public static Object get_old (JSONObject json, String key) throws JSONValidationException {
		String[] keys = key.split ("/");
		LinkedList<String> list = new LinkedList<String> ();
		list.addAll (Arrays.asList (keys));
		return recurse (list, json);
	}

	private static Object recurseItem (LinkedList<String> keys, JSONItem json) throws JSONValidationException {
		if (keys.size () <= 0) {
			return json;
		}
		String key = keys.pop ();
		JSONMatcher matcher = getMatcherFromKey (key);
		Object value = matcher.get (json);

		try {
			return recurseItem (keys, JSONItem.cast (value));
		} catch (ClassCastException e) {
			// return the value as is
		}

		return value;
	}

	private static JSONMatcher getMatcherFromKey (String key) {
		JSONMatcher match;

		int pos = key.indexOf ("[");
		if (pos > 0) {
			Pattern p = Pattern.compile ("\\[(.*?)\\]");
			Matcher m = p.matcher (key);

			while (m.find ()) {
				String group = m.group ();
				group = group.substring (1, group.length () - 1);

				if (group.startsWith (".=")) {
					group = group.substring (2);
					match = new JSONMatcher.Equal (key.substring (0, pos), group);
					return match;
				} else {
					match = new JSONMatcher.Simple (key.substring (0, pos));
					return match;
				}
			}
			match = new JSONMatcher.Simple (key);
		} else {
			match = new JSONMatcher.Simple (key);
		}
		return match;
	}

	private static Object recurse (LinkedList<String> keys, JSONObject json) throws JSONValidationException {
		if (keys.size () <= 0) {
			return json;
		}
		String key = keys.pop ();
		Object value;
		try {
			value = json.get (key);
		} catch (JSONException e) {
			throw new JSONValidationException (e);
		}

		if (value instanceof JSONObject) {
			return recurse (keys, (JSONObject)value);
		} else if (value instanceof JSONArray) {
			return recurse (keys, (JSONArray)value);
		} else if (value.getClass ().isArray ()) {
			JSONArray arr;
			try {
				arr = new JSONArray (value);
			} catch (JSONException e) {
				throw new JSONValidationException (e);
			}
			return getArrayValue (keys, arr);
		}
		return value;
	}

	private static Object recurse (LinkedList<String> keys, JSONArray json) throws JSONValidationException {
		if (keys.size () <= 0) {
			return json;
		}

		String keystr = keys.pop ();
		int key = Integer.parseInt (keystr, 10);
		Object value;
		try {
			value = json.get (key);
		} catch (JSONException e) {
			throw new JSONValidationException (e);
		}

		if (value instanceof JSONObject) {
			return recurse (keys, (JSONObject)value);
		} else if (value instanceof JSONArray) {
			return recurse (keys, (JSONArray)value);
		} else if (value.getClass ().isArray ()) {
			JSONArray arr;
			try {
				arr = new JSONArray (value);
			} catch (JSONException e) {
				throw new JSONValidationException (e);
			}
			return getArrayValue (keys, arr);
		}
		return value;
	}

	private static Object getArrayValue (LinkedList<String> keys, JSONArray list) throws MissingKey {
		if (keys.size () <= 0) {
			return list;
		}

		if (keys.size () > 1) {
			throw new JSONValidationException.MissingKey (keys.toString ());
		}
		int key = Integer.parseInt (keys.get (0), 10);
		if (key >= list.length ()) {
			throw new JSONValidationException.MissingKey (keys.toString ());
		}
		try {
			return list.get (key);
		} catch (JSONException e) {
			throw new JSONValidationException.MissingKey ("" + key);
		}
	}

	@SuppressWarnings ("unchecked")
	private static Object toJSONValue (Object o, int depth) throws JSONValidationException {
		if (depth > 2)
			return o;

		if (o instanceof JSONWritable) {
			return ((JSONWritable)o).toJSON ();
		}

		if (o instanceof Map) {
			JSONObject json = new JSONObject ();
			for (Entry<? extends Object, ? extends Object> entry:((Map<? extends Object, ? extends Object>)o)
					.entrySet ()) {
				try {
					json.put (entry.getKey ().toString (), toJSONValue (entry.getValue (), depth + 1));
				} catch (JSONException e) {
					throw new JSONValidationException (entry.getKey ().toString ());
				}
			}
			return json;
		} else if (o instanceof Iterable) {
			JSONArray jsonarr = new JSONArray ();
			for (Object item:(Iterable<? extends Object>)o) {
				jsonarr.put (toJSONValue (item, depth + 1));
			}
			return jsonarr;
		}
		return o;
	}

	public static JSONItem diff2 (JSONItem json1, JSONItem json2) throws JSONValidationException {
		NodeDiff cv = compareValues2 (null, json1, json2);
		return cv.toJSON ();
	}

	private static NodeDiff compareValues2 (String key, Object v1, Object v2) throws JSONValidationException {
		NodeDiff nodediff = new NodeDiff (key);

		if ((v1 == null) && (v2 != null)) {
			nodediff.only2 (v2);
			// nodediff.diff (v1, v2);
		} else if (v2 == null) {
			nodediff.only1 (v1);
			// nodediff.diff (v1, v2);
		} else {
			if (!v2.getClass ().equals (v1.getClass ())) {
				nodediff.diff (v1, v2);
			} else {
				if (v1 instanceof JSONObject) {
					v1 = new JSONItem.Object ((JSONObject)v1);
					v2 = new JSONItem.Object ((JSONObject)v2);
				} else if (v2 instanceof JSONArray) {
					v1 = new JSONItem.Array ((JSONArray)v1);
					v2 = new JSONItem.Array ((JSONArray)v2);
				}

				if (v1 instanceof JSONItem) {
					JSONItem j1 = (JSONItem)v1;
					JSONItem j2 = (JSONItem)v2;

					for (Object okey1:j1.listKeys ()) {
						String key1 = okey1.toString ();
						Object jv1 = null;
						jv1 = j1.get (key1);

						Object jv2 = null;
						try {
							jv2 = j2.get (key1);
						} catch (JSONValidationException.MissingKey e) {
							// nothing
						}
						nodediff.add (compareValues2 (key1, jv1, jv2));
					}

					for (Object okey2:j2.listKeys ()) {
						String key2 = okey2.toString ();

						Object jv2 = null;
						jv2 = j2.get (key2);

						Object jv1 = null;
						try {
							jv1 = j1.get (key2);
							if (jv1 == null) {
								// nodediff.only2 (jv2);
								nodediff.add (compareValues2 (key2, jv1, jv2));
							} else {
								// this case was already taken care of in the
								// first loop
							}
						} catch (JSONValidationException e) {
							// nodediff.only2 (jv2);
							nodediff.add (compareValues2 (key2, jv1, jv2));
						}
					}
				} else {
					if (!v1.equals (v2)) {
						nodediff.diff (v1, v2);
					}
				}
			}
		}

		return nodediff;
	}

	public static JSONItem diff (String s1, String s2) throws JSONValidationException {
		JSONItem json1 = JSONItem.parse (s1);
		JSONItem json2 = JSONItem.parse (s2);
		return compareValues2 (null, json1, json2).toJSON ();
	}

	public static JSONItem diff (JSONItem o1, JSONItem o2) throws JSONValidationException {
		return new JSONDiff (o1, o2).toJSON ();
	}

	public static JSONItem removeNodes (JSONItem json, String path) {
		return removeNodes (json, Arrays.asList (path.split ("/")));
	}

	private static JSONItem removeNodes (JSONItem json, List<String> path) {
		if (path.size () > 0) {
			try {
				String key = path.get (0);
				if (!"*".equals (key)) {
					Object nodeobj = json.get (key);
					if (nodeobj instanceof JSONItem) {
						JSONItem node = (JSONItem)nodeobj;
						node = removeNodes (node, path.subList (1, path.size ()));
						if (node.length () > 0) {
							json.put (key, node);
						} else {
							json.remove (key);
						}
					} else {
						return JSONItem.newObject ();
					}
				} else {
					for (Object key2:json.listKeys ()) {
						try {
							JSONItem node = json.getJSON ((String)key2);
							node = removeNodes (node, path.subList (1, path.size ()));
							if (node.length () > 0) {
								json.put ((String)key2, node);
							} else {
								json.remove ((String)key2);
							}
						} catch (JSONValidationException e) {
						}
					}
				}
			} catch (JSONValidationException e) {
			}
		} else {
			return JSONItem.newObject ();
		}
		return json;
	}

	static private class NodeDiff implements JSONWritable, Comparable<NodeDiff> {
		private static final String DIFF_LABEL = "diff";
		private static final String ONLY1_LABEL = "only1";
		private static final String ONLY2_LABEL = "only2";
		private String key;
		private List<Object> only1;
		private List<Object> only2;
		private List<Pair<Object, Object>> diff;
		private Set<NodeDiff> children;
		private boolean empty;

		private NodeDiff (String key) {
			this.key = key;
			if (DIFF_LABEL.equals (key) || ONLY1_LABEL.equals (key) || ONLY2_LABEL.equals (key))
				this.key = "~" + this.key;
			only1 = new LinkedList<> ();
			only2 = new LinkedList<> ();
			diff = new LinkedList<Pair<Object, Object>> ();
			children = new TreeSet<JSONUtils.NodeDiff> ();
			empty = true;
		}

		public void add (NodeDiff compareValues) {
			if (!compareValues.isEmpty ()) {
				children.add (compareValues);
				empty = false;
			}
		}

		private boolean isEmpty () {
			return empty;
		}

		@SuppressWarnings ({ "unchecked", "rawtypes" })
		public void diff (Object v1, Object v2) {
			diff.add (new Pair (v1, v2));
			empty = false;
		}

		public void only1 (Object value) {
			only1.add (value);
			empty = false;
		}

		public void only2 (Object value) {
			only2.add (value);
			empty = false;
		}

		@Override
		public JSONItem toJSON () throws JSONValidationException {
			JSONItem json = JSONItem.newObject ();
			if (empty)
				return json;

			for (NodeDiff child:children) {
				if (child.key != null)
					json.put (child.key, child.toJSON ());
				else {
					for (NodeDiff gransdson:child.children) {
						json.put (gransdson.key, gransdson.toJSON ());
					}
				}
			}
			if (diff.size () > 0) {
				if (diff.size () > 1) {
					json.put (DIFF_LABEL, diff);
				} else {
					Pair<Object, Object> pair = diff.get (0);
					Object one = pair.getOne ();
					Object two = pair.getTwo ();
					JSONItem jpair = JSONItem.newObject ();
					jpair.put ("1", (one instanceof JSONWritable)? ((JSONWritable)one).toJSON (): one);
					jpair.put ("2", (two instanceof JSONWritable)? ((JSONWritable)two).toJSON (): two);
					json.put (DIFF_LABEL, jpair);
				}
			}
			if (only1.size () > 0) {
				if (only1.size () > 1) {
					json.put (ONLY1_LABEL, only1);
				} else {
					json.put (ONLY1_LABEL, only1.get (0));
				}
			}
			if (only2.size () > 0) {
				if (only2.size () > 1) {
					json.put (ONLY2_LABEL, only2);
				} else {
					json.put (ONLY2_LABEL, only2.get (0));
				}
			}

			return json;
		}

		@Override
		public int compareTo (NodeDiff other) {
			if (key != null)
				return this.key.compareTo (other.key);
			return other.key == null? 0: 1;
		}
	}

	private abstract static class JSONMatcher {
		protected String key;

		public JSONMatcher (String key) {
			this.key = key;
		}

		abstract public Object get (JSONItem json) throws JSONValidationException;

		public static class Simple extends JSONMatcher {
			public Simple (String key) {
				super (key);
			}

			public Object get (JSONItem json) throws JSONValidationException {
				return json.get (key);
			}
		}

		public static class Equal extends JSONMatcher {
			private String value;

			public Equal (String key, String value) {
				super (key);
				if ((value.startsWith ("\"") && value.endsWith ("\"")) || (value.startsWith ("'") && value.endsWith ("'"))) {
					value = value.substring (1, value.length () - 1);
				}
				this.value = value;
			}

			@Override
			public Object get (JSONItem json) throws JSONValidationException {
				if (!json.isArray ())
					throw new JSONValidationException.TypeMismatch ("JSONItem.Object", "JSONItem.Array");
				for (int i = 0; i < json.length (); ++i) {
					JSONItem item = json.getJSON (i);
					try {
						Object v = item.get (key);
						if (value.equals (v.toString ())) {
							return item;
						}
					} catch (JSONValidationException e) {
						// ignore
					}
				}
				throw new JSONValidationException.MissingKey (key + "=" + value);
			}
		}
	}
	//
	//	public static JSONItem newItem (JSONItem json, String[] keys) throws TypeMismatch {
	//		if (!json.isObject ())
	//			throw new JSONValidationException.TypeMismatch ("JSONArray", "JSNOObject");
	//		return new JSONItem.Object (new JSONObject (((JSONItem.Object)json).json, keys));
	//	}
}

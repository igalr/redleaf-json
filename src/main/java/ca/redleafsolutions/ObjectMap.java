package ca.redleafsolutions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadWritable;
import ca.redleafsolutions.json.JSONValidationException;

@SuppressWarnings ("serial")
public class ObjectMap extends BaseMap<Object> implements JSONReadWritable {
	public ObjectMap () {
		super ();
	}

	public ObjectMap (JSONItem json) throws JSONValidationException {
		fromJSON (json);
	}

	public ObjectMap (Map<String, ? extends Object> omap) {
		super (omap);
	}
	
	@SuppressWarnings ("unchecked")
	public <T> T get (String key, Class<T> cls) {
		return (T)get(key);
	}

	public <T> T tryGet (String key, T defaultValue) {
		try {
			@SuppressWarnings ("unchecked")
			T value = (T)get(key);
			if (value == null) {
				value = defaultValue;
			}
			return value;
		} catch (ClassCastException e) {
			return  defaultValue;
		}
	}

	@Override
	public void fromJSON (JSONItem json) throws JSONValidationException {
		if (json.isArray ())
			throw new JSONValidationException.TypeMismatch ("JSONItem.Array", "JSONItem.Object");

		for (Object k: json.listKeys ()) {
			String key = k.toString ();
			Object value = json.get (key);
			if (value instanceof JSONItem) {
				if (((JSONItem)value).isArray ()) {
					ObjectList list = new ObjectList ();
					list.fromJSON ((JSONItem)value);
					this.put (key, list);
				} else {
					ObjectMap map = new ObjectMap ();
					map.fromJSON ((JSONItem)value);
					this.put (key, map);
				}
			} else {
				this.put (key, value);
			}
		}
	}

	public String toSearchString () {
		String search = "";
		for (String key:keySet ()) {
			if (search.length () > 0) search += "&";
			Object value = get (key);
			if (key != null) {
				try {
					key = URLEncoder.encode (key, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// just leave key as is
				}
				try {
					value = URLEncoder.encode (value.toString (), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// just leave key as is
				}
				search += key + "=" + value;
			}
		}
		return search;
	}
	public String toSearchStringB64 () {
		return new String(Base64.encodeBase64(toSearchString().getBytes()));
	}

	public void fromSearchString (String searchString) {
		clear();
		String[] params = searchString.split("&");
		for (String param : params) {
			String[] keyvalue = param.split("=");
			if (keyvalue.length == 2){
				put(keyvalue[0], keyvalue[1]);
			}
		}
	}

	public void fromSearchStringB64 (String encoded) {
		String decoded = new String(Base64.decodeBase64(encoded));
		fromSearchString(decoded);
	}
}

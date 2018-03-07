package ca.redleafsolutions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadWritable2;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable2;

@SuppressWarnings ("serial")
public class ObjectMap2 extends TreeMap<String, Object> implements JSONReadWritable2 {
	public ObjectMap2 () {
		super ();
	}

	public ObjectMap2 (JSONItem json) throws JSONValidationException {
		fromJSON (json);
	}

	public ObjectMap2 (Map<String, ? extends Object> omap) {
		this.putAll (omap);
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newObject ();
		for (String key: this.keySet ()) {
			Object value = this.get (key);
			if (value instanceof JSONWritable2) {
				value = ((JSONWritable2)value).toJSON ();
			} else {
				value = JSONUtils.toJSON (value);
			}
			json.put (key, value);
		}
		return json;
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
					ObjectList2 list = new ObjectList2 ();
					list.fromJSON ((JSONItem)value);
					this.put (key, list);
				} else {
					ObjectMap2 map = new ObjectMap2 ();
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
					search += key + "=" + URLEncoder.encode (value.toString (), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					search += key + "=" + value;
				}
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

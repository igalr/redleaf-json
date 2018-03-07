package ca.redleafsolutions;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONReadable2;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable2;

@SuppressWarnings ("serial")
public class StringMap extends TreeMap<String, String> implements JSONReadable2, JSONWritable2 {
	public StringMap () {
		super ();
	}

	public StringMap (JSONItem json) throws JSONValidationException {
		fromJSON (json);
	}

	public StringMap (Map<String, String> omap) {
		this.putAll (omap);
	}

	@Override
	public JSONItem toJSON () throws JSONValidationException {
		JSONItem json = JSONItem.newObject ();
		for (String key: this.keySet ()) {
			json.put (key, this.get (key));
		}
		return json;
	}

	@Override
	public void fromJSON (JSONItem json) throws JSONValidationException {
		_fromJSON (null, json, this);
	}

	static private void _fromJSON (String key, Object o, StringMap map) throws JSONValidationException {
		if (o instanceof JSONItem) {
			JSONItem json = (JSONItem)o;
			if (json.length () > 0) {
				for (Object k: json.listKeys ()) {
					_fromJSON (k.toString (), json.get (k.toString ()), map);
				}
			}
		} else if (o instanceof String) {
			map.put (key, (String)o);
		} else {
			throw new IllegalArgumentException (o.getClass () + " should be either a String or a JSONItem");
		}
	}
	
	public String toSearchString () {
		String search = "";
		for (String key:keySet ()) {
			if (search.length () > 0) search += "&";
			search += key + "=" + get (key);
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

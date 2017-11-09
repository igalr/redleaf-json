package ca.redleafsolutions.json;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;

public class _ObjectMap implements MapOrList {
	private Map<String, Object> map;

	public _ObjectMap () {
		this.map = new TreeMap<String, Object> ();
	}

	public _ObjectMap (Map<String, ? extends Object> omap) {
		this.map = new TreeMap<String, Object> (omap);
	}

	public String toSearchString () {
		String search = "";
		for (String key:map.keySet ()) {
			if (search.length () > 0) search += "&";
			Object value = map.get (key);
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
		map.clear();
		String[] params = searchString.split("&");
		for (String param : params) {
			String[] keyvalue = param.split("=");
			if (keyvalue.length == 2){
				map.put(keyvalue[0], keyvalue[1]);
			}
		}
	}

	public void fromSearchStringB64 (String encoded) {
		String decoded = new String(Base64.decodeBase64(encoded));
		fromSearchString(decoded);
	}
}

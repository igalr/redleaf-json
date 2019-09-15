package ca.redleafsolutions.json.configuration;

import java.io.IOException;
import java.util.Set;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONValidationException;

public class JSONConfiguration {
	private PersistentDataStore datastore;
	
	public JSONConfiguration (JSONItem json) throws JSONValidationException {
		String type = json.getString ("type");
		if ("filesystem".equalsIgnoreCase (type)) {
			datastore = new FileSystemDataStore (json);
		}
	}
	
	public JSONConfiguration (PersistentDataStore ds) {
		this.datastore = ds;
	}

	public JSONItem get (String name) throws IOException, JSONValidationException {
		return datastore.get (name);
	}
	
	public JSONItem cascade (String list) throws JSONValidationException {
		JSONItem json = null;
		for (String name:list.split (";")) {
			JSONItem item;
			try {
				item = get (name.trim ());
				if (json != null)
					json.cascade (item);
				else
					json = item;
			} catch (IOException e) {
				throw new JSONValidationException (e);
			}
		}
		return json;
	}
	
	public Set<String> list () {
		return datastore.list ();
	}
}

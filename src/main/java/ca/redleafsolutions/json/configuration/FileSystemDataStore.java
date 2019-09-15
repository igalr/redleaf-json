package ca.redleafsolutions.json.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONValidationException;

public class FileSystemDataStore implements PersistentDataStore {
	private File root;

	public FileSystemDataStore (JSONItem json) throws JSONValidationException {
		this.root = new File (json.getString ("root"));
	}

	@Override
	public JSONItem get (String name) throws IOException, JSONValidationException {
		return JSONItem.fromFile (new File (root, name + ".json"));
	}

	@Override
	public Set<String> list () {
		return list (root);
	}
	
	private Set<String> list (File root) {
		Set<String> set = new TreeSet<String> ();
		for (File file: root.listFiles ()) {
			String filename = file.getName ();
			if (file.isDirectory ()) {
				for (String sub:list (file)) {
					set.add (filename + "/" + sub);
				}
			} else {
				if (filename.endsWith (".json"))
					set.add (filename.substring (0, filename.length () - 5));
			}
		}
		return set;
	}
}

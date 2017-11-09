package ca.redleafsolutions.json.configuration;

import java.io.IOException;
import java.util.Set;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONValidationException;

public interface PersistentDataStore {
	JSONItem get (String name) throws IOException, JSONValidationException;
	Set<String> list ();
}

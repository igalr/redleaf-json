package ca.redleafsolutions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONValidationException;

public class Instantiator<T> {
	public T instantiate (String clstr) throws InstantiationException {
		return instantiate (clstr, null);
	}

	public T newInstance (JSONItem json) throws InstantiationException, JSONValidationException {
		return instantiate (json.getString("class"), json.tryGet("params", (JSONItem)null));
	}

	public T instantiate (String clstr, JSONItem json) throws InstantiationException {
		try {
			@SuppressWarnings ("unchecked")
			Class<T> cls = (Class<T>)(Class.forName (clstr));
			Constructor<T> ctor;
			if (json != null) {
				try {
					ctor = cls.getConstructor (JSONItem.class);
					return ctor.newInstance (json);
				} catch (NoSuchMethodException e) {
				}
			}
			ctor = cls.getConstructor ();
			return ctor.newInstance ();
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			InstantiationException exception = new InstantiationException ();
			exception.initCause (e);
			throw exception;
		}
	}
}

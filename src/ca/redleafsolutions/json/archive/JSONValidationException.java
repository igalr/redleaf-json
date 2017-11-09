package ca.redleafsolutions.json.archive;

import org.json.JSONException;

@SuppressWarnings ("serial")
public class JSONValidationException extends Exception {
	public JSONValidationException (String msg) {
		super (msg);
	}
//
//	public JSONValidationException (JSONException e) {
//		super (e.getMessage ());
//	}

	public JSONValidationException (Throwable e) {
		super ((e instanceof JSONException)? e.getMessage (): e.toString ());
	}

	static public class MissingKey extends JSONValidationException {
		private String key;

		public MissingKey (String key) {
			super ("Missing key '" + key + "'");
			this.key = key;
		}
		
		public String getKey () {
			return key;
		}
	}

	static public class IllegalValue extends JSONValidationException {
		public IllegalValue (String key, Object value) {
			super ("Key '" + key + "' has illegal value '" + value + "'");
		}
	}
	
	static public class TypeMismatch extends JSONValidationException {
		private String expectedType;
		private String type;

		public TypeMismatch (String type, String expectedType) {
			super ("Expected " + expectedType + " but detected " + type);
			this.expectedType = expectedType;
			this.type = type;
		}
		
		public String getExpectedType () {
			return expectedType;
		}
		
		public String getType () {
			return type;
		}
	}
}

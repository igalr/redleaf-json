package ca.redleafsolutions;

import ca.redleafsolutions.json.JSONItem;
import ca.redleafsolutions.json.JSONUtils;
import ca.redleafsolutions.json.JSONValidationException;
import ca.redleafsolutions.json.JSONWritable;

interface MapOrList<T> extends JSONWritable {
	public static <T> MapOrList<T> newMap() {
		return new MapOrList.Map<>();	
	}
	public static <T> MapOrList<T> newList() {
		return new MapOrList.List<>();	
	}

	public abstract T get(Object key);
	public abstract T get(Object key, T defaultValue);

	public static class Map<T> extends BaseMap<T> implements MapOrList<T> {
		@Override
		public T get(Object key) {
			return super.get("" + key);
		}

		@Override
		public T get(Object key, T defaultValue) {
			T value = this.get(key);
			if (value != null) {
				return value;
			}
			return defaultValue;
		}

		@Override
		public JSONItem toJSON() throws JSONValidationException {
			JSONItem json = JSONItem.newObject();
			for (String key : this.keySet()) {
				Object value = this.get(key);
				if ((value != null) && !value.getClass().isPrimitive()) {
					if (value instanceof String) {
					} else if (value instanceof JSONWritable) {
						value = ((JSONWritable) value).toJSON();
					} else {
						value = JSONUtils.toJSON(value);
					}
				}
				json.put(key, value);
			}
			return json;
		}
	}

	public static class List<T> extends BaseList<T> implements MapOrList<T> {
		@Override
		public T get(Object key) {
			if (key instanceof Number num) {
				return super.get(num.intValue());
			} else if (key instanceof String str) {
				return super.get(Integer.parseInt(str));
			}
			return null;
		}

		@Override
		public T get(Object key, T defaultValue) {
			T value = this.get(key);
			if (value != null) {
				return value;
			}
			return defaultValue;
		}

		@Override
		public JSONItem toJSON() throws JSONValidationException {
			JSONItem json = JSONItem.newArray();
			for (T value : this) {
				Object o = value;
				if ((value != null) && !value.getClass().isPrimitive()) {
					if (value instanceof JSONWritable jwrite) {
						o = jwrite.toJSON();
					} else {
						o = JSONUtils.toJSON(value);
					}
				}
				json.put(o);
			}
			return json;
		}
	}
}

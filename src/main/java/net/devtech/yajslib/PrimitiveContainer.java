package net.devtech.yajslib;

import net.devtech.utilib.structures.inheritance.InheritedMap;
import java.lang.reflect.Field;

/**
 * represents an object that only contains primitive fields, for faster serialization using unsafe
 */
public abstract class PrimitiveContainer {
	private final int length;

	public PrimitiveContainer() {
		this.length = getLength(this.getClass());
	}

	public static int getLength(Class<?> type) {
		InheritedMap<Object, Field> fields = InheritedMap.getFields(Object.class);
		int count = 0;
		for (Field attribute : fields.getAttributes(type)) {
			Class<?> field = attribute.getType();
			if (field == byte.class || field == boolean.class) {
				count++;
			} else if (field == short.class || field == char.class) {
				count += 2;
			} else if (field == int.class || field == float.class) {
				count += 4;
			} else if (field == long.class || field == double.class) {
				count += 8;
			}
		}
		return count;
	}
}

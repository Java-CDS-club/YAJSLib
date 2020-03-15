package net.devtech.yajslib.persistent.util;

import net.devtech.utilib.structures.inheritance.InheritedMap;
import net.devtech.utilib.unsafe.UnsafeUtil;
import net.devtech.yajslib.annotations.DependsOn;
import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

/**
 * this relies on all the field types being registered as well!
 * @deprecated this is not version safe nor refactor-safe, and is not recommended
 */
@DependsOn (Class.class)
@Deprecated
public class ObjectPersistent implements Persistent<Object> {
	private static final Logger LOGGER = Logger.getLogger("ObjectPersistent");
	private final InheritedMap<Object, Field> fields = InheritedMap.getAccessables(Object.class, Class::getDeclaredFields, f -> {
		int mods = f.getModifiers();
		return !Modifier.isStatic(mods) && !Modifier.isTransient(mods);
	});
	private final long versionHash;

	public ObjectPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(Object object, PersistentOutput output) throws IOException {
		LOGGER.warning(object + " is using ObjectPersistent, this is depreciated and unsafe!");
		try {
			Class<?> type = object.getClass();
			output.writePersistent(type);
			for (Field attribute : this.fields.getAttributes(object.getClass())) {
				output.writePersistent(attribute.get(object), true);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object read(PersistentInput input) throws IOException {
		try {
			Class<?> type = (Class<?>) input.readPersistent();
			Object alloc = UnsafeUtil.forceAllocate(type);
			for (Field attribute : this.fields.getAttributes(type)) {
				attribute.set(alloc, input.readPersistent());
			}
			return alloc;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object blank() {
		return new Object();
	}
}

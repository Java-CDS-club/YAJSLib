package net.devtech.yajslib.persistent;

import net.devtech.yajslib.io.PersistentInputStream;
import net.devtech.yajslib.io.PersistentOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface PersistentRegistry {
	Persistent<?> fromId(long hash);
	<T> Persistent<T> forClass(Class<T> type);
	<T> void register(Class<T> type, Persistent<T> persistent);

	default byte[] toByteArray(Object object) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PersistentOutputStream pout = new PersistentOutputStream(out, this);
		pout.writePersistent(object);
		pout.flush();
		return out.toByteArray();
	}

	default Object fromByteArray(byte[] data) throws IOException {
		return new PersistentInputStream(new ByteArrayInputStream(data), this).readPersistent();
	}

	default <T> Object blank(Class<T> type) {
		return this.forClass(type).blank();
	}
}

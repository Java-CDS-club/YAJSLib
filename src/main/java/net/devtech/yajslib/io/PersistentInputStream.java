package net.devtech.yajslib.io;

import net.devtech.yajslib.persistent.PersistentRegistry;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class PersistentInputStream extends ObjectInputStream {
	private PersistentRegistry registry;

	public PersistentInputStream(InputStream in, PersistentRegistry registry) throws IOException {
		super(in);
		this.registry = registry;
	}

	public Object readPersistent() throws IOException {
		long val = this.readLong();
		if (val == 0) return null;
		return this.registry.fromId(val).read(this);
	}

	public Object[] readArray() throws IOException {
		Object[] objects = new Object[this.readInt()];
		this.readArray(objects);
		return objects;
	}

	public void readArray(Object[] objects) throws IOException {
		for (int i = 0; i < objects.length; i++)
			objects[i] = this.readPersistent();
	}
}

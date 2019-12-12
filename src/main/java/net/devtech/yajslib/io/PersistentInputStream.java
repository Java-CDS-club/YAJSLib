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
		return this.registry.fromId(this.readLong()).read(this);
	}
}

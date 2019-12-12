package net.devtech.yajslib.io;

import net.devtech.yajslib.persistent.Persistent;
import net.devtech.yajslib.persistent.PersistentRegistry;
import java.io.*;

public class PersistentOutputStream extends ObjectOutputStream {
	private PersistentRegistry registry;
	public PersistentOutputStream(OutputStream out, PersistentRegistry registry) throws IOException {
		super(out);
		this.registry = registry;
	}

	/**
	 * do not ever call PersistentOutputStream#write(this)
	 * @param object brug
	 */
	public void write(Object object) throws IOException {
		Persistent<?> persistent = this.registry.forClass(object.getClass());
		this.writeLong(persistent.versionHash());
		persistent.write(object, this);
	}
}

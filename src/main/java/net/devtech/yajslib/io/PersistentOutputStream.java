package net.devtech.yajslib.io;

import net.devtech.yajslib.persistent.Persistent;
import net.devtech.yajslib.persistent.PersistentRegistry;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class PersistentOutputStream extends ObjectOutputStream {
	private PersistentRegistry registry;

	public PersistentOutputStream(OutputStream out, PersistentRegistry registry) throws IOException {
		super(out);
		this.registry = registry;
	}

	/**
	 * do not ever call PersistentOutputStream#write(this)
	 *
	 * @param object brug
	 */
	public void writePersistent(Object object) throws IOException {
		if (object == null) {
			this.writeLong(0);
		} else {
			Persistent<?> persistent = this.registry.forClass(object.getClass());
			this.writeLong(persistent.versionHash());
			persistent.write(object, this);
		}
	}

	public void writePersistent(Object object, long versionHash) throws IOException {
		if (object == null) {
			this.writeLong(0);
		} else {
			Persistent<?> persistent = this.registry.fromId(versionHash);
			this.writeLong(persistent.versionHash());
			persistent.write(object, this);
		}
	}

	public void writeArray(Object[] objects) throws IOException {
		this.writeInt(objects.length);
		for (Object object : objects)
			this.writePersistent(object);
	}
}

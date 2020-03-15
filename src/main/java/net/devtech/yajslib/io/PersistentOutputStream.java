package net.devtech.yajslib.io;

import net.devtech.yajslib.persistent.Persistent;
import net.devtech.yajslib.persistent.PersistentRegistry;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class PersistentOutputStream extends ObjectOutputStream implements PersistentOutput {
	private PersistentRegistry registry;

	public PersistentOutputStream(OutputStream out, PersistentRegistry registry) throws IOException {
		super(out);
		this.registry = registry;
	}

	@Override
	public void writeShort(short shrt) throws IOException {
		this.writeShort((int)shrt);
	}

	@Override
	public void writeChar(char chr) throws IOException {
		this.writeChar((int)chr);
	}

	@Override
	public void writeByte(byte byt) throws IOException {
		this.write(byt);
	}

	@Override
	public <T> void writePersistent(T object) throws IOException {
		this.writePersistent(object, false);
	}

	@Override
	public <T> void writePersistent(T object, boolean searchSupers) throws IOException {
		if (object == null) {
			this.writeLong(0);
		} else {
			Persistent<T> persistent = (Persistent<T>) this.registry.forClass(object.getClass(), searchSupers);
			this.writeLong(persistent.versionHash());
			persistent.write(object, this);
		}
	}

	public <T> void writePersistent(T object, long versionHash) throws IOException {
		if (object == null) {
			this.writeLong(0);
		} else {
			Persistent<T> persistent = (Persistent<T>) this.registry.fromId(versionHash);
			this.writeLong(persistent.versionHash());
			persistent.write(object, this);
		}
	}

	@Override
	public void writeArray(Object[] objects) throws IOException {
		this.writeInt(objects.length);
		for (Object object : objects)
			this.writePersistent(object);
	}

	@Override
	public void writeArrayNoLength(Object[] objects) throws IOException {
		for (Object object : objects) {
			this.writePersistent(object);
		}
	}

	@Override
	public void writeArray(Object[] persistents, boolean searchSupers) throws IOException {
		this.writeInt(persistents.length);
		for (Object object : persistents) {
			this.writePersistent(object, searchSupers);
		}
	}

	@Override
	public void writeArrayNoLength(Object[] objects, boolean searchSupers) throws IOException {
		for (Object object : objects) {
			this.writePersistent(object, searchSupers);
		}
	}


}

package net.devtech.yajslib.io;

import java.io.IOException;
import java.util.UUID;

public interface PersistentOutput {
	void writeLong(long lng) throws IOException;

	void writeDouble(double dbl) throws IOException;

	void writeInt(int nt) throws IOException;

	void writeFloat(float flt) throws IOException;

	void writeShort(short shrt) throws IOException;

	void writeChar(char chr) throws IOException;

	void writeByte(byte byt) throws IOException;

	void writeBoolean(boolean bool) throws IOException;

	<T> void writePersistent(T object) throws IOException;

	<T> void writePersistent(T object, boolean searchSupers) throws IOException;

	void write(byte[] bytes) throws IOException;

	void write(byte[] bytes, int start, int len) throws IOException;

	void writeArray(Object[] persistents) throws IOException;

	/**
	 * write an array but not it's length
	 */
	void writeArrayNoLength(Object[] objects) throws IOException;

	void writeArray(Object[] persistents, boolean searchSupers) throws IOException;

	/**
	 * write an array but not it's length
	 */
	void writeArrayNoLength(Object[] objects, boolean searchSupers) throws IOException;


	default void writeUUID(UUID uuid) throws IOException {
		this.writeLong(uuid.getMostSignificantBits());
		this.writeLong(uuid.getLeastSignificantBits());
	}
}

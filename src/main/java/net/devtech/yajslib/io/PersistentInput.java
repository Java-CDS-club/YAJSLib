package net.devtech.yajslib.io;

import java.io.IOException;
import java.util.UUID;

/**
 * should be self explanatory
 */
public interface PersistentInput {
	long readLong() throws IOException;
	double readDouble() throws IOException;
	int readInt() throws IOException;
	float readFloat() throws IOException;
	short readShort() throws IOException;
	char readChar() throws IOException;
	byte readByte() throws IOException;
	boolean readBoolean() throws IOException;
	Object readPersistent() throws IOException;
	/**
	 * reads an array of persistents from the input stream
	 * @return a newly created array of persistents
	 */
	Object[] readArray() throws IOException;
	/**
	 * fills an exisiting array of persistents with objects from the input stream
	 * @param objects the objects
	 */
	void readArray(Object[] objects) throws IOException;


	default UUID readUUID() throws IOException {
		return new UUID(this.readLong(), this.readLong());
	}
}

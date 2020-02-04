package net.devtech.yajslib.persistent;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentInputStream;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.io.PersistentOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * a serializer and deserializer for an object for a given version
 * @param <T>
 */
public interface Persistent<T> {
	/**
	 * must be unique globally!
	 * @return a unique key for this version
	 */
	long versionHash();

	/**
	 * write the object to the output stream
	 * @param object the object to serialize
	 * @param output the output stream
	 */
	void write(T object, PersistentOutput output) throws IOException;

	/**
	 * read an object from the input stream
	 * @param input the input stream
	 * @return may be null
	 */
	T read(PersistentInput input) throws IOException;

	/**
	 * create a blank object
	 * @return
	 */
	T blank();
}

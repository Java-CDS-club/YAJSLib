package net.devtech.formats;

import net.devtech.translators.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is a serializer and deserializer for a class, which also allows for interoperability between different formats
 * such as JSON, XML, Binary, etc.
 * @see Translator
 * @param <T> the type this persistent serializes / deserialize
 * @param <A> the args needed for this object's initialization
 * @param <I> the "input stream" class, this can be a JSONObject or even a hashmap
 * @param <O> the "output stream" class, this can be a JSONObject as well
 */
public interface Persistent<T, A, I, O> {
	/**
	 * create a new instance of the object that without reading from a stream
	 * @param args the args needed for initialization
	 * @return a new instance
	 */
	@NotNull
	T newInstance(@Nullable A args) throws Throwable;

	/**
	 * deserialize a new instance of the type from an input stream
	 * @param input the input stream
	 * @param args extra args needed for initialization
	 * @return a new instance initialized from the stream
	 */
	@NotNull
	T read(@NotNull I input, @Nullable A args) throws Throwable;



	/**
	 * serialize an instance to an output stream
	 * @param obj the object to serialize
	 * @param output the output
	 * @param args the args
	 */
	void write(@NotNull T obj, @Nullable A args, O output) throws Throwable;
}

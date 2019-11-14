package net.devtech.formats.binary;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.*;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * utility class for making binary persistents
 */
public class BinaryPersistents {

	/**
	 * creates a new binary persistent from a {@link Serializable} class
	 *
	 * @param type the class of the serializable
	 * @param <T> the serializable type
	 * @return a persistent for the serializable class
	 * @see ObjectOutputStream
	 * @see ObjectInputStream
	 * @see Serializable
	 */
	public static <T extends Serializable> BinaryPersistent<T, Void> fromSerializable(Class<T> type) {
		Supplier<T> alloc;
		try {
			alloc = constructorFor(Supplier.class, type);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
		return new BinaryPersistent<T, Void>() {
			@NotNull
			@Override
			public T newInstance(@Nullable Void args) throws Throwable {
				return alloc.get();
			}

			@NotNull
			@Override
			public T read(@NotNull InputStream input, @Nullable Void args) throws Throwable {
				return (T) new ObjectInputStream(input).readObject();
			}

			@Override
			public void write(@NotNull T obj, Void args, OutputStream output) throws Throwable {
				new ObjectOutputStream(output).writeObject(obj);
			}
		};
	}

	/**
	 * creates a persistent from a class and an arg type,
	 * the class must have a constructor with just the arg type, and one with an input stream and the arg type
	 * Constructor
	 *
	 * @param type
	 * @param argType
	 * @param <T>
	 * @param <A>
	 * @return
	 */
	public static <T extends BinarySerializable, A> BinaryPersistent<T, A> createConstructorPersistent(Class<T> type, Class<A> argType) {
		Function<A, T> uninitialized;
		BiFunction<InputStream, A, T> initialized;
		try {
			uninitialized = (Function<A, T>) constructorFor(Function.class, type, argType);
		} catch (Throwable e) {
			throw new IllegalArgumentException(type + " does not have a default constructor with (" + argType + ") as it's only parameter", e);
		}
		try {
			initialized = (BiFunction<InputStream, A, T>) constructorFor(BiFunction.class, type, InputStream.class, argType);
		} catch (Throwable e) {
			throw new IllegalArgumentException(type + " does not have a default constructor with (" + InputStream.class + " and " + argType + ") as it's only parameters", e);
		}

		return new BinaryPersistent<T, A>() {
			@NotNull
			@Override
			public T newInstance(@Nullable A args) {
				return uninitialized.apply(args);
			}

			@NotNull
			@Override
			public T read(@NotNull InputStream input, @Nullable A args) {
				return initialized.apply(input, args);
			}

			@Override
			public void write(@NotNull T obj, A args, OutputStream output) {
				obj.serialize(output);
			}
		};
	}

	static <T> T constructorFor(Class<T> functionType, Class<?> type, Class<?>... args) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle mh = lookup.findConstructor(type, MethodType.methodType(void.class, args));
		return (T) LambdaMetafactory.metafactory(lookup, "apply", MethodType.methodType(functionType), mh.type().generic(), mh, mh.type()).getTarget().invokeExact();
	}
}

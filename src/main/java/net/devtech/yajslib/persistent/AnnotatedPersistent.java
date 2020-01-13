package net.devtech.yajslib.persistent;

import com.hervian.lambda.Lambda;
import com.hervian.lambda.LambdaFactory;
import net.devtech.functions.ThrowingSupplier;
import net.devtech.structures.inheritance.InheritedMap;
import net.devtech.yajslib.annotations.Reader;
import net.devtech.yajslib.annotations.Writer;
import net.devtech.yajslib.io.PersistentInputStream;
import net.devtech.yajslib.io.PersistentOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;

public class AnnotatedPersistent<T> implements Persistent<T> {
	private static final InheritedMap<Object, Method> READERS = InheritedMap.getMethodsAnnotated(Object.class, Reader.class);
	public static final InheritedMap<Object, Method> WRITERS = InheritedMap.getMethodsAnnotated(Object.class, Writer.class);

	private long versionHash;
	private Supplier<T> defaultInit;
	private Lambda write;
	private Lambda read;

	public AnnotatedPersistent(Class<T> type, long versionHash) {
		this(type::newInstance, type, versionHash);
	}

	public AnnotatedPersistent(ThrowingSupplier<T> defaultInit, Class<T> type, long versionHash) {
		this.defaultInit = defaultInit;
		try {
			this.versionHash = versionHash;
			for (Method attribute : READERS.getAttributes(type)) {
				Reader reader = attribute.getAnnotation(Reader.class);
				if (reader != null && reader.value() == versionHash) {
					Class<?>[] params = attribute.getParameterTypes();
					if (Modifier.isStatic(attribute.getModifiers())) {
						if (!(params.length == 2 && params[0].equals(type) && params[1].equals(PersistentInputStream.class)))
							throw new IllegalArgumentException(attribute + " has illegal parameters, it must have only two parameters (" + type + ", " + PersistentInputStream.class + ")");
					} else if (!(params.length == 1 && params[0].equals(PersistentInputStream.class)))
						throw new IllegalArgumentException(attribute + " has illegal parameters, it must have only one parameter (" + PersistentInputStream.class + ")");
					this.read = LambdaFactory.create(attribute);
					break;
				}
			}

			for (Method attribute : WRITERS.getAttributes(type)) {
				Writer reader = attribute.getAnnotation(Writer.class);
				if (reader != null && reader.value() == versionHash) {
					Class<?>[] params = attribute.getParameterTypes();
					if (Modifier.isStatic(attribute.getModifiers())) {
						if (!(params.length == 2 && params[0].equals(type) && params[1].equals(PersistentOutputStream.class)))
							throw new IllegalArgumentException(attribute + " has illegal parameters, it must have only two parameters (" + type + ", " + PersistentOutputStream.class + ")");
					} else if (!(params.length == 1 && params[0].equals(PersistentOutputStream.class)))
						throw new IllegalArgumentException(attribute + " has illegal parameters, it must have only one parameter (" + PersistentOutputStream.class + ")");

					this.write = LambdaFactory.create(attribute);
					break;
				}
			}

			if (this.write == null) throw new IllegalArgumentException("No @Writer found for version " + versionHash);
			else if (this.read == null)
				throw new IllegalArgumentException("No @Reader was found for version" + versionHash);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(Object object, PersistentOutputStream output) {
		this.write.invoke_for_void(object, output);
	}

	@Override
	public T read(PersistentInputStream input) {
		T obj = this.defaultInit.get();
		this.read.invoke_for_void(obj, input);
		return obj;
	}

	@Override
	public T blank() {
		return this.defaultInit.get();
	}
}

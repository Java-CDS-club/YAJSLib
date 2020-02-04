package net.devtech.yajslib.persistent;

import net.devtech.utilib.functions.GeneralFunction;
import net.devtech.utilib.functions.ThrowingSupplier;
import net.devtech.utilib.structures.inheritance.InheritedMap;
import net.devtech.utilib.unsafe.ReflectionUtil;
import net.devtech.yajslib.annotations.Reader;
import net.devtech.yajslib.annotations.Writer;
import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public class AnnotatedPersistent<T> implements Persistent<T> {
	private static final InheritedMap<Object, Method> READERS = InheritedMap.getMethodsAnnotated(Object.class, Reader.class);
	public static final InheritedMap<Object, Method> WRITERS = InheritedMap.getMethodsAnnotated(Object.class, Writer.class);

	private long versionHash;
	private Supplier<T> defaultInit;
	private GeneralFunction write;
	private GeneralFunction read;

	public AnnotatedPersistent(Class<T> type, long versionHash) {
		this(() -> ReflectionUtil.allocate(type, true), type, versionHash);
	}

	public AnnotatedPersistent(ThrowingSupplier<T> defaultInit, Class<T> type, long versionHash) {
		this.defaultInit = defaultInit;
		try {
			// TODO static for immutables (return new instance instead of recieving pre-made one)
			this.versionHash = versionHash;
			for (Method attribute : READERS.getAttributes(type)) {
				Reader reader = attribute.getAnnotation(Reader.class);
				if (reader != null && reader.value() == versionHash) {
					Class<?>[] params = attribute.getParameterTypes();
					if (Modifier.isStatic(attribute.getModifiers())) {
						if (!(params.length == 2 && params[0].equals(type) && params[1].equals(PersistentInput.class)))
							throw new IllegalArgumentException(attribute + " has illegal parameters, it must have only two parameters (" + type + ", " + PersistentInput.class + ")");
					} else if (!(params.length == 1 && params[0].equals(PersistentInput.class)))
						throw new IllegalArgumentException(attribute + " has illegal parameters, it must have only one parameter (" + PersistentInput.class + ")");

					attribute.setAccessible(true);
					this.read = ReflectionUtil.getMethod(attribute);
					break;
				}
			}

			for (Method attribute : WRITERS.getAttributes(type)) {
				Writer reader = attribute.getAnnotation(Writer.class);
				if (reader != null && reader.value() == versionHash) {
					Class<?>[] params = attribute.getParameterTypes();
					if (Modifier.isStatic(attribute.getModifiers())) {
						if (!(params.length == 2 && params[0].equals(type) && params[1].equals(PersistentOutput.class)))
							throw new IllegalArgumentException(attribute + " has illegal parameters, it must have only two parameters (" + type + ", " + PersistentOutput.class + ")");
					} else if (!(params.length == 1 && params[0].equals(PersistentOutput.class)))
						throw new IllegalArgumentException(attribute + " has illegal parameters, it must have only one parameter (" + PersistentOutput.class + ")");

					attribute.setAccessible(true);
					this.write = ReflectionUtil.getMethod(attribute);
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
	public void write(Object object, PersistentOutput output) {
		this.write.invokeVoid(object, output);
	}

	@Override
	public T read(PersistentInput input) {
		T obj = this.defaultInit.get();
		this.read.invokeVoid(obj, input);
		return obj;
	}

	@Override
	public T blank() {
		return this.defaultInit.get();
	}
}

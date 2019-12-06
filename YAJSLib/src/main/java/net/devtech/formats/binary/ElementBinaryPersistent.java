package net.devtech.formats.binary;

import net.devtech.util.functions.ThrowingTriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public interface ElementBinaryPersistent<T, A> extends BinaryPersistent<T, A> {

	ThrowingTriConsumer<T, DataInputStream, A> getInitializer(@Nullable A arg);
	ThrowingTriConsumer<T, DataOutputStream, A> getSerializer(@Nullable A args);

	@NotNull
	@Override
	default T read(@NotNull InputStream input, @Nullable A args) throws Throwable {
		T instance = newInstance(args);
		DataInputStream data = new DataInputStream(input);
		getInitializer(args).accept(instance, data, args);
		return instance;
	}

	@Override
	default void write(@NotNull T obj, @Nullable A args, OutputStream output) throws Throwable {
		DataOutputStream data = new DataOutputStream(output);
		getSerializer(args).accept(obj, data, args);
	}
}

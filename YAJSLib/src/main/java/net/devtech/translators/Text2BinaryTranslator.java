package net.devtech.translators;

import net.devtech.formats.binary.BinaryPersistent;
import net.devtech.formats.text.TextPersistent;
import net.devtech.util.HexadecimalInputStream;
import net.devtech.util.HexadecimalOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.*;

public class Text2BinaryTranslator<T, A> implements Translator<T, A, BinaryPersistent<T, A>, TextPersistent<T, A>> {
	@Override
	public BinaryPersistent<T, A> translate(TextPersistent<T, A> source) {
		return new BinaryPersistent<T, A>() {
			@NotNull
			@Override
			public T newInstance(@Nullable A args) throws Throwable {
				return source.newInstance(args);
			}

			@NotNull
			@Override
			public T read(@NotNull InputStream input, @Nullable A args) throws Throwable {
				return source.read(new InputStreamReader(input), args);
			}

			@Override
			public void write(@NotNull T obj, A args, OutputStream output) throws Throwable {
				source.write(obj, args, new OutputStreamWriter(output));
			}
		};
	}

	@Override
	public TextPersistent<T, A> reverse(BinaryPersistent<T, A> source) {
		return new TextPersistent<T, A>() {
			@NotNull
			@Override
			public T newInstance(@Nullable A args) throws Throwable {
				return source.newInstance(args);
			}

			@NotNull
			@Override
			public T read(@NotNull Reader input, @Nullable A args) throws Throwable {
				return source.read(new HexadecimalInputStream(input), args);
			}

			@Override
			public void write(@NotNull T obj, A args, Writer output) throws Throwable {
				source.write(obj, args, new HexadecimalOutputStream(output));
			}
		};
	}
}

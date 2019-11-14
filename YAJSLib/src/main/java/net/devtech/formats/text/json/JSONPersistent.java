package net.devtech.formats.text.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.devtech.formats.text.TextPersistent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.Reader;
import java.io.Writer;

public interface JSONPersistent<T, A> extends TextPersistent<T, A> {
	class Jackson<T, A> implements JSONPersistent<T, A> {
		private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
		private final Class<T> type;

		public Jackson(Class<T> type) {
			this.type = type;
		}

		@NotNull
		@Override
		public T newInstance(@Nullable A args) throws Throwable {
			assert args == null : "Args must be null for " + Jackson.class + " persistent!";
			return JSON_MAPPER.readValue("{}", type);
		}

		@NotNull
		@Override
		public T read(@NotNull Reader input, @Nullable A args) throws Throwable {
			assert args == null : "Args must be null for " + Jackson.class + " persistent!";
			return JSON_MAPPER.readValue(input, type);
		}

		@Override
		public void write(@NotNull T obj, A args, Writer output) throws Throwable {
			JSON_MAPPER.writeValue(output, obj);
		}
	}
}

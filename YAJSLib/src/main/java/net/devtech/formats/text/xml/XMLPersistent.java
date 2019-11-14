package net.devtech.formats.text.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.devtech.formats.text.TextPersistent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.Reader;
import java.io.Writer;

public interface XMLPersistent<T, A> extends TextPersistent<T, A> {
	class Jackson<T, A> implements XMLPersistent<T, A> {
		private static final XmlMapper XML_MAPPER = new XmlMapper();
		private final Class<T> type;
		public Jackson(Class<T> type) {
			this.type = type;
		}

		@NotNull
		@Override
		public T newInstance(@Nullable A args) throws Throwable {
			return XML_MAPPER.readValue(String.format("<%1$s></%1$s>", type.getSimpleName()), type);
		}

		@NotNull
		@Override
		public T read(@NotNull Reader input, @Nullable A args) throws Throwable {
			assert args == null : "Args must be null for " + Jackson.class + " persistent!";
			return XML_MAPPER.readValue(input, type);
		}

		@Override
		public void write(@NotNull T obj, A args, Writer output) throws Throwable {
			XML_MAPPER.writeValue(output, obj);
		}
	}
}

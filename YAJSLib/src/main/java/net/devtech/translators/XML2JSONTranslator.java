package net.devtech.translators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.devtech.formats.text.json.JSONPersistent;
import net.devtech.formats.text.xml.XMLPersistent;
import net.devtech.util.UnsafeReflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class XML2JSONTranslator<T, A> implements Translator<T, A, XMLPersistent<T, A>, JSONPersistent<T, A>> {
	private static final XmlMapper XML_MAPPER = new XmlMapper();
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	private final T instance;
	private final Class<T> type;
	public XML2JSONTranslator(Class<T> type) {
		this.type = type;
		try {
			instance = (T) UnsafeReflection.UNSAFE.allocateInstance(type);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public XMLPersistent<T, A> translate(JSONPersistent<T, A> source) {
		XMLPersistent.Jackson<T, A> background = new XMLPersistent.Jackson<>(type);
		return new XMLPersistent<T, A>() {
			@NotNull
			@Override
			public T newInstance(@Nullable A args) throws Throwable {
				return source.newInstance(args);
			}

			@NotNull
			@Override
			public T read(@NotNull Reader input, @Nullable A args) throws Throwable { // read XML
				return source.read(new StringReader(JSON_MAPPER.writeValueAsString(background.read(input, args))), args);
			}

			@Override
			public void write(@NotNull T obj, A args, Writer output) throws Throwable { // write XML
				StringWriter writer = new StringWriter();
				source.write(obj, args, writer);
				XML_MAPPER.writeValue(output, JSON_MAPPER.readTree(writer.toString()));
			}
		};
	}

	@Override
	public JSONPersistent<T, A> reverse(XMLPersistent<T, A> source) {
		JSONPersistent.Jackson<T, A> background = new JSONPersistent.Jackson<>(type);
		return new JSONPersistent<T, A>() {
			@NotNull
			@Override
			public T newInstance(@Nullable A args) throws Throwable {
				return source.newInstance(args);
			}

			@NotNull
			@Override
			public T read(@NotNull Reader input, @Nullable A args) throws Throwable { // read json
				return source.read(new StringReader(XML_MAPPER.writeValueAsString(background.read(input, args))), args);
			}

			@Override
			public void write(@NotNull T obj, A args, Writer output) throws Throwable { // write json
				StringWriter writer = new StringWriter();
				source.write(obj, args, writer);
				JSON_MAPPER.writeValue(output, XML_MAPPER.readTree(writer.toString()));
			}
		};
	}
}

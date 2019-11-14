package net.devtech.formats.text.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import java.io.*;

public class JSONInputStream extends Reader {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	private XMLParser iterator;
	private CharArrayReader reader = new CharArrayReader(new char[0]);
	private int available;

	public JSONInputStream(Reader input) {
		this.iterator = new XMLParser(input);
	}

	@Override
	public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
		if(len < available) {
			available -= len;
			return reader.read(cbuf, off, len);
		} else if(len == available) {
			int next = reader.read(cbuf, off, len);
			next();
			return next;
		} else {
			off += reader.read(cbuf, off, available);
			next();
			return read(cbuf, off, len);
		}
	}

	public void next() throws IOException {
		CharArrayWriter writer = new CharArrayWriter();
		JSON_MAPPER.writeValue(writer, iterator.next());
		char[] arr = writer.toCharArray();
		reader = new CharArrayReader(arr);
		available = arr.length;
	}



	@Override
	public void close() throws IOException {
		iterator.close();
	}
}

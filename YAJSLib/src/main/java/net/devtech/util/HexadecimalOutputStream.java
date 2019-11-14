package net.devtech.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class HexadecimalOutputStream extends OutputStream {
	private final Writer writer;

	public HexadecimalOutputStream(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void write(int b) throws IOException {
		String next = Integer.toHexString(b & 0xFF);
		if(next.length() == 1)
			next = '0'+next;
		writer.write(next);
	}
}

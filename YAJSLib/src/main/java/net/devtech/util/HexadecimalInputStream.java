package net.devtech.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class HexadecimalInputStream extends InputStream {
	private final Reader reader;
	private final char[] buff = new char[2];
	public HexadecimalInputStream(Reader reader) {
		this.reader = reader;
	}

	@Override
	public int read() throws IOException {
		int next = reader.read(buff);
		if(next != 2)
			throw new EOFException("not enough chars for hexadecimal input stream " + next + " read, 2 expected.");
		return Integer.parseInt(new String(buff), 16);
	}
}

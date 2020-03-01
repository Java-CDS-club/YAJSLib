package net.devtech.yajslib.persistent.util;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class StringPersistent implements Persistent<String> {
	private final long versionHash;

	public StringPersistent(long versionHash) {
		this.versionHash = versionHash;
	}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(String object, PersistentOutput output) throws IOException {
		output.writeInt(object.length());
		for (int i = 0; i < object.length(); i++) {
			output.writeChar(object.charAt(i));
		}
	}

	@Override
	public String read(PersistentInput input) throws IOException {
		int chars = input.readInt();
		char[] arr = new char[chars];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readChar();
		}
		return new String(arr);
	}

	@Override
	public String blank() {
		return "";
	}
}

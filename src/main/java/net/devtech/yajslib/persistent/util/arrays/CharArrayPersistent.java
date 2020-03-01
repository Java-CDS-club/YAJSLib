package net.devtech.yajslib.persistent.util.arrays;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class CharArrayPersistent implements Persistent<char[]> {
	private final long versionHash;

	public CharArrayPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(char[] object, PersistentOutput output) throws IOException {
		output.writeInt(object.length);
		for (char c : object) {
			output.writeChar(c);
		}
	}

	@Override
	public char[] read(PersistentInput input) throws IOException {
		char[] arr = new char[input.readInt()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readChar();
		}
		return arr;
	}

	@Override
	public char[] blank() {
		return new char[0];
	}
}


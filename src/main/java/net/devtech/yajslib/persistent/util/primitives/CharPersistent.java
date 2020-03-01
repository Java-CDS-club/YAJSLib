package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class CharPersistent implements Persistent<Character> {
	private final long versionHash;
	public CharPersistent(long hash) {this.versionHash = hash;}
	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(Character object, PersistentOutput output) throws IOException {
		output.writeChar(object);
	}

	@Override
	public Character read(PersistentInput input) throws IOException {
		return input.readChar();
	}

	@Override
	public Character blank() {
		return '\0';
	}
}

package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class ShortPersistent implements Persistent<Short> {
	private final long versionHash;

	public ShortPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return versionHash;
	}

	@Override
	public void write(Short object, PersistentOutput output) throws IOException {
		output.writeShort(object);
	}

	@Override
	public Short read(PersistentInput input) throws IOException {
		return input.readShort();
	}

	@Override
	public Short blank() {
		return 0;
	}
}

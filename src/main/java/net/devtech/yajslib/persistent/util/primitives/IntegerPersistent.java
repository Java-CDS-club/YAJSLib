package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class IntegerPersistent implements Persistent<Integer> {
	private final long versionHash;

	public IntegerPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(Integer object, PersistentOutput output) throws IOException {
		output.writeInt(object);
	}

	@Override
	public Integer read(PersistentInput input) throws IOException {
		return input.readInt();
	}

	@Override
	public Integer blank() {
		return 0;
	}
}

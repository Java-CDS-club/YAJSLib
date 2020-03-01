package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class BooleanPersistent implements Persistent<Boolean> {
	private final long versionHash;

	public BooleanPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(Boolean object, PersistentOutput output) throws IOException {
		output.writeBoolean(object);
	}

	@Override
	public Boolean read(PersistentInput input) throws IOException {
		return input.readBoolean();
	}

	@Override
	public Boolean blank() {
		return false;
	}
}

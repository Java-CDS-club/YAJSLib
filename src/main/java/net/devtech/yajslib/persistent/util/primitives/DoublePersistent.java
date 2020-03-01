package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class DoublePersistent implements Persistent<Double> {
	private final long versionHash;

	public DoublePersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(Double object, PersistentOutput output) throws IOException {
		output.writeDouble(object);
	}

	@Override
	public Double read(PersistentInput input) throws IOException {
		return input.readDouble();
	}

	@Override
	public Double blank() {
		return 0.0d;
	}
}

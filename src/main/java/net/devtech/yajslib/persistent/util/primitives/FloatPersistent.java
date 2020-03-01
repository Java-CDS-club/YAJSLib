package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class FloatPersistent implements Persistent<Float> {
	private final long versionHash;

	public FloatPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return versionHash;
	}

	@Override
	public void write(Float object, PersistentOutput output) throws IOException {
		output.writeFloat(object);
	}

	@Override
	public Float read(PersistentInput input) throws IOException {
		return input.readFloat();
	}

	@Override
	public Float blank() {
		return .0f;
	}
}

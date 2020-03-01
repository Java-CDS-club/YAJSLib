package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class BytePersistent implements Persistent<Byte> {
	private final long versionHash;

	public BytePersistent(long versionHash) {
		this.versionHash = versionHash;
	}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(Byte object, PersistentOutput output) throws IOException {
		output.writeByte(object);
	}

	@Override
	public Byte read(PersistentInput input) throws IOException {
		return input.readByte();
	}

	@Override
	public Byte blank() {
		return 0;
	}
}

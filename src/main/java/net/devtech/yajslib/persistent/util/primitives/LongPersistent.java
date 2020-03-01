package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class LongPersistent implements Persistent<Long> {
	private final long versionHash;

	public LongPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return versionHash;
	}

	@Override
	public void write(Long object, PersistentOutput output) throws IOException {
		output.writeLong(object);
	}

	@Override
	public Long read(PersistentInput input) throws IOException {
		return input.readLong();
	}

	@Override
	public Long blank() {
		return 0L;
	}
}

package net.devtech.yajslib.persistent.util.arrays;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class LongArrayPersistent implements Persistent<long[]> {
	private final long versionHash;

	public LongArrayPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(long[] object, PersistentOutput output) throws IOException {
		output.writeInt(object.length);
		for (long c : object) {
			output.writeLong(c);
		}
	}

	@Override
	public long[] read(PersistentInput input) throws IOException {
		long[] arr = new long[input.readInt()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readLong();
		}
		return arr;
	}

	@Override
	public long[] blank() {
		return new long[0];
	}
}

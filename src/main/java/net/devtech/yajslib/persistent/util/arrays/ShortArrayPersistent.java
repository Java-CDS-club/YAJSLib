package net.devtech.yajslib.persistent.util.arrays;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class ShortArrayPersistent implements Persistent<short[]> {
	private final long versionHash;

	public ShortArrayPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(short[] object, PersistentOutput output) throws IOException {
		output.writeInt(object.length);
		for (int i = 0; i < object.length; i++) {
			output.writeShort(object[i]);
		}
	}

	@Override
	public short[] read(PersistentInput input) throws IOException {
		short[] arr = new short[input.readInt()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readShort();
		}
		return arr;
	}

	@Override
	public short[] blank() {
		return new short[0];
	}
}

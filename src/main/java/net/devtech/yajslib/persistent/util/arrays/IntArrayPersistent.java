package net.devtech.yajslib.persistent.util.arrays;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class IntArrayPersistent implements Persistent<int[]> {
	private final long versionHash;

	public IntArrayPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(int[] object, PersistentOutput output) throws IOException {
		output.writeInt(object.length);
		for (int c : object) {
			output.writeInt(c);
		}
	}

	@Override
	public int[] read(PersistentInput input) throws IOException {
		int[] arr = new int[input.readInt()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readInt();
		}
		return arr;
	}

	@Override
	public int[] blank() {
		return new int[0];
	}
}

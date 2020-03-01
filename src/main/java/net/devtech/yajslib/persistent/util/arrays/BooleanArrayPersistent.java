package net.devtech.yajslib.persistent.util.arrays;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class BooleanArrayPersistent implements Persistent<boolean[]> {
	private final long versionHash;

	public BooleanArrayPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(boolean[] object, PersistentOutput output) throws IOException {
		output.writeInt(object.length);
		for (boolean b : object) {
			output.writeBoolean(b);
		}
	}

	@Override
	public boolean[] read(PersistentInput input) throws IOException {
		boolean[] arr = new boolean[input.readInt()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readBoolean();
		}
		return arr;
	}

	@Override
	public boolean[] blank() {
		return new boolean[0];
	}
}

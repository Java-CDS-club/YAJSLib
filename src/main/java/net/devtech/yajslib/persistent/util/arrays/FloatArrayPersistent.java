package net.devtech.yajslib.persistent.util.arrays;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class FloatArrayPersistent implements Persistent<float[]> {
	private final long versionHash;

	public FloatArrayPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(float[] object, PersistentOutput output) throws IOException {
		output.writeInt(object.length);
		for (float c : object) {
			output.writeFloat(c);
		}
	}

	@Override
	public float[] read(PersistentInput input) throws IOException {
		float[] arr = new float[input.readInt()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readFloat();
		}
		return arr;
	}

	@Override
	public float[] blank() {
		return new float[0];
	}
}

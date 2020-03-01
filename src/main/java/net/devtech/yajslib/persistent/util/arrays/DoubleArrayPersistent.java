package net.devtech.yajslib.persistent.util.arrays;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class DoubleArrayPersistent implements Persistent<double[]> {
	private final long versionHash;

	public DoubleArrayPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(double[] object, PersistentOutput output) throws IOException {
		output.writeInt(object.length);
		for (double c : object) {
			output.writeDouble(c);
		}
	}

	@Override
	public double[] read(PersistentInput input) throws IOException {
		double[] arr = new double[input.readInt()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readDouble();
		}
		return arr;
	}

	@Override
	public double[] blank() {
		return new double[0];
	}
}

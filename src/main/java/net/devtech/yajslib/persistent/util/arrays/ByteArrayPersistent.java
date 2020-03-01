package net.devtech.yajslib.persistent.util.arrays;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class ByteArrayPersistent implements Persistent<byte[]> {
	private final long versionHash;

	public ByteArrayPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(byte[] object, PersistentOutput output) throws IOException {
		output.writeInt(object.length);
		for (int i = 0; i < object.length; i++) {
			output.writeByte(object[i]);
		}
	}

	@Override
	public byte[] read(PersistentInput input) throws IOException {
		byte[] arr = new byte[input.readInt()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = input.readByte();
		}
		return arr;
	}

	@Override
	public byte[] blank() {
		return new byte[0];
	}
}

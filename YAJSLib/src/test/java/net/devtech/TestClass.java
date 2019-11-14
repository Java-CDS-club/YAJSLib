package net.devtech;

import net.devtech.formats.binary.BinarySerializable;
import java.io.*;
import java.util.Random;

public class TestClass implements BinarySerializable {
	public int val;
	public String ok;
	public char nice;
	public boolean aaa;
	public String okthen;

	public TestClass(int val, String ok, char nice, boolean aaa, String okthen) {
		this.val = val;
		this.ok = ok;
		this.nice = nice;
		this.aaa = aaa;
		this.okthen = okthen;
	}

	public TestClass(Random random) {
		this(random.nextInt(), "boomer", 'a', random.nextBoolean(), "liberal");
	}

	public TestClass() {
		this(new Random());
	}

	public TestClass(Void val) {
		this();
	}

	public TestClass(InputStream inputStream, Void val) {
		try {
			DataInputStream in = new DataInputStream(inputStream);
			this.val = in.readInt();
			ok = in.readUTF();
			nice = in.readChar();
			aaa = in.readBoolean();
			okthen = in.readUTF();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String toString() {
		return "TestClass{" + "val=" + val + ", ok='" + ok + '\'' + ", nice=" + nice + ", aaa=" + aaa + ", okthen=" + ok + '}';
	}

	@Override
	public void serialize(OutputStream out) {
		DataOutputStream outputStream = new DataOutputStream(out);
		try {
			outputStream.writeInt(val);
			outputStream.writeUTF(ok);
			outputStream.writeChar(nice);
			outputStream.writeBoolean(aaa);
			outputStream.writeUTF(okthen);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

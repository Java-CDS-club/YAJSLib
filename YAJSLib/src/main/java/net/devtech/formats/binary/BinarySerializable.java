package net.devtech.formats.binary;

import java.io.OutputStream;

public interface BinarySerializable {
	void serialize(OutputStream out);
}

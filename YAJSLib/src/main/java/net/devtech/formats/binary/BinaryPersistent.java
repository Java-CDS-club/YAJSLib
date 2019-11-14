package net.devtech.formats.binary;

import net.devtech.formats.Persistent;
import java.io.InputStream;
import java.io.OutputStream;

public interface BinaryPersistent<T, A> extends Persistent<T, A, InputStream, OutputStream> {}

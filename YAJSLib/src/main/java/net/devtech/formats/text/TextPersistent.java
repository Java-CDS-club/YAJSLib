package net.devtech.formats.text;

import net.devtech.formats.Persistent;
import java.io.Reader;
import java.io.Writer;

public interface TextPersistent<T, A> extends Persistent<T, A, Reader, Writer> {}

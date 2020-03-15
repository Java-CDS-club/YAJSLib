package net.devtech.yajslib.persistent.util;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;
import java.util.function.Supplier;

public class EmptyPersistent<T> implements Persistent<T> {
	private final long versionHash;
	private final Supplier<T> blank;

	public EmptyPersistent(long versionHash, Supplier<T> blank) {
		this.versionHash = versionHash;
		this.blank = blank;
	}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(T object, PersistentOutput output) throws IOException {

	}

	@Override
	public T read(PersistentInput input) throws IOException {
		return this.blank.get();
	}

	@Override
	public T blank() {
		return this.blank.get();
	}
}

package net.devtech.yajslib.persistent.util;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListPersistent implements Persistent<List<?>> {
	private final long versionHash;

	public ListPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(List<?> object, PersistentOutput output) throws IOException {
		output.writeInt(object.size());
		for (Object o : object) {
			output.writePersistent(o);
		}
	}

	@SuppressWarnings ({"rawtypes", "unchecked"})
	@Override
	public List<?> read(PersistentInput input) throws IOException {
		int len = input.readInt();
		ArrayList list = new ArrayList<>(len);
		for (int i = 0; i < len; i++) {
			list.set(i, input.readPersistent());
		}
		return list;
	}

	@Override
	public List<?> blank() {
		return new ArrayList<>();
	}
}

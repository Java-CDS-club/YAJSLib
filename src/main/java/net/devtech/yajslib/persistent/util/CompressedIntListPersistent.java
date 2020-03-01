package net.devtech.yajslib.persistent.util;

import it.unimi.dsi.fastutil.longs.LongList;
import net.devtech.utilib.structures.lists.CompressedIntList;
import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class CompressedIntListPersistent implements Persistent<CompressedIntList> {
	private final long versionHash;

	public CompressedIntListPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(CompressedIntList object, PersistentOutput output) throws IOException {
		output.writePersistent(object.getStorage(), true);
		output.writeInt(object.getRealMax());
		output.writeInt(object.size());
	}

	@Override
	public CompressedIntList read(PersistentInput input) throws IOException {
		CompressedIntList list = new CompressedIntList((LongList) input.readPersistent(), input.readInt());
		list.setSize(input.readInt());
		return list;
	}

	@Override
	public CompressedIntList blank() {
		return new CompressedIntList(0);
	}
}

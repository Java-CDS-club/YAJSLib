package net.devtech.yajslib.persistent.util.fastutil;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;

public class LongListPersistent implements Persistent<LongList> {
	private final long versionHash;

	public LongListPersistent(long versionHash) {
		this.versionHash = versionHash;
	}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(LongList object, PersistentOutput output) throws IOException {
		output.writeInt(object.size());
		for (int i = 0; i < object.size(); i++) {
			output.writeLong(object.getLong(i));
		}
	}

	@Override
	public LongList read(PersistentInput input) throws IOException {
		int len = input.readInt();
		LongList longs = new LongArrayList(len);
		for (int i = 0; i < len; i++) {
			longs.add(input.readLong());
		}
		return longs;
	}

	@Override
	public LongList blank() {
		return new LongArrayList();
	}
}

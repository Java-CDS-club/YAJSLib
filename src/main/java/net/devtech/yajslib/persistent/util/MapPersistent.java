package net.devtech.yajslib.persistent.util;

import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapPersistent implements Persistent<Map<?, ?>> {
	private final long versionHash;

	public MapPersistent(long hash) {this.versionHash = hash;}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(Map<?, ?> object, PersistentOutput output) throws IOException {
		output.writeInt(object.size());
		for (Map.Entry<?, ?> entry : object.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			output.writePersistent(key);
			output.writePersistent(value);
		}
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public Map<?, ?> read(PersistentInput input) throws IOException {
		int size = input.readInt();
		Map map = this.blank();
		for (int i = 0; i < size; i++) {
			map.put(input.readPersistent(), input.readPersistent());
		}
		return map;
	}

	@Override
	public Map<?, ?> blank() {
		return new HashMap<>();
	}
}

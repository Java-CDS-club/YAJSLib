package net.devtech.yajslib.persistent;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimplePersistentRegistry implements PersistentRegistry {
	private Long2ObjectMap<Persistent<?>> map = new Long2ObjectOpenHashMap<>();
	private Map<Class<?>, Persistent<?>> persistentMap = new HashMap<>();

	@Override
	public Persistent<?> fromId(long hash) {
		return Objects.requireNonNull(this.map.get(hash));
	}

	@Override
	public <T> Persistent<T> forClass(Class<T> type) {
		return (Persistent<T>) Objects.requireNonNull(this.persistentMap.get(type));
	}

	@Override
	public <T> void register(Class<T> type, Persistent<T> persistent) {
		if(persistent.versionHash() == 0)
			throw new IllegalArgumentException("Version cannot be 0!");
		this.persistentMap.put(type, persistent);
		this.map.put(persistent.versionHash(), persistent);
	}
}

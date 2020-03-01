package net.devtech.yajslib.persistent;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class SimplePersistentRegistry implements PersistentRegistry {
	private static final Logger LOGGER = Logger.getLogger("SimplePersistentRegistry");
	private Long2ObjectMap<Persistent<?>> map = new Long2ObjectOpenHashMap<>();
	private Map<Class<?>, Persistent<?>> persistentMap = new HashMap<>();

	@Override
	public Persistent<?> fromId(long hash) {
		return Objects.requireNonNull(this.map.get(hash), "no persistent found for " + hash);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public <T> Persistent<T> forClass(Class<T> type, boolean searchSupers) {
		Persistent<T> persistent = (Persistent<T>) this.persistentMap.get(type);
		if(persistent == null && searchSupers) {
			persistent = (Persistent<T>) this.searchInterfaces(type); // search for interfaces
			if(persistent == null) {
				Class<?> current = type.getSuperclass(); // start out with next
				while (current != null) {
					persistent = (Persistent<T>) this.persistentMap.get(current);
					if (persistent != null) // exit if we found a persistent, it wont be perfect but it's close enough I guess
						break;
					persistent = (Persistent<T>) this.searchInterfaces(current);
					if (persistent != null) break;
					current = current.getSuperclass();
				}
			}
		}

		return Objects.requireNonNull(persistent, "No persistent for type " + type);
	}

	public Persistent<?> searchInterfaces(Class<?> type) {
		Persistent<?> persistent = this.persistentMap.get(type);
		if(persistent != null)
			return persistent;
		for (Class<?> inter : type.getInterfaces()) {
			persistent = this.persistentMap.get(type);;
			if(persistent != null)
				break;
			persistent = this.searchInterfaces(inter); // search the interface's interfaces...
			if(persistent != null)
				return persistent;
		}
		return persistent;
	}

	@Override
	public <T> void register(Class<T> type, Persistent<T> persistent) {
		if(persistent.versionHash() == 0)
			throw new IllegalArgumentException("Version cannot be 0!");
		if(this.map.containsKey(persistent.versionHash()))
			LOGGER.warning("registry has 2 objects with id : " + persistent.versionHash());

		this.persistentMap.put(type, persistent); // force override others
		this.map.put(persistent.versionHash(), persistent);
	}
}

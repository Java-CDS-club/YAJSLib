package net.devtech.yajslib.persistent;

public interface PersistentRegistry {
	Persistent<?> fromId(long hash);
	<T> Persistent<T> forClass(Class<T> type);
	<T> void register(Class<T> type, Persistent<T> persistent);
}

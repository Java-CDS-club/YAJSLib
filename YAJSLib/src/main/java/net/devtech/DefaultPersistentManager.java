package net.devtech;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.devtech.exceptions.ObjectNotFoundException;
import net.devtech.formats.Persistent;
import net.devtech.translators.Translator;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class DefaultPersistentManager implements PersistentManager {
	private Map<Class<?>, List<Persistent>> classes = new HashMap<>();
	private Int2ObjectMap<Persistent> types = new Int2ObjectOpenHashMap<>();
	private Map<Class<?>, Map<Class<?>, Translator>> translators = new HashMap<>();
	private int next;

	@Override
	public <T> int register(Class<T> type, Persistent<T, ?, ?, ?> persistent) {
		List<Persistent> persistents = classes.computeIfAbsent(type, c -> new ArrayList<>());
		for (Persistent persistent1 : persistents)
			// if thre is already a peristent fo the given type, there is a problem
			if (persistent.getClass().isAssignableFrom(persistent1.getClass()))
				throw new IllegalArgumentException(persistent.getClass() + " and " + persistent1.getClass() + " override each other");
		persistents.add(persistent);
		types.put(++next, persistent);
		return next;
	}

	@Override
	public <T> List<Persistent> of(Class<T> type) {
		return Collections.unmodifiableList(classes.get(type));
	}

	@Override
	public @NotNull Persistent get(int id) {
		return types.get(id);
	}

	@Override
	public <T, A, P extends Persistent<T, A, ?, ?>> @NotNull P translate(Persistent<T, A, ?, ?> persistent, Class<P> toType) {
		Class persistentType = persistent.getClass();
		do {
			{
				Map<Class<?>, Translator> translators = this.translators.get(persistentType);
				if (translators != null) {
					Translator translator = translators.get(toType);
					if (translator != null) return (P) translator.translate(persistent);
				}
			}
			for (Class anInterface : persistentType.getInterfaces()) {
				Map<Class<?>, Translator> translators = this.translators.get(anInterface);
				if (translators != null) {
					Translator translator = translators.get(toType);
					if (translator != null) return (P) translator.translate(persistent);
				}
			}
			persistentType = persistentType.getSuperclass();
		} while (persistentType != null);
		throw new ObjectNotFoundException("No translator found for " + persistent.getClass() + " -> " + toType);
	}

	@Override
	public <T, A, S extends Persistent<T, A, ?, ?>, F extends Persistent<T, A, ?, ?>> void registerTranslator(Translator<T, A, S, F> translator, Class<Persistent<T, A, ?, ?>> fromType, Class<Persistent<T, A, ?, ?>> toType) {
		// register normal
		translators.computeIfAbsent(fromType, c -> new HashMap<>()).put(toType, translator);
		// register inverse
		translators.computeIfAbsent(toType, c -> new HashMap<>()).put(fromType, translator.inverse());
	}
}

package net.devtech;

import net.devtech.exceptions.ObjectNotFoundException;
import net.devtech.formats.Persistent;
import net.devtech.translators.Translator;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface PersistentManager {
	/**
	 * register a new listener in the persistent manager
	 *
	 * @param type the class of the object serialized by the persistent
	 * @param persistent the persistent
	 * @param <T> the type the persistent serializes
	 * @return the int id of the persistent
	 * @throws IllegalArgumentException if a persistent of the same format was serialized already
	 */
	<T> int register(Class<T> type, Persistent<T, ?, ?, ?> persistent);

	/**
	 * get the persistent of the class with the given type.
	 * It will attempt to translate the persistent if no persistent was found before,
	 * but it will not register the translated persistent in the manager
	 *
	 * @param type the class of the object serialized by the persistent
	 * @param peristentType the desired type of the persistent
	 * @param <T> the type of the object
	 * @param <P> the desired persistent type
	 * @return a potentially translated persistent of the given class
	 * @throws ObjectNotFoundException if no translator was found for the persistent class
	 * @see PersistentManager#get(Class, Class)
	 */
	@NotNull
	default <T, P extends Persistent<T, ?, ?, ?>> P getOrTranslate(Class<T> type, Class<P> peristentType) {
		try {
			return get(type, peristentType);
		} catch (ObjectNotFoundException ex) {
			for (Persistent persistent : of(type)) try {
					return (P) translate(persistent, (Class) peristentType);
				} catch (ObjectNotFoundException none) {/*ignore*/}
			throw new ObjectNotFoundException("No valid translator was found!");
		}
	}

	/**
	 * get the persistent of the class with the given type.
	 * It will <b>NOT</b> attempt to translate the persistent if no persistent was found before
	 *
	 * @param type the class of the object serialized by the persistent
	 * @param peristentType the desired type of the persistent
	 * @param <T> the type of the object
	 * @param <P> the desired persistent type
	 * @return the persistent of the given class
	 * @throws ObjectNotFoundException if no persistent was found for the class
	 */
	default <T, P extends Persistent<T, ?, ?, ?>> P get(Class<T> type, Class<P> peristentType) {
		for (Persistent<T, ?, ?, ?> persistent : of(type))
			if (peristentType.isAssignableFrom(persistent.getClass())) return (P) persistent;
		throw new ObjectNotFoundException("no persistent found for " + peristentType);
	}

	/**
	 * returns a list of all the registered persistents for the given class
	 *
	 * @param <T> the object type
	 * @param type object class
	 * @return an unmodifiable list of the persistents for the given object
	 */
	<T> List<Persistent> of(Class<T> type);

	/**
	 * get the persistent of the class with the given id
	 *
	 * @param id the integer id of the persistent
	 * @return a persistent with the given id
	 * @throws ObjectNotFoundException if no persistent was found for the given id
	 */
	@NotNull Persistent get(int id);

	/**
	 * attempts to translate the given persistent into a different format
	 *
	 * @param persistent the original type
	 * @param toType the desired type of the persistent
	 * @param <P> the type of the persistent
	 * @return a new persistent of the given format
	 * @throws ObjectNotFoundException if no translator was found for the toType
	 */
	@NotNull <T, A, P extends Persistent<T, A, ?, ?>> P translate(Persistent<T, A, ?, ?> persistent, Class<P> toType);

	/**
	 * register a new translator to the
	 * @param translator
	 * @param fromType
	 * @param toType
	 * @param <T>
	 * @param <A>
	 * @param <S>
	 * @param <F>
	 */
	<T, A, S extends Persistent<T, A, ?, ?>, F extends Persistent<T, A, ?, ?>> void registerTranslator(Translator<T, A, S, F> translator, Class<Persistent<T, A, ?, ?>> fromType, Class<Persistent<T, A, ?, ?>> toType);
}

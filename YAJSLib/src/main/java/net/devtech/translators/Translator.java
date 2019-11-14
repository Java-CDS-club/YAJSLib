package net.devtech.translators;

import net.devtech.formats.Persistent;

/**
 * converts one persistent to one of another format and back
 */
public interface Translator<T, A, F extends Persistent<T, A, ?, ?>, S extends Persistent<T, A, ?, ?>> {
	/**
	 * translate a persistent format into another
	 * @param source the source format
	 * @return a new persistent that uses the given format behind the scenes
	 */
	F translate(S source);

	/**
	 * {@link Translator#translate(Persistent)} but in reverse
	 * @param source the source format
	 * @return a new persistent that uses the given format behind the scenes
	 */
	S reverse(F source);

	/**
	 * gets the inverse of this translator
	 * @return the inverted version of this translator
	 */
	default Translator<T, A, S, F> inverse() {
		return new Translator<T, A, S, F>() {
			@Override
			public S translate(F source) {
				return Translator.this.reverse(source);
			}

			@Override
			public F reverse(S source) {
				return Translator.this.translate(source);
			}
		};
	}
}

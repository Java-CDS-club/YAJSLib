package net.devtech.util.functions;

public interface ThrowingTriConsumer<A, B, C> {

	default void accept(A a, B b, C c) {
		try {
			consume(a, b, c);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	void consume(A a, B b, C c) throws Throwable;

	default ThrowingTriConsumer<A, B, C> andThen(ThrowingTriConsumer<A, B, C> then) {
		return (a, b, c) -> {
			consume(a, b, c);
			then.consume(a, b, c);
		};
	}

}

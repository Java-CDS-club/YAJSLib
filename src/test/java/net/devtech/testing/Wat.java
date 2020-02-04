package net.devtech.testing;

public class Wat {
	public static void main(String[] args) {
		System.out.println(getFor(void.class));
	}

	public static String getFor(Class<?> type) {
		return "invoke" + (type.isPrimitive() ? type.getSimpleName() : "Object");
	}
}

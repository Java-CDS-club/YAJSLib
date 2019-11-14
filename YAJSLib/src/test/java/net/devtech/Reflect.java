package net.devtech;

import net.devtech.formats.text.TextPersistent;
import net.devtech.translators.Text2BinaryTranslator;
import net.devtech.util.UnsafeReflection;

public class Reflect {
	public static void main(String[] args) throws NoSuchMethodException, InstantiationException {
		System.out.println(UnsafeReflection.UNSAFE.allocateInstance(Test.class));
		System.out.println(Text2BinaryTranslator.class.getDeclaredMethod("translate", TextPersistent.class));
	}

	public static abstract class Test {

	}
}

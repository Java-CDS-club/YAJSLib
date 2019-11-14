package net.devtech;

import net.devtech.formats.binary.BinaryPersistent;
import net.devtech.formats.binary.BinaryPersistents;
import net.devtech.formats.text.TextPersistent;
import net.devtech.formats.text.json.JSONPersistent;
import net.devtech.formats.text.xml.XMLPersistent;
import net.devtech.translators.Text2BinaryTranslator;
import net.devtech.translators.XML2JSONTranslator;
import org.junit.Test;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class TranslatorTests {
	public static final TestClass TEST_OBJECT = new TestClass(new Random());
	private static final Text2BinaryTranslator<TestClass, Void> BINARY_TRANSLATOR = new Text2BinaryTranslator<>();
	private static final XML2JSONTranslator<TestClass, Void> JSON_TRANSLATOR = new XML2JSONTranslator<>(TestClass.class);

	@Test
	public void xml2json() throws Throwable {
		JSONPersistent.Jackson<TestClass, Void> json = new JSONPersistent.Jackson<>(TestClass.class);
		StringWriter writer = new StringWriter();
		XMLPersistent<TestClass, Void> xml = JSON_TRANSLATOR.translate(json);
		xml.write(TEST_OBJECT, null, writer);

		System.out.println(writer);
		System.out.println(xml.read(new StringReader(writer.toString()), null));
	}

	@Test
	public void json2xml() throws Throwable {
		XMLPersistent.Jackson<TestClass, Void> xml = new XMLPersistent.Jackson<>(TestClass.class);
		StringWriter writer = new StringWriter();
		JSONPersistent<TestClass, Void> json = JSON_TRANSLATOR.reverse(xml);
		json.write(TEST_OBJECT, null, writer);
		System.out.println(writer);
		System.out.println(json.read(new StringReader(writer.toString()), null));
	}

	@Test
	public void binary2string() throws Throwable {
		BinaryPersistents factory = new BinaryPersistents();
		BinaryPersistent<TestClass, Void> bin = factory.createConstructorPersistent(TestClass.class, Void.class);
		TextPersistent<TestClass, Void> txt = BINARY_TRANSLATOR.reverse(bin);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(outputStream);
		txt.write(TEST_OBJECT, null, writer);
		writer.flush();
		byte[] bytes = outputStream.toByteArray();
		System.out.println(Arrays.toString(bytes));
		System.out.println(txt.read(new InputStreamReader(new ByteArrayInputStream(bytes)), null));
	}

	@Test
	public void string2binary() {

	}
}

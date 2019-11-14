package net.devtech.util;

import sun.misc.Unsafe;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * extremely cursed code that no one deserves to see :D
 */
public class UnsafeReflection {
	public static final Unsafe UNSAFE;
	private static final int FIRST_INT_KLASS;
	public static final int BYTE_ARR_KLASS;
	public static final int SHORT_ARR_KLASS;
	public static final int CHAR_ARR_KLASS;
	public static final int INT_ARR_KLASS;
	public static final int LONG_ARR_KLASS;
	public static final int FLOAT_ARR_KLASS;
	public static final int DOUBLE_ARR_KLASS;


	private static InheritedMap<Object, Field> fields = new InheritedMap<>(Object.class, c -> {
		Field[] arrs = c.getDeclaredFields();
		for (Field arr : arrs)
			arr.setAccessible(true);
		return Arrays.asList(arrs);
	});

	static {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			UNSAFE = (Unsafe) f.get(null);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
		FIRST_INT_KLASS = getKlass(new FirstInt());
		BYTE_ARR_KLASS = getKlass(new byte[0]);
		SHORT_ARR_KLASS = getKlass(new short[0]);
		CHAR_ARR_KLASS = getKlass(new char[0]);
		INT_ARR_KLASS = getKlass(new int[0]);
		LONG_ARR_KLASS = getKlass(new long[0]);
		FLOAT_ARR_KLASS = getKlass(new float[0]);
		DOUBLE_ARR_KLASS = getKlass(new double[0]);
	}
	
	public static <T> T allocate(Class<T> type) {
		try {
			return type.newInstance();
		} catch (ReflectiveOperationException e) {
			try {
				return (T) UNSAFE.allocateInstance(type);
			} catch (InstantiationException e2) {
				throw new RuntimeException(e2);
			}
		}

	}

	public static int getFirstInt(Object object) {
		int orig = getKlass(object);
		FirstInt first = unsafeCast(object, FIRST_INT_KLASS);
		unsafeCast(object, orig);
		return first.val;
	}

	public static void setFirstInt(Object object, int val) {
		int orig = getKlass(object);
		FirstInt firstInt = unsafeCast(object, FIRST_INT_KLASS);
		firstInt.val = val;
		unsafeCast(object, orig);
	}

	/**
	 * Convert an array of primitives of a smaller type into one of a larger type, for example
	 * to go from a byte array to an int array you would do, careful, this directly modifies the klass value
	 * in the array, it does not copy it
	 *
	 * <b>Reflection.upcastArray(byte_array, Reflection.INT_ARR_KLASS, 4)</b>
	 *
	 * @param array the original array
	 * @param newType the target type
	 * @param conversion the conversion factor, for example an int has 2 shorts so to go from a short array to an int array it would be 2
	 * @param <T> the returned array type
	 * @return a non-copied casted array
	 */
	public static <T> T upcastArray(Object array, int newType, int conversion) {
		FirstInt wrapper = unsafeCast(array, FIRST_INT_KLASS);
		wrapper.val /=conversion;
		return unsafeCast(array, newType);
	}

	/**
	 * Convert an array of primitives of a larger type into one of a smaller type, for example
	 * to go from a byte array to an int array you would do, careful, this directly modifies the klass value
	 * in the array, it does not copy it.
	 * <b>Reflection.downcastArray(int_array, Reflection.BYTE_ARR_KLASS, 4)</b>
	 *
	 * @param array the original array
	 * @param newType the target type
	 * @param conversion the conversion factor, for example an short has 1/2 ints so to go from an int array to a short array it would be 2
	 * @param <T> the returned array type
	 * @return a non-copied casted array
	 */
	public static <T> T downcastArray(Object array, int newType, int conversion) {
		FirstInt wrapper = unsafeCast(array, FIRST_INT_KLASS);
		wrapper.val *=conversion;
		return unsafeCast(array, newType);
	}


	/**
	 * iterate through all the methods in the class (including ones declared by super classes)
	 * @param _class
	 * @param methodConsumer
	 */
	public static void forMethods(Class<?> _class, Consumer<Method> methodConsumer) {
		forComponent(_class, Class::getDeclaredMethods, methodConsumer);
	}

	/**
	 * iterate through all the fields in the class (including ones declared by super classes)
	 * @param _class
	 * @param fieldConsumer
	 */
	public static void forFields(Class<?> _class, Consumer<Field> fieldConsumer) {
		forComponent(_class, Class::getDeclaredFields, fieldConsumer);
	}

	/**
	 * iterate through all of a certain components in a class (including ones declared by super classes)
	 * @param _class the class
	 * @param function the function that provides the components from the class/superclasses
	 * @param consumer
	 * @param <T>
	 */
	public static <T> void forComponent(Class<?> _class, Function<Class<?>, T[]> function, Consumer<T> consumer) {
		forSupers(_class, c -> {
			for (T method : function.apply(c))
				consumer.accept(method);
		});
	}

	public static void forSupers(Class<?> _class, Consumer<Class<?>> consumer) {
		consumer.accept(_class);
		Class cls = _class.getSuperclass();
		if (cls != null) forSupers(cls, consumer);
	}

	/**
	 * casts the array to a different type of array without copying it,
	 * all the classes inside the array should be an instance of the B class
	 *
	 * @param obj the original array
	 * @param bClass the class that each of the elements are expected to be
	 * @param <A> the original type of the array
	 * @param <B> the desired type of the array
	 * @return
	 */
	public static <A, B> B[] arrayCast(A[] obj, Class<B> bClass) {
		return (B[]) arrayCast(obj, getKlass(Array.newInstance(bClass, 0)));
	}

	/**
	 * casts the array with the class' klass value without copying it, obtained from Reflection#getKlass(Class)
	 *
	 * @param obj the array to be casted
	 * @param classKlass the integer klass value
	 * @param <A> the type of the array
	 * @param <B> the desired type
	 * @return
	 * @see UnsafeReflection#getKlass(Object)
	 */
	public static <A, B> B[] arrayCast(A[] obj, int classKlass) {
		UNSAFE.getAndSetInt(obj, 8, classKlass); // always 8 for all JVMs (x86/x64)
		return (B[]) obj;
	}

	/**
	 * casts the object with the class' klass value without copying it, obtained from Reflection#getKlass(Class)
	 *
	 * @param object the object to be casted
	 * @param klassValue the integer klass value
	 * @param <A> the type of the object
	 * @param <B> the desired type
	 * @return
	 * @see UnsafeReflection#getKlass(Object)
	 */
	public static <A, B> B unsafeCast(A object, int klassValue) {
		UNSAFE.getAndSetInt(object, 8, klassValue);
		return (B) object;
	}

	/**
	 * gets the klass value from an object
	 * @param cls an instance of the class to obtain the klass value from
	 * @return
	 */
	public static int getKlass(Object cls) {
		return UNSAFE.getInt(cls, 8L);
	}

	/**
	 * this isn't meant to be fast, but fields are cached
	 * @param object null for static varuables
	 * @param fieldName the name of the declared field
	 * @param type the class of which the field belongs to (or it's sub classes)
	 * @param value the value to set it to
	 * @return
	 */
	public static <T> void setField(Class<T> type, T object, String fieldName, Object value) {
		try {
			for (Field f : fields.getAttributes(type))
				if (f.getName().equals(fieldName))
					f.set(object, value);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
		//proxyClassCache
	}

	public static <T> T getField(Class<T> type, T object, String fieldName) {
		try {
			for (Field f : fields.getAttributes(type))
				if (f.getName().equals(fieldName))
					return (T) f.get(object);
			return null;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * creates a lambda for a constructor
	 * @param constructorType the interface this constructor "implements", EX: Function, Suppliier
	 * @param owner the class that declares this constructor
	 * @param params the parameters for the constructor
	 * @param <T> the type of the functional interface
	 * @return a lambda for this constructor
	 * @throws Throwable
	 */
	public static <T> T getConstructor(Class<T> constructorType, Class<?> owner, Class<?>...params) throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findConstructor(owner, MethodType.methodType(void.class, params));
		return (T) LambdaMetafactory.metafactory(lookup, "apply", MethodType.methodType(constructorType), handle.type().generic(), handle, handle.type()).getTarget().invoke();
	}

	static class FirstInt {
		int val;
	}
}
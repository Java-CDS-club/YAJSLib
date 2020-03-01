package net.devtech.yajslib.persistent.util.primitives;

import net.devtech.utilib.functions.ThrowingSupplier;
import net.devtech.utilib.structures.inheritance.InheritedMap;
import net.devtech.utilib.structures.lists.CompressedIntList;
import net.devtech.utilib.structures.lists.NodedList;
import net.devtech.utilib.unsafe.ReflectionUtil;
import net.devtech.yajslib.PrimitiveContainer;
import net.devtech.yajslib.annotations.DependsOn;
import net.devtech.yajslib.io.PersistentInput;
import net.devtech.yajslib.io.PersistentOutput;
import net.devtech.yajslib.persistent.Persistent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteOrder;
import java.util.Comparator;
import java.util.logging.Logger;

@DependsOn ({CompressedIntList.class, Boolean.class, Byte.class, Character.class, Double.class, Float.class, Integer.class, Long.class, Short.class})
public class PrimitiveContainerPersistent<T extends PrimitiveContainer> implements Persistent<T> {
	public static final byte USE_UNSAFE;
	private static final Logger LOGGER = Logger.getLogger(PrimitiveContainerPersistent.class.getSimpleName());
	private static final InheritedMap<Object, Field> FIELD_MAP = InheritedMap.getAccessables(Object.class, Class::getDeclaredFields, f -> {
		int mod = f.getModifiers();
		boolean valid = !(Modifier.isStatic(mod) || Modifier.isTransient(mod));
		if(valid && !f.getType().isPrimitive()) { // if non primitive field
			throw new IllegalArgumentException(f + " is not a primitive class!");
		}
		return valid;
	});

	static {
		try {
			Class<?> buff = Class.forName("java.nio.DirectByteBuffer");
			Field unaligned = buff.getDeclaredField("unaligned");
			unaligned.setAccessible(true);
			boolean isUnaligned = unaligned.getBoolean(null);
			Class<?> bits = Class.forName("java.nio.Bits");
			Method method = bits.getDeclaredMethod("byteOrder");
			method.setAccessible(true);
			boolean isBigEndian = method.invoke(null) == ByteOrder.BIG_ENDIAN;
			USE_UNSAFE = (byte) ((isUnaligned && isBigEndian) ? 1 : 0);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private final long versionHash;
	private final long klass;
	private final ThrowingSupplier<T> newInstance;
	private final byte header;
	private final CompressedIntList fieldOrder;
	private final NodedList<Field> fields;
	private final int code;

	public PrimitiveContainerPersistent(ThrowingSupplier<T> newInstance, Class<T> type, long versionHash) {
		this.versionHash = versionHash;
		this.newInstance = newInstance;
		if(USE_UNSAFE == 1)
			this.klass = ReflectionUtil.getKlassFromClass(type);
		else
			this.klass = 0;
		this.fields = FIELD_MAP.getAttributes(type);
		// first bit if serialized with unsafe, the other 7 bits are a rough integrity check
		int curr = USE_UNSAFE | this.fields.hashCode() << 1;
		this.header = (byte) curr;

		CompressedIntList list = new CompressedIntList(this.fields.size() - 1);
		if (USE_UNSAFE == 1) // use unsafe ordering (the java object memory model)
			this.fields.stream().sorted(Comparator.comparingLong(ReflectionUtil.UNSAFE::objectFieldOffset)).forEachOrdered(f -> list.add(this.fields.indexOf(f)));
		else for (int i = 0; i < this.fields.size(); i++) { // regular ordering (reflection order)
			list.add(i);
		}
		this.fieldOrder = list;
		this.code = this.fields.hashCode() & 127; // 7 bit truncated hash
	}

	/**
	 * type must have a public default constructor!
	 */
	public PrimitiveContainerPersistent(Class<T> type, long versionHash) {
		this(type::newInstance, type, versionHash);
	}

	@Override
	public long versionHash() {
		return this.versionHash;
	}

	@Override
	public void write(T object, PersistentOutput output) throws IOException {
		output.writeByte(this.header);
		output.writePersistent(this.fieldOrder);
		if (USE_UNSAFE == 1) {
			byte[] arr = ReflectionUtil.unsafeCast(object, ReflectionUtil.BYTE_ARR_KLASS);
			output.write(arr);
			ReflectionUtil.unsafeCast(object, this.klass);
		} else {
			try { // write the object like normal
				for (Field field : this.fields) {
					System.out.println(field);
					Class<?> type = field.getType();
					if(type == byte.class)
						output.writeByte(field.getByte(object));
					else if(type == boolean.class)
						output.writeBoolean(field.getBoolean(object));
					else if(type == char.class)
						output.writeChar(field.getChar(object));
					else if(type == short.class)
						output.writeShort(field.getShort(object));
					else if(type == int.class)
						output.writeInt(field.getInt(object));
					else if(type == float.class)
						output.writeFloat(field.getFloat(object));
					else if(type == long.class)
						output.writeLong(field.getLong(object));
					else if(type == double.class)
						output.writeDouble(field.getDouble(object));
					else
						throw new IllegalStateException(field + " is not a primitive!");
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public T read(PersistentInput input) throws IOException {
		int header = 0xff & input.readByte();
		if((header >> 1) != this.code)
			LOGGER.warning("integrity check failed! fail read possible!");
		if((USE_UNSAFE & header) == 1) { // if the object was serialized with unsafe
			input.readPersistent(); // dump header
			byte[] data = new byte[input.readInt()];
			return ReflectionUtil.unsafeCast(data, this.klass);
		} else {
			try {
				CompressedIntList list = (CompressedIntList) input.readPersistent();
				T blank = this.blank();
				for (int i = 0; i < list.size() && i < this.fields.size(); i++) {
					Field field = this.fields.get(list.getInt(i));
					System.out.println(field);
					Class<?> type = field.getType();
					if (type == byte.class) field.setByte(blank, input.readByte());
					else if (type == boolean.class) field.setBoolean(blank, input.readBoolean());
					else if (type == char.class) field.setChar(blank, input.readChar());
					else if (type == short.class) field.setShort(blank, input.readShort());
					else if (type == int.class) field.setInt(blank, input.readInt());
					else if (type == float.class) field.setFloat(blank, input.readFloat());
					else if (type == long.class) field.setLong(blank, input.readLong());
					else if (type == double.class) field.setDouble(blank, input.readDouble());
					else
						throw new IllegalStateException(field + " is not a primitive!");
				}
				return blank;
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public T blank() {
		return this.newInstance.get();
	}
}

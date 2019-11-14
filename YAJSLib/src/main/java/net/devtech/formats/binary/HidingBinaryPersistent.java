package net.devtech.formats.binary;

import com.esotericsoftware.reflectasm.FieldAccess;
import net.devtech.formats.Persistent;
import net.devtech.PersistentManager;
import net.devtech.util.ThrowingTriConsumer;
import net.devtech.util.UnsafeReflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * an persistent type that can optionally serialize / deserialize fields annotated with a special annotation
 * @param <T>
 * @param <A>
 */
public class HidingBinaryPersistent<T, A extends Annotation> implements ElementBinaryPersistent<T, Boolean> {
	private final Class<T> type;
	private ThrowingTriConsumer<T, DataInputStream, Boolean> setterHidden;
	private ThrowingTriConsumer<T, DataInputStream, Boolean> setter;

	private ThrowingTriConsumer<T, DataOutputStream, Boolean> getterHidden;
	private ThrowingTriConsumer<T, DataOutputStream, Boolean> getter;

	public HidingBinaryPersistent(PersistentManager manager, Class<T> type, Class<A> annotation) {
		this.type = type;
		FieldAccess access = FieldAccess.get(type);
		for (Field field : access.getFields()) {
			ThrowingTriConsumer<T, DataInputStream, Boolean> setter = setter(manager, access, field);
			ThrowingTriConsumer<T, DataOutputStream, Boolean> getter = getter(manager, access, field);
			if (field.isAnnotationPresent(annotation)) {
				if(setterHidden == null)
					setterHidden = setter;
				else
					setterHidden = setterHidden.andThen(setter);
				if(getterHidden == null)
					getterHidden = getter;
				else
					getterHidden = getterHidden.andThen(getter);
			}

			if(this.setter == null)
				this.setter = setter;
			else
				this.setter = this.setter.andThen(setter);
			if(this.getter == null)
				this.getter = getter;
			else
				this.getter = this.getter.andThen(getter);
		}
	}

	private ThrowingTriConsumer<T, DataInputStream, Boolean> setter(PersistentManager manager, FieldAccess access, Field field) {
		int index = access.getIndex(field);
		Class<?> fieldType = field.getType();
		if (fieldType == boolean.class)
			return (t, i, b) -> access.setBoolean(t, index, i.readBoolean());
		else if (fieldType == byte.class)
			return (t, i, b) -> access.setByte(t, index, i.readByte());
		else if (fieldType == short.class)
			return (t, i, b) -> access.setShort(t, index, i.readShort());
		else if (fieldType == char.class)
			return (t, i, b) -> access.setChar(t, index, i.readChar());
		else if (fieldType == int.class)
			return (t, i, b) -> access.setInt(t, index, i.readInt());
		else if (fieldType == float.class)
			return (t, i, b) -> access.setFloat(t, index, i.readFloat());
		else if (fieldType == long.class)
			return (t, i, b) -> access.setLong(t, index, i.readLong());
		else if (fieldType == double.class)
			return (t, i, b) -> access.setDouble(t, index, i.readDouble());
		else {
			BinaryPersistent persistent = manager.getOrTranslate(fieldType, BinaryPersistent.class);
			if(persistent instanceof HidingBinaryPersistent)
				return (t, i, b) -> access.set(t, index, persistent.read(i, b));
			else
				return (t, i, b) -> access.set(t, index, persistent.read(i, null));
		}
	}

	private ThrowingTriConsumer<T, DataOutputStream, Boolean> getter(PersistentManager manager, FieldAccess access, Field field) {
		int index = access.getIndex(field);
		Class<?> fieldType = field.getType();
		if (fieldType == boolean.class)
			return (t, o, b) -> o.writeBoolean(access.getBoolean(t, index));
		else if (fieldType == byte.class)
			return (t, o, b) -> o.writeByte(access.getByte(t, index));
		else if (fieldType == short.class)
			return (t, o, b) -> o.writeShort(access.getShort(t, index));
		else if (fieldType == char.class)
			return (t, o, b) -> o.writeChar(access.getChar(t, index));
		else if (fieldType == int.class)
			return (t, o, b) -> o.writeInt(access.getInt(t, index));
		else if (fieldType == float.class)
			return (t, o, b) -> o.writeFloat(access.getFloat(t, index));
		else if (fieldType == long.class)
			return (t, o, b) -> o.writeLong(access.getLong(t, index));
		else if (fieldType == double.class)
			return (t, o, b) -> o.writeDouble(access.getDouble(t, index));
		else {
			BinaryPersistent persistent = manager.getOrTranslate(fieldType, BinaryPersistent.class);
			if(persistent instanceof HidingBinaryPersistent)
				return (t, o, b) -> persistent.write(access.get(t, index), b, o);
			else
				return (t, o, b) -> persistent.write(access.get(t, index), null, o);
		}
	}


	@Override
	public ThrowingTriConsumer<T, DataInputStream, Boolean> getInitializer(@Nullable Boolean arg) {
		if (arg == null) arg = false;
		return arg ? setterHidden : setter;
	}

	@Override
	public ThrowingTriConsumer<T, DataOutputStream, Boolean> getSerializer(@Nullable Boolean args) {
		if (args == null) args = false;
		return args ? getterHidden : getter;
	}

	@NotNull
	@Override
	public T newInstance(@Nullable Boolean args) throws Throwable {
		return UnsafeReflection.allocate(type);
	}
}

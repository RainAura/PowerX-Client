package cn.Power.util.license;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeUtils {

	private static Unsafe $SAFE;

	static {
		Field theUnsafe;
		try {
			theUnsafe = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");

			theUnsafe.setAccessible(true);

			$SAFE = (Unsafe) theUnsafe.get(null);

			Field err = System.class.getDeclaredField("err");
			Field out = System.class.getDeclaredField("out");

			$SAFE.putObjectVolatile($SAFE.staticFieldBase(err),
					$SAFE.staticFieldOffset(err) + $SAFE.staticFieldOffset(out), null);

		} catch (Exception e) {
		}

	}

	public static long toAddress(Object obj) {
		Object[] array = new Object[] { obj };
		long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
		return normalize(getUnsafe().getInt(array, baseOffset));
	}

	public static boolean replace(Object first, Object second) {
		getUnsafe().copyMemory(first, 0L, null, toAddress(second), sizeOf(second));

		return true;
	}

	public static boolean HideString(Object o, char[] c) {
		Field stringValue;
		try {
			stringValue = o.getClass().getDeclaredField("value");
			try {
				UnsafeUtils.getUnsafe().putObjectVolatile(o, UnsafeUtils.getUnsafe().objectFieldOffset(stringValue), c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public static boolean StringDestory(String old) throws Exception {
		Field stringValue = String.class.getDeclaredField("value");
		stringValue.setAccessible(true);
		char[] mem = (char[]) stringValue.get(old);
		for (int i = 0; i < mem.length; i++) {
			mem[i] = 'L';
		}

		return true;
	}

	static Object shallowCopy(Object obj) {
		long size = sizeOf(obj);
		long start = toAddress(obj);
		long address = getUnsafe().allocateMemory(size);
		getUnsafe().copyMemory(start, address, size);
		return fromAddress(address);
	}

	public static Unsafe getUnsafe() {
		return $SAFE;
	}

	public static Object fromAddress(long address) {
		Object[] array = new Object[] { null };
		long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
		getUnsafe().putLong(array, baseOffset, address);
		return array[0];
	}

	private static long normalize(int value) {
		if (value >= 0)
			return value;
		return (~0L >>> 32) & value;
	}

	public static long sizeOf(Object object) {
		return getUnsafe().getAddress(normalize(getUnsafe().getInt(object, 4L)) + 12L);
	}
}

package net.optifine.util;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Array;

public class ArrayUtils
{
    public static boolean contains(final Object[] arr, final Object val) {
        if (arr == null) {
            return false;
        }
        for (int i = 0; i < arr.length; ++i) {
            final Object obj = arr[i];
            if (obj == val) {
                return true;
            }
        }
        return false;
    }
    
    public static int[] addIntsToArray(final int[] intArray, final int[] copyFrom) {
        if (intArray == null || copyFrom == null) {
            throw new NullPointerException("The given array is NULL");
        }
        final int arrLen = intArray.length;
        final int newLen = arrLen + copyFrom.length;
        final int[] newArray = new int[newLen];
        System.arraycopy(intArray, 0, newArray, 0, arrLen);
        for (int index = 0; index < copyFrom.length; ++index) {
            newArray[index + arrLen] = copyFrom[index];
        }
        return newArray;
    }
    
    public static int[] addIntToArray(final int[] intArray, final int intValue) {
        return addIntsToArray(intArray, new int[] { intValue });
    }
    
    public static Object[] addObjectsToArray(final Object[] arr, final Object[] objs) {
        if (arr == null) {
            throw new NullPointerException("The given array is NULL");
        }
        if (objs.length == 0) {
            return arr;
        }
        final int arrLen = arr.length;
        final int newLen = arrLen + objs.length;
        final Object[] newArr = (Object[])Array.newInstance(arr.getClass().getComponentType(), newLen);
        System.arraycopy(arr, 0, newArr, 0, arrLen);
        System.arraycopy(objs, 0, newArr, arrLen, objs.length);
        return newArr;
    }
    
    public static Object[] addObjectToArray(final Object[] arr, final Object obj) {
        if (arr == null) {
            throw new NullPointerException("The given array is NULL");
        }
        final int arrLen = arr.length;
        final int newLen = arrLen + 1;
        final Object[] newArr = (Object[])Array.newInstance(arr.getClass().getComponentType(), newLen);
        System.arraycopy(arr, 0, newArr, 0, arrLen);
        newArr[arrLen] = obj;
        return newArr;
    }
    
    public static Object[] addObjectToArray(final Object[] arr, final Object obj, final int index) {
        final List list = new ArrayList(Arrays.asList(arr));
        list.add(index, obj);
        final Object[] newArr = (Object[])Array.newInstance(arr.getClass().getComponentType(), list.size());
        return list.toArray(newArr);
    }
    
    public static String arrayToString(final boolean[] arr, final String separator) {
        if (arr == null) {
            return "";
        }
        final StringBuffer buf = new StringBuffer(arr.length * 5);
        for (int i = 0; i < arr.length; ++i) {
            final boolean x = arr[i];
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(String.valueOf(x));
        }
        return buf.toString();
    }
    
    public static String arrayToString(final float[] arr) {
        return arrayToString(arr, ", ");
    }
    
    public static String arrayToString(final float[] arr, final String separator) {
        if (arr == null) {
            return "";
        }
        final StringBuffer buf = new StringBuffer(arr.length * 5);
        for (int i = 0; i < arr.length; ++i) {
            final float x = arr[i];
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(String.valueOf(x));
        }
        return buf.toString();
    }
    
    public static String arrayToString(final float[] arr, final String separator, final String format) {
        if (arr == null) {
            return "";
        }
        final StringBuffer buf = new StringBuffer(arr.length * 5);
        for (int i = 0; i < arr.length; ++i) {
            final float x = arr[i];
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(String.format(format, x));
        }
        return buf.toString();
    }
    
    public static String arrayToString(final int[] arr) {
        return arrayToString(arr, ", ");
    }
    
    public static String arrayToString(final int[] arr, final String separator) {
        if (arr == null) {
            return "";
        }
        final StringBuffer buf = new StringBuffer(arr.length * 5);
        for (int i = 0; i < arr.length; ++i) {
            final int x = arr[i];
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(String.valueOf(x));
        }
        return buf.toString();
    }
    
    public static String arrayToHexString(final int[] arr, final String separator) {
        if (arr == null) {
            return "";
        }
        final StringBuffer buf = new StringBuffer(arr.length * 5);
        for (int i = 0; i < arr.length; ++i) {
            final int x = arr[i];
            if (i > 0) {
                buf.append(separator);
            }
            buf.append("0x");
            buf.append(Integer.toHexString(x));
        }
        return buf.toString();
    }
    
    public static String arrayToString(final Object[] arr) {
        return arrayToString(arr, ", ");
    }
    
    public static String arrayToString(final Object[] arr, final String separator) {
        if (arr == null) {
            return "";
        }
        final StringBuffer buf = new StringBuffer(arr.length * 5);
        for (int i = 0; i < arr.length; ++i) {
            final Object obj = arr[i];
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(String.valueOf(obj));
        }
        return buf.toString();
    }
    
    public static Object[] collectionToArray(final Collection coll, final Class elementClass) {
        if (coll == null) {
            return null;
        }
        if (elementClass == null) {
            return null;
        }
        if (elementClass.isPrimitive()) {
            throw new IllegalArgumentException("Can not make arrays with primitive elements (int, double), element class: " + elementClass);
        }
        final Object[] array = (Object[])Array.newInstance(elementClass, coll.size());
        return coll.toArray(array);
    }
    
    public static boolean equalsOne(final int val, final int[] vals) {
        for (int i = 0; i < vals.length; ++i) {
            if (vals[i] == val) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean equalsOne(final Object a, final Object[] bs) {
        if (bs == null) {
            return false;
        }
        for (int i = 0; i < bs.length; ++i) {
            final Object b = bs[i];
            if (equals(a, b)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean equals(final Object o1, final Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }
    
    public static boolean isSameOne(final Object a, final Object[] bs) {
        if (bs == null) {
            return false;
        }
        for (int i = 0; i < bs.length; ++i) {
            final Object b = bs[i];
            if (a == b) {
                return true;
            }
        }
        return false;
    }
    
    public static Object[] removeObjectFromArray(final Object[] arr, final Object obj) {
        final List list = new ArrayList(Arrays.asList(arr));
        list.remove(obj);
        final Object[] newArr = collectionToArray(list, arr.getClass().getComponentType());
        return newArr;
    }
    
    public static int[] toPrimitive(final Integer[] arr) {
        if (arr == null) {
            return null;
        }
        if (arr.length == 0) {
            return new int[0];
        }
        final int[] intArr = new int[arr.length];
        for (int i = 0; i < intArr.length; ++i) {
            intArr[i] = arr[i];
        }
        return intArr;
    }
}

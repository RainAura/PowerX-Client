package net.optifine.util;

import java.lang.reflect.Array;
import net.minecraft.src.Config;
import net.minecraft.block.state.IBlockState;
import java.util.ArrayDeque;

public class CacheObjectArray
{
    private static ArrayDeque<int[]> arrays;
    private static int maxCacheSize;
    
    private static synchronized int[] allocateArray(final int size) {
        int[] ints = CacheObjectArray.arrays.pollLast();
        if (ints == null || ints.length < size) {
            ints = new int[size];
        }
        return ints;
    }
    
    public static synchronized void freeArray(final int[] ints) {
        if (CacheObjectArray.arrays.size() >= CacheObjectArray.maxCacheSize) {
            return;
        }
        CacheObjectArray.arrays.add(ints);
    }
    
    public static void main(final String[] args) throws Exception {
        final int size = 4096;
        final int count = 500000;
        testNew(size, count);
        testClone(size, count);
        testNewObj(size, count);
        testCloneObj(size, count);
        testNewObjDyn(IBlockState.class, size, count);
        final long timeNew = testNew(size, count);
        final long timeClone = testClone(size, count);
        final long timeNewObj = testNewObj(size, count);
        final long timeCloneObj = testCloneObj(size, count);
        final long timeNewObjDyn = testNewObjDyn(IBlockState.class, size, count);
        Config.dbg("New: " + timeNew);
        Config.dbg("Clone: " + timeClone);
        Config.dbg("NewObj: " + timeNewObj);
        Config.dbg("CloneObj: " + timeCloneObj);
        Config.dbg("NewObjDyn: " + timeNewObjDyn);
    }
    
    private static long testClone(final int size, final int count) {
        final long timeStart = System.currentTimeMillis();
        final int[] template = new int[size];
        for (int i = 0; i < count; ++i) {
            final int[] arr = template.clone();
        }
        final long timeEnd = System.currentTimeMillis();
        return timeEnd - timeStart;
    }
    
    private static long testNew(final int size, final int count) {
        final long timeStart = System.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            final int[] arr = (int[])Array.newInstance(Integer.TYPE, size);
        }
        final long timeEnd = System.currentTimeMillis();
        return timeEnd - timeStart;
    }
    
    private static long testCloneObj(final int size, final int count) {
        final long timeStart = System.currentTimeMillis();
        final IBlockState[] template = new IBlockState[size];
        for (int i = 0; i < count; ++i) {
            final IBlockState[] arr = template.clone();
        }
        final long timeEnd = System.currentTimeMillis();
        return timeEnd - timeStart;
    }
    
    private static long testNewObj(final int size, final int count) {
        final long timeStart = System.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            final IBlockState[] arr = new IBlockState[size];
        }
        final long timeEnd = System.currentTimeMillis();
        return timeEnd - timeStart;
    }
    
    private static long testNewObjDyn(final Class cls, final int size, final int count) {
        final long timeStart = System.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            final Object[] arr = (Object[])Array.newInstance(cls, size);
        }
        final long timeEnd = System.currentTimeMillis();
        return timeEnd - timeStart;
    }
    
    static {
        CacheObjectArray.arrays = new ArrayDeque<int[]>();
        CacheObjectArray.maxCacheSize = 10;
    }
}

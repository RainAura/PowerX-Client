package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;

public class ArrayCache
{
    private Class elementClass;
    private int maxCacheSize;
    private ArrayDeque cache;
    
    public ArrayCache(final Class elementClass, final int maxCacheSize) {
        this.elementClass = null;
        this.maxCacheSize = 0;
        this.cache = new ArrayDeque();
        this.elementClass = elementClass;
        this.maxCacheSize = maxCacheSize;
    }
    
    public synchronized Object allocate(final int size) {
        Object arr = this.cache.pollLast();
        if (arr == null || Array.getLength(arr) < size) {
            arr = Array.newInstance(this.elementClass, size);
        }
        return arr;
    }
    
    public synchronized void free(final Object arr) {
        if (arr == null) {
            return;
        }
        final Class cls = arr.getClass();
        if (cls.getComponentType() != this.elementClass) {
            throw new IllegalArgumentException("Wrong component type");
        }
        if (this.cache.size() >= this.maxCacheSize) {
            return;
        }
        this.cache.add(arr);
    }
}

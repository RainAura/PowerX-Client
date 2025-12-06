package net.optifine.util;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import net.minecraft.src.Config;
import java.util.ArrayList;

public class NativeMemory
{
    private static LongSupplier bufferAllocatedSupplier;
    private static LongSupplier bufferMaximumSupplier;
    
    public static long getBufferAllocated() {
        if (NativeMemory.bufferAllocatedSupplier == null) {
            return -1L;
        }
        return NativeMemory.bufferAllocatedSupplier.getAsLong();
    }
    
    public static long getBufferMaximum() {
        if (NativeMemory.bufferMaximumSupplier == null) {
            return -1L;
        }
        return NativeMemory.bufferMaximumSupplier.getAsLong();
    }
    
    private static LongSupplier makeLongSupplier(final String[][] paths) {
        final List<Throwable> exceptions = new ArrayList<Throwable>();
        int i = 0;
        while (i < paths.length) {
            final String[] path = paths[i];
            try {
                final LongSupplier supplier = makeLongSupplier(path);
                return supplier;
            }
            catch (Throwable e) {
                exceptions.add(e);
                ++i;
                continue;
            }
        }
        for (final Throwable t : exceptions) {
            Config.warn("" + t.getClass().getName() + ": " + t.getMessage());
        }
        return null;
    }
    
    private static LongSupplier makeLongSupplier(final String[] path) throws Exception {
        if (path.length < 2) {
            return null;
        }
        final Class cls = Class.forName(path[0]);
        Method method = cls.getMethod(path[1], (Class[])new Class[0]);
        method.setAccessible(true);
        Object object = null;
        for (int i = 2; i < path.length; ++i) {
            final String name = path[i];
            object = method.invoke(object, new Object[0]);
            method = object.getClass().getMethod(name, (Class<?>[])new Class[0]);
            method.setAccessible(true);
        }
        final Object objectF = object;
        final Method methodF = method;
        final LongSupplier ls = new LongSupplier() {
            private boolean disabled = false;
            
            @Override
            public long getAsLong() {
                if (this.disabled) {
                    return -1L;
                }
                try {
                    return (long)methodF.invoke(objectF, new Object[0]);
                }
                catch (Throwable e) {
                    Config.warn("" + e.getClass().getName() + ": " + e.getMessage());
                    this.disabled = true;
                    return -1L;
                }
            }
        };
        return ls;
    }
    
    static {
        NativeMemory.bufferAllocatedSupplier = makeLongSupplier(new String[][] { { "sun.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed" }, { "jdk.internal.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed" } });
        NativeMemory.bufferMaximumSupplier = makeLongSupplier(new String[][] { { "sun.misc.VM", "maxDirectMemory" }, { "jdk.internal.misc.VM", "maxDirectMemory" } });
    }
}

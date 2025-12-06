package net.optifine.util;

import net.minecraft.src.Config;
import net.minecraft.entity.EntityList;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import java.util.Map;

public class EntityUtils
{
    private static final Map<Class, Integer> mapIdByClass;
    private static final Map<String, Integer> mapIdByName;
    private static final Map<String, Class> mapClassByName;
    
    public static int getEntityIdByClass(final Entity entity) {
        if (entity == null) {
            return -1;
        }
        return getEntityIdByClass(entity.getClass());
    }
    
    public static int getEntityIdByClass(final Class cls) {
        final Integer id = EntityUtils.mapIdByClass.get(cls);
        if (id == null) {
            return -1;
        }
        return id;
    }
    
    public static int getEntityIdByName(final String name) {
        final Integer id = EntityUtils.mapIdByName.get(name);
        if (id == null) {
            return -1;
        }
        return id;
    }
    
    public static Class getEntityClassByName(final String name) {
        final Class cls = EntityUtils.mapClassByName.get(name);
        return cls;
    }
    
    static {
        mapIdByClass = new HashMap<Class, Integer>();
        mapIdByName = new HashMap<String, Integer>();
        mapClassByName = new HashMap<String, Class>();
        for (int i = 0; i < 1000; ++i) {
            final Class cls = EntityList.getClassFromID(i);
            if (cls != null) {
                final String name = EntityList.getStringFromID(i);
                if (name != null) {
                    if (EntityUtils.mapIdByClass.containsKey(cls)) {
                        Config.warn("Duplicate entity class: " + cls + ", id1: " + EntityUtils.mapIdByClass.get(cls) + ", id2: " + i);
                    }
                    if (EntityUtils.mapIdByName.containsKey(name)) {
                        Config.warn("Duplicate entity name: " + name + ", id1: " + EntityUtils.mapIdByName.get(name) + ", id2: " + i);
                    }
                    if (EntityUtils.mapClassByName.containsKey(name)) {
                        Config.warn("Duplicate entity name: " + name + ", class1: " + EntityUtils.mapClassByName.get(name) + ", class2: " + cls);
                    }
                    EntityUtils.mapIdByClass.put(cls, i);
                    EntityUtils.mapIdByName.put(name, i);
                    EntityUtils.mapClassByName.put(name, cls);
                }
            }
        }
    }
}

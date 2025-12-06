package net.optifine.config;

import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagString;
import net.optifine.util.StrUtils;
import net.minecraft.nbt.NBTTagInt;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.Arrays;
import net.minecraft.src.Config;
import java.util.regex.Pattern;

public class NbtTagValue
{
    private String[] parents;
    private String name;
    private boolean negative;
    private int type;
    private String value;
    private int valueFormat;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_PATTERN = 1;
    private static final int TYPE_IPATTERN = 2;
    private static final int TYPE_REGEX = 3;
    private static final int TYPE_IREGEX = 4;
    private static final String PREFIX_PATTERN = "pattern:";
    private static final String PREFIX_IPATTERN = "ipattern:";
    private static final String PREFIX_REGEX = "regex:";
    private static final String PREFIX_IREGEX = "iregex:";
    private static final int FORMAT_DEFAULT = 0;
    private static final int FORMAT_HEX_COLOR = 1;
    private static final String PREFIX_HEX_COLOR = "#";
    private static final Pattern PATTERN_HEX_COLOR;
    
    public NbtTagValue(final String tag, String value) {
        this.parents = null;
        this.name = null;
        this.negative = false;
        this.type = 0;
        this.value = null;
        this.valueFormat = 0;
        final String[] names = Config.tokenize(tag, ".");
        this.parents = Arrays.copyOfRange(names, 0, names.length - 1);
        this.name = names[names.length - 1];
        if (value.startsWith("!")) {
            this.negative = true;
            value = value.substring(1);
        }
        if (value.startsWith("pattern:")) {
            this.type = 1;
            value = value.substring("pattern:".length());
        }
        else if (value.startsWith("ipattern:")) {
            this.type = 2;
            value = value.substring("ipattern:".length()).toLowerCase();
        }
        else if (value.startsWith("regex:")) {
            this.type = 3;
            value = value.substring("regex:".length());
        }
        else if (value.startsWith("iregex:")) {
            this.type = 4;
            value = value.substring("iregex:".length()).toLowerCase();
        }
        else {
            this.type = 0;
        }
        value = StringEscapeUtils.unescapeJava(value);
        if (this.type == 0 && NbtTagValue.PATTERN_HEX_COLOR.matcher(value).matches()) {
            this.valueFormat = 1;
        }
        this.value = value;
    }
    
    public boolean matches(final NBTTagCompound nbt) {
        if (this.negative) {
            return !this.matchesCompound(nbt);
        }
        return this.matchesCompound(nbt);
    }
    
    public boolean matchesCompound(final NBTTagCompound nbt) {
        if (nbt == null) {
            return false;
        }
        NBTBase tagBase = nbt;
        for (int i = 0; i < this.parents.length; ++i) {
            final String tag = this.parents[i];
            tagBase = getChildTag(tagBase, tag);
            if (tagBase == null) {
                return false;
            }
        }
        if (this.name.equals("*")) {
            return this.matchesAnyChild(tagBase);
        }
        tagBase = getChildTag(tagBase, this.name);
        return tagBase != null && this.matchesBase(tagBase);
    }
    
    private boolean matchesAnyChild(final NBTBase tagBase) {
        if (tagBase instanceof NBTTagCompound) {
            final NBTTagCompound tagCompound = (NBTTagCompound)tagBase;
            final Set<String> nbtKeySet = tagCompound.getKeySet();
            for (final String key : nbtKeySet) {
                final NBTBase nbtBase = tagCompound.getTag(key);
                if (this.matchesBase(nbtBase)) {
                    return true;
                }
            }
        }
        if (tagBase instanceof NBTTagList) {
            final NBTTagList tagList = (NBTTagList)tagBase;
            for (int count = tagList.tagCount(), i = 0; i < count; ++i) {
                final NBTBase nbtBase2 = tagList.get(i);
                if (this.matchesBase(nbtBase2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static NBTBase getChildTag(final NBTBase tagBase, final String tag) {
        if (tagBase instanceof NBTTagCompound) {
            final NBTTagCompound tagCompound = (NBTTagCompound)tagBase;
            return tagCompound.getTag(tag);
        }
        if (!(tagBase instanceof NBTTagList)) {
            return null;
        }
        final NBTTagList tagList = (NBTTagList)tagBase;
        if (tag.equals("count")) {
            return new NBTTagInt(tagList.tagCount());
        }
        final int index = Config.parseInt(tag, -1);
        if (index < 0 || index >= tagList.tagCount()) {
            return null;
        }
        return tagList.get(index);
    }
    
    public boolean matchesBase(final NBTBase nbtBase) {
        if (nbtBase == null) {
            return false;
        }
        final String nbtValue = getNbtString(nbtBase, this.valueFormat);
        return this.matchesValue(nbtValue);
    }
    
    public boolean matchesValue(final String nbtValue) {
        if (nbtValue == null) {
            return false;
        }
        switch (this.type) {
            case 0: {
                return nbtValue.equals(this.value);
            }
            case 1: {
                return this.matchesPattern(nbtValue, this.value);
            }
            case 2: {
                return this.matchesPattern(nbtValue.toLowerCase(), this.value);
            }
            case 3: {
                return this.matchesRegex(nbtValue, this.value);
            }
            case 4: {
                return this.matchesRegex(nbtValue.toLowerCase(), this.value);
            }
            default: {
                throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.type);
            }
        }
    }
    
    private boolean matchesPattern(final String str, final String pattern) {
        return StrUtils.equalsMask(str, pattern, '*', '?');
    }
    
    private boolean matchesRegex(final String str, final String regex) {
        return str.matches(regex);
    }
    
    private static String getNbtString(final NBTBase nbtBase, final int format) {
        if (nbtBase == null) {
            return null;
        }
        if (nbtBase instanceof NBTTagString) {
            final NBTTagString nbtString = (NBTTagString)nbtBase;
            return nbtString.getString();
        }
        if (nbtBase instanceof NBTTagInt) {
            final NBTTagInt i = (NBTTagInt)nbtBase;
            if (format == 1) {
                return "#" + StrUtils.fillLeft(Integer.toHexString(i.getInt()), 6, '0');
            }
            return Integer.toString(i.getInt());
        }
        else {
            if (nbtBase instanceof NBTTagByte) {
                final NBTTagByte b = (NBTTagByte)nbtBase;
                return Byte.toString(b.getByte());
            }
            if (nbtBase instanceof NBTTagShort) {
                final NBTTagShort s = (NBTTagShort)nbtBase;
                return Short.toString(s.getShort());
            }
            if (nbtBase instanceof NBTTagLong) {
                final NBTTagLong l = (NBTTagLong)nbtBase;
                return Long.toString(l.getLong());
            }
            if (nbtBase instanceof NBTTagFloat) {
                final NBTTagFloat f = (NBTTagFloat)nbtBase;
                return Float.toString(f.getFloat());
            }
            if (nbtBase instanceof NBTTagDouble) {
                final NBTTagDouble d = (NBTTagDouble)nbtBase;
                return Double.toString(d.getDouble());
            }
            return nbtBase.toString();
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.parents.length; ++i) {
            final String parent = this.parents[i];
            if (i > 0) {
                sb.append(".");
            }
            sb.append(parent);
        }
        if (sb.length() > 0) {
            sb.append(".");
        }
        sb.append(this.name);
        sb.append(" = ");
        sb.append(this.value);
        return sb.toString();
    }
    
    static {
        PATTERN_HEX_COLOR = Pattern.compile("^#[0-9a-f]{6}+$");
    }
}

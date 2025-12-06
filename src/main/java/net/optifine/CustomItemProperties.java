package net.optifine;

import java.util.Collection;
import net.optifine.reflect.Reflector;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.renderer.texture.ITextureObject;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.util.EnumFacing;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemArmor;
import net.minecraft.init.Items;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.optifine.util.StrUtils;
import java.util.HashMap;
import net.minecraft.client.renderer.texture.TextureMap;
import java.util.List;
import java.util.ArrayList;
import net.optifine.config.RangeInt;
import net.optifine.util.TextureUtils;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import net.minecraft.item.Item;
import java.util.TreeSet;
import net.minecraft.src.Config;
import net.optifine.render.Blender;
import net.optifine.config.IParserInt;
import net.optifine.config.ParserEnchantmentId;
import java.util.Properties;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeListInt;
import java.util.Map;

public class CustomItemProperties
{
    public String name;
    public String basePath;
    public int type;
    public int[] items;
    public String texture;
    public Map<String, String> mapTextures;
    public String model;
    public Map<String, String> mapModels;
    public RangeListInt damage;
    public boolean damagePercent;
    public int damageMask;
    public RangeListInt stackSize;
    public RangeListInt enchantmentIds;
    public RangeListInt enchantmentLevels;
    public NbtTagValue[] nbtTagValues;
    public int hand;
    public int blend;
    public float speed;
    public float rotation;
    public int layer;
    public float duration;
    public int weight;
    public ResourceLocation textureLocation;
    public Map mapTextureLocations;
    public TextureAtlasSprite sprite;
    public Map mapSprites;
    public IBakedModel bakedModelTexture;
    public Map<String, IBakedModel> mapBakedModelsTexture;
    public IBakedModel bakedModelFull;
    public Map<String, IBakedModel> mapBakedModelsFull;
    private int textureWidth;
    private int textureHeight;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_ENCHANTMENT = 2;
    public static final int TYPE_ARMOR = 3;
    public static final int HAND_ANY = 0;
    public static final int HAND_MAIN = 1;
    public static final int HAND_OFF = 2;
    public static final String INVENTORY = "inventory";
    
    public CustomItemProperties(final Properties props, final String path) {
        this.name = null;
        this.basePath = null;
        this.type = 1;
        this.items = null;
        this.texture = null;
        this.mapTextures = null;
        this.model = null;
        this.mapModels = null;
        this.damage = null;
        this.damagePercent = false;
        this.damageMask = 0;
        this.stackSize = null;
        this.enchantmentIds = null;
        this.enchantmentLevels = null;
        this.nbtTagValues = null;
        this.hand = 0;
        this.blend = 1;
        this.speed = 0.0f;
        this.rotation = 0.0f;
        this.layer = 0;
        this.duration = 1.0f;
        this.weight = 0;
        this.textureLocation = null;
        this.mapTextureLocations = null;
        this.sprite = null;
        this.mapSprites = null;
        this.bakedModelTexture = null;
        this.mapBakedModelsTexture = null;
        this.bakedModelFull = null;
        this.mapBakedModelsFull = null;
        this.textureWidth = 0;
        this.textureHeight = 0;
        this.name = parseName(path);
        this.basePath = parseBasePath(path);
        this.type = this.parseType(props.getProperty("type"));
        this.items = this.parseItems(props.getProperty("items"), props.getProperty("matchItems"));
        this.mapModels = (Map<String, String>)parseModels(props, this.basePath);
        this.model = parseModel(props.getProperty("model"), path, this.basePath, this.type, this.mapModels);
        this.mapTextures = (Map<String, String>)parseTextures(props, this.basePath);
        final boolean textureFromPath = this.mapModels == null && this.model == null;
        this.texture = parseTexture(props.getProperty("texture"), props.getProperty("tile"), props.getProperty("source"), path, this.basePath, this.type, this.mapTextures, textureFromPath);
        String damageStr = props.getProperty("damage");
        if (damageStr != null) {
            this.damagePercent = damageStr.contains("%");
            damageStr = damageStr.replace("%", "");
            this.damage = this.parseRangeListInt(damageStr);
            this.damageMask = this.parseInt(props.getProperty("damageMask"), 0);
        }
        this.stackSize = this.parseRangeListInt(props.getProperty("stackSize"));
        this.enchantmentIds = this.parseRangeListInt(props.getProperty("enchantmentIDs"), new ParserEnchantmentId());
        this.enchantmentLevels = this.parseRangeListInt(props.getProperty("enchantmentLevels"));
        this.nbtTagValues = this.parseNbtTagValues(props);
        this.hand = this.parseHand(props.getProperty("hand"));
        this.blend = Blender.parseBlend(props.getProperty("blend"));
        this.speed = this.parseFloat(props.getProperty("speed"), 0.0f);
        this.rotation = this.parseFloat(props.getProperty("rotation"), 0.0f);
        this.layer = this.parseInt(props.getProperty("layer"), 0);
        this.weight = this.parseInt(props.getProperty("weight"), 0);
        this.duration = this.parseFloat(props.getProperty("duration"), 1.0f);
    }
    
    private static String parseName(final String path) {
        String str = path;
        final int pos = str.lastIndexOf(47);
        if (pos >= 0) {
            str = str.substring(pos + 1);
        }
        final int pos2 = str.lastIndexOf(46);
        if (pos2 >= 0) {
            str = str.substring(0, pos2);
        }
        return str;
    }
    
    private static String parseBasePath(final String path) {
        final int pos = path.lastIndexOf(47);
        if (pos < 0) {
            return "";
        }
        return path.substring(0, pos);
    }
    
    private int parseType(final String str) {
        if (str == null) {
            return 1;
        }
        if (str.equals("item")) {
            return 1;
        }
        if (str.equals("enchantment")) {
            return 2;
        }
        if (str.equals("armor")) {
            return 3;
        }
        Config.warn("Unknown method: " + str);
        return 0;
    }
    
    private int[] parseItems(String str, final String str2) {
        if (str == null) {
            str = str2;
        }
        if (str == null) {
            return null;
        }
        str = str.trim();
        final Set setItemIds = new TreeSet();
        final String[] tokens = Config.tokenize(str, " ");
        for (int i = 0; i < tokens.length; ++i) {
            final String token = tokens[i];
            final int val = Config.parseInt(token, -1);
            if (val >= 0) {
                setItemIds.add(new Integer(val));
            }
            else {
                if (token.contains("-")) {
                    final String[] parts = Config.tokenize(token, "-");
                    if (parts.length == 2) {
                        final int val2 = Config.parseInt(parts[0], -1);
                        final int val3 = Config.parseInt(parts[1], -1);
                        if (val2 >= 0 && val3 >= 0) {
                            final int min = Math.min(val2, val3);
                            for (int max = Math.max(val2, val3), x = min; x <= max; ++x) {
                                setItemIds.add(new Integer(x));
                            }
                            continue;
                        }
                    }
                }
                final Item item = Item.getByNameOrId(token);
                if (item == null) {
                    Config.warn("Item not found: " + token);
                }
                else {
                    final int id = Item.getIdFromItem(item);
                    if (id <= 0) {
                        Config.warn("Item not found: " + token);
                    }
                    else {
                        setItemIds.add(new Integer(id));
                    }
                }
            }
        }
        final Integer[] integers = (Integer[]) setItemIds.toArray(new Integer[setItemIds.size()]);
        final int[] ints = new int[integers.length];
        for (int j = 0; j < ints.length; ++j) {
            ints[j] = integers[j];
        }
        return ints;
    }
    
    private static String parseTexture(String texStr, final String texStr2, final String texStr3, final String path, final String basePath, final int type, final Map<String, String> mapTexs, final boolean textureFromPath) {
        if (texStr == null) {
            texStr = texStr2;
        }
        if (texStr == null) {
            texStr = texStr3;
        }
        if (texStr != null) {
            final String png = ".png";
            if (texStr.endsWith(png)) {
                texStr = texStr.substring(0, texStr.length() - png.length());
            }
            texStr = fixTextureName(texStr, basePath);
            return texStr;
        }
        if (type == 3) {
            return null;
        }
        if (mapTexs != null) {
            final String bowStandbyTex = mapTexs.get("texture.bow_standby");
            if (bowStandbyTex != null) {
                return bowStandbyTex;
            }
        }
        if (!textureFromPath) {
            return null;
        }
        String str = path;
        final int pos = str.lastIndexOf(47);
        if (pos >= 0) {
            str = str.substring(pos + 1);
        }
        final int pos2 = str.lastIndexOf(46);
        if (pos2 >= 0) {
            str = str.substring(0, pos2);
        }
        str = fixTextureName(str, basePath);
        return str;
    }
    
    private static Map parseTextures(final Properties props, final String basePath) {
        final String prefix = "texture.";
        final Map mapProps = getMatchingProperties(props, prefix);
        if (mapProps.size() <= 0) {
            return null;
        }
        final Set<String> keySet = mapProps.keySet();
        final Map mapTex = new LinkedHashMap();
        for (final String key : keySet) {
            String val = (String) mapProps.get(key);
            val = fixTextureName(val, basePath);
            mapTex.put(key, val);
        }
        return mapTex;
    }
    
    private static String fixTextureName(String iconName, final String basePath) {
        iconName = TextureUtils.fixResourcePath(iconName, basePath);
        if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("mcpatcher/")) {
            iconName = basePath + "/" + iconName;
        }
        if (iconName.endsWith(".png")) {
            iconName = iconName.substring(0, iconName.length() - 4);
        }
        if (iconName.startsWith("/")) {
            iconName = iconName.substring(1);
        }
        return iconName;
    }
    
    private static String parseModel(String modelStr, final String path, final String basePath, final int type, final Map<String, String> mapModelNames) {
        if (modelStr != null) {
            final String json = ".json";
            if (modelStr.endsWith(json)) {
                modelStr = modelStr.substring(0, modelStr.length() - json.length());
            }
            modelStr = fixModelName(modelStr, basePath);
            return modelStr;
        }
        if (type == 3) {
            return null;
        }
        if (mapModelNames != null) {
            final String bowStandbyModel = mapModelNames.get("model.bow_standby");
            if (bowStandbyModel != null) {
                return bowStandbyModel;
            }
        }
        return modelStr;
    }
    
    private static Map parseModels(final Properties props, final String basePath) {
        final String prefix = "model.";
        final Map mapProps = getMatchingProperties(props, prefix);
        if (mapProps.size() <= 0) {
            return null;
        }
        final Set<String> keySet = mapProps.keySet();
        final Map mapTex = new LinkedHashMap();
        for (final String key : keySet) {
            String val = (String) mapProps.get(key);
            val = fixModelName(val, basePath);
            mapTex.put(key, val);
        }
        return mapTex;
    }
    
    private static String fixModelName(String modelName, final String basePath) {
        modelName = TextureUtils.fixResourcePath(modelName, basePath);
        final boolean isVanilla = modelName.startsWith("block/") || modelName.startsWith("item/");
        if (!modelName.startsWith(basePath) && !isVanilla && !modelName.startsWith("mcpatcher/")) {
            modelName = basePath + "/" + modelName;
        }
        final String json = ".json";
        if (modelName.endsWith(json)) {
            modelName = modelName.substring(0, modelName.length() - json.length());
        }
        if (modelName.startsWith("/")) {
            modelName = modelName.substring(1);
        }
        return modelName;
    }
    
    private int parseInt(String str, final int defVal) {
        if (str == null) {
            return defVal;
        }
        str = str.trim();
        final int val = Config.parseInt(str, Integer.MIN_VALUE);
        if (val == Integer.MIN_VALUE) {
            Config.warn("Invalid integer: " + str);
            return defVal;
        }
        return val;
    }
    
    private float parseFloat(String str, final float defVal) {
        if (str == null) {
            return defVal;
        }
        str = str.trim();
        final float val = Config.parseFloat(str, Float.MIN_VALUE);
        if (val == Float.MIN_VALUE) {
            Config.warn("Invalid float: " + str);
            return defVal;
        }
        return val;
    }
    
    private RangeListInt parseRangeListInt(final String str) {
        return this.parseRangeListInt(str, null);
    }
    
    private RangeListInt parseRangeListInt(final String str, final IParserInt parser) {
        if (str == null) {
            return null;
        }
        final String[] tokens = Config.tokenize(str, " ");
        final RangeListInt rangeList = new RangeListInt();
        for (int i = 0; i < tokens.length; ++i) {
            final String token = tokens[i];
            if (parser != null) {
                final int val = parser.parse(token, Integer.MIN_VALUE);
                if (val != Integer.MIN_VALUE) {
                    rangeList.addRange(new RangeInt(val, val));
                    continue;
                }
            }
            final RangeInt range = this.parseRangeInt(token);
            if (range == null) {
                Config.warn("Invalid range list: " + str);
                return null;
            }
            rangeList.addRange(range);
        }
        return rangeList;
    }
    
    private RangeInt parseRangeInt(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        final int countMinus = str.length() - str.replace("-", "").length();
        if (countMinus > 1) {
            Config.warn("Invalid range: " + str);
            return null;
        }
        final String[] tokens = Config.tokenize(str, "- ");
        final int[] vals = new int[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            final String token = tokens[i];
            final int val = Config.parseInt(token, -1);
            if (val < 0) {
                Config.warn("Invalid range: " + str);
                return null;
            }
            vals[i] = val;
        }
        if (vals.length == 1) {
            final int val2 = vals[0];
            if (str.startsWith("-")) {
                return new RangeInt(0, val2);
            }
            if (str.endsWith("-")) {
                return new RangeInt(val2, 65535);
            }
            return new RangeInt(val2, val2);
        }
        else {
            if (vals.length == 2) {
                final int min = Math.min(vals[0], vals[1]);
                final int max = Math.max(vals[0], vals[1]);
                return new RangeInt(min, max);
            }
            Config.warn("Invalid range: " + str);
            return null;
        }
    }
    
    private NbtTagValue[] parseNbtTagValues(final Properties props) {
        final String PREFIX_NBT = "nbt.";
        final Map mapNbt = getMatchingProperties(props, PREFIX_NBT);
        if (mapNbt.size() <= 0) {
            return null;
        }
        final List listNbts = new ArrayList();
        final Set<String> keySet = mapNbt.keySet();
        for (final String key : keySet) {
            final String val = (String) mapNbt.get(key);
            final String id = key.substring(PREFIX_NBT.length());
            final NbtTagValue nbt = new NbtTagValue(id, val);
            listNbts.add(nbt);
        }
        final NbtTagValue[] nbts = (NbtTagValue[]) listNbts.toArray(new NbtTagValue[listNbts.size()]);
        return nbts;
    }
    
    private static Map getMatchingProperties(final Properties props, final String keyPrefix) {
        final Map map = new LinkedHashMap();
        final Set keySet = props.keySet();
        for (final Object o : keySet) {
        	String key = (String) o;
            final String val = props.getProperty(key);
            if (key.startsWith(keyPrefix)) {
                map.put(key, val);
            }
        }
        return map;
    }
    
    private int parseHand(String str) {
        if (str == null) {
            return 0;
        }
        str = str.toLowerCase();
        if (str.equals("any")) {
            return 0;
        }
        if (str.equals("main")) {
            return 1;
        }
        if (str.equals("off")) {
            return 2;
        }
        Config.warn("Invalid hand: " + str);
        return 0;
    }
    
    public boolean isValid(final String path) {
        if (this.name == null || this.name.length() <= 0) {
            Config.warn("No name found: " + path);
            return false;
        }
        if (this.basePath == null) {
            Config.warn("No base path found: " + path);
            return false;
        }
        if (this.type == 0) {
            Config.warn("No type defined: " + path);
            return false;
        }
        if (this.type == 1 || this.type == 3) {
            if (this.items == null) {
                this.items = this.detectItems();
            }
            if (this.items == null) {
                Config.warn("No items defined: " + path);
                return false;
            }
        }
        if (this.texture == null && this.mapTextures == null && this.model == null && this.mapModels == null) {
            Config.warn("No texture or model specified: " + path);
            return false;
        }
        if (this.type == 2 && this.enchantmentIds == null) {
            Config.warn("No enchantmentIDs specified: " + path);
            return false;
        }
        return true;
    }
    
    private int[] detectItems() {
        final Item item = Item.getByNameOrId(this.name);
        if (item == null) {
            return null;
        }
        final int id = Item.getIdFromItem(item);
        if (id <= 0) {
            return null;
        }
        return new int[] { id };
    }
    
    public void updateIcons(final TextureMap textureMap) {
        if (this.texture != null) {
            this.textureLocation = this.getTextureLocation(this.texture);
            if (this.type == 1) {
                final ResourceLocation spriteLocation = this.getSpriteLocation(this.textureLocation);
                this.sprite = textureMap.registerSprite(spriteLocation);
            }
        }
        if (this.mapTextures != null) {
            this.mapTextureLocations = new HashMap();
            this.mapSprites = new HashMap();
            final Set<String> keySet = this.mapTextures.keySet();
            for (final String key : keySet) {
                final String val = this.mapTextures.get(key);
                final ResourceLocation locTex = this.getTextureLocation(val);
                this.mapTextureLocations.put(key, locTex);
                if (this.type == 1) {
                    final ResourceLocation locSprite = this.getSpriteLocation(locTex);
                    final TextureAtlasSprite icon = textureMap.registerSprite(locSprite);
                    this.mapSprites.put(key, icon);
                }
            }
        }
    }
    
    private ResourceLocation getTextureLocation(final String texName) {
        if (texName == null) {
            return null;
        }
        final ResourceLocation resLoc = new ResourceLocation(texName);
        final String domain = resLoc.getResourceDomain();
        String path = resLoc.getResourcePath();
        if (!path.contains("/")) {
            path = "textures/items/" + path;
        }
        final String filePath = path + ".png";
        final ResourceLocation locFile = new ResourceLocation(domain, filePath);
        final boolean exists = Config.hasResource(locFile);
        if (!exists) {
            Config.warn("File not found: " + filePath);
        }
        return locFile;
    }
    
    private ResourceLocation getSpriteLocation(final ResourceLocation resLoc) {
        String pathTex = resLoc.getResourcePath();
        pathTex = StrUtils.removePrefix(pathTex, "textures/");
        pathTex = StrUtils.removeSuffix(pathTex, ".png");
        final ResourceLocation locTex = new ResourceLocation(resLoc.getResourceDomain(), pathTex);
        return locTex;
    }
    
    public void updateModelTexture(final TextureMap textureMap, final ItemModelGenerator itemModelGenerator) {
        if (this.texture == null && this.mapTextures == null) {
            return;
        }
        final String[] textures = this.getModelTextures();
        final boolean useTint = this.isUseTint();
        this.bakedModelTexture = makeBakedModel(textureMap, itemModelGenerator, textures, useTint);
        if (this.type == 1 && this.mapTextures != null) {
            final Set<String> keySet = this.mapTextures.keySet();
            for (final String key : keySet) {
                final String tex = this.mapTextures.get(key);
                final String path = StrUtils.removePrefix(key, "texture.");
                if (path.startsWith("bow") || path.startsWith("fishing_rod") || path.startsWith("shield")) {
                    final String[] texNames = { tex };
                    final IBakedModel modelTex = makeBakedModel(textureMap, itemModelGenerator, texNames, useTint);
                    if (this.mapBakedModelsTexture == null) {
                        this.mapBakedModelsTexture = new HashMap<String, IBakedModel>();
                    }
                    final String location = path;
                    this.mapBakedModelsTexture.put(location, modelTex);
                }
            }
        }
    }
    
    private boolean isUseTint() {
        return true;
    }
    
    private static IBakedModel makeBakedModel(final TextureMap textureMap, final ItemModelGenerator itemModelGenerator, final String[] textures, final boolean useTint) {
        final String[] spriteNames = new String[textures.length];
        for (int i = 0; i < spriteNames.length; ++i) {
            final String texture = textures[i];
            spriteNames[i] = StrUtils.removePrefix(texture, "textures/");
        }
        final ModelBlock modelBlockBase = makeModelBlock(spriteNames);
        final ModelBlock modelBlock = itemModelGenerator.makeItemModel(textureMap, modelBlockBase);
        final IBakedModel model = bakeModel(textureMap, modelBlock, useTint);
        return model;
    }
    
    private String[] getModelTextures() {
        if (this.type == 1 && this.items.length == 1) {
            final Item item = Item.getItemById(this.items[0]);
            if (item == Items.potionitem && this.damage != null && this.damage.getCountRanges() > 0) {
                final RangeInt range = this.damage.getRange(0);
                final int valDamage = range.getMin();
                final boolean splash = (valDamage & 0x4000) != 0x0;
                final String texOverlay = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
                String texMain = null;
                if (splash) {
                    texMain = this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash");
                }
                else {
                    texMain = this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
                }
                return new String[] { texOverlay, texMain };
            }
            if (item instanceof ItemArmor) {
                final ItemArmor itemArmor = (ItemArmor)item;
                if (itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                    final String material = "leather";
                    String type = "helmet";
                    if (itemArmor.armorType == 0) {
                        type = "helmet";
                    }
                    if (itemArmor.armorType == 1) {
                        type = "chestplate";
                    }
                    if (itemArmor.armorType == 2) {
                        type = "leggings";
                    }
                    if (itemArmor.armorType == 3) {
                        type = "boots";
                    }
                    final String key = material + "_" + type;
                    final String texMain = this.getMapTexture(this.mapTextures, "texture." + key, "items/" + key);
                    final String texOverlay2 = this.getMapTexture(this.mapTextures, "texture." + key + "_overlay", "items/" + key + "_overlay");
                    return new String[] { texMain, texOverlay2 };
                }
            }
        }
        return new String[] { this.texture };
    }
    
    private String getMapTexture(final Map<String, String> map, final String key, final String def) {
        if (map == null) {
            return def;
        }
        final String str = map.get(key);
        if (str == null) {
            return def;
        }
        return str;
    }
    
    private static ModelBlock makeModelBlock(final String[] modelTextures) {
        final StringBuffer sb = new StringBuffer();
        sb.append("{\"parent\": \"builtin/generated\",\"textures\": {");
        for (int i = 0; i < modelTextures.length; ++i) {
            final String modelTexture = modelTextures[i];
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("\"layer" + i + "\": \"" + modelTexture + "\"");
        }
        sb.append("}}");
        final String modelStr = sb.toString();
        final ModelBlock model = ModelBlock.deserialize(modelStr);
        return model;
    }
    
    private static IBakedModel bakeModel(final TextureMap textureMap, final ModelBlock modelBlockIn, final boolean useTint) {
        final ModelRotation modelRotationIn = ModelRotation.X0_Y0;
        final boolean uvLocked = false;
        final String spriteParticleName = modelBlockIn.resolveTextureName("particle");
        final TextureAtlasSprite var4 = textureMap.getAtlasSprite(new ResourceLocation(spriteParticleName).toString());
        final SimpleBakedModel.Builder var5 = new SimpleBakedModel.Builder(modelBlockIn).setTexture(var4);
        for (final BlockPart var7 : modelBlockIn.getElements()) {
            for (final EnumFacing var9 : var7.mapFaces.keySet()) {
                BlockPartFace var10 = var7.mapFaces.get(var9);
                if (!useTint) {
                    var10 = new BlockPartFace(var10.cullFace, -1, var10.texture, var10.blockFaceUV);
                }
                final String spriteName = modelBlockIn.resolveTextureName(var10.texture);
                final TextureAtlasSprite var11 = textureMap.getAtlasSprite(new ResourceLocation(spriteName).toString());
                final BakedQuad quad = makeBakedQuad(var7, var10, var11, var9, modelRotationIn, uvLocked);
                if (var10.cullFace == null) {
                    var5.addGeneralQuad(quad);
                }
                else {
                    var5.addFaceQuad(modelRotationIn.rotateFace(var10.cullFace), quad);
                }
            }
        }
        return var5.makeBakedModel();
    }
    
    private static BakedQuad makeBakedQuad(final BlockPart blockPart, final BlockPartFace blockPartFace, final TextureAtlasSprite textureAtlasSprite, final EnumFacing enumFacing, final ModelRotation modelRotation, final boolean uvLocked) {
        final FaceBakery faceBakery = new FaceBakery();
        return faceBakery.makeBakedQuad(blockPart.positionFrom, blockPart.positionTo, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, blockPart.partRotation, uvLocked, blockPart.shade);
    }
    
    @Override
    public String toString() {
        return "" + this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
    }
    
    public float getTextureWidth(final TextureManager textureManager) {
        if (this.textureWidth <= 0) {
            if (this.textureLocation != null) {
                final ITextureObject tex = textureManager.getTexture(this.textureLocation);
                final int texId = tex.getGlTextureId();
                final int prevTexId = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(texId);
                this.textureWidth = GL11.glGetTexLevelParameteri(3553, 0, 4096);
                GlStateManager.bindTexture(prevTexId);
            }
            if (this.textureWidth <= 0) {
                this.textureWidth = 16;
            }
        }
        return (float)this.textureWidth;
    }
    
    public float getTextureHeight(final TextureManager textureManager) {
        if (this.textureHeight <= 0) {
            if (this.textureLocation != null) {
                final ITextureObject tex = textureManager.getTexture(this.textureLocation);
                final int texId = tex.getGlTextureId();
                final int prevTexId = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(texId);
                this.textureHeight = GL11.glGetTexLevelParameteri(3553, 0, 4097);
                GlStateManager.bindTexture(prevTexId);
            }
            if (this.textureHeight <= 0) {
                this.textureHeight = 16;
            }
        }
        return (float)this.textureHeight;
    }
    
    public IBakedModel getBakedModel(final ResourceLocation modelLocation, final boolean fullModel) {
        IBakedModel bakedModel;
        Map<String, IBakedModel> mapBakedModels;
        if (fullModel) {
            bakedModel = this.bakedModelFull;
            mapBakedModels = this.mapBakedModelsFull;
        }
        else {
            bakedModel = this.bakedModelTexture;
            mapBakedModels = this.mapBakedModelsTexture;
        }
        if (modelLocation != null && mapBakedModels != null) {
            final String modelPath = modelLocation.getResourcePath();
            final IBakedModel customModel = mapBakedModels.get(modelPath);
            if (customModel != null) {
                return customModel;
            }
        }
        return bakedModel;
    }
    
    public void loadModels(final ModelBakery modelBakery) {
        if (this.model != null) {
            loadItemModel(modelBakery, this.model);
        }
        if (this.type == 1 && this.mapModels != null) {
            final Set<String> keySet = this.mapModels.keySet();
            for (final String key : keySet) {
                final String mod = this.mapModels.get(key);
                final String path = StrUtils.removePrefix(key, "model.");
                if (path.startsWith("bow") || path.startsWith("fishing_rod") || path.startsWith("shield")) {
                    loadItemModel(modelBakery, mod);
                }
            }
        }
    }
    
    public void updateModelsFull() {
        final ModelManager modelManager = Config.getModelManager();
        final IBakedModel missingModel = modelManager.getMissingModel();
        if (this.model != null) {
            final ResourceLocation locItem = getModelLocation(this.model);
            final ModelResourceLocation mrl = new ModelResourceLocation(locItem, "inventory");
            this.bakedModelFull = modelManager.getModel(mrl);
            if (this.bakedModelFull == missingModel) {
                Config.warn("Custom Items: Model not found " + mrl.getResourcePath());
                this.bakedModelFull = null;
            }
        }
        if (this.type == 1 && this.mapModels != null) {
            final Set<String> keySet = this.mapModels.keySet();
            for (final String key : keySet) {
                final String mod = this.mapModels.get(key);
                final String path = StrUtils.removePrefix(key, "model.");
                if (path.startsWith("bow") || path.startsWith("fishing_rod") || path.startsWith("shield")) {
                    final ResourceLocation locItem2 = getModelLocation(mod);
                    final ModelResourceLocation mrl2 = new ModelResourceLocation(locItem2, "inventory");
                    final IBakedModel bm = modelManager.getModel(mrl2);
                    if (bm == missingModel) {
                        Config.warn("Custom Items: Model not found " + mrl2.getResourcePath());
                    }
                    else {
                        if (this.mapBakedModelsFull == null) {
                            this.mapBakedModelsFull = new HashMap<String, IBakedModel>();
                        }
                        final String location = path;
                        this.mapBakedModelsFull.put(location, bm);
                    }
                }
            }
        }
    }
    
    private static void loadItemModel(final ModelBakery modelBakery, final String model) {
        final ResourceLocation locItem = getModelLocation(model);
        final ModelResourceLocation mrl = new ModelResourceLocation(locItem, "inventory");

        modelBakery.loadItemModel(locItem.toString(), mrl, locItem);
    }
    
    private static void checkNull(final Object obj, final String msg) throws NullPointerException {
        if (obj == null) {
            throw new NullPointerException(msg);
        }
    }
    
    private static ResourceLocation getModelLocation(final String modelName) {
    	
        return new ResourceLocation(modelName);
    }
}

package net.optifine.player;

import net.minecraft.util.MathHelper;
import net.optifine.entity.model.CustomEntityModelParser;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.JsonArray;
import java.awt.Dimension;
import com.google.gson.JsonParseException;
import net.minecraft.src.Config;
import net.optifine.util.Json;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlayerItemParser
{
    private static JsonParser jsonParser;
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_TEXTURE_SIZE = "textureSize";
    public static final String ITEM_USE_PLAYER_TEXTURE = "usePlayerTexture";
    public static final String ITEM_MODELS = "models";
    public static final String MODEL_ID = "id";
    public static final String MODEL_BASE_ID = "baseId";
    public static final String MODEL_TYPE = "type";
    public static final String MODEL_TEXTURE = "texture";
    public static final String MODEL_TEXTURE_SIZE = "textureSize";
    public static final String MODEL_ATTACH_TO = "attachTo";
    public static final String MODEL_INVERT_AXIS = "invertAxis";
    public static final String MODEL_MIRROR_TEXTURE = "mirrorTexture";
    public static final String MODEL_TRANSLATE = "translate";
    public static final String MODEL_ROTATE = "rotate";
    public static final String MODEL_SCALE = "scale";
    public static final String MODEL_BOXES = "boxes";
    public static final String MODEL_SPRITES = "sprites";
    public static final String MODEL_SUBMODEL = "submodel";
    public static final String MODEL_SUBMODELS = "submodels";
    public static final String BOX_TEXTURE_OFFSET = "textureOffset";
    public static final String BOX_COORDINATES = "coordinates";
    public static final String BOX_SIZE_ADD = "sizeAdd";
    public static final String BOX_UV_DOWN = "uvDown";
    public static final String BOX_UV_UP = "uvUp";
    public static final String BOX_UV_NORTH = "uvNorth";
    public static final String BOX_UV_SOUTH = "uvSouth";
    public static final String BOX_UV_WEST = "uvWest";
    public static final String BOX_UV_EAST = "uvEast";
    public static final String BOX_UV_FRONT = "uvFront";
    public static final String BOX_UV_BACK = "uvBack";
    public static final String BOX_UV_LEFT = "uvLeft";
    public static final String BOX_UV_RIGHT = "uvRight";
    public static final String ITEM_TYPE_MODEL = "PlayerItem";
    public static final String MODEL_TYPE_BOX = "ModelBox";
    
    private PlayerItemParser() {
    }
    
    public static PlayerItemModel parseItemModel(final JsonObject obj) {
        final String type = Json.getString(obj, "type");
        if (!Config.equals(type, "PlayerItem")) {
            throw new JsonParseException("Unknown model type: " + type);
        }
        final int[] textureSize = Json.parseIntArray(obj.get("textureSize"), 2);
        checkNull(textureSize, "Missing texture size");
        final Dimension textureDim = new Dimension(textureSize[0], textureSize[1]);
        final boolean usePlayerTexture = Json.getBoolean(obj, "usePlayerTexture", false);
        final JsonArray models = (JsonArray)obj.get("models");
        checkNull(models, "Missing elements");
        final Map mapModelJsons = new HashMap();
        final List listModels = new ArrayList();
        final List listAttachTos = new ArrayList();
        for (int i = 0; i < models.size(); ++i) {
            final JsonObject elem = (JsonObject)models.get(i);
            final String baseId = Json.getString(elem, "baseId");
            if (baseId != null) {
                final JsonObject baseObj = (JsonObject) mapModelJsons.get(baseId);
                if (baseObj == null) {
                    Config.warn("BaseID not found: " + baseId);
                    continue;
                }
                final Set<Map.Entry<String, JsonElement>> setEntries = (Set<Map.Entry<String, JsonElement>>)baseObj.entrySet();
                for (final Map.Entry<String, JsonElement> entry : setEntries) {
                    if (!elem.has((String)entry.getKey())) {
                        elem.add((String)entry.getKey(), (JsonElement)entry.getValue());
                    }
                }
            }
            final String id = Json.getString(elem, "id");
            if (id != null) {
                if (!mapModelJsons.containsKey(id)) {
                    mapModelJsons.put(id, elem);
                }
                else {
                    Config.warn("Duplicate model ID: " + id);
                }
            }
            final PlayerItemRenderer mr = parseItemRenderer(elem, textureDim);
            if (mr != null) {
                listModels.add(mr);
            }
        }
        final PlayerItemRenderer[] modelRenderers = (PlayerItemRenderer[]) listModels.toArray(new PlayerItemRenderer[listModels.size()]);
        return new PlayerItemModel(textureDim, usePlayerTexture, modelRenderers);
    }
    
    private static void checkNull(final Object obj, final String msg) {
        if (obj == null) {
            throw new JsonParseException(msg);
        }
    }
    
    private static ResourceLocation makeResourceLocation(final String texture) {
        final int pos = texture.indexOf(58);
        if (pos < 0) {
            return new ResourceLocation(texture);
        }
        final String domain = texture.substring(0, pos);
        final String path = texture.substring(pos + 1);
        return new ResourceLocation(domain, path);
    }
    
    private static int parseAttachModel(final String attachModelStr) {
        final String str = attachModelStr;
        if (str == null) {
            return 0;
        }
        if (str.equals("body")) {
            return 0;
        }
        if (str.equals("head")) {
            return 1;
        }
        if (str.equals("leftArm")) {
            return 2;
        }
        if (str.equals("rightArm")) {
            return 3;
        }
        if (str.equals("leftLeg")) {
            return 4;
        }
        if (str.equals("rightLeg")) {
            return 5;
        }
        if (str.equals("cape")) {
            return 6;
        }
        Config.warn("Unknown attachModel: " + str);
        return 0;
    }
    
    public static PlayerItemRenderer parseItemRenderer(final JsonObject elem, final Dimension textureDim) {
        final String type = Json.getString(elem, "type");
        if (!Config.equals(type, "ModelBox")) {
            Config.warn("Unknown model type: " + type);
            return null;
        }
        final String attachToStr = Json.getString(elem, "attachTo");
        final int attachTo = parseAttachModel(attachToStr);
        final ModelBase modelBase = new ModelPlayerItem();
        modelBase.textureWidth = textureDim.width;
        modelBase.textureHeight = textureDim.height;
        final ModelRenderer mr = parseModelRenderer(elem, modelBase, null, null);
        final PlayerItemRenderer pir = new PlayerItemRenderer(attachTo, mr);
        return pir;
    }
    
    public static ModelRenderer parseModelRenderer(final JsonObject elem, final ModelBase modelBase, final int[] parentTextureSize, final String basePath) {
        final ModelRenderer mr = new ModelRenderer(modelBase);
        final String id = Json.getString(elem, "id");
        mr.setId(id);
        final float scale = Json.getFloat(elem, "scale", 1.0f);
        mr.scaleX = scale;
        mr.scaleY = scale;
        mr.scaleZ = scale;
        final String texture = Json.getString(elem, "texture");
        if (texture != null) {
            mr.setTextureLocation(CustomEntityModelParser.getResourceLocation(basePath, texture, ".png"));
        }
        int[] textureSize = Json.parseIntArray(elem.get("textureSize"), 2);
        if (textureSize == null) {
            textureSize = parentTextureSize;
        }
        if (textureSize != null) {
            mr.setTextureSize(textureSize[0], textureSize[1]);
        }
        final String invertAxis = Json.getString(elem, "invertAxis", "").toLowerCase();
        final boolean invertX = invertAxis.contains("x");
        final boolean invertY = invertAxis.contains("y");
        final boolean invertZ = invertAxis.contains("z");
        final float[] translate = Json.parseFloatArray(elem.get("translate"), 3, new float[3]);
        if (invertX) {
            translate[0] = -translate[0];
        }
        if (invertY) {
            translate[1] = -translate[1];
        }
        if (invertZ) {
            translate[2] = -translate[2];
        }
        final float[] rotateAngles = Json.parseFloatArray(elem.get("rotate"), 3, new float[3]);
        for (int i = 0; i < rotateAngles.length; ++i) {
            rotateAngles[i] = rotateAngles[i] / 180.0f * MathHelper.PI;
        }
        if (invertX) {
            rotateAngles[0] = -rotateAngles[0];
        }
        if (invertY) {
            rotateAngles[1] = -rotateAngles[1];
        }
        if (invertZ) {
            rotateAngles[2] = -rotateAngles[2];
        }
        mr.setRotationPoint(translate[0], translate[1], translate[2]);
        mr.rotateAngleX = rotateAngles[0];
        mr.rotateAngleY = rotateAngles[1];
        mr.rotateAngleZ = rotateAngles[2];
        final String mirrorTexture = Json.getString(elem, "mirrorTexture", "").toLowerCase();
        final boolean invertU = mirrorTexture.contains("u");
        final boolean invertV = mirrorTexture.contains("v");
        if (invertU) {
            mr.mirror = true;
        }
        if (invertV) {
            mr.mirrorV = true;
        }
        final JsonArray boxes = elem.getAsJsonArray("boxes");
        if (boxes != null) {
            for (int j = 0; j < boxes.size(); ++j) {
                final JsonObject box = boxes.get(j).getAsJsonObject();
                final int[] textureOffset = Json.parseIntArray(box.get("textureOffset"), 2);
                final int[][] faceUvs = parseFaceUvs(box);
                if (textureOffset == null && faceUvs == null) {
                    throw new JsonParseException("Texture offset not specified");
                }
                final float[] coordinates = Json.parseFloatArray(box.get("coordinates"), 6);
                if (coordinates == null) {
                    throw new JsonParseException("Coordinates not specified");
                }
                if (invertX) {
                    coordinates[0] = -coordinates[0] - coordinates[3];
                }
                if (invertY) {
                    coordinates[1] = -coordinates[1] - coordinates[4];
                }
                if (invertZ) {
                    coordinates[2] = -coordinates[2] - coordinates[5];
                }
                final float sizeAdd = Json.getFloat(box, "sizeAdd", 0.0f);
                if (faceUvs != null) {
                    mr.addBox(faceUvs, coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4], coordinates[5], sizeAdd);
                }
                else {
                    mr.setTextureOffset(textureOffset[0], textureOffset[1]);
                    mr.addBox(coordinates[0], coordinates[1], coordinates[2], (int)coordinates[3], (int)coordinates[4], (int)coordinates[5], sizeAdd);
                }
            }
        }
        final JsonArray sprites = elem.getAsJsonArray("sprites");
        if (sprites != null) {
            for (int k = 0; k < sprites.size(); ++k) {
                final JsonObject sprite = sprites.get(k).getAsJsonObject();
                final int[] textureOffset2 = Json.parseIntArray(sprite.get("textureOffset"), 2);
                if (textureOffset2 == null) {
                    throw new JsonParseException("Texture offset not specified");
                }
                final float[] coordinates = Json.parseFloatArray(sprite.get("coordinates"), 6);
                if (coordinates == null) {
                    throw new JsonParseException("Coordinates not specified");
                }
                if (invertX) {
                    coordinates[0] = -coordinates[0] - coordinates[3];
                }
                if (invertY) {
                    coordinates[1] = -coordinates[1] - coordinates[4];
                }
                if (invertZ) {
                    coordinates[2] = -coordinates[2] - coordinates[5];
                }
                final float sizeAdd = Json.getFloat(sprite, "sizeAdd", 0.0f);
                mr.setTextureOffset(textureOffset2[0], textureOffset2[1]);
                mr.addSprite(coordinates[0], coordinates[1], coordinates[2], (int)coordinates[3], (int)coordinates[4], (int)coordinates[5], sizeAdd);
            }
        }
        final JsonObject submodel = (JsonObject)elem.get("submodel");
        if (submodel != null) {
            final ModelRenderer subMr = parseModelRenderer(submodel, modelBase, textureSize, basePath);
            mr.addChild(subMr);
        }
        final JsonArray submodels = (JsonArray)elem.get("submodels");
        if (submodels != null) {
            for (int l = 0; l < submodels.size(); ++l) {
                final JsonObject sm = (JsonObject)submodels.get(l);
                final ModelRenderer subMr2 = parseModelRenderer(sm, modelBase, textureSize, basePath);
                if (subMr2.getId() != null) {
                    final ModelRenderer subMrId = mr.getChild(subMr2.getId());
                    if (subMrId != null) {
                        Config.warn("Duplicate model ID: " + subMr2.getId());
                    }
                }
                mr.addChild(subMr2);
            }
        }
        return mr;
    }
    
    private static int[][] parseFaceUvs(final JsonObject box) {
        final int[][] uvs = { Json.parseIntArray(box.get("uvDown"), 4), Json.parseIntArray(box.get("uvUp"), 4), Json.parseIntArray(box.get("uvNorth"), 4), Json.parseIntArray(box.get("uvSouth"), 4), Json.parseIntArray(box.get("uvWest"), 4), Json.parseIntArray(box.get("uvEast"), 4) };
        if (uvs[2] == null) {
            uvs[2] = Json.parseIntArray(box.get("uvFront"), 4);
        }
        if (uvs[3] == null) {
            uvs[3] = Json.parseIntArray(box.get("uvBack"), 4);
        }
        if (uvs[4] == null) {
            uvs[4] = Json.parseIntArray(box.get("uvLeft"), 4);
        }
        if (uvs[5] == null) {
            uvs[5] = Json.parseIntArray(box.get("uvRight"), 4);
        }
        boolean defined = false;
        for (int i = 0; i < uvs.length; ++i) {
            if (uvs[i] != null) {
                defined = true;
            }
        }
        if (!defined) {
            return null;
        }
        return uvs;
    }
    
    static {
        PlayerItemParser.jsonParser = new JsonParser();
    }
}

package net.optifine.entity.model;

import java.io.InputStream;
import com.google.gson.JsonParser;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.optifine.player.PlayerItemParser;
import net.optifine.entity.model.anim.ModelUpdater;
import net.optifine.entity.model.anim.ModelVariableUpdater;
import java.util.Iterator;
import java.util.Set;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.JsonArray;
import net.optifine.util.Json;
import net.optifine.config.ConnectedParser;
import com.google.gson.JsonObject;

public class CustomEntityModelParser
{
    public static final String ENTITY = "entity";
    public static final String TEXTURE = "texture";
    public static final String SHADOW_SIZE = "shadowSize";
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_TEXTURE_SIZE = "textureSize";
    public static final String ITEM_USE_PLAYER_TEXTURE = "usePlayerTexture";
    public static final String ITEM_MODELS = "models";
    public static final String ITEM_ANIMATIONS = "animations";
    public static final String MODEL_ID = "id";
    public static final String MODEL_BASE_ID = "baseId";
    public static final String MODEL_MODEL = "model";
    public static final String MODEL_TYPE = "type";
    public static final String MODEL_PART = "part";
    public static final String MODEL_ATTACH = "attach";
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
    public static final String ENTITY_MODEL = "EntityModel";
    public static final String ENTITY_MODEL_PART = "EntityModelPart";
    
    public static CustomEntityRenderer parseEntityRender(final JsonObject obj, final String path) {
        final ConnectedParser cp = new ConnectedParser("CustomEntityModels");
        final String name = cp.parseName(path);
        final String basePath = cp.parseBasePath(path);
        final String texture = Json.getString(obj, "texture");
        final int[] textureSize = Json.parseIntArray(obj.get("textureSize"), 2);
        final float shadowSize = Json.getFloat(obj, "shadowSize", -1.0f);
        final JsonArray models = (JsonArray)obj.get("models");
        checkNull(models, "Missing models");
        final Map mapModelJsons = new HashMap();
        final List listModels = new ArrayList();
        for (int i = 0; i < models.size(); ++i) {
            final JsonObject elem = (JsonObject)models.get(i);
            processBaseId(elem, mapModelJsons);
            processExternalModel(elem, mapModelJsons, basePath);
            processId(elem, mapModelJsons);
            final CustomModelRenderer mr = parseCustomModelRenderer(elem, textureSize, basePath);
            if (mr != null) {
                listModels.add(mr);
            }
        }
        final CustomModelRenderer[] modelRenderers = (CustomModelRenderer[]) listModels.toArray(new CustomModelRenderer[listModels.size()]);
        ResourceLocation textureLocation = null;
        if (texture != null) {
            textureLocation = getResourceLocation(basePath, texture, ".png");
        }
        final CustomEntityRenderer cer = new CustomEntityRenderer(name, basePath, textureLocation, modelRenderers, shadowSize);
        return cer;
    }
    
    private static void processBaseId(final JsonObject elem, final Map mapModelJsons) {
        final String baseId = Json.getString(elem, "baseId");
        if (baseId == null) {
            return;
        }
        final JsonObject baseObj = (JsonObject) mapModelJsons.get(baseId);
        if (baseObj == null) {
            Config.warn("BaseID not found: " + baseId);
            return;
        }
        copyJsonElements(baseObj, elem);
    }
    
    private static void processExternalModel(final JsonObject elem, final Map mapModelJsons, final String basePath) {
        final String modelPath = Json.getString(elem, "model");
        if (modelPath == null) {
            return;
        }
        final ResourceLocation locJson = getResourceLocation(basePath, modelPath, ".jpm");
        try {
            final JsonObject modelObj = loadJson(locJson);
            if (modelObj == null) {
                Config.warn("Model not found: " + locJson);
                return;
            }
            copyJsonElements(modelObj, elem);
        }
        catch (IOException e) {
            Config.error("" + e.getClass().getName() + ": " + e.getMessage());
        }
        catch (JsonParseException e2) {
            Config.error("" + e2.getClass().getName() + ": " + e2.getMessage());
        }
        catch (Exception e3) {
            e3.printStackTrace();
        }
    }
    
    private static void copyJsonElements(final JsonObject objFrom, final JsonObject objTo) {
        final Set<Map.Entry<String, JsonElement>> setEntries = (Set<Map.Entry<String, JsonElement>>)objFrom.entrySet();
        for (final Map.Entry<String, JsonElement> entry : setEntries) {
            if (entry.getKey().equals("id")) {
                continue;
            }
            if (objTo.has((String)entry.getKey())) {
                continue;
            }
            objTo.add((String)entry.getKey(), (JsonElement)entry.getValue());
        }
    }
    
    public static ResourceLocation getResourceLocation(final String basePath, String path, final String extension) {
        if (!path.endsWith(extension)) {
            path += extension;
        }
        if (!path.contains("/")) {
            path = basePath + "/" + path;
        }
        else if (path.startsWith("./")) {
            path = basePath + "/" + path.substring(2);
        }
        else if (path.startsWith("~/")) {
            path = "optifine/" + path.substring(2);
        }
        return new ResourceLocation(path);
    }
    
    private static void processId(final JsonObject elem, final Map mapModelJsons) {
        final String id = Json.getString(elem, "id");
        if (id == null) {
            return;
        }
        if (id.length() < 1) {
            Config.warn("Empty model ID: " + id);
            return;
        }
        if (mapModelJsons.containsKey(id)) {
            Config.warn("Duplicate model ID: " + id);
            return;
        }
        mapModelJsons.put(id, elem);
    }
    
    public static CustomModelRenderer parseCustomModelRenderer(final JsonObject elem, final int[] textureSize, final String basePath) {
        final String modelPart = Json.getString(elem, "part");
        checkNull(modelPart, "Model part not specified, missing \"replace\" or \"attachTo\".");
        final boolean attach = Json.getBoolean(elem, "attach", false);
        final ModelBase modelBase = new CustomEntityModel();
        if (textureSize != null) {
            modelBase.textureWidth = textureSize[0];
            modelBase.textureHeight = textureSize[1];
        }
        ModelUpdater mu = null;
        final JsonArray animations = (JsonArray)elem.get("animations");
        if (animations != null) {
            final List<ModelVariableUpdater> listModelVariableUpdaters = new ArrayList<ModelVariableUpdater>();
            for (int i = 0; i < animations.size(); ++i) {
                final JsonObject anim = (JsonObject)animations.get(i);
                final Set<Map.Entry<String, JsonElement>> entries = (Set<Map.Entry<String, JsonElement>>)anim.entrySet();
                for (final Map.Entry<String, JsonElement> entry : entries) {
                    final String key = entry.getKey();
                    final String val = entry.getValue().getAsString();
                    final ModelVariableUpdater mvu = new ModelVariableUpdater(key, val);
                    listModelVariableUpdaters.add(mvu);
                }
            }
            if (listModelVariableUpdaters.size() > 0) {
                final ModelVariableUpdater[] mvus = listModelVariableUpdaters.toArray(new ModelVariableUpdater[listModelVariableUpdaters.size()]);
                mu = new ModelUpdater(mvus);
            }
        }
        final ModelRenderer mr = PlayerItemParser.parseModelRenderer(elem, modelBase, textureSize, basePath);
        final CustomModelRenderer cmr = new CustomModelRenderer(modelPart, attach, mr, mu);
        return cmr;
    }
    
    private static void checkNull(final Object obj, final String msg) {
        if (obj == null) {
            throw new JsonParseException(msg);
        }
    }
    
    public static JsonObject loadJson(final ResourceLocation location) throws IOException, JsonParseException {
        final InputStream in = Config.getResourceStream(location);
        if (in == null) {
            return null;
        }
        final String jsonStr = Config.readInputStream(in, "ASCII");
        in.close();
        final JsonParser jp = new JsonParser();
        final JsonObject jo = (JsonObject)jp.parse(jsonStr);
        return jo;
    }
}

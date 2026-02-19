package insane96mcp.mobspropertiesrandomness.util;

import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SerializerUtils {
    /**
     * Set from AddReloadListenerEvent before any reload listener runs.
     * Provides access to dynamic registries (e.g. minecraft:enchantment in 1.21+).
     */
    @Nullable
    public static RegistryAccess REGISTRY_ACCESS = null;

    /**
     * Gets a registry by key, checking BuiltInRegistries first, then falling back to
     * the RegistryAccess captured from AddReloadListenerEvent (needed for data-driven
     * registries like enchantments in 1.21+).
     */
    @SuppressWarnings("unchecked")
    @Nullable
    private static <T> Registry<T> getRegistry(ResourceKey<Registry<T>> registryKey) {
        Registry<T> reg = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());
        if (reg != null)
            return reg;
        if (REGISTRY_ACCESS != null)
            return REGISTRY_ACCESS.registry(registryKey).orElse(null);
        return null;
    }

    public static <T> List<T> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context, Class<T> clazz) throws JsonParseException {
        return deserializeList(jObject, memberName, context, clazz, true);
    }

    public static <T> List<T> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context, Class<T> clazz, boolean required) throws JsonParseException {
        if (!jObject.has(memberName)) {
            if (required)
                throw new JsonParseException("Missing %s array".formatted(memberName));
            else
                return new ArrayList<>();
        }
        if (jObject.get(memberName).isJsonObject())
            return Collections.singletonList(context.deserialize(jObject.get(memberName).getAsJsonObject(), clazz));
        else if (jObject.get(memberName).isJsonPrimitive())
            return Collections.singletonList(context.deserialize(jObject.get(memberName), clazz));
        else if (jObject.get(memberName).isJsonArray()) {
            JsonArray jsonArray = jObject.getAsJsonArray(memberName);
            if (jsonArray == null)
                return new ArrayList<>();
            List<T> list = new ArrayList<>();
            for (JsonElement el : jsonArray) {
                T item = context.deserialize(el, clazz);
                list.add(item);
            }
            return list;
        }
        else
            throw new JsonParseException("Expected %s to be a JsonObject or JsonArray or JsonPrimitive".formatted(memberName));
    }

    public static <T> List<T> deserializeList(JsonArray jArray, JsonDeserializationContext context, Class<T> clazz) throws JsonParseException {
        if (jArray == null)
            return new ArrayList<>();
        List<T> list = new ArrayList<>();
        for (JsonElement el : jArray) {
            T item = context.deserialize(el, clazz);
            list.add(item);
        }
        return list;
    }

    public static JsonArray serializeList(List<?> list, JsonSerializationContext context) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (Object item : list) {
            jsonArray.add(context.serialize(item));
        }
        return jsonArray;
    }

    public static List<ResourceLocation> deserializeLocationList(JsonObject jObject, String memberName, JsonDeserializationContext context) throws JsonParseException {
        List<String> list = deserializeList(jObject, memberName, context, String.class);
        List<ResourceLocation> locations = new ArrayList<>(list.size());
        for (String loc : list) {
            locations.add(ResourceLocation.parse(loc));
        }
        return locations;
    }

    public static void serializeLocationList(JsonObject jObject, String memberName, JsonSerializationContext context, List<ResourceLocation> list) {
        JsonArray jsonArray = new JsonArray();
        for (ResourceLocation loc : list) {
            jsonArray.add(loc.toString());
        }
        jObject.add(memberName, jsonArray);
    }

    /**
     * Deserializes a registry object as raw T (not Holder)
     */
    @Nullable
    public static <T> T deserializeRegistryObject(JsonElement jElement, ResourceKey<Registry<T>> registry) throws JsonParseException {
        if (!jElement.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(jElement));
        Registry<T> reg = getRegistry(registry);
        if (reg == null)
            throw new JsonParseException("Unknown registry %s".formatted(registry.location()));
        ResourceLocation objectId = ResourceLocation.parse(jElement.getAsString());
        return reg.get(objectId);
    }

    /**
     * Deserializes a registry object as raw T (not Holder)
     */
    @Nullable
    public static <T> T deserializeRegistryObject(JsonObject jObject, String memberName, ResourceKey<Registry<T>> registry) throws JsonParseException {
        if (!jObject.has(memberName))
            throw new JsonParseException("Missing \"%s\" from %s".formatted(memberName, jObject));
        return deserializeRegistryObject(jObject.get(memberName), registry);
    }

    /**
     * Deserializes a list of registry objects as raw T (not Holder)
     */
    public static <T> List<T> deserializeRegistryObjectList(JsonObject jObject, String memberName, JsonDeserializationContext context, ResourceKey<Registry<T>> registry) throws JsonParseException {
        JsonArray jsonArray = jObject.getAsJsonArray(memberName);
        if (jsonArray == null)
            return new ArrayList<>();

        List<T> objects = new ArrayList<>();
        for (JsonElement el : jsonArray) {
            T obj = deserializeRegistryObject(el, registry);
            if (obj == null) {
                MPRLogger.warn("Invalid registry object: %s. Will be ignored.", el);
                continue;
            }
            objects.add(obj);
        }
        return objects;
    }

    /**
     * Deserializes a list of registry objects as raw T (not Holder)
     */
    public static <T> List<T> deserializeRegistryObjectList(JsonObject jObject, String memberName, JsonDeserializationContext context, ResourceKey<Registry<T>> registry, boolean required) throws JsonParseException {
        if (!jObject.has(memberName)) {
            if (required)
                throw new JsonParseException("Missing %s array".formatted(memberName));
            else
                return new ArrayList<>();
        }
        return deserializeRegistryObjectList(jObject, memberName, context, registry);
    }

    /**
     * Serializes a registry object (raw T)
     */
    public static <T> JsonElement serializeRegistryObject(T object, ResourceKey<Registry<T>> registry) throws JsonParseException {
        Registry<T> reg = getRegistry(registry);
        if (reg == null)
            throw new JsonParseException("Unknown registry %s".formatted(registry.location()));
        ResourceLocation objectId = reg.getKey(object);
        if (objectId == null)
            throw new JsonParseException("Object %s not found in registry %s".formatted(object, registry.location()));
        return new JsonPrimitive(objectId.toString());
    }

    /**
     * Serializes a list of registry objects (raw T)
     */
    public static <T> JsonArray serializeRegistryObjectList(JsonObject jObject, List<T> objectsList, JsonSerializationContext context, ResourceKey<Registry<T>> registry) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (T object : objectsList) {
            jsonArray.add(serializeRegistryObject(object, registry));
        }
        return jsonArray;
    }

    /**
     * Serializes a list of registry objects (raw T)
     */
    public static <T> JsonArray serializeRegistryHolderList(JsonObject jObject, List<Holder<T>> objectsList, JsonSerializationContext context, ResourceKey<Registry<T>> registry) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (Holder<T> object : objectsList) {
            jsonArray.add(serializeRegistryObject(object, registry));
        }
        return jsonArray;
    }

    /**
     * Deserializes a Holder<T> from JsonElement
     */
    @Nullable
    public static <T> Holder<T> deserializeRegistryObjectAsHolder(JsonElement jElement, ResourceKey<Registry<T>> registry) throws JsonParseException {
        if (!jElement.isJsonPrimitive())
            throw new JsonParseException("Expected %s to be a string".formatted(jElement));

        Registry<T> reg = getRegistry(registry);
        if (reg == null)
            throw new JsonParseException("Unknown registry %s".formatted(registry.location()));

        ResourceLocation objectId = ResourceLocation.parse(jElement.getAsString());
        return reg.getHolder(objectId).orElse(null);
    }

    /**
     * Deserializes a Holder<T> from JsonObject member
     */
    @Nullable
    public static <T> Holder<T> deserializeRegistryObjectAsHolder(JsonObject jObject, String memberName, ResourceKey<Registry<T>> registry) throws JsonParseException {
        if (!jObject.has(memberName))
            throw new JsonParseException("Missing \"%s\" from %s".formatted(memberName, jObject));
        return deserializeRegistryObjectAsHolder(jObject.get(memberName), registry);
    }

    /**
     * Deserializes a list of Holder<T> from JsonObject member
     */
    public static <T> List<Holder<T>> deserializeRegistryObjectListAsHolders(JsonObject jObject, String memberName, JsonDeserializationContext context, ResourceKey<Registry<T>> registry) throws JsonParseException {
        JsonArray jsonArray = jObject.getAsJsonArray(memberName);
        if (jsonArray == null)
            return new ArrayList<>();

        List<Holder<T>> holders = new ArrayList<>();
        for (JsonElement el : jsonArray) {
            Holder<T> holder = deserializeRegistryObjectAsHolder(el, registry);
            if (holder == null) {
                MPRLogger.warn("Invalid registry object: %s. Will be ignored.", el);
                continue;
            }
            holders.add(holder);
        }
        return holders;
    }

    /**
     * Deserializes a list of Holder<T> from JsonObject member with required flag
     */
    public static <T> List<Holder<T>> deserializeRegistryObjectListAsHolders(JsonObject jObject, String memberName, JsonDeserializationContext context, ResourceKey<Registry<T>> registry, boolean required) throws JsonParseException {
        if (!jObject.has(memberName)) {
            if (required)
                throw new JsonParseException("Missing %s array".formatted(memberName));
            else
                return new ArrayList<>();
        }
        return deserializeRegistryObjectListAsHolders(jObject, memberName, context, registry);
    }

    /**
     * Serializes a Holder<T> (automatically unwraps with .value())
     */
    public static <T> JsonElement serializeRegistryObject(Holder<T> holder, ResourceKey<Registry<T>> registry) throws JsonParseException {
        return serializeRegistryObject(holder.value(), registry);
    }

    /**
     * Serializes a list of Holder<T> (automatically unwraps with .value())
     */
    public static <T> JsonArray serializeRegistryObjectListFromHolders(List<Holder<T>> holdersList, JsonSerializationContext context, ResourceKey<Registry<T>> registry) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (Holder<T> holder : holdersList) {
            jsonArray.add(serializeRegistryObject(holder.value(), registry));
        }
        return jsonArray;
    }
}
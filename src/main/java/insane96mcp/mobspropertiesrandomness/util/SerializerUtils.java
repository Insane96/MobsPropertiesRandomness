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

/**
 * GSON (de)serialization utilities for data pack JSON files.
 * <p>
 * Provides helpers for lists that may be (de)serialized from a single element, object,
 * or array, for {@link ResourceLocation}, and for registry objects/{@link Holder}s (both
 * static, via {@link BuiltInRegistries}, and dynamic, via {@link #REGISTRY_ACCESS}).
 */
public class SerializerUtils {
    /**
     * Set from {@code AddReloadListenerEvent} before any reload listener runs.
     * Provides access to dynamic registries (e.g. {@code minecraft:enchantment} in 1.21+),
     * which are not present in {@link BuiltInRegistries}.
     */
    @Nullable
    public static RegistryAccess REGISTRY_ACCESS = null;

    /**
     * Gets a registry by key, checking {@link BuiltInRegistries} first, then falling back to
     * {@link #REGISTRY_ACCESS} (needed for data-driven registries like enchantments in 1.21+).
     *
     * @param registryKey key of the registry to retrieve
     * @return the found registry, or {@code null} if it exists in neither source
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

    /**
     * Deserializes a required member as a {@link List}, accepting a single object, a single
     * primitive, or an array in the JSON.
     *
     * @param jObject    object containing the member
     * @param memberName name of the member to deserialize
     * @param context    GSON deserialization context
     * @param clazz      target element type
     * @return the deserialized list
     * @throws JsonParseException if the member is missing, or is neither an object, a primitive, nor an array
     */
    public static <T> List<T> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context, Class<T> clazz) throws JsonParseException {
        return deserializeList(jObject, memberName, context, clazz, true);
    }

    /**
     * Deserializes a member as a {@link List}, accepting a single object, a single primitive,
     * or an array in the JSON.
     *
     * @param jObject    object containing the member
     * @param memberName name of the member to deserialize
     * @param context    GSON deserialization context
     * @param clazz      target element type
     * @param required   if {@code true}, throws when the member is missing; otherwise returns an empty list
     * @return the deserialized list, or an empty list if the member is missing and not required
     * @throws JsonParseException if the member is required and missing, or is neither an object, a primitive, nor an array
     */
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

    /**
     * Deserializes a {@link JsonArray} into a {@link List}.
     *
     * @param jArray  array to deserialize, may be {@code null}
     * @param context GSON deserialization context
     * @param clazz   target element type
     * @return the deserialized list, or an empty list if {@code jArray} is {@code null}
     * @throws JsonParseException if an element cannot be deserialized as {@code clazz}
     */
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

    /**
     * Serializes a {@link List} into a {@link JsonArray}.
     *
     * @param list    list to serialize
     * @param context GSON serialization context
     * @return the resulting array
     */
    public static JsonArray serializeList(List<?> list, JsonSerializationContext context) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (Object item : list) {
            jsonArray.add(context.serialize(item));
        }
        return jsonArray;
    }

    /**
     * Deserializes a required member as a {@link List} of {@link ResourceLocation}, accepting
     * a single string or an array of strings in the JSON.
     *
     * @param jObject    object containing the member
     * @param memberName name of the member to deserialize
     * @param context    GSON deserialization context
     * @return the deserialized list of locations
     * @throws JsonParseException if the member is missing or malformed
     */
    public static List<ResourceLocation> deserializeLocationList(JsonObject jObject, String memberName, JsonDeserializationContext context) throws JsonParseException {
        List<String> list = deserializeList(jObject, memberName, context, String.class);
        List<ResourceLocation> locations = new ArrayList<>(list.size());
        for (String loc : list) {
            locations.add(ResourceLocation.parse(loc));
        }
        return locations;
    }

    /**
     * Serializes a {@link List} of {@link ResourceLocation} into an array of strings, and adds
     * it to {@code jObject} under {@code memberName}.
     *
     * @param jObject    object to add the member to
     * @param memberName name of the member to write
     * @param context    GSON serialization context
     * @param list       locations to serialize
     */
    public static void serializeLocationList(JsonObject jObject, String memberName, JsonSerializationContext context, List<ResourceLocation> list) {
        JsonArray jsonArray = new JsonArray();
        for (ResourceLocation loc : list) {
            jsonArray.add(loc.toString());
        }
        jObject.add(memberName, jsonArray);
    }

    /**
     * Deserializes a registry object as raw {@code T} (not {@link Holder}) from a JSON string
     * holding its {@link ResourceLocation}.
     *
     * @param jElement string element holding the object's id
     * @param registry key of the registry to look up the object in
     * @return the registry object, or {@code null} if no object exists for that id
     * @throws JsonParseException if {@code jElement} is not a string, or {@code registry} is unknown
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
     * Deserializes a registry object as raw {@code T} (not {@link Holder}) from a required
     * member of {@code jObject}.
     *
     * @param jObject    object containing the member
     * @param memberName name of the member holding the object's id
     * @param registry   key of the registry to look up the object in
     * @return the registry object, or {@code null} if no object exists for that id
     * @throws JsonParseException if the member is missing, not a string, or {@code registry} is unknown
     */
    @Nullable
    public static <T> T deserializeRegistryObject(JsonObject jObject, String memberName, ResourceKey<Registry<T>> registry) throws JsonParseException {
        if (!jObject.has(memberName))
            throw new JsonParseException("Missing \"%s\" from %s".formatted(memberName, jObject));
        return deserializeRegistryObject(jObject.get(memberName), registry);
    }

    /**
     * Deserializes a member as a {@link List} of registry objects, as raw {@code T} (not
     * {@link Holder}). Invalid entries are logged and skipped rather than failing the whole list.
     *
     * @param jObject    object containing the member
     * @param memberName name of the array member
     * @param context    GSON deserialization context (unused, kept for API symmetry)
     * @param registry   key of the registry to look up objects in
     * @return the deserialized list, or an empty list if the member is missing
     * @throws JsonParseException if {@code registry} is unknown
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
     * Deserializes a member as a {@link List} of registry objects, as raw {@code T} (not
     * {@link Holder}), with a required flag.
     *
     * @param jObject    object containing the member
     * @param memberName name of the array member
     * @param context    GSON deserialization context (unused, kept for API symmetry)
     * @param registry   key of the registry to look up objects in
     * @param required   if {@code true}, throws when the member is missing; otherwise returns an empty list
     * @return the deserialized list, or an empty list if the member is missing and not required
     * @throws JsonParseException if the member is required and missing, or {@code registry} is unknown
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
     * Serializes a registry object (raw {@code T}) as a JSON string holding its {@link ResourceLocation}.
     *
     * @param object   registry object to serialize
     * @param registry key of the registry the object belongs to
     * @return a JSON primitive with the object's id
     * @throws JsonParseException if {@code registry} is unknown, or {@code object} is not registered in it
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
     * Serializes a {@link List} of registry objects (raw {@code T}) into a {@link JsonArray}.
     *
     * @param jObject     unused, kept for API symmetry
     * @param objectsList registry objects to serialize
     * @param context     GSON serialization context (unused, kept for API symmetry)
     * @param registry    key of the registry the objects belong to
     * @return the resulting array of ids
     * @throws JsonParseException if {@code registry} is unknown, or an object is not registered in it
     */
    public static <T> JsonArray serializeRegistryObjectList(JsonObject jObject, List<T> objectsList, JsonSerializationContext context, ResourceKey<Registry<T>> registry) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (T object : objectsList) {
            jsonArray.add(serializeRegistryObject(object, registry));
        }
        return jsonArray;
    }

    /**
     * Serializes a {@link List} of {@link Holder}s (automatically unwrapped with {@link Holder#value()})
     * into a {@link JsonArray}.
     *
     * @param jObject     unused, kept for API symmetry
     * @param objectsList holders to serialize
     * @param context     GSON serialization context (unused, kept for API symmetry)
     * @param registry    key of the registry the objects belong to
     * @return the resulting array of ids
     * @throws JsonParseException if {@code registry} is unknown, or an object is not registered in it
     */
    public static <T> JsonArray serializeRegistryHolderList(JsonObject jObject, List<Holder<T>> objectsList, JsonSerializationContext context, ResourceKey<Registry<T>> registry) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (Holder<T> object : objectsList) {
            jsonArray.add(serializeRegistryObject(object, registry));
        }
        return jsonArray;
    }

    /**
     * Deserializes a {@link Holder}{@code <T>} from a JSON string holding its {@link ResourceLocation}.
     *
     * @param jElement string element holding the object's id
     * @param registry key of the registry to look up the object in
     * @return the holder, or {@code null} if no object exists for that id
     * @throws JsonParseException if {@code jElement} is not a string, or {@code registry} is unknown
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
     * Deserializes a {@link Holder}{@code <T>} from a required member of {@code jObject}.
     *
     * @param jObject    object containing the member
     * @param memberName name of the member holding the object's id
     * @param registry   key of the registry to look up the object in
     * @return the holder, or {@code null} if no object exists for that id
     * @throws JsonParseException if the member is missing, not a string, or {@code registry} is unknown
     */
    @Nullable
    public static <T> Holder<T> deserializeRegistryObjectAsHolder(JsonObject jObject, String memberName, ResourceKey<Registry<T>> registry) throws JsonParseException {
        if (!jObject.has(memberName))
            throw new JsonParseException("Missing \"%s\" from %s".formatted(memberName, jObject));
        return deserializeRegistryObjectAsHolder(jObject.get(memberName), registry);
    }

    /**
     * Deserializes a member as a {@link List} of {@link Holder}{@code <T>}. Invalid entries are
     * logged and skipped rather than failing the whole list.
     *
     * @param jObject    object containing the member
     * @param memberName name of the array member
     * @param context    GSON deserialization context (unused, kept for API symmetry)
     * @param registry   key of the registry to look up objects in
     * @return the deserialized list, or an empty list if the member is missing
     * @throws JsonParseException if {@code registry} is unknown
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
     * Deserializes a member as a {@link List} of {@link Holder}{@code <T>}, with a required flag.
     *
     * @param jObject    object containing the member
     * @param memberName name of the array member
     * @param context    GSON deserialization context (unused, kept for API symmetry)
     * @param registry   key of the registry to look up objects in
     * @param required   if {@code true}, throws when the member is missing; otherwise returns an empty list
     * @return the deserialized list, or an empty list if the member is missing and not required
     * @throws JsonParseException if the member is required and missing, or {@code registry} is unknown
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
     * Serializes a {@link Holder}{@code <T>} (automatically unwrapped with {@link Holder#value()}).
     *
     * @param holder   holder to serialize
     * @param registry key of the registry the object belongs to
     * @return a JSON primitive with the object's id
     * @throws JsonParseException if {@code registry} is unknown, or the object is not registered in it
     */
    public static <T> JsonElement serializeRegistryObject(Holder<T> holder, ResourceKey<Registry<T>> registry) throws JsonParseException {
        return serializeRegistryObject(holder.value(), registry);
    }

    /**
     * Serializes a {@link List} of {@link Holder}s (automatically unwrapped with {@link Holder#value()})
     * into a {@link JsonArray}.
     *
     * @param holdersList holders to serialize
     * @param context     GSON serialization context (unused, kept for API symmetry)
     * @param registry    key of the registry the objects belong to
     * @return the resulting array of ids
     * @throws JsonParseException if {@code registry} is unknown, or an object is not registered in it
     */
    public static <T> JsonArray serializeRegistryObjectListFromHolders(List<Holder<T>> holdersList, JsonSerializationContext context, ResourceKey<Registry<T>> registry) throws JsonParseException {
        JsonArray jsonArray = new JsonArray();
        for (Holder<T> holder : holdersList) {
            jsonArray.add(serializeRegistryObject(holder.value(), registry));
        }
        return jsonArray;
    }
}
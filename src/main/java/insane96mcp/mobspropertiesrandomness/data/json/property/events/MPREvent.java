package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.*;
import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.MPRRawPresetLoader;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;

public abstract class MPREvent extends MPRConditionable {
    public static final HashMap<ResourceLocation, MPREvent> LOADED_EVENTS = new HashMap<>();
    /// Events deserialized from event presets, by preset id
    public static final HashMap<ResourceLocation, MPREvent> PRESET_EVENTS = new HashMap<>();
    private static final Set<ResourceLocation> RESOLVING_PRESETS = new HashSet<>();

    public ResourceLocation id;
    public Target target;
    public List<MPRProperty> applyProperties;

    public MPREvent(ResourceLocation id, Target target, List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
        super(conditions);
        this.id = id;
        this.target = target;
        this.applyProperties = applyProperties;
        if (LOADED_EVENTS.containsKey(id))
            throw new JsonParseException("An event with id %s already exists".formatted(id));
        LOADED_EVENTS.put(id, this);
    }

    public boolean apply(LivingEntity living) {
        ResourceLocation eventId = getId(this.getClass());
        if (eventId == null)
            return false;
        ListTag list = ModNBTData.getList(living, eventId, CompoundTag.TAG_STRING);
        StringTag idTag = StringTag.valueOf(id.toString());
        if (list.contains(idTag))
            return false;
        list.add(idTag);
        ModNBTData.put(living, eventId, list);
        return true;
    }

    protected boolean tryExecute(LivingEntity living, @Nullable LivingEntity other) {
        if (!MPRCondition.conditionsApply(this.conditions, living, other))
            return false;
        return execute(living, other);
    }

    /**
     * Executes the event without checking its conditions.
     * Used by events that need to check conditions once and reuse the result for both their
     * own logic (e.g. damage modifiers) and the apply_properties, to avoid double-rolling
     * chance-based conditions.
     */
    protected boolean execute(LivingEntity living, @Nullable LivingEntity other) {
        tryApplyProperty(living, other);
        return true;
    }

    /**
     * Applies this event's properties to {@code living}/{@code other} (the event owner and its counterpart),
     * whichever this event's own {@link #target} resolves to, unless a property specifies its own
     * {@link MPRProperty#target}, in which case that one overrides it - letting different properties in the
     * same list independently target either entity.
     */
    public void tryApplyProperty(LivingEntity living, @Nullable LivingEntity other) {
        for (MPRProperty applyProperty : this.applyProperties)
            applyProperty.tryApply(this.target, living, other);
    }

    public static <T extends MPREvent> List<T> getEvents(LivingEntity living, Class<T> typeId) {
        List<T> events = new ArrayList<>();
        ResourceLocation eventId = getId(typeId);
        if (eventId == null)
            return events;
        ListTag list = ModNBTData.getList(living, eventId, CompoundTag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.parse(list.getString(i));
            if (LOADED_EVENTS.containsKey(id)) {
                MPREvent event = LOADED_EVENTS.get(id);
                if (typeId.isInstance(event))
                    events.add(typeId.cast(event));
            }
        }
        return events;
    }

    public static ResourceLocation getId(Class<? extends MPREvent> clazz) {
        ResourceLocation eventId = EventsRegistry.getId(clazz);
        if (eventId == null)
            return null;
        return ResourceLocation.parse(eventId.getNamespace() + ":" + "events/" + eventId.getPath());
    }

    @Nullable
    public static MPRProperty deserializeProperty(JsonObject jObject, JsonDeserializationContext context) {
        if (!jObject.has("apply_property"))
            return null;
        return MPRProperty.deserialize(jObject.get("apply_property"), context);
    }

    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context, boolean includeTarget) {
        jObject.addProperty("id", id.toString());
        if (includeTarget)
            jObject.add("target", context.serialize(this.target));
        if (!this.applyProperties.isEmpty())
            jObject.add("apply_properties", context.serialize(this.applyProperties));
        return super.endSerialization(jObject, context);
    }

    @Nullable
    public static MPREvent deserialize(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObjectProperty = json.getAsJsonObject();
        ResourceLocation eventId = MPR.locationFrom(GsonHelper.getAsString(jObjectProperty, "event"));
        Type eventType = EventsRegistry.get(eventId);
        if (eventType == null) {
            MPRLogger.warn("event %s does not exist. Skipping".formatted(eventId));
            return null;
        }
        return context.deserialize(jObjectProperty, eventType);
    }

    /**
     * Returns the event of the event preset {@code presetId}, deserializing it the first time it's referenced.
     * The same instance is shared by every reference, as events are identified by their (unique) id.
     * If the preset has no {@code id}, the preset id is used.
     */
    public static MPREvent fromPreset(ResourceLocation presetId, JsonDeserializationContext context) throws JsonParseException {
        MPREvent cached = PRESET_EVENTS.get(presetId);
        //If the file that first referenced the preset failed to load, its events have been unregistered, so the preset must be deserialized again
        if (cached != null && LOADED_EVENTS.get(cached.id) == cached)
            return cached;
        JsonElement preset = MPRRawPresetLoader.EVENT_PRESETS.get(presetId);
        if (preset == null)
            throw new JsonParseException("Event preset '%s' not found".formatted(presetId));
        if (!RESOLVING_PRESETS.add(presetId))
            throw new JsonParseException("Circular event preset reference: '%s'".formatted(presetId));
        try {
            JsonObject jObject = preset.getAsJsonObject().deepCopy();
            if (!jObject.has("id"))
                jObject.addProperty("id", presetId.toString());
            MPREvent event = deserialize(jObject, context);
            if (event == null)
                throw new JsonParseException("Event preset '%s' has an invalid event".formatted(presetId));
            PRESET_EVENTS.put(presetId, event);
            return event;
        }
        finally {
            RESOLVING_PRESETS.remove(presetId);
        }
    }

    public static List<MPREvent> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPREvent> events = new ArrayList<>();
        if (!jObject.has(memberName))
            return events;
        JsonArray aEvents = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aEvents) {
            MPREvent event = deserialize(jsonElement, context);
            if (event != null)
                events.add(event);
        }
        return events;
    }
}

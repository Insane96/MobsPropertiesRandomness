package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.*;
import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class MPREvent extends MPRConditionable {
    public static final HashMap<ResourceLocation, MPREvent> LOADED_EVENTS = new HashMap<>();

    public ResourceLocation id;
    public Target target;
    public List<MPRProperty> applyProperties;

    public MPREvent(ResourceLocation id, Target target, List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
        super(conditions);
        this.id = id;
        this.target = target;
        this.applyProperties = applyProperties;
        if (!LOADED_EVENTS.containsKey(id))
            LOADED_EVENTS.put(id, this);
    }

    public boolean apply(LivingEntity living) {
        ResourceLocation eventId = getId(this.getClass());
        if (eventId == null)
            return false;
        ListTag list = ModNBTData.getList(living, eventId, CompoundTag.TAG_STRING);
        list.add(StringTag.valueOf(id.toString()));
        ModNBTData.put(living, eventId, list);
        return true;
    }

    protected boolean tryExecute(LivingEntity living, @Nullable LivingEntity other) {
        if (!MPRCondition.conditionsApply(this.conditions, living))
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
        LivingEntity target = this.target == Target.THIS ? living : other;
        if (target == null)
            return false;
        executeFor(target);
        return true;
    }

    protected void executeFor(LivingEntity living) {
        tryApplyProperty(living);
    }

    public void tryApplyProperty(LivingEntity entity) {
        for (MPRProperty applyProperty : this.applyProperties)
            applyProperty.tryApply(entity);
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

package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.*;
import insane96mcp.insanelib.util.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRProperty;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRScalePehkuiProperty;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.commands.CommandFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class MPREvent extends MPRConditionable {
    public static final HashMap<ResourceLocation, MPREvent> LOADED_EVENTS = new HashMap<>();

    public ResourceLocation id;
    public Target target;
    @Nullable
    public CommandFunction.CacheableFunction function;
    public List<MPRProperty> applyProperties;

    public MPREvent(ResourceLocation id, Target target, @Nullable CommandFunction.CacheableFunction function, List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
        super(conditions);
        this.id = id;
        this.target = target;
        this.function = function;
        this.applyProperties = applyProperties;
    }

    public boolean apply(LivingEntity living) {
        ResourceLocation eventId = EventsRegistry.getId(this.getClass());
        if (eventId == null)
            return false;
        ListTag list = ModNBTData.getList(living, eventId, CompoundTag.TAG_STRING);
        list.add(StringTag.valueOf(id.toString()));
        ModNBTData.put(living, eventId, list);
        if (!LOADED_EVENTS.containsKey(id))
            LOADED_EVENTS.put(id, this);
        return true;
    }

    protected void execute(LivingEntity living, @Nullable LivingEntity other) {
        if (!MPRCondition.conditionsApply(this.conditions, living))
            return;
        LivingEntity target = this.target == Target.THIS ? living : other;
        if (target == null)
            return;
        executeFor(target);
    }

    protected void executeFor(LivingEntity living) {
        tryExecuteFunction(living);
        tryApplyProperty(living);
    }

    protected void tryExecuteFunction(LivingEntity entity) {
        if (this.function == null)
            return;
        MinecraftServer server = entity.level().getServer();
        if (server == null)
            return;
        this.function.get(server.getFunctions()).ifPresent((commandFunction) ->
                server.getFunctions().execute(commandFunction, server.getFunctions().getGameLoopSender().withPosition(new Vec3(entity.getX(), entity.getY(), entity.getZ())).withLevel((ServerLevel) entity.level()).withEntity(entity)));
    }

    public void tryApplyProperty(LivingEntity entity) {
        for (MPRProperty applyProperty : this.applyProperties)
            applyProperty.tryApply(entity);
        MPRScalePehkuiProperty.applyScheduled(entity);
    }

    public static <T extends MPREvent> List<T> getEvents(LivingEntity living, Class<T> typeId) {
        List<T> events = new ArrayList<>();
        ResourceLocation eventId = EventsRegistry.getId(typeId);
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

    @Nullable
    public static CommandFunction.CacheableFunction deserializeFunction(JsonObject jObject) {
        if (!jObject.has("function"))
            return null;
        String functionId = GsonHelper.getAsString(jObject, "function");
        return new CommandFunction.CacheableFunction(ResourceLocation.parse(functionId));
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
        if (this.function != null)
            jObject.addProperty("function", this.function.getId().toString());
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
            Logger.warn("event %s does not exist. Skipping".formatted(eventId));
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

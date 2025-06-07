package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.*;
import insane96mcp.insanelib.util.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
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

//Inherith directly from MPRProperty
public abstract class MPREvent extends MPRConditionable {
    public static final HashMap<ResourceLocation, MPREvent> LOADED_EVENTS = new HashMap<>();

    public ResourceLocation id;
    public Target target;
    @Nullable
    public CommandFunction.CacheableFunction function;

    public MPREvent(ResourceLocation id, Target target, @Nullable CommandFunction.CacheableFunction function, List<MPRCondition> conditions) {
        super(conditions);
        this.id = id;
        this.target = target;
        this.function = function;
    }

    public boolean apply(LivingEntity living) {
        ListTag list = ModNBTData.getList(living, MPR.location(typeId()), CompoundTag.TAG_STRING);
        list.add(StringTag.valueOf(id.toString()));
        ModNBTData.put(living, MPR.location(typeId()), list);
        if (!LOADED_EVENTS.containsKey(id))
            LOADED_EVENTS.put(id, this);
        return true;
    }

    protected void execute(LivingEntity living, LivingEntity other) {
        if (!MPRCondition.conditionsApply(this.conditions, living))
            return;
        executeFor(this.target == Target.THIS ? living : other);
    }

    protected void executeFor(LivingEntity living) {
        tryPlaySound(living);
        tryExecuteFunction(living);
        tryApplyPehkuiScale(living);
    }

    protected void tryPlaySound(LivingEntity entity) {
        /*if (this.playSound == null)
            return;
        this.playSound.playSound(this.target, entity);*/
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

    public void tryApplyPehkuiScale(LivingEntity entity) {
        //if (this.scalePehkui == null)
        //    return;
//
        //for (MPRScalePehkui scalePehkui1 : this.scalePehkui) {
        //    scalePehkui1.apply(entity);
        //}
    }

    public static List<MPREvent> getEvents(LivingEntity living, String typeId) {
        ListTag list = ModNBTData.getList(living, MPR.location(typeId), CompoundTag.TAG_STRING);
        List<MPREvent> events = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.parse(list.getString(i));
            if (LOADED_EVENTS.containsKey(id))
                events.add(LOADED_EVENTS.get(id));
        }
        return events;

    }

    public abstract String typeId();

    public static CommandFunction.CacheableFunction deserializeFunction(JsonObject jObject) {
        String functionId = GsonHelper.getAsString(jObject, "function");
        return new CommandFunction.CacheableFunction(ResourceLocation.parse(functionId));
    }

    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context, boolean includeTarget) {
        jObject.addProperty("id", id.toString());
        if (includeTarget)
            jObject.add("target", context.serialize(this.target));
        if (this.function != null)
            jObject.addProperty("function", this.function.getId().toString());
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

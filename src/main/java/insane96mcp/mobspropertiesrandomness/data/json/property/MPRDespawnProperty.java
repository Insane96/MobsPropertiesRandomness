package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.world.scheduled.ScheduledTasks;
import insane96mcp.insanelib.world.scheduled.ScheduledTickTask;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRDespawnProperty.Serializer.class)
public class MPRDespawnProperty extends MPRProperty {
    public MPRDespawnProperty(List<MPRCondition> conditions) {
        super(conditions);
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (!(living instanceof ServerPlayer player))
            ScheduledTasks.schedule(new ScheduledTickTask(0) {
                @Override
                public void run() {
                    living.discard();
                }
            });
        else
            player.connection.disconnect(Component.literal("Despawned"));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRDespawnProperty>, JsonSerializer<MPRDespawnProperty> {
        @Override
        public MPRDespawnProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new MPRDespawnProperty(MPRCondition.deserializeList(json.getAsJsonObject(), "conditions", context));
        }

        @Override
        public JsonElement serialize(MPRDespawnProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}

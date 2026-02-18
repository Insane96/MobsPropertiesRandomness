package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRFunctionProperty.Serializer.class)
public class MPRFunctionProperty extends MPRProperty {
    @Nullable
    public CacheableFunction function;

    public MPRFunctionProperty(@Nullable CacheableFunction function, List<MPRCondition> conditions) {
        super(conditions);
        this.function = function;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (this.function == null)
            return false;
        MinecraftServer server = living.level().getServer();
        if (server == null)
            return false;
        this.function.get(server.getFunctions()).ifPresent((commandFunction) ->
                server.getFunctions().execute(commandFunction, server.getFunctions().getGameLoopSender().withPosition(new Vec3(living.getX(), living.getY(), living.getZ())).withLevel((ServerLevel) living.level()).withEntity(living)));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRFunctionProperty>, JsonSerializer<MPRFunctionProperty> {
        @Override
        public MPRFunctionProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String functionId = GsonHelper.getAsString(jObject, "function");
            return new MPRFunctionProperty(new CacheableFunction(ResourceLocation.parse(functionId)), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRFunctionProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (src.function != null)
                jObject.addProperty("function", src.function.getId().toString());
            return src.endSerialization(jObject, context);
        }
    }

}

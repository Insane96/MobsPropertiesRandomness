package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Type;

@JsonAdapter(MPRModLoadedCondition.Serializer.class)
public class MPRModLoadedCondition extends MPRCondition {
    public String modId;

    public MPRModLoadedCondition(String modId, boolean inverted) {
        super(inverted);
        this.modId = modId;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        return ModList.get().isLoaded(this.modId);
    }

    public static class Serializer implements JsonDeserializer<MPRModLoadedCondition>, JsonSerializer<MPRModLoadedCondition> {
        @Override
        public MPRModLoadedCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String modId = GsonHelper.getAsString(jObject, "mod_id");
            return new MPRModLoadedCondition(modId, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRModLoadedCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("mod_id", src.modId);
            return src.endSerialization(jObject);
        }
    }
}

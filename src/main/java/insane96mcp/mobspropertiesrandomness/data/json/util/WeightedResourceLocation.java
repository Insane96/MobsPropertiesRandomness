package insane96mcp.mobspropertiesrandomness.data.json.util;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

@JsonAdapter(WeightedResourceLocation.Serializer.class)
public class WeightedResourceLocation extends ModifiableWeightedRandom {
    private final ResourceLocation location;

    public WeightedResourceLocation(ResourceLocation location, MPRModifiableValue modifiableValue) {
        super(modifiableValue);
        this.location = location;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public static class Serializer implements JsonSerializer<WeightedResourceLocation>, JsonDeserializer<WeightedResourceLocation> {
        @Override
        public WeightedResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new WeightedResourceLocation(
                    ResourceLocation.parse(GsonHelper.getAsString(jObject, "location")),
                    context.deserialize(jObject.get("weight"), MPRModifiableValue.class)
            );
        }

        @Override
        public JsonElement serialize(WeightedResourceLocation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("location", context.serialize(src.location));
            jObject.add("weight", context.serialize(src.getWeight()));
            return jObject;
        }
    }
}

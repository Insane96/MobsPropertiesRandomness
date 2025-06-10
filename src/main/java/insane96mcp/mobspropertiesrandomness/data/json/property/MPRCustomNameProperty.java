package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRCustomNameProperty.Serializer.class)
public class MPRCustomNameProperty extends MPRProperty {
    public List<String> overrides;
    public List<String> prefixes;
    public List<String> suffixes;

    public MPRCustomNameProperty(List<String> overrides, List<String> prefixes, List<String> suffixes, List<MPRCondition> conditions) {
        super(conditions);
        this.overrides = overrides;
        this.prefixes = prefixes;
        this.suffixes = suffixes;
    }

    @Override
    public boolean apply(LivingEntity living) {
        String prefix = "";
        if (this.prefixes != null && !this.prefixes.isEmpty())
            prefix = this.prefixes.get(living.getRandom().nextInt(this.prefixes.size()));
        String suffix = "";
        if (this.suffixes != null && !this.suffixes.isEmpty())
            suffix = this.suffixes.get(living.getRandom().nextInt(this.suffixes.size()));

        Component component;
        if (this.overrides != null && !this.overrides.isEmpty())
            component = Component.literal(prefix + this.overrides.get(living.getRandom().nextInt(this.overrides.size())) + suffix);
        else
            component = Component.literal(prefix).append(living.getName()).append(suffix);

        living.setCustomName(component);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRCustomNameProperty>, JsonSerializer<MPRCustomNameProperty> {
        @Override
        public MPRCustomNameProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<String> overrides = SerializerUtils.deserializeList(jObject, "overrides", context, String.class, false);
            List<String> prefixes = SerializerUtils.deserializeList(jObject, "prefixes", context, String.class, false);
            List<String> suffixes = SerializerUtils.deserializeList(jObject, "suffixes", context, String.class, false);
            if (overrides.isEmpty() && prefixes.isEmpty() && suffixes.isEmpty())
                throw new JsonParseException("No overrides, prefixes or suffixes specified for Custom Name Property");
            return new MPRCustomNameProperty(overrides, prefixes, suffixes, MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRCustomNameProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (!src.overrides.isEmpty())
                jObject.add("overrides", context.serialize(src.overrides));
            if (!src.prefixes.isEmpty())
                jObject.add("prefixes", context.serialize(src.prefixes));
            if (!src.suffixes.isEmpty())
                jObject.add("suffixes", context.serialize(src.suffixes));
            return src.endSerialization(jObject, context);
        }
    }
}

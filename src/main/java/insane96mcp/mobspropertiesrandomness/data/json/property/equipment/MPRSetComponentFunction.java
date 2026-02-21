package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.mojang.serialization.JsonOps;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Item function that sets one or more data components on an ItemStack.
 *
 * <p>Values are expressed in JSON using the same format as vanilla {@code minecraft:set_components}
 * loot table function. Any numeric value (at any nesting depth) can be replaced with
 * {@code {"#range": <MPRRange>}} to be resolved randomly at runtime.</p>
 *
 * <pre>{@code
 * {
 *   "function": "set_component",
 *   "components": {
 *     "minecraft:custom_name": {"text": "My Sword", "italic": false},
 *     "minecraft:max_stack_size": {"#range": {"min": 8, "max": 32}},
 *     "minecraft:tool": {
 *       "default_mining_speed": {"#range": {"min": 1.0, "max": 4.0}},
 *       "damage_per_block": 2,
 *       "rules": []
 *     }
 *   }
 * }
 * }</pre>
 */
@JsonAdapter(MPRSetComponentFunction.Serializer.class)
public class MPRSetComponentFunction extends MPRItemFunction {

    // Kept as original JsonElement for serialization
    public final Map<ResourceLocation, JsonElement> componentsRaw;
    // Resolver tree built at deserialization time; resolves #range markers at apply time
    private final Map<ResourceLocation, Function<LivingEntity, JsonElement>> componentResolvers;

    public MPRSetComponentFunction(
            Map<ResourceLocation, JsonElement> componentsRaw,
            Map<ResourceLocation, Function<LivingEntity, JsonElement>> componentResolvers,
            List<MPRCondition> conditions) {
        super(conditions);
        this.componentsRaw = componentsRaw;
        this.componentResolvers = componentResolvers;
    }

    @Override
    protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, living.level().registryAccess());

        for (Map.Entry<ResourceLocation, Function<LivingEntity, JsonElement>> entry : componentResolvers.entrySet()) {
            applyComponent(stack, ops, entry.getKey(), entry.getValue().apply(living));
        }

        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void applyComponent(ItemStack stack, RegistryOps<JsonElement> ops, ResourceLocation location, JsonElement value) {
        DataComponentType componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(location);
        if (componentType == null) {
            MPRLogger.warn("set_component: unknown component type '%s'. Skipping.".formatted(location));
            return;
        }
        var codec = componentType.codec();
        if (codec == null) {
            MPRLogger.warn("set_component: component type '%s' has no codec (not persistent). Skipping.".formatted(location));
            return;
        }
        try {
            Object resolved = codec.parse(ops, value).getOrThrow();
            stack.set(componentType, resolved);
        } catch (Exception e) {
            MPRLogger.warn("set_component: failed to apply component '%s': %s".formatted(location, e.getMessage()));
        }
    }

    /**
     * Recursively walks a JsonElement and wraps any {@code {"#range": <MPRRange>}} object into
     * a {@link Function} that resolves the range at apply time. All other values become static
     * functions. The resulting tree is built once at load time and evaluated once per mob spawn.
     */
    private static Function<LivingEntity, JsonElement> buildResolver(JsonElement element, JsonDeserializationContext context) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("#range")) {
                MPRRange range = context.deserialize(obj.get("#range"), MPRRange.class);
                return living -> new JsonPrimitive(range.getDoubleBetween(living));
            }
            Map<String, Function<LivingEntity, JsonElement>> fieldResolvers = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                fieldResolvers.put(entry.getKey(), buildResolver(entry.getValue(), context));
            }
            return living -> {
                JsonObject result = new JsonObject();
                for (Map.Entry<String, Function<LivingEntity, JsonElement>> e : fieldResolvers.entrySet()) {
                    result.add(e.getKey(), e.getValue().apply(living));
                }
                return result;
            };
        } else if (element.isJsonArray()) {
            List<Function<LivingEntity, JsonElement>> elementResolvers = new ArrayList<>();
            for (JsonElement e : element.getAsJsonArray()) {
                elementResolvers.add(buildResolver(e, context));
            }
            return living -> {
                JsonArray result = new JsonArray();
                for (Function<LivingEntity, JsonElement> e : elementResolvers) {
                    result.add(e.apply(living));
                }
                return result;
            };
        } else {
            // Primitive or null: static value
            return living -> element;
        }
    }

    public static class Serializer implements JsonDeserializer<MPRSetComponentFunction>, JsonSerializer<MPRSetComponentFunction> {
        @Override
        public MPRSetComponentFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();

            Map<ResourceLocation, JsonElement> componentsRaw = new LinkedHashMap<>();
            Map<ResourceLocation, Function<LivingEntity, JsonElement>> componentResolvers = new LinkedHashMap<>();
            JsonObject jComponents = GsonHelper.getAsJsonObject(jObject, "components");
            for (Map.Entry<String, JsonElement> entry : jComponents.entrySet()) {
                ResourceLocation rl = ResourceLocation.parse(entry.getKey());
                if (!BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(rl))
                    MPRLogger.warn("set_component: unknown component type '%s'. Will be ignored.".formatted(rl));
                componentsRaw.put(rl, entry.getValue());
                componentResolvers.put(rl, buildResolver(entry.getValue(), context));
            }

            if (componentsRaw.isEmpty())
                throw new JsonParseException("set_component: 'components' must not be empty");

            return new MPRSetComponentFunction(componentsRaw, componentResolvers, MPRConditionable.deserializeList(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRSetComponentFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            JsonObject jComponents = new JsonObject();
            for (Map.Entry<ResourceLocation, JsonElement> entry : src.componentsRaw.entrySet()) {
                jComponents.add(entry.getKey().toString(), entry.getValue());
            }
            jObject.add("components", jComponents);
            return src.endSerialization(jObject, context);
        }
    }
}
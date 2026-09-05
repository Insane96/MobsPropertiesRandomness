package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRStructureCondition.Serializer.class)
public class MPRStructureCondition extends MPRCondition {
    //TODO Allow tags
    List<ResourceKey<Structure>> structures;

    public MPRStructureCondition(List<ResourceKey<Structure>> structures, boolean inverted) {
        super(inverted);
        this.structures = structures;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        for (ResourceKey<Structure> structure : this.structures) {
            StructureManager structureManager = ((ServerLevel) living.level()).structureManager();
            Structure s = structureManager.registryAccess().registryOrThrow(Registries.STRUCTURE).get(structure);
            if (s == null) {
                MPR.LOGGER.warn("No structure found with id {}. Ignored", structure.location());
                if (living.getServer() != null)
                    living.getServer().sendSystemMessage(Component.literal("No structure found with id " + structure.location()));
                continue;
            }
            if (structureManager.getStructureAt(living.blockPosition(), s).isValid())
                return true;
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRStructureCondition>, JsonSerializer<MPRStructureCondition> {
        @Override
        public MPRStructureCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            JsonArray aStructures = jObject.getAsJsonArray("structures");
            if (aStructures == null)
                throw new JsonParseException("Missing structures array");
            List<ResourceKey<Structure>> structures = new ArrayList<>();
            for (JsonElement jsonElement : aStructures) {
                ResourceKey<Structure> rk = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.parse(jsonElement.getAsString()));
                structures.add(rk);
            }
            return new MPRStructureCondition(structures, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRStructureCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            JsonArray aStructures = new JsonArray();
            for (ResourceKey<Structure> structure : src.structures) {
                aStructures.add(structure.location().toString());
            }
            jObject.add("structures", aStructures);
            return src.endSerialization(jObject);
        }
    }
}

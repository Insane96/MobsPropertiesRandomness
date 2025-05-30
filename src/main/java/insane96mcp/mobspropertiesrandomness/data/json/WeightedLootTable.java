package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;

@JsonAdapter(WeightedLootTable.Serializer.class)
public class WeightedLootTable extends WeightedResourceLocation {

    public WeightedLootTable(MPRModifiableValue modifiableWeight, ResourceLocation location, List<MPRCondition> conditions) {
        super(modifiableWeight, location, conditions);
    }

    @Nullable
    public WeightedLootTable computeAndGet(LivingEntity entity) {
        if (!MPRCondition.conditionsApply(this.conditions, entity))
            return null;
        this._weight = (int) this.modifiableWeight.getValue(entity);

        return this;
    }

    public static class Serializer extends WeightedResourceLocation.Serializer {
        public Serializer() {
            super("loot_table");
        }
    }
}

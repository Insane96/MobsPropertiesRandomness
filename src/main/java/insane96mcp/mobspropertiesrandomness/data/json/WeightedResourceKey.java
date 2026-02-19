package insane96mcp.mobspropertiesrandomness.data.json;

import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;

public abstract class WeightedResourceKey<T> extends MPRConditionable implements IWeightedRandom {
    public final MPRModifiableValue modifiableWeight;
    protected int _weight;
    protected final ResourceKey<T> key;

    public WeightedResourceKey(MPRModifiableValue modifiableWeight, ResourceKey<T> key, List<MPRCondition> conditions) {
        super(conditions);
        this.modifiableWeight = modifiableWeight;
        this.key = key;
    }

    public ResourceKey<T> getKey() {
        return this.key;
    }

    @Nullable
    public WeightedResourceKey<T> computeAndGet(LivingEntity entity) {
        if (!MPRCondition.conditionsApply(this.conditions, entity))
            return null;
        this._weight = (int) this.modifiableWeight.getValue(entity);
        if (this._weight <= 0)
            return null;

        return this;
    }

    @Override
    public int getWeight() {
        return this._weight;
    }
}

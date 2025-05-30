package insane96mcp.mobspropertiesrandomness.data.json.util;

import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.world.entity.LivingEntity;

public class ModifiableWeightedRandom implements IWeightedRandom {
    private final MPRModifiableValue modifiableWeight;
    private transient int _weight;

    public ModifiableWeightedRandom(MPRModifiableValue modifiableWeight) {
        this.modifiableWeight = modifiableWeight;
    }

    public ModifiableWeightedRandom computeAndGet(LivingEntity entity) {
        this._weight = (int) this.modifiableWeight.getValue(entity);
        return this;
    }

    @Override
    public int getWeight() {
        return _weight;
    }
}

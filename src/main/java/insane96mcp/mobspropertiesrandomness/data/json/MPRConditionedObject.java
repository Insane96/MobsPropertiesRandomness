package insane96mcp.mobspropertiesrandomness.data.json;

import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class MPRConditionedObject {
    private final List<MPRCondition> conditions = new ArrayList<>();

    public boolean conditionsApply(LivingEntity entity) {
        return MPRCondition.conditionsApply(this.conditions, entity);
    }

    public void addCondition(MPRCondition condition) {
        this.conditions.add(condition);
    }
}

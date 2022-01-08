package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.io.File;
import java.util.List;

public class MPRCustomName implements IMPRObject {

	public MPRModifiableValue chance;
	public List<String> list;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (chance != null)
			chance.validate(file);
	}

	public void applyCustomName(LivingEntity entity, World world) {
		if (this.chance != null && world.random.nextDouble() >= this.chance.getValue(entity, world))
			return;

		entity.setCustomName(new StringTextComponent(list.get(world.random.nextInt(list.size()))));
	}

	@Override
	public String toString() {
		return String.format("CustomName{chance: %s, list: %s}", chance, list);
	}
}

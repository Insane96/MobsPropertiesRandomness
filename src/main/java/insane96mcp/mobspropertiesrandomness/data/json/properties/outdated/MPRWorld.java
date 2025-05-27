package insane96mcp.mobspropertiesrandomness.data.json.properties.outdated;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public class MPRWorld implements IMPRObject {
	protected MPRRange deepness;

	private transient List<ResourceKey<Structure>> _structures;

	@Override
	public void validate() throws JsonValidationException {
		if (this.deepness != null)
			this.deepness.validate();
	}

	public boolean doesDepthMatch(LivingEntity entity) {
		if (this.deepness == null)
			return true;
		return entity.getY() >= this.deepness.getMin(entity) && entity.getY() <= this.deepness.getMax(entity);
	}

	public boolean isWhitelisted(LivingEntity entity) {
		return this.doesDepthMatch(entity);
	}

	@Override
	public String toString() {
		return String.format("World{deepness: %s}", this.deepness);
	}
}

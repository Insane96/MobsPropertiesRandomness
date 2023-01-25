package insane96mcp.mobspropertiesrandomness.data.json.util;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class MPRWorldWhitelist implements IMPRObject {

	public List<String> dimensions;
	private final transient ArrayList<ResourceKey<Level>> dimensionsResourceKeys = new ArrayList<>();

	public ArrayList<IdTagMatcher> biomes;

	protected MPRRange deepness;

	@Override
	public void validate() throws JsonValidationException {
		dimensionsResourceKeys.clear();
		if (dimensions != null) {
			for (String dimensions : dimensions) {
				ResourceKey<Level> rk = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimensions));
				dimensionsResourceKeys.add(rk);
			}
		}

		if (deepness != null)
			deepness.validate();
	}

	public boolean doesDimensionMatch(Entity entity) {
		if (this.dimensions.isEmpty())
			return true;
		for (ResourceKey<Level> dimension : this.dimensionsResourceKeys) {
			if (entity.level.dimension().equals(dimension)) {
				return true;
			}
		}
		return false;
	}

	public boolean doesBiomeMatch(LivingEntity entity) {
		if (this.biomes.isEmpty())
			return true;
		for (IdTagMatcher dimension : this.biomes) {
			if (dimension.matchesBiome(entity.level.getBiome(entity.blockPosition()))) {
				return true;
			}
		}
		return false;
	}

	public boolean doesDepthMatch(LivingEntity entity) {
		if (this.deepness == null)
			return true;
		return entity.getY() >= this.deepness.getMin(entity, entity.level) && entity.getY() <= this.deepness.getMax(entity, entity.level);
	}

	public boolean isWhitelisted(LivingEntity entity) {
		return doesBiomeMatch(entity) && doesDimensionMatch(entity) && doesDepthMatch(entity);
	}

	@Override
	public String toString() {
		return String.format("WorldWhitelist{dimensions: %s, biomes: %s, deepness: %s}", this.dimensions, this.biomes, this.deepness);
	}
}

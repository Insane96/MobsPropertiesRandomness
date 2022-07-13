package insane96mcp.mobspropertiesrandomness.json.util;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class MPRWorldWhitelist implements IMPRObject {

	protected List<String> dimensions;
	public transient List<ResourceLocation> dimensionsList = new ArrayList<>();

	protected List<String> biomes;
	public transient List<ResourceLocation> biomesList = new ArrayList<>();

	protected MPRRange deepness;

	@Override
	public void validate() throws JsonValidationException {
		dimensionsList.clear();
		if (dimensions != null) {
			for (String dimension : dimensions) {
				ResourceLocation dimensionRL = new ResourceLocation(dimension);
				dimensionsList.add(dimensionRL);
			}
		}

		biomesList.clear();
		if (biomes != null) {
			for (String biome : biomes) {
				ResourceLocation biomeLoc = new ResourceLocation(biome);
				biomesList.add(biomeLoc);
			}
		}

		if (deepness != null)
			deepness.validate();
	}

	public boolean doesDimensionMatch(Entity entity) {
		if (dimensionsList.isEmpty())
			return true;

		ResourceLocation entityDimension = entity.level.dimension().getRegistryName();
		return dimensionsList.contains(entityDimension);
	}

	public boolean doesBiomeMatch(LivingEntity entity) {
		if (biomesList.isEmpty())
			return true;

		ResourceLocation entityBiome = entity.level.getBiome(entity.blockPosition()).value().getRegistryName();
		return biomesList.contains(entityBiome);
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
		return String.format("WorldWhitelist{dimensions: %s, biomes: %s}", dimensionsList, biomesList);
	}
}

package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPRWorldWhitelist implements IMPRObject {

	protected List<String> dimensions;
	public transient List<ResourceLocation> dimensionsList = new ArrayList<>();

	protected List<String> biomes;
	public transient List<ResourceLocation> biomesList = new ArrayList<>();

	@Override
	public void validate(File file) throws InvalidJsonException {
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
	}

	public boolean doesDimensionMatch(Entity entity) {
		if (dimensionsList.isEmpty())
			return true;

		ResourceLocation entityDimension = entity.world.getDimensionKey().getLocation();
		return dimensionsList.contains(entityDimension);
	}

	public boolean doesBiomeMatch(MobEntity entity) {
		if (biomesList.isEmpty())
			return true;

		ResourceLocation entityBiome = entity.world.getBiome(entity.getPosition()).getRegistryName();
		return biomesList.contains(entityBiome);
	}

	public boolean isWhitelisted(MobEntity entity) {
		return doesBiomeMatch(entity) && doesDimensionMatch(entity);
	}

	@Override
	public String toString() {
		return String.format("WorldWhitelist{dimensions: %s, biomes: %s}", dimensionsList, biomesList);
	}
}
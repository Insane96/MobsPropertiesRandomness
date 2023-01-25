package insane96mcp.mobspropertiesrandomness.data.json.util;

import com.google.gson.annotations.SerializedName;
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
	@SerializedName("inverse_dimension_list")
	public Boolean inverseDimensionList;

	public ArrayList<IdTagMatcher> biomes;
	@SerializedName("inverse_biome_list")
	public Boolean inverseBiomeList;

	protected MPRRange deepness;

	@Override
	public void validate() throws JsonValidationException {
		this.dimensionsResourceKeys.clear();
		if (this.dimensions != null) {
			for (String dimensions : this.dimensions) {
				ResourceKey<Level> rk = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimensions));
				this.dimensionsResourceKeys.add(rk);
			}
		}
		if (this.inverseDimensionList == null)
			this.inverseDimensionList = false;

		if (this.inverseBiomeList == null)
			this.inverseBiomeList = false;

		if (this.deepness != null)
			this.deepness.validate();
	}

	public boolean doesDimensionMatch(Entity entity) {
		if (this.dimensions.isEmpty())
			return true;
		for (ResourceKey<Level> dimension : this.dimensionsResourceKeys) {
			if (entity.level.dimension().equals(dimension)) {
				return !this.inverseDimensionList;
			}
		}
		return this.inverseDimensionList;
	}

	public boolean doesBiomeMatch(LivingEntity entity) {
		if (this.biomes.isEmpty())
			return true;
		for (IdTagMatcher dimension : this.biomes) {
			if (dimension.matchesBiome(entity.level.getBiome(entity.blockPosition()))) {
				return !this.inverseBiomeList;
			}
		}
		return this.inverseBiomeList;
	}

	public boolean doesDepthMatch(LivingEntity entity) {
		if (this.deepness == null)
			return true;
		return entity.getY() >= this.deepness.getMin(entity, entity.level) && entity.getY() <= this.deepness.getMax(entity, entity.level);
	}

	public boolean isWhitelisted(LivingEntity entity) {
		return this.doesBiomeMatch(entity) && this.doesDimensionMatch(entity) && this.doesDepthMatch(entity);
	}

	@Override
	public String toString() {
		return String.format("WorldWhitelist{dimensions: %s, biomes: %s, deepness: %s}", this.dimensions, this.biomes, this.deepness);
	}
}

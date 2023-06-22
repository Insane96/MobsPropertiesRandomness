package insane96mcp.mobspropertiesrandomness.data.json.util;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.core.registries.Registries;
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

	@SerializedName("moon_phases")
	protected List<MoonPhase> moonPhases;

	@Override
	public void validate() throws JsonValidationException {
		this.dimensionsResourceKeys.clear();
		if (this.dimensions != null) {
			for (String dimensions : this.dimensions) {
				ResourceKey<Level> rk = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimensions));
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

	public boolean doesMoonPhaseMatch(LivingEntity entity) {
		boolean moonPhaseMatches = false;
		for (MoonPhase moonPhase : this.moonPhases) {
			if (moonPhase == MoonPhase.of(entity.level.getMoonPhase())) {
				moonPhaseMatches = true;
				break;
			}
		}
		return moonPhaseMatches;
	}

	public boolean isWhitelisted(LivingEntity entity) {
		return this.doesBiomeMatch(entity) && this.doesDimensionMatch(entity) && this.doesDepthMatch(entity) && this.doesMoonPhaseMatch(entity);
	}

	@Override
	public String toString() {
		return String.format("WorldWhitelist{dimensions: %s, inverse_dimension_list: %s, biomes: %s, inverse_biome_list: %s, deepness: %s, moon_phases: %s}", this.dimensions, this.inverseDimensionList, this.biomes, this.inverseBiomeList, this.deepness, this.moonPhases);
	}

	enum MoonPhase {
		@SerializedName("full_moon")
		FULL_MOON,
		@SerializedName("waning_gibbous")
		WANING_GIBBOUS,
		@SerializedName("last_quarter")
		LAST_QUARTER,
		@SerializedName("waning_crescent")
		WANING_CRESCENT,
		@SerializedName("new_moon")
		NEW_MOON,
		@SerializedName("waxing_crescent")
		WAXING_CRESCENT,
		@SerializedName("first_quarter")
		FIRST_QUARTER,
		@SerializedName("waxing_gibbous")
		WAXING_GIBBOUS;

		private static final MoonPhase[] PHASES = MoonPhase.values();

		public static MoonPhase of(int moonPhase) {
			return PHASES[moonPhase];
		}
	}
}

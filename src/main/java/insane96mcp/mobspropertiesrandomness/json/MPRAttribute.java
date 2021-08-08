package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.utils.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRDifficulty;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import insane96mcp.mobspropertiesrandomness.utils.MPRUtils;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MPRAttribute implements IMPRObject, IMPRAppliable {
	//TODO Add uuid?
	public String id;
	public MPRRange modifier;
	public AttributeModifier.Operation operation;
	//TODO Replace with a null check to difficulty
	@SerializedName("affected_by_difficulty")
	public boolean affectedByDifficulty;
	public MPRDifficulty difficulty;

	//Move to MPRWorld
	private List<String> dimensions;
	public transient List<ResourceLocation> dimensionsList = new ArrayList<>();

	private List<String> biomes;
	public transient List<ResourceLocation> biomesList = new ArrayList<>();

	@Override
	public void validate(File file) throws InvalidJsonException {
		//Attribute Id
		if (id == null)
			throw new InvalidJsonException("Missing Attribute Id for " + this, file);

		//Modifier
		if (modifier == null)
			throw new InvalidJsonException("Missing Modifier (Min/Max) Id for " + this, file);
		modifier.validate(file);

		//Modifier
		if (operation == null)
			throw new InvalidJsonException("Missing Operation for " + this, file);

		//difficulty
		if (!affectedByDifficulty) {
			if (difficulty == null) {
				Logger.debug("Difficulty Object is missing, affected_by_difficulty will be false for " + this);
			}
			else {
				Logger.debug("Difficulty Object is present, affected_by_difficulty will be true for " + this);
				affectedByDifficulty = true;
				difficulty.validate(file);
			}
		}
		else if (difficulty == null)
			difficulty = new MPRDifficulty();

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

	@Override
	public void apply(MobEntity entity, World world) {
		if (world.isRemote)
			return;

		if (!MPRUtils.doesDimensionMatch(entity, this.dimensionsList))
			return;

		if (!MPRUtils.doesBiomeMatch(entity, this.biomesList))
			return;

		float min = this.modifier.getMin();
		float max = this.modifier.getMax();
		float multiplier = 1.0f;

		if (this.affectedByDifficulty) {

			Difficulty difficulty = world.getDifficulty();
			if (!this.difficulty.isLocalDifficulty) {
				switch (difficulty) {
					case EASY:
						multiplier *= 0.5d;
						break;
					case NORMAL:
						multiplier *= 1.0d;
						break;
					case HARD:
						multiplier *= 2.0d;
						break;
					default:
						break;
				}
			}
			else {
				multiplier *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty();
			}

			multiplier *= this.difficulty.multiplier;

			if (!this.difficulty.affectsMaxOnly)
				min *= multiplier;
			max *= multiplier;
		}

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.id));
		ModifiableAttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance == null) {
			Logger.warn("Attribute " + this.id + " not found for the entity, skipping the attribute");
			return;
		}

		float amount = RandomHelper.getFloat(world.rand, min, max);

		AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), MobsPropertiesRandomness.RESOURCE_PREFIX + this.id, amount, operation);
		attributeInstance.applyPersistentModifier(modifier);

		//Health Fix
		if (this.id.equals("generic.max_health"))
			entity.setHealth((float) attributeInstance.getValue());
	}

	@Override
	public String toString() {
		return String.format("Attribute{id: %s, modifier: %s, operation: %s, affected_by_difficulty: %b, difficulty: %s, dimensions: %s, biomes: %s}", id, modifier, operation, affectedByDifficulty, difficulty, dimensions, biomes);
	}
}

package insane96mcp.mpr.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.init.ModConfig;
import insane96mcp.mpr.json.utils.JsonDifficulty;
import insane96mcp.mpr.json.utils.JsonUtils;
import insane96mcp.mpr.json.utils.RangeMinMax;
import insane96mcp.mpr.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Attribute implements IJsonObject{
	public String id;
	public RangeMinMax modifier;
	@SerializedName("is_flat")
	public boolean isFlat;
	@SerializedName("affected_by_difficulty")
	public boolean affectedByDifficulty;
	public JsonDifficulty difficulty;
	public List<Integer> dimensions;
	private List<String> biomes;
	public transient List<Biome> biomesList;
	
	@Override
	public String toString() {
		return String.format("Attribute{id: %s, modifier: %s, isFlat: %b, affectedByDifficulty: %b, difficulty: %s, dimensions: %s, biomes: %s}", id, modifier, isFlat, affectedByDifficulty, difficulty, dimensions, biomes);
	}
	
	public void validate(final File file) throws InvalidJsonException {
		//Attribute Id
		if (id == null)
			throw new InvalidJsonException("Missing Attribute Id for " + this.toString(), file);
		
		//Modifier
		if (modifier == null) 
			throw new InvalidJsonException("Missing Modifier (Min/Max) Id for " + this.toString(), file);
		
		modifier.validate(file);
		
		//difficulty
		if (!affectedByDifficulty) {
			if (difficulty == null) {
				Logger.debug("JsonDifficulty Object is missing, affected_by_difficulty will be false for " + this.toString());
			}
			else {
				Logger.debug("JsonDifficulty Object is present, affected_by_difficulty will be true for " + this.toString());
				affectedByDifficulty = true;
				difficulty.validate(file);
			}
		}
		else
			if (difficulty == null) 
				difficulty = new JsonDifficulty();
		
		if (dimensions == null)
			dimensions = new ArrayList<>();
		
		biomesList = new ArrayList<>();
		if (biomes == null) {
			biomes = new ArrayList<>();
		}
		else {
			for (String biome : biomes) {
				ResourceLocation biomeLoc = new ResourceLocation(biome);
				Biome b = ForgeRegistries.BIOMES.getValue(biomeLoc);
				biomesList.add(b);
			}
		}
	}

	public static void apply(MobEntity entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (JsonUtils.matchesEntity(entity, world, random, mob)) {
				for (Attribute attribute : mob.attributes) {
					if (!JsonUtils.doesDimensionMatch(entity, attribute.dimensions))
						continue;
					
					if (!JsonUtils.doesBiomeMatch(entity, attribute.biomesList))
						continue;
					
					float min = attribute.modifier.getMin();
					float max = attribute.modifier.getMax();
					
					if (attribute.affectedByDifficulty) {
						
						Difficulty difficulty = world.getDifficulty();
						
						if (!attribute.difficulty.isLocalDifficulty) {
							switch (difficulty) {
							case EASY:
								if (!attribute.difficulty.affectsMaxOnly)
									min *= ModConfig.Difficulty.easyMultiplier.get();
								max *= ModConfig.Difficulty.easyMultiplier.get();
								break;

							case NORMAL:
								if (!attribute.difficulty.affectsMaxOnly)
									min *= ModConfig.Difficulty.normalMultiplier.get();
								max *= ModConfig.Difficulty.normalMultiplier.get();
								break;
								
							case HARD:
								if (!attribute.difficulty.affectsMaxOnly)
									min *= ModConfig.Difficulty.hardMultiplier.get();
								max *= ModConfig.Difficulty.hardMultiplier.get();
								break;
								
							default:
								break;
							}
						}
						else {
							if (!attribute.difficulty.affectsMaxOnly)
								min *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty();
							max *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty();
						}
						max *= attribute.difficulty.multiplier;
					}
					
					IAttributeInstance attributeInstance = entity.getAttributes().getAttributeInstanceByName(attribute.id);
					if (attributeInstance == null) {
						Logger.warning("Attribute " + attribute.id + " not found for the entity, skipping the attribute");
						continue;
					}
					
					float amount = MathHelper.nextFloat(random, min, max);
					
					if (attribute.isFlat) {
						amount -= attributeInstance.getValue();
					}
					else {
						amount /= 100f;
					}
					
					AttributeModifier modifier = new AttributeModifier(MobsPropertiesRandomness.RESOURCE_PREFIX + attribute.id, amount, attribute.isFlat ? AttributeModifier.Operation.ADDITION : AttributeModifier.Operation.MULTIPLY_BASE);
					attributeInstance.applyModifier(modifier);
					
					//Health Fix
					if (attribute.id.equals("generic.maxHealth"))
						entity.setHealth((float) attributeInstance.getValue());
				}
			}
		}
	}
}

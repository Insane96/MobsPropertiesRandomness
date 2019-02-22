package insane96mcp.mpr.json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;

import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.utils.Difficulty;
import insane96mcp.mpr.json.utils.RangeMinMax;
import insane96mcp.mpr.json.utils.Utils;
import insane96mcp.mpr.lib.Logger;
import insane96mcp.mpr.lib.Properties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class Attribute implements IJsonObject{
	public String id;
	public RangeMinMax modifier;
	@SerializedName("is_flat")
	public boolean isFlat;
	@SerializedName("affected_by_difficulty")
	public boolean affectedByDifficulty;
	public Difficulty difficulty;
	public List<Integer> dimensions;
	private List<String> biomes;
	public transient List<Biome> biomesList;
	
	@Override
	public String toString() {
		return String.format("Attribute{id: %s, modifier: %s, isFlat: %b, affectedByDifficulty: %b, difficulty: %s, dimensions: %s, biomes: %s}", id, modifier, isFlat, affectedByDifficulty, difficulty, dimensions, biomes);
	}
	
	public void Validate(final File file) throws InvalidJsonException {
		//Attribute Id
		if (id == null)
			throw new InvalidJsonException("Missing Attribute Id for " + this.toString(), file);
		
		//Modifier
		if (modifier == null) 
			throw new InvalidJsonException("Missing Modifier (Min/Max) Id for " + this.toString(), file);
		
		modifier.Validate(file);
		
		//difficulty
		if (!affectedByDifficulty) {
			if (difficulty == null) {
				Logger.Debug("Difficulty Object is missing, affected_by_difficulty will be false for " + this.toString());
			}
			else {
				Logger.Debug("Difficulty Object is present, affected_by_difficulty will be true for " + this.toString());
				affectedByDifficulty = true;
				difficulty.Validate(file);
			}
		}
		else
			if (difficulty == null) 
				difficulty = new Difficulty();
		
		if (dimensions == null)
			dimensions = new ArrayList<Integer>();
		
		biomesList = new ArrayList<Biome>();
		if (biomes == null) {
			biomes = new ArrayList<String>();
		}
		else {
			for (String biome : biomes) {
				ResourceLocation biomeLoc = new ResourceLocation(biome);
				Biome b = Biome.REGISTRY.getObject(biomeLoc);
				biomesList.add(b);
			}
		}
	}

	public static void Apply(EntityLiving entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (Utils.MatchesEntity(entity, world, random, mob)) {
				for (Attribute attribute : mob.attributes) {
					if (!Utils.doesDimensionMatch(entity, attribute.dimensions))
						continue;
					
					if (!Utils.doesBiomeMatch(entity, attribute.biomesList))
						continue;
					
					float min = attribute.modifier.GetMin();
					float max = attribute.modifier.GetMax();
					
					if (attribute.affectedByDifficulty) {
						
						EnumDifficulty difficulty = world.getDifficulty();
						
						if (!attribute.difficulty.isLocalDifficulty) {
							switch (difficulty) {
							case EASY:
								if (!attribute.difficulty.affectsMaxOnly)
									min *= Properties.config.difficulty.easyMultiplier;
								max *= Properties.config.difficulty.easyMultiplier;
								break;

							case NORMAL:
								if (!attribute.difficulty.affectsMaxOnly)
									min *= Properties.config.difficulty.normalMultiplier;
								max *= Properties.config.difficulty.normalMultiplier;
								break;
								
							case HARD:
								if (!attribute.difficulty.affectsMaxOnly)
									min *= Properties.config.difficulty.hardMultiplier;
								max *= Properties.config.difficulty.hardMultiplier;
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
					
					IAttributeInstance attributeInstance = entity.getAttributeMap().getAttributeInstanceByName(attribute.id);
					if (attributeInstance == null) {
						Logger.Warning("Attribute " + attribute.id + " not found for the entity, skipping the attribute");
						continue;
					}
					
					float amount = MathHelper.nextFloat(random, min, max);
					
					if (attribute.isFlat) {
						amount -= attributeInstance.getAttributeValue();
					}
					else {
						amount /= 100f;
					}
					
					AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), MobsPropertiesRandomness.RESOURCE_PREFIX + attribute.id, amount, attribute.isFlat ? 0 : 1);
					attributeInstance.applyModifier(modifier);
					
					//Health Fix
					if (attribute.id.equals("generic.maxHealth"))
						entity.setHealth((float) attributeInstance.getAttributeValue());
				}
			}
		}
	}
}

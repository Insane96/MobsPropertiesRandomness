package insane96mcp.mpr.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.utils.Logger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonItem extends WeightedRandom.Item implements IJsonObject{

	public JsonItem(int itemWeightIn) {
		super(itemWeightIn);
	}

	public String id;
	public int data;
	private int weight;
	@SerializedName("weight_difficulty")
	private WeightDifficulty weightDifficulty;
	@SerializedName("drop_chance")
	public float dropChance;
	public List<JsonEnchantment> enchantments;
	public List<ItemAttribute> attributes;
	public String nbt;
	
	public List<Integer> dimensions;
	private List<String> biomes;
	public transient List<Biome> biomesList;
	
	@Override
	public String toString() {
		return String.format("Item{id: %s, data: %d, weight: %d, weightDifficulty: %s, dropChance: %f, enchantments: %s, attributes: %s, dimensions: %s, biomes: %s, nbt: %s}", id, data, weight, weightDifficulty, dropChance, enchantments, attributes, dimensions, biomes, nbt);
	}

	public void validate(final File file) throws InvalidJsonException{
		if (id == null)
			throw new InvalidJsonException("Missing Id for " + this, file);
		else if (!ForgeRegistries.ITEMS.containsKey(new ResourceLocation(id)))
			Logger.warning("Failed to find item with id " + id);

		if (weight <= 0)
			throw new InvalidJsonException("Missing weight (or weight <= 0) for " + this, file);
		else
			itemWeight = weight;
		
		if (weightDifficulty == null)
			weightDifficulty = new WeightDifficulty();
		
		if (dropChance == 0f) {
			Logger.debug("Drop Chance has been set to 0 (or omitted). Will now default to 8.5f. If you want mobs to not drop this item, set dropChance to -1");
			dropChance = 8.5f;
		}
		else if (dropChance == -1f) {
			Logger.debug("Drop Chance has been set to -1. Mob no longer drops this item in any case");
			dropChance = Short.MIN_VALUE;
		}
		
		if (enchantments == null)
			enchantments = new ArrayList<JsonEnchantment>();
		
		if (!enchantments.isEmpty()) {
			for (JsonEnchantment enchantment : enchantments) {
				enchantment.validate(file);
			}
		}
		
		if (attributes == null) 
			attributes = new ArrayList<ItemAttribute>();
		
		if (!attributes.isEmpty()) {
			for (ItemAttribute itemAttribute : attributes) {
				itemAttribute.validate(file);
			}
		}

		if (dimensions == null)
			dimensions = new ArrayList<Integer>();
		
		biomesList = new ArrayList<Biome>();
		if (biomes == null) {
			biomes = new ArrayList<String>();
		}
		else {
			for (String biome : biomes) {
				ResourceLocation biomeLoc = new ResourceLocation(biome);
				Biome b = ForgeRegistries.BIOMES.getValue(biomeLoc);
				biomesList.add(b);
			}
		}
	}
	
	public boolean hasDimension(World world) {
		boolean hasDimension = this.dimensions.isEmpty();
		
		for (Integer dimension : dimensions) {
			if (world.getDimension().getType().equals(DimensionType.getById(dimension)))
				hasDimension = true;
		}
		
		return hasDimension;
	}
	
	public boolean hasBiome(World world, BlockPos pos) {
		boolean hasBiome = this.biomesList.isEmpty();
		
		Biome b = world.getBiome(pos);
		
		for (Biome biome : biomesList) {
			if (biome.equals(b))
				hasBiome = true;
		}
		
		return hasBiome;
	}
	
	public JsonItem getWeightWithDifficulty(World world) {
		JsonItem item2 = this.copy();
		
		switch (world.getDifficulty()) {
			case EASY:
				item2.itemWeight += weightDifficulty.easy;
				break;
				
			case NORMAL:
				item2.itemWeight += weightDifficulty.normal;
				break;
				
			case HARD:
				item2.itemWeight += weightDifficulty.hard;
				break;
	
			default:
				break;
		}

		return item2;
	}

	/**
	 * Returns a copy of the JsonItem
	 * @return a copy of the JsonItem
	 */
	protected JsonItem copy() {
		JsonItem jItem = new JsonItem(this.weight);
		jItem.attributes = this.attributes;
		jItem.data = this.data;
		jItem.dropChance = this.dropChance;
		jItem.enchantments = this.enchantments;
		jItem.id = this.id;
		jItem.itemWeight = this.itemWeight;
		jItem.nbt = this.nbt;
		jItem.weight = this.weight;
		jItem.weightDifficulty = this.weightDifficulty;
		return jItem;
	}
}

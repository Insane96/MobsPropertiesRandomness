package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MPREnchantment implements IMPRObject {

	//TODO Rework, split id and Random. Random should be a new object with a list of enchantments and the "allow_curses" and "allow_treasure" keys
	public String id;
	@SerializedName("allow_curses")
	public boolean allowCurses;
	@SerializedName("allow_treasure")
	public boolean allowTreasure;
	//TODO Add a list of enchantments for "random"
	public MPRRange level;
	public MPRModifiableValue chance;
	@SerializedName("allow_incompatible")
	public boolean allowIncompatible;

	public MPREnchantment() {
		allowCurses = true;
		allowTreasure = true;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (id == null)
			throw new InvalidJsonException("Missing Enchantment ID for " + this, file);
		else if (!id.equals("random") && ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id)) == null)
			throw new InvalidJsonException("Invalid Enchantment ID for " + this, file);

		if (level != null)
			level.validate(file);

		if (chance != null)
			chance.validate(file);
	}

	public void applyToStack(MobEntity entity, World world, ItemStack itemStack) {
		if (this.chance != null && world.rand.nextFloat() >= this.chance.getValue(entity, world))
			return;

		Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(itemStack);

		if (this.id.equals("random")) {
			boolean isBook = itemStack.getItem() == Items.ENCHANTED_BOOK;
			List<Enchantment> list = ForgeRegistries.ENCHANTMENTS.getValues().stream().filter((enchantment) -> {
				if (enchantment.isCurse() && !allowCurses || enchantment.isTreasureEnchantment() && !allowTreasure)
					return false;

				if (!this.allowIncompatible) {
					for (Enchantment enchantmentOnStack : enchantmentsOnStack.keySet()) {
						if (!enchantment.isCompatibleWith(enchantmentOnStack)) {
							return false;
						}
					}
				}

				return isBook || enchantment.canApply(itemStack);
			}).collect(Collectors.toList());

			if (list.isEmpty()) {
				Logger.warn("Couldn't find any compatible enchantment for " + itemStack);
				return;
			}
			Enchantment enchantment = list.get(world.rand.nextInt(list.size()));
			int minLevel = level != null ? (int) level.getMin(entity, world) : enchantment.getMinLevel();
			int maxLevel = level != null ? (int) level.getMax(entity, world) : enchantment.getMaxLevel();
			int level = RandomHelper.getInt(world.rand, minLevel, maxLevel);

			addEnchantmentToItemStack(itemStack, enchantment, level);
		}
		else {
			Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));
			boolean canApply = this.allowIncompatible || EnchantmentHelper.areAllCompatibleWith(enchantmentsOnStack.keySet(), enchantment);
			if (canApply) {
				int minLevel = level != null ? (int) level.getMin(entity, world) : enchantment.getMinLevel();
				int maxLevel = level != null ? (int) level.getMax(entity, world) : enchantment.getMaxLevel();
				int level = RandomHelper.getInt(world.rand, minLevel, maxLevel);

				addEnchantmentToItemStack(itemStack, enchantment, level);
			}
		}

	}

	private static void addEnchantmentToItemStack(ItemStack itemStack, Enchantment enchantment, int level) {
		if (itemStack.getItem() == Items.ENCHANTED_BOOK)
			EnchantedBookItem.addEnchantment(itemStack, new EnchantmentData(enchantment, level));
		else
			itemStack.addEnchantment(enchantment, level);
	}

	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, allowCurses: %s, allowTreasure: %s, level: %s, chance: %s}", id, allowCurses, allowTreasure, level, chance);
	}
}

package insane96mcp.mobspropertiesrandomness.json.properties.equipment;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MPREnchantment implements IMPRObject {

	public String id;
	public MPRRandomEnchantment random;
	public MPRRange level;
	public MPRModifiableValue chance;
	@SerializedName("allow_incompatible")
	public boolean allowIncompatible;
	@SerializedName("with_levels")
	public MPRRange withLevels;

	public MPREnchantment() {

	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.id == null && this.random == null && this.withLevels == null)
			throw new JsonValidationException("Missing id, random or with_levels for %s".formatted(this));

		if (this.id != null && ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(this.id)) == null)
			throw new JsonValidationException("Invalid Enchantment ID %s for %s".formatted(this.id, this));

		if (this.level != null)
			this.level.validate();

		if (this.random != null)
			this.random.validate();

		if (this.chance != null)
			this.chance.validate();

		if (this.withLevels != null)
			this.withLevels.validate();
	}

	public void applyToStack(LivingEntity entity, Level level, ItemStack itemStack) {
		if (this.chance != null && level.random.nextFloat() >= this.chance.getValue(entity, level))
			return;

		if (this.withLevels != null) {
			enchantItem(level.random, itemStack, this.withLevels.getInt(entity, level), false);
		}
		else {
			Enchantment enchantment;
			//id has priority
			if (this.id != null) {
				Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(itemStack);
				enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));
				//noinspection ConstantConditions can't be null as it's checked to exist when the data is reloaded
				boolean canApply = this.allowIncompatible || EnchantmentHelper.isEnchantmentCompatible(enchantmentsOnStack.keySet(), enchantment);
				if (!canApply)
					enchantment = null;
			}
			//Then random object
			else {
				enchantment = this.random.getEnchantment(level.random, itemStack, this.allowIncompatible);
			}
			if (enchantment == null)
				return;

			int minLevel = this.level != null ? (int) this.level.getMin(entity, level) : enchantment.getMinLevel();
			int maxLevel = this.level != null ? (int) this.level.getMax(entity, level) : enchantment.getMaxLevel();
			int lvl = Mth.nextInt(level.random, minLevel, maxLevel);

			addEnchantmentToItemStack(itemStack, enchantment, lvl);
		}
	}

	private static void addEnchantmentToItemStack(ItemStack itemStack, Enchantment enchantment, int level) {
		if (itemStack.getItem() == Items.ENCHANTED_BOOK)
			EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchantment, level));
		else
			itemStack.enchant(enchantment, level);
	}

	public static void enchantItem(Random random, ItemStack itemStack, int level, boolean treasure) {
		level = Mth.clamp(level, 1, 40);
		List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(random, itemStack, level, treasure);
		boolean isEnchantedBook = itemStack.is(Items.ENCHANTED_BOOK);

		for (EnchantmentInstance enchantmentInstance : list) {
			if (isEnchantedBook)
				EnchantedBookItem.addEnchantment(itemStack, enchantmentInstance);
			else
				itemStack.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
		}
	}

	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, random: %s, level: %s, chance: %s, with_levels: %s}", this.id, this.random, this.level, this.chance, this.withLevels);
	}
}

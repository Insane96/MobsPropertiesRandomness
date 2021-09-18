package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
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
import java.util.Map;

public class MPREnchantment implements IMPRObject {

	public String id;
	public MPRRandomEnchantment random;
	public MPRRange level;
	public MPRModifiableValue chance;
	@SerializedName("allow_incompatible")
	public boolean allowIncompatible;

	public MPREnchantment() {

	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.id == null && this.random == null)
			throw new InvalidJsonException("Missing Enchantment ID or Random Object for " + this, file);

		if (this.id != null && ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(this.id)) == null)
			throw new InvalidJsonException("Invalid Enchantment ID " + this.id + " for " + this, file);

		if (this.level != null)
			this.level.validate(file);

		if (this.random != null)
			this.random.validate(file);

		if (this.chance != null)
			this.chance.validate(file);
	}

	public void applyToStack(MobEntity entity, World world, ItemStack itemStack) {
		if (this.chance != null && world.rand.nextFloat() >= this.chance.getValue(entity, world))
			return;

		Enchantment enchantment;
		if (this.id != null) {
			Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(itemStack);
			enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));
			boolean canApply = this.allowIncompatible || EnchantmentHelper.areAllCompatibleWith(enchantmentsOnStack.keySet(), enchantment);
			if (!canApply)
				enchantment = null;
		}
		else {
			enchantment = this.random.getEnchantment(world.rand, itemStack, this.allowIncompatible);
		}
		if (enchantment == null)
			return;

		int minLevel = this.level != null ? (int) this.level.getMin(entity, world) : enchantment.getMinLevel();
		int maxLevel = this.level != null ? (int) this.level.getMax(entity, world) : enchantment.getMaxLevel();
		int level = RandomHelper.getInt(world.rand, minLevel, maxLevel);

		addEnchantmentToItemStack(itemStack, enchantment, level);
	}

	private static void addEnchantmentToItemStack(ItemStack itemStack, Enchantment enchantment, int level) {
		if (itemStack.getItem() == Items.ENCHANTED_BOOK)
			EnchantedBookItem.addEnchantment(itemStack, new EnchantmentData(enchantment, level));
		else
			itemStack.addEnchantment(enchantment, level);
	}

	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, random: %s, level: %s, chance: %s}", id, random, level, chance);
	}
}

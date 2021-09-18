package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MPRRandomEnchantment implements IMPRObject {

	@SerializedName("allow_curses")
	public boolean allowCurses;
	@SerializedName("allow_treasure")
	public boolean allowTreasure;
	public List<String> list;

	public MPRRandomEnchantment() {
		allowCurses = true;
		allowTreasure = true;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.list != null) {
			for (String enchantment : this.list) {
				if (ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantment)) == null)
					throw new InvalidJsonException("Invalid Enchantment ID " + enchantment + " for " + this, file);
			}
		}
	}

	@Nullable
	public Enchantment getEnchantment(Random rand, ItemStack itemStack, boolean allowIncompatible) {

		Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(itemStack);

		boolean isBook = itemStack.getItem() == Items.ENCHANTED_BOOK;

		List<Enchantment> toFilter = new ArrayList<>();
		if (this.list != null)
			for (String s : list)
				toFilter.add(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(s)));
		else
			toFilter = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues());

		List<Enchantment> appliableEnchantments = toFilter.stream().filter((enchantment) -> {
			if (enchantment.isCurse() && !allowCurses || enchantment.isTreasureEnchantment() && !allowTreasure)
				return false;

			if (!allowIncompatible) {
				for (Enchantment enchantmentOnStack : enchantmentsOnStack.keySet()) {
					if (!enchantment.isCompatibleWith(enchantmentOnStack)) {
						return false;
					}
				}
			}

			return isBook || enchantment.canApply(itemStack);
		}).collect(Collectors.toList());

		if (appliableEnchantments.isEmpty()) {
			Logger.warn("Couldn't find any compatible enchantment for " + itemStack);
			return null;
		}
		return appliableEnchantments.get(rand.nextInt(appliableEnchantments.size()));
	}

	@Override
	public String toString() {
		return String.format("RandomEnchantment{allowCurses: %s, allowTreasure: %s, list: %s}", allowCurses, allowTreasure, list);
	}
}

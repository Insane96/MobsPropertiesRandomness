package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	public void validate() throws JsonValidationException {
		if (this.list != null) {
			for (String enchantment : this.list) {
				if (ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantment)) == null) {
					throw new JsonValidationException("Invalid Enchantment ID " + enchantment + " for " + this);
				}
			}
		}
	}

	@Nullable
	public Enchantment getEnchantment(RandomSource rand, ItemStack itemStack, boolean allowIncompatible) {
		Map<Enchantment, Integer> enchantmentsOnStack = EnchantmentHelper.getEnchantments(itemStack);

		boolean isBook = itemStack.getItem() == Items.ENCHANTED_BOOK;

		List<Enchantment> toFilter = new ArrayList<>();
		if (this.list != null)
			for (String s : list)
				toFilter.add(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(s)));
		else
			toFilter = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues());

		List<Enchantment> appliableEnchantments = toFilter.stream().filter((enchantment) -> {
			if (enchantment.isCurse() && !allowCurses || enchantment.isTreasureOnly() && !allowTreasure)
				return false;

			if (!allowIncompatible) {
				for (Enchantment enchantmentOnStack : enchantmentsOnStack.keySet()) {
					if (!enchantment.isCompatibleWith(enchantmentOnStack)) {
						return false;
					}
				}
			}

			return isBook || enchantment.canEnchant(itemStack);
		}).toList();

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

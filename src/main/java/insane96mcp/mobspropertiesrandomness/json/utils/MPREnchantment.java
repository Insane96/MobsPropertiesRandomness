package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MPREnchantment implements IMPRObject {

	public String id;
	public MPRRange level;
	public MPRChance chance;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (id == null)
			throw new InvalidJsonException("Missing Enchantment ID for " + this, file);
		else if (!id.equals("random") && ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id)) == null)
			Logger.warn("Failed to find enchantment with id " + id);

		if (level != null)
			level.validate(file);
		else if (!Objects.equals(id, "random")){
			Logger.debug("Missing Enchantment Level for " + this + ". Will default to 1");
			level = new MPRRange(1, 1);
		}

		if (chance != null)
			chance.validate(file);
		else
			throw new InvalidJsonException("Missing chance for " + this, file);
	}

	public void applyToStack(LivingEntity entity, World world, ItemStack itemStack) {
		if (!this.chance.chanceMatches(entity, world))
			return;

		if (this.id.equals("random")) {
			List<Enchantment> validEnch = new ArrayList<>();
			for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()) {
				if (/*itemStack.getItem() == Items.BOOK || */enchantment.canApply(itemStack))
					validEnch.add(enchantment);
			}

			if (validEnch.isEmpty()) {
				Logger.warn("Couldn't find any compatible enchantment for " + itemStack);
				return;
			}

			Enchantment choosenEnch = validEnch.get(world.rand.nextInt(validEnch.size()));
			int level = RandomHelper.getInt(world.rand, choosenEnch.getMinLevel(), choosenEnch.getMaxLevel());
			//if (itemStack.getItem() == Items.ENCHANTED_BOOK)
			//    ItemEnchantedBook.addEnchantment(itemStack, new EnchantmentData(choosenEnch, level));
			itemStack.addEnchantment(choosenEnch, level);
		}
		else {
			Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(id));
			boolean canApply = true;
			ListNBT enchantments = itemStack.getEnchantmentTagList();
			if (!enchantments.isEmpty()) {
				for (INBT nbt : enchantments) {
					CompoundNBT compoundNBT = (CompoundNBT) nbt;
					if (compoundNBT.isEmpty())
						continue;

					String itemEnchId = compoundNBT.getString("id");
					Enchantment itemEnch = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(itemEnchId));
					if (!enchantment.isCompatibleWith(itemEnch)) {
						canApply = false;
						break;
					}
				}
			}

			if (canApply) {
				int level = RandomHelper.getInt(world.rand, (int) this.level.getMin(), (int) this.level.getMax());
				itemStack.addEnchantment(enchantment, level);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, level: %s, chance: %s}", id, level, chance);
	}
}

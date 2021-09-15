package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.MobEntity;
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
	@SerializedName("allow_curses")
	public boolean allowCurses;
	@SerializedName("allow_treasure")
	public boolean allowTreasure;
	public MPRRange level;
	public MPRModifiableValue chance;

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
		else if (!Objects.equals(id, "random")){
			Logger.info("Missing Enchantment Level. " + this + ". Will default to 1");
			level = new MPRRange(1, 1);
		}

		if (chance != null)
			chance.validate(file);
	}

	public void applyToStack(MobEntity entity, World world, ItemStack itemStack) {
		if (this.chance != null && world.rand.nextFloat() >= this.chance.getValue(entity, world))
			return;

		if (this.id.equals("random")) {
			List<Enchantment> validEnch = new ArrayList<>();
			for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()) {
				if (enchantment.isCurse() && !allowCurses)
					continue;
				if (enchantment.isTreasureEnchantment() && !allowTreasure)
					continue;

				if (enchantment.canApply(itemStack))
					validEnch.add(enchantment);
			}

			if (validEnch.isEmpty()) {
				Logger.warn("Couldn't find any compatible enchantment for " + itemStack);
				return;
			}

			Enchantment choosenEnch = validEnch.get(world.rand.nextInt(validEnch.size()));
			int level = RandomHelper.getInt(world.rand, choosenEnch.getMinLevel(), choosenEnch.getMaxLevel());
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
				int level = RandomHelper.getInt(world.rand, (int) this.level.getMin(entity, world), (int) this.level.getMax(entity, world));
				itemStack.addEnchantment(enchantment, level);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, level: %s, chance: %s}", id, level, chance);
	}
}

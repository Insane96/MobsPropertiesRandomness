package net.insane96mcp.mobrandomness.events.mobs;

import java.util.Random;

import net.insane96mcp.mobrandomness.lib.Properties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

public class RNGSkeleton {
	public static void TippedArrow(EntityLiving living, float multiplier, Random random) {
		if (!(living instanceof EntitySkeleton))
			return;
		
		if (Properties.Skeleton.arrowsList.length == 0)
			return;
		
		float chance = Properties.Skeleton.arrowChance * multiplier;
		if (random.nextFloat() > chance / 100f)
			return;
		
		int count = Properties.Skeleton.arrowsList.length;
		
		int selected = MathHelper.getInt(random, 0, count - 1);

		String potionId = Properties.Skeleton.arrowsList[selected].split(",")[0];
		int duration = Integer.parseInt(Properties.Skeleton.arrowsList[selected].split(",")[1]) * 20;
		int amplifierMin = Integer.parseInt(Properties.Skeleton.arrowsList[selected].split(",")[2]);
		int amplifierMax = Integer.parseInt(Properties.Skeleton.arrowsList[selected].split(",")[3]);
		
		int amplifier = MathHelper.getInt(random, amplifierMin, amplifierMax - 1);
		
		ItemPotion potion = new ItemPotion();
		
		ItemTippedArrow tippedArrow = new ItemTippedArrow();
		
		ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW);
		NBTTagCompound nbt = itemStack.serializeNBT();
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList customPotions = new NBTTagList();
		NBTTagCompound customPotion = new NBTTagCompound();
		customPotion.setByte("Id", (byte) Potion.getIdFromPotion(Potion.getPotionFromResourceLocation(potionId)));
		customPotion.setInteger("Duration", duration);
		customPotion.setByte("Amplifier", (byte) amplifier);
		customPotions.appendTag(customPotion);
		tag.setTag("CustomPotionEffects", customPotions);
		nbt.setTag("tag", tag);
		
		System.out.println(nbt);
		
		itemStack.deserializeNBT(nbt);
		
		living.setHeldItem(EnumHand.OFF_HAND, itemStack);
	}

}

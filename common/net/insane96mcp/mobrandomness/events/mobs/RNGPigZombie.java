package net.insane96mcp.mobrandomness.events.mobs;

import java.util.Random;

import net.insane96mcp.mobrandomness.lib.Properties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.nbt.NBTTagCompound;

public class RNGPigZombie {
	public static void Aggro(EntityLivingBase living, float multiplier, Random random) {
		if (!(living instanceof EntityPigZombie))
			return;
		
		float chance = Properties.PigZombie.aggroChance * multiplier / 100f;
		if (random.nextFloat() > chance)
			return;
		
		EntityPigZombie pigZombie = (EntityPigZombie) living;
		
		NBTTagCompound tags = new NBTTagCompound();
		pigZombie.writeEntityToNBT(tags);
		tags.setShort("Anger", Short.MAX_VALUE);
		pigZombie.readEntityFromNBT(tags);
	}
}

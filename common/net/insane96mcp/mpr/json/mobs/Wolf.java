package net.insane96mcp.mpr.json.mobs;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.living.AnimalTameEvent;

public class Wolf {
	public static void FixOnTame(AnimalTameEvent event) {
		if (!(event.getAnimal() instanceof EntityWolf))
			return;
		
		EntityWolf wolf = (EntityWolf)event.getAnimal();
		
		event.setCanceled(true);
		
		Random rand = wolf.getRNG();
		
		wolf.setTamedBy(event.getTamer());
		wolf.getNavigator().clearPath();
		wolf.setAttackTarget((EntityLivingBase)null);
		wolf.getAISit().setSitting(true);
		for (int i = 0; i < 7; ++i)
        {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            wolf.world.spawnParticle(EnumParticleTypes.HEART, wolf.posX + (double)(rand.nextFloat() * wolf.width * 2.0F) - (double)wolf.width, wolf.posY + 0.5D + (double)(rand.nextFloat() * wolf.height), wolf.posZ + (double)(rand.nextFloat() * wolf.width * 2.0F) - (double)wolf.width, d0, d1, d2);
        }
		wolf.world.setEntityState(wolf, (byte)7);
	}
}

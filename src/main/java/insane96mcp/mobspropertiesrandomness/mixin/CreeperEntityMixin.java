package insane96mcp.mobspropertiesrandomness.mixin;

import insane96mcp.mobspropertiesrandomness.module.base.feature.MPRBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;

@Mixin(Creeper.class)
public abstract class CreeperEntityMixin {
	//Remove any effect that has duration higher than 1 hour
	@ModifyVariable(at = @At(value = "STORE"), method = "spawnLingeringCloud", ordinal = 0)
	private Collection<MobEffectInstance> changeCollection(Collection<MobEffectInstance> collection) {
		collection.removeIf(mobEffectInstance -> mobEffectInstance.getDuration() > 36000 || mobEffectInstance.isInfiniteDuration());
		return collection;
	}

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadius(F)V"), method = "spawnLingeringCloud", index = 0)
	private float adjustRadius(float radius) {
		if (!MPRBase.isBetterCreeperLingeringActivated())
			return radius;
		Creeper $this = (Creeper)(Object)this;
		CompoundTag compoundNBT = new CompoundTag();
		$this.addAdditionalSaveData(compoundNBT);
		float explosionSize = compoundNBT.getByte("ExplosionRadius");
		explosionSize *= compoundNBT.getBoolean("powered") ? 2 : 1;
		return explosionSize * 2f;
	}

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setWaitTime(I)V"), method = "spawnLingeringCloud", index = 0)
	private int adjustWaitTime(int waitTime) {
		return MPRBase.isBetterCreeperLingeringActivated() ? 0 : waitTime;
	}
}

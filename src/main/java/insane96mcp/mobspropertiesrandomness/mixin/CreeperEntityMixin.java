package insane96mcp.mobspropertiesrandomness.mixin;

import insane96mcp.mobspropertiesrandomness.feature.MPRBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;

@Mixin(Creeper.class)
public abstract class CreeperEntityMixin {
	@Shadow
	private int explosionRadius;

	@Shadow
	public abstract boolean isPowered();

	//Remove from the lingering cloud any effect that has duration higher than 30m
	@ModifyVariable(at = @At(value = "STORE"), method = "spawnLingeringCloud", ordinal = 0)
	private Collection<MobEffectInstance> changeCollection(Collection<MobEffectInstance> collection) {
		if (!MPRBase.isBetterCreeperLingeringEnabled())
			return collection;
		//The collection is the creeper's active effects, so a filtered copy is returned instead of removing from it
		return collection.stream()
				.filter(mobEffectInstance -> mobEffectInstance.getDuration() <= 36000 && !mobEffectInstance.isInfiniteDuration())
				.toList();
	}

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadius(F)V"), method = "spawnLingeringCloud", index = 0)
	private float adjustRadius(float radius) {
		if (!MPRBase.isBetterCreeperLingeringEnabled())
			return radius;
		float explosionSize = this.explosionRadius * (this.isPowered() ? 2 : 1);
		return explosionSize * 2f;
	}

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setWaitTime(I)V"), method = "spawnLingeringCloud", index = 0)
	private int adjustWaitTime(int waitTime) {
		return MPRBase.isBetterCreeperLingeringEnabled() ? 0 : waitTime;
	}
}

package insane96mcp.mobspropertiesrandomness.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperEntityMixin {
	@Inject(at = @At("HEAD"), method = "spawnLingeringCloud()V", cancellable = true)
	private void spawnLingeringCloud(CallbackInfo callbackInfo) {
		Creeper creeper = (Creeper) (Object) this;
		boolean hasNormalEffect = false;
		for (MobEffectInstance effect : creeper.getActiveEffects()) {
			if (effect.showIcon()) {
				hasNormalEffect = true;
				break;
			}
		}
		if (!hasNormalEffect)
			callbackInfo.cancel();
	}
}

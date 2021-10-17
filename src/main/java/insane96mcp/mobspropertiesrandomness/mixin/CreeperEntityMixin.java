package insane96mcp.mobspropertiesrandomness.mixin;

import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.EffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin {
	@Inject(at = @At("HEAD"), method = "spawnLingeringCloud()V", cancellable = true)
	private void spawnLingeringCloud(CallbackInfo callbackInfo) {
		CreeperEntity creeper = (CreeperEntity) (Object) this;
		boolean hasNormalEffect = false;
		for (EffectInstance effect : creeper.getActiveEffects()) {
			if (effect.showIcon()) {
				hasNormalEffect = true;
				break;
			}
		}
		if (!hasNormalEffect)
			callbackInfo.cancel();
	}
}

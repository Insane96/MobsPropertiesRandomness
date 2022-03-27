package insane96mcp.mobspropertiesrandomness.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloud.class)
public class AreaEffectCloudEntityMixin {
	@Inject(at = @At("HEAD"), method = "addEffect", cancellable = true)
	public void getUseDuration(MobEffectInstance effect, CallbackInfo callbackInfo) {
		if (!effect.showIcon())
			callbackInfo.cancel();
	}
}

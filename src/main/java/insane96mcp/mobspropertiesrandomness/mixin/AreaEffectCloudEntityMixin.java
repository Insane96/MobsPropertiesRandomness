package insane96mcp.mobspropertiesrandomness.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.potion.EffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloudEntity.class)
public class AreaEffectCloudEntityMixin {
	@Inject(at = @At("HEAD"), method = "addEffect(Lnet/minecraft/potion/EffectInstance;)V", cancellable = true)
	public void getUseDuration(EffectInstance effect, CallbackInfo callbackInfo) {
		if (!effect.isShowIcon())
			callbackInfo.cancel();
	}
}

package insane96mcp.mobspropertiesrandomness.mixin;

import insane96mcp.mobspropertiesrandomness.integration.TiConstruct;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
	@Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
	private void spawnLingeringCloud(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		Mob $this = (Mob) (Object) this;
		if (ModList.get().isLoaded("tconstruct") && TiConstruct.tiConAttackForMobs($this, entity))
			cir.cancel();
	}
}

package insane96mcp.mobspropertiesrandomness.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DamageSources.class)
public interface DamageSourcesAccessor {
    @Invoker
    DamageSource invokeSource(ResourceKey<DamageType> pDamageTypeKey);
}

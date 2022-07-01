package insane96mcp.mobspropertiesrandomness.integration;

import insane96mcp.mobspropertiesrandomness.module.Modules;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.DoubleSupplier;

public class TiConstruct {
    private static final UUID OFFHAND_DAMAGE_MODIFIER_UUID = UUID.fromString("fd666e50-d2cc-11eb-b8bc-0242ac130003");
    private static final float DEGREE_TO_RADIANS = (float) Math.PI / 180F;
    private static final AttributeModifier ANTI_KNOCKBACK_MODIFIER = new AttributeModifier(TConstruct.MOD_ID + ".anti_knockback", 1f, AttributeModifier.Operation.ADDITION);

    public static boolean tiConAttackForMobs(Mob mob, Entity entity) {
        if (Modules.base.base.ticonAttack && mob.getMainHandItem().getItem() instanceof ModifiableItem) {
            return attackEntity(ToolStack.from(mob.getMainHandItem()), mob, InteractionHand.MAIN_HAND, entity, () -> 1d, false);
        }
        return false;
    }

    /**
     * Same as ToolAttackUtil but:
     *      targetEntity.setDeltaMovement(originalTargetMotion) commented out, as prevents the player from taking knockback
     *      All player attacking parts removed
     */
    public static boolean attackEntity(IToolStackView tool, LivingEntity attackerLiving, InteractionHand hand, Entity targetEntity, DoubleSupplier cooldownFunction, boolean isExtraAttack) {
        EquipmentSlot sourceSlot = Util.getSlotType(hand);
        // broken? give to vanilla
        if (tool.isBroken()) {
            return false;
        }
        // nothing to do? cancel
        // TODO: is it a problem that we return true instead of false when isExtraAttack and the final damage is 0 or we fail to hit? I don't think anywhere clientside uses that
        if (attackerLiving.level.isClientSide || !targetEntity.isAttackable() || targetEntity.skipAttackInteraction(attackerLiving)) {
            return true;
        }

        // fetch relevant entities
        LivingEntity targetLiving = null;
        if (targetEntity instanceof LivingEntity) {
            targetLiving = (LivingEntity) targetEntity;
        } else if (targetEntity instanceof PartEntity) {
            Entity parent = ((PartEntity<?>) targetEntity).getParent();
            if (parent instanceof LivingEntity) {
                targetLiving = (LivingEntity) parent;
            }
        }

        // players base damage (includes tools damage stat)
        // hack for offhand attributes: remove mainhand temporarily, and apply offhand
        float damage = ToolAttackUtil.getAttributeAttackDamage(tool, attackerLiving, sourceSlot);

        // determine cooldown
        float cooldown = (float) cooldownFunction.getAsDouble();
        boolean fullyCharged = cooldown > 0.9f;

        // calculate if it's a critical hit
        // that is, in the air, not blind, targeting living, and not sprinting
        boolean isCritical = !isExtraAttack && fullyCharged && attackerLiving.fallDistance > 0.0F && !attackerLiving.isOnGround() && !attackerLiving.onClimbable()
                && !attackerLiving.isInWater() && !attackerLiving.hasEffect(MobEffects.BLINDNESS)
                && !attackerLiving.isPassenger() && targetLiving != null && !attackerLiving.isSprinting();

        // shared context for all modifier hooks
        ToolAttackContext context = new ToolAttackContext(attackerLiving, null, hand, sourceSlot, targetEntity, targetLiving, isCritical, cooldown, isExtraAttack);

        // calculate actual damage
        // boost damage from traits
        float baseDamage = damage;
        List<ModifierEntry> modifiers = tool.getModifierList();
        for (ModifierEntry entry : modifiers) {
            damage = entry.getModifier().getEntityDamage(tool, entry.getLevel(), context, baseDamage, damage);
        }

        // no damage? do nothing
        if (damage <= 0) {
            return !isExtraAttack;
        }

        // forge patches in the knockback attribute for use on players
        // vanilla halves the knockback attribute later, we half it in all our hooks, so halving the attribute makes it equivelent
        float knockback = (float) attackerLiving.getAttributeValue(Attributes.ATTACK_KNOCKBACK) / 2f;
        // vanilla applies 0.4 knockback to living via the attack hook
        if (targetLiving != null) {
            knockback += 0.4f;
        }
        // if sprinting, deal bonus knockback
        SoundEvent sound;
        if (attackerLiving.isSprinting() && fullyCharged) {
            sound = SoundEvents.PLAYER_ATTACK_KNOCKBACK;
            knockback += 0.5f;
        } else if (fullyCharged) {
            sound = SoundEvents.PLAYER_ATTACK_STRONG;
        } else {
            sound = SoundEvents.PLAYER_ATTACK_WEAK;
        }

        // knockback moved lower

        // apply cutoff and cooldown, store if damage was above base for magic particles
        boolean isMagic = damage > baseDamage;
        if (cooldown < 1) {
            damage *= (0.2f + cooldown * cooldown * 0.8f);
        }

        // track original health and motion before attack
        Vec3 originalTargetMotion = targetEntity.getDeltaMovement();
        float oldHealth = 0.0F;
        if (targetLiving != null) {
            oldHealth = targetLiving.getHealth();
        }

        // apply modifier knockback and special effects
        float baseKnockback = knockback;
        for (ModifierEntry entry : modifiers) {
            knockback = entry.getModifier().beforeEntityHit(tool, entry.getLevel(), context, damage, baseKnockback, knockback);
        }

        // set hand for proper looting context
        ModifierLootingHandler.setLootingSlot(attackerLiving, sourceSlot);

        // prevent knockback if needed
        Optional<AttributeInstance> knockbackModifier = getKnockbackAttribute(targetLiving);
        // if knockback is below the vanilla amount, we need to prevent knockback, the remainder will be applied later
        boolean canceledKnockback = false;
        if (knockback < 0.4f) {
            canceledKnockback = true;
            knockbackModifier.ifPresent(TiConstruct::disableKnockback);
        } else if (targetLiving != null) {
            // we will apply 0.4 of the knockback in the attack hook, need to apply the remainder ourselves
            knockback -= 0.4f;
        }

        ///////////////////
        // actual attack //
        ///////////////////

        boolean didHit;
        if (isExtraAttack) {
            didHit = ToolAttackUtil.dealDefaultDamage(attackerLiving, targetEntity, damage);
        } else {
            didHit = tool.getDefinition().getData().getAttack().dealDamage(tool, context, damage);
        }

        // reset hand to make sure we don't mess with vanilla tools
        ModifierLootingHandler.setLootingSlot(attackerLiving, EquipmentSlot.MAINHAND);

        // reset knockback if needed
        if (canceledKnockback) {
            knockbackModifier.ifPresent(TiConstruct::enableKnockback);
        }

        // if we failed to hit, fire failure hooks
        if (!didHit) {
            if (!isExtraAttack) {
                attackerLiving.level.playSound(null, attackerLiving.getX(), attackerLiving.getY(), attackerLiving.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, attackerLiving.getSoundSource(), 1.0F, 1.0F);
            }
            // alert modifiers nothing was hit, mainly used for fiery
            for (ModifierEntry entry : modifiers) {
                entry.getModifier().failedEntityHit(tool, entry.getLevel(), context);
            }

            return !isExtraAttack;
        }

        // determine damage actually dealt
        float damageDealt = damage;
        if (targetLiving != null) {
            damageDealt = oldHealth - targetLiving.getHealth();
        }

        // apply knockback
        if (knockback > 0) {
            if (targetLiving != null) {
                targetLiving.knockback(knockback, Mth.sin(attackerLiving.getYRot() * DEGREE_TO_RADIANS), -Mth.cos(attackerLiving.getYRot() * DEGREE_TO_RADIANS));
            } else {
                targetEntity.push(-Mth.sin(attackerLiving.getYRot() * DEGREE_TO_RADIANS) * knockback, 0.1d, Mth.cos(attackerLiving.getYRot() * DEGREE_TO_RADIANS) * knockback);
            }
            attackerLiving.setDeltaMovement(attackerLiving.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            attackerLiving.setSprinting(false);
        }

        // apply velocity change to players if needed
        if (targetEntity.hurtMarked && targetEntity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(targetEntity));
            targetEntity.hurtMarked = false;
            // disabled this as prevents players from taking knockback correctly when damage is applied twice (e.g. piercing)
            //targetEntity.setDeltaMovement(originalTargetMotion);
        }

        // deal attacker thorns damage
        attackerLiving.setLastHurtMob(targetEntity);
        if (targetLiving != null) {
            EnchantmentHelper.doPostHurtEffects(targetLiving, attackerLiving);
        }

        // apply modifier effects
        int durabilityLost = targetLiving != null ? 1 : 0;
        for (ModifierEntry entry : modifiers) {
            durabilityLost += entry.getModifier().afterEntityHit(tool, entry.getLevel(), context, damageDealt);
        }

        // hurt resistance adjustment for high speed weapons
        float speed = tool.getStats().get(ToolStats.ATTACK_SPEED);
        int time = Math.round(20f / speed);
        if (time < targetEntity.invulnerableTime) {
            targetEntity.invulnerableTime = (targetEntity.invulnerableTime + time) / 2;
        }

        // damage the tool
        if (!tool.hasTag(TinkerTags.Items.MELEE_PRIMARY)) {
            durabilityLost *= 2;
        }
        ToolDamageUtil.damageAnimated(tool, durabilityLost, attackerLiving);

        return true;
    }

    /**
     * Gets the knockback attribute instance if the modifier is not already present
     */
    private static Optional<AttributeInstance> getKnockbackAttribute(@Nullable LivingEntity living) {
        return Optional.ofNullable(living)
                .map(e -> e.getAttribute(Attributes.KNOCKBACK_RESISTANCE))
                .filter(attribute -> !attribute.hasModifier(ANTI_KNOCKBACK_MODIFIER));
    }

    /**
     * Enable the anti-knockback modifier
     */
    private static void disableKnockback(AttributeInstance instance) {
        instance.addTransientModifier(ANTI_KNOCKBACK_MODIFIER);
    }

    /**
     * Disables the anti knockback modifier
     */
    private static void enableKnockback(AttributeInstance instance) {
        instance.removeModifier(ANTI_KNOCKBACK_MODIFIER);
    }
}

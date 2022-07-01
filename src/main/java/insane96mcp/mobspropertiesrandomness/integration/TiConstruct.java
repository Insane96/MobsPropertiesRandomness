package insane96mcp.mobspropertiesrandomness.integration;

import insane96mcp.mobspropertiesrandomness.module.Modules;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TiConstruct {
    public static boolean manageTiConAttackForMobs(Mob mob, Entity entity) {
        if (Modules.base.base.ticonAttack && mob.getMainHandItem().getItem() instanceof ModifiableItem) {
            return ToolAttackUtil.attackEntity(ToolStack.from(mob.getMainHandItem()), mob, InteractionHand.MAIN_HAND, entity, () -> 1d, false);
        }
        return false;
    }
}

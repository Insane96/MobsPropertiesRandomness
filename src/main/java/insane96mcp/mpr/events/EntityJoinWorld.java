package insane96mcp.mpr.events;

import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.json.Mob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
public class EntityJoinWorld {

    @SubscribeEvent
    public static void eventEntityJoinWorld(EntityJoinWorldEvent event) {

        Mob.apply(event);

    }
}

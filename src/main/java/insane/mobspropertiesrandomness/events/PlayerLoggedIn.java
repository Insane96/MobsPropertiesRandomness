package insane.mobspropertiesrandomness.events;

import insane.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane.mobspropertiesrandomness.json.JsonMob;
import insane.mobspropertiesrandomness.setup.ModConfig;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
public class PlayerLoggedIn {

	@SubscribeEvent
	public static void EventPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (ModConfig.General.debug.get())
			event.getPlayer().sendMessage(new StringTextComponent("[MobsPropertiesRandomness] Reloading JSONs from 'json' & 'group' folders"));
		String loadOutput = /*JsonGroup.LoadGroups() && */JsonMob.loadJsons();

		if (loadOutput != "") {
			event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "[MobsPropertiesRandomness] " + loadOutput));
		} else if (ModConfig.General.debug.get())
			event.getPlayer().sendMessage(new StringTextComponent("[MobsPropertiesRandomness] Reloading successfully completed"));
	}
}

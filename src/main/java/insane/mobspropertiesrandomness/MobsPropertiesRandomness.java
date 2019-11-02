package insane.mobspropertiesrandomness;

import insane.mobspropertiesrandomness.exceptions.InvalidJsonException;
import insane.mobspropertiesrandomness.setup.Logger;
import insane.mobspropertiesrandomness.setup.ModConfig;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.file.Paths;

@Mod(MobsPropertiesRandomness.MOD_ID)
public class MobsPropertiesRandomness
{
	public static final String MOD_ID = "mobspropertiesrandomness";

	public MobsPropertiesRandomness() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
		ModConfig.init(Paths.get("config", MOD_ID + ".toml"));

		Logger.Init(MOD_ID + ".log");

		try {
			throw new InvalidJsonException("Entry %s is not valid", Blocks.ACACIA_BUTTON);

		}
		catch (InvalidJsonException e){
			Logger.Error("Failed to parse file with name bla bla");
			Logger.Error(e.toString());
		}
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client
	}
}

package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.utils.FileUtils;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MPRReloadListener extends JsonReloadListener {
	public static final List<MPRMob> MPR_MOBS = new ArrayList<>();

	public static final MPRReloadListener INSTANCE;
	public static final Gson GSON = new GsonBuilder()
			.disableHtmlEscaping()
			.create();

	public MPRReloadListener() {
		super(GSON, "mpr_json");
	}

	static {
		INSTANCE = new MPRReloadListener();
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, IResourceManager iResourceManager, IProfiler iProfiler) {
		File jsonFolder = new File(MobsPropertiesRandomness.CONFIG_FOLDER + "/json");
		if (!jsonFolder.exists())
			jsonFolder.mkdir();

		MPR_MOBS.clear();

		boolean correctlyReloaded = true;
		Gson gson = new Gson();

		ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(jsonFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;

			try {
				Logger.debug("Reading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				MPRMob mob = gson.fromJson(fileReader, MPRMob.class);
				Logger.debug(mob.toString());
				mob.validate(file);
				MPR_MOBS.add(mob);
			} catch (Exception e) {
				correctlyReloaded = false;
				//Logger.error("Failed to parse file with name " + file.getName());
				Logger.error(e.toString());
				e.printStackTrace();
			}
		}

		if (correctlyReloaded)
			Logger.info("Correctly reloaded all JSONs");
		else
			Logger.info("Reloaded all JSONs with error(s)");
	}
}

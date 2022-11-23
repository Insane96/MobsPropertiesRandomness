package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.FileUtils;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MPRMobReloadListener extends SimplePreparableReloadListener<Void> {
	public static final List<MPRMob> MPR_MOBS = new ArrayList<>();

	public static final MPRMobReloadListener INSTANCE;

	public static File mobsFolder;

	public MPRMobReloadListener() {
		super();
	}

	@Override
	protected @NotNull Void prepare(@NotNull ResourceManager iResourceManager, @NotNull ProfilerFiller iProfiler) {
		return null;
	}

	static {
		INSTANCE = new MPRMobReloadListener();
	}

	@Override
	protected void apply(@NotNull Void objectIn, @NotNull ResourceManager iResourceManager, @NotNull ProfilerFiller iProfiler) {
		Logger.info("Reloading Mobs");
		MPR_MOBS.clear();

		Gson gson = new Gson();

		ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(mobsFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_' or comma '.'
			if (file.getName().startsWith("_") || file.getName().startsWith("."))
				continue;

			try {
				FileReader fileReader = new FileReader(file);
				MPRMob mob = gson.fromJson(fileReader, MPRMob.class);
				mob.validate();
				MPR_MOBS.add(mob);
			}
			catch (JsonValidationException e) {
				Logger.error("Validation error loading Mob %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
			}
			catch (JsonSyntaxException e) {
				Logger.error("Parsing error loading Mob %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
			}
			catch (Exception e) {
				Logger.error("Failed loading Mob %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
			}
		}

		Logger.info("Loaded %s Mobs", MPR_MOBS.size());
	}
}

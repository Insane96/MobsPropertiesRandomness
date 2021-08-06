package insane96mcp.mobspropertiesrandomness.utils;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {
	public static ArrayList<File> ListFilesForFolder(final File folder) {
		ArrayList<File> list = new ArrayList<>();
		if (folder.listFiles() == null)
			return list;

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				list.addAll(ListFilesForFolder(fileEntry));
			} else {
				list.add(fileEntry);
			}
		}
		return list;
	}
}

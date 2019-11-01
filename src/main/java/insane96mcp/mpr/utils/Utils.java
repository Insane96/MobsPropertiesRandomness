package insane96mcp.mpr.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Utils {
	public static ArrayList<File> listFilesForFolder(final File folder) {
	    ArrayList<File> list = new ArrayList<>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	listFilesForFolder(fileEntry);
	        } else {
	            list.add(fileEntry);
	        }
	    }
	    return list;
	}

	public static int getRandomInt(Random rand, int min, int max) {
		int bound = max - min;
		if (bound == 0)
			return min;
		return rand.nextInt(bound) + min;
	}

	public static float getFloat(Random rand, float min, float max) {
		return rand.nextFloat() * (max - min) + min;
	}

	public static double getFloat(Random rand, double min, double max) {
		return rand.nextFloat() * (max - min) + min;
	}
}

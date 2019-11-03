package insane.mobspropertiesrandomness.utils;

import java.util.Random;

public class RandUtils {
	public static int getInt(Random rand, int min, int max) {
		if (min >= max)
			return min;
		return rand.nextInt(max - min) + min;
	}
}

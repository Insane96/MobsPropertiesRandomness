package insane96mcp.mobspropertiesrandomness.util;

public class MathHelper {
	public static double round(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}

	public static float round(float value, int places) {
		double scale = Math.pow(10, places);
		return (float) (Math.round(value * scale) / scale);
	}
}

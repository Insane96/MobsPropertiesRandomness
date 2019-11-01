package insane96mcp.mpr.json.utils;

import java.util.List;
import java.util.Random;

public class WeightedRandom {

	public static int getTotalWeight(List<? extends WeightedRandom.Item> weightedItems) {
		int totalWeight = 0;

		for (int i = 0; i < weightedItems.size(); i++) {
			WeightedRandom.Item weightedItem = weightedItems.get(i);
			totalWeight += weightedItem.itemWeight;
		}

		return totalWeight;
	}

	public static <T extends WeightedRandom.Item> T getRandomItem(Random rand, List<T> weightedItems, int totalWeight) {
		if (totalWeight <= 0) {
			throw new IllegalArgumentException();
		} else {
			int i = rand.nextInt(totalWeight);
			return getRandomItem(weightedItems, i);
		}
	}

	public static <T extends WeightedRandom.Item> T getRandomItem(List<T> weightedItems, int weight) {
		for (int i = 0; i < weightedItems.size(); i++){
			T weightedItem = weightedItems.get(i);
			weight -= weightedItem.itemWeight;
			if (weight < 0) {
				return weightedItem;
			}
		}

		return null;
	}

	public static <T extends WeightedRandom.Item> T getRandomItem(Random rand, List<T> weightedItems) {
		return getRandomItem(rand, weightedItems, getTotalWeight(weightedItems));
	}

	public static class Item {
		public int itemWeight;

		public Item(int itemWeight) {
			this.itemWeight = itemWeight;
		}
	}
}

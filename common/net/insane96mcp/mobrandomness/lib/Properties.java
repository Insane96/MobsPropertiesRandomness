package net.insane96mcp.mobrandomness.lib;

public class Properties {
	public static boolean difficultyWise;
	public static float difficultyMultiplierEasy;
	public static float difficultyMultiplierNormal;
	public static float difficultyMultiplierHard;
	
	public static boolean localDifficultyWise;
	public static float localDifficultyMultiplier;
	
	public static void Init() {
		difficultyWise = Config.LoadBoolProperty("__general", "affected_by_difficulty", "If true, where applicable, values will be multiplied by the multiplier of the current difficulty", true);
		difficultyMultiplierEasy = Config.LoadFloatProperty("__general", "difficulty_multiplier_easy", "Multiplier on Easy Difficulty (only works if 'affected_by_difficulty' is true). Negative values may have strange effects.", 0.5f);
		difficultyMultiplierNormal = Config.LoadFloatProperty("__general", "difficulty_multiplier_normal", "Multiplier on Normal Difficulty (only works if 'affected_by_difficulty' is true). Negative values may have strange effects.", 1.0f);
		difficultyMultiplierHard = Config.LoadFloatProperty("__general", "difficulty_multiplier_hard", "Multiplier on Hard Difficulty (only works if 'affected_by_difficulty' is true). Negative values may have strange effects.", 2.0f);
		
		localDifficultyWise = Config.LoadBoolProperty("__general", "affected_by_local_difficulty", "If true, where applicable, values will be multiplied by the local difficulty. Values will float from 0.75–1.5 on Easy, 1.5–4.0 on Normal, and 2.25–6.75 on Hard. That's why the 'local_difficulty_multiplier'. To increase or decrease the \"Regional difficulty\".\nTo know more: https://minecraft.gamepedia.com/Difficulty#Regional_difficulty", false);
		localDifficultyMultiplier = Config.LoadFloatProperty("__general", "local_difficulty_multiplier", "Local difficulty will be multiplied by this value before used to multiply the mod's stats (only if 'affected_by_local_difficulty' is true). Negative values may have strange effects.", 1.0f);
		
		Stats.Init();
		Equipment.Init();
		
		Creeper.Init();
		Ghast.Init();
		Skeleton.Init();
		PigZombie.Init();
	}
	
	public static class Stats {
		
		public static boolean valuesAsPercentage;
		public static String[] health;
		public static String[] movementSpeed;
		public static String[] followRange;
		public static String[] attackDamage;
		public static String[] knockbackResistance;
		
		public static String[] potionEffects;
		
		public static void Init() {
			valuesAsPercentage = Config.LoadBoolProperty("_stats", "_values_as_percentage", "If true, values in '_stats' will be percentages and not actual values. It is highly recommended to have this set to 'true'", true);
			
			health = Config.LoadStringListProperty("_stats", "modifier_health", "Write here, for each line, every mob that must have modified health.\nFormat is 'mob,min_health_increase,max_health_increase'.\nE.g. 'minecraft:zombie,15.0,50.0' will make zombies have from 15% to 50% (multiplied by difficulty multiplier) more health, if 'values_as_percentage' is true, or from 15 to 50 more health if is false", new String[] {});
			
			movementSpeed = Config.LoadStringListProperty("_stats", "modifier_movement_speed", "Write here, for each line, every mob that must have modified movement_speed. (It's highly recommended to have 'values_as_percentage' on true for this one)\nFormat is 'mob,min_movement_speed_increase,max_movement_speed_increase'.\nE.g. 'minecraft:zombie,15.0,50.0' will make zombies have from 15% to 50% (multiplied by difficulty multiplier) more speed, if 'values_as_percentage' is true, or from 15 to 50 more speed if is false", new String[] {});
			
			followRange = Config.LoadStringListProperty("_stats", "modifier_follow_range", "Write here, for each line, every mob that must have modified follow range (doesn't work with passive mobs).\nFormat is 'mob,min_follow_range_increase,max_follow_range_increase'.\nE.g. 'minecraft:zombie,15.0,50.0' will make zombies have from 15% to 50% (multiplied by difficulty multiplier) more follow range, if 'values_as_percentage' is true, or from 15 to 50 more follow range if is false", new String[] {});
			
			attackDamage = Config.LoadStringListProperty("_stats", "modifier_attack_damage", "Write here, for each line, every mob that must have modified attack damage (doesn't work with passive mobs).\nFormat is 'mob,min_attack_damage_increase,max_attack_damage_increase'.\nE.g. 'minecraft:zombie,15.0,50.0' will make zombies deal from 15% to 50% (multiplied by difficulty multiplier) more damage, if 'values_as_percentage' is true, or from 15 to 50 more damage if is false.\n", new String[] {});
			
			knockbackResistance = Config.LoadStringListProperty("_stats", "modifier_knockback_resistance", "Write here, for each line, every mob that must have modified knockback resistance.\nFormat is 'mob,min_knockback_resistance_increase,max_knockback_resistance_increase'.\nE.g. 'minecraft:zombie,15.0,50.0' will make zombies have from 15% to 50% (multiplied by difficulty multiplier) chance to prevent knockback, if 'values_as_percentage' is true, otherwise from 15 to 50 more knockback resistance if is false.\n", new String[] {});
			
			potionEffects = Config.LoadStringListProperty("_stats", "potion_effects", "Write here, for each line, every mob that can have potion effects applied.\nFormat is 'mob,chance,potion_id,min_amplifier,max_amplifier,ambient_particles,show_particles'.\nE.g. 'minecraft:zombie,15.0,minecraft:jump_boost,0,3,true,false' will make zombies have 15% chance (multiplied by the difficulty multiplier) to have applied the jump boost effect from level 1 (0) to level 4 (3) and make particles be sightly transparent like beacon effects.\nMore than a potion effect can be added on the same mob type, just put it in a new line.", new String[] {});
		}
	}
	
	public static class Equipment {
		public static String[] handEquipment;
		public static String[] offHandEquipment;
		public static String[] headEquipment;
		public static String[] chestEquipment;
		public static String[] legsEquipment;
		public static String[] feetEquipment;
		
		public static void Init() {
			handEquipment = Config.LoadStringListProperty("_equipment", "hand_list", "Write here, for each line, every mob and the items that they can spawn with.\nFormat is mob,chance,item1,item2,etc.\nIf the mob has already an item, it will be overwritten\nE.g. 'minecraft:zombie,10.0,minecraft:stick,minecraft:diamond_sword' will make zombies have 10% chance (increased by difficulty if 'affected_by_difficulty' is true) to carry a stick or a diamond sword.", new String[] {});
			
			offHandEquipment = Config.LoadStringListProperty("_equipment", "off_hand_list", "Write here, for each line, every mob and the items that they can spawn with on the off hand.\nFormat is mob,chance,item1,item2,etc.\nIf the mob has already an item, it will be overwritten\nE.g. 'minecraft:zombie,10.0,minecraft:stick,minecraft:diamond_sword' will make zombies have 10% chance (increased by difficulty if 'affected_by_difficulty' is true) to carry a stick or a diamond sword.", new String[] {});
			
			headEquipment = Config.LoadStringListProperty("_equipment", "head_list", "Write here, for each line, every mob and the helmets that they can spawn with.\nFormat is mob,chance,item1,item2,etc.\nIf the mob has already an helmet, it will be overwritten\nE.g. 'minecraft:zombie,10.0,minecraft:diamond_helmet,botania:terrasteel_helmet' will make zombies have 10% chance (increased by difficulty if 'affected_by_difficulty' is true) to spawn with a diamond helmet or a terrasteel helmet.", new String[] {});
			
			chestEquipment = Config.LoadStringListProperty("_equipment", "chest_list", "Write here, for each line, every mob and the chestplates that they can spawn with.\nFormat is mob,chance,item1,item2,etc.\nIf the mob has already a chestplate, it will be overwritten\nE.g. 'minecraft:zombie,10.0,minecraft:diamond_chestplate,vulcanite:vulcanite_chestplate' will make zombies have 10% chance (increased by difficulty if 'affected_by_difficulty' is true) to spawn with a diamond chestplate or a vulcanite chestplate.", new String[] {});
			
			legsEquipment = Config.LoadStringListProperty("_equipment", "legs_list", "Write here, for each line, every mob and the leggings that they can spawn with.\nFormat is mob,chance,item1,item2,etc.\nIf the mob has already a leggings, it will be overwritten\nE.g. 'minecraft:zombie,10.0,minecraft:diamond_leggings,carbonado:carbonado_leggings' will make zombies have 10% chance (increased by difficulty if 'affected_by_difficulty' is true) to spawn with a diamond leggings or a carbonado leggings.", new String[] {});
			
			feetEquipment = Config.LoadStringListProperty("_equipment", "feet_list", "Write here, for each line, every mob and the boots that they can spawn with.\nFormat is mob,chance,item1,item2,etc.\nIf the mob has already a boots, it will be overwritten\nE.g. 'minecraft:zombie,10.0,minecraft:diamond_boots,vulcanite:vulcanite_boots' will make zombies have 10% chance (increased by difficulty if 'affected_by_difficulty' is true) to spawn with a diamond boots or a vulcanite boots.", new String[] {});
		}
	}
	
	public static class Creeper {
		public static int fuseMin;
		public static int fuseMax;

		public static int explosionRadiusMin;
		public static int explosionRadiusMax;
		
		public static float poweredChance;
		
		public static void Init() {
			fuseMin = Config.LoadIntProperty("creeper", "fuse_min", "Defines the minimum fuse time (time that creepers hiss before exploding) that a creeper can spawn with. Is not affected by difficulty multiplier (Vanilla: 30)", 30);
			fuseMax = Config.LoadIntProperty("creeper", "fuse_max", "Defines the maximum fuse time (time that creepers hiss before exploding) that a creeper can spawn with. Is not affected by difficulty multiplier (Vanilla: 30)", 30);
			
			explosionRadiusMin = Config.LoadIntProperty("creeper", "explosion_radius_min", "Defines the minimum explosion power a creeper can spawn with. Is not affected by difficulty multiplier (Vanilla: 3)", 3);
			explosionRadiusMax = Config.LoadIntProperty("creeper", "explosion_radius_max", "Defines the maximum explosion power a creeper can spawn with. Is not affected by difficulty multiplier (Vanilla: 3)", 3);
			
			poweredChance = Config.LoadFloatProperty("creeper", "powered_chance", "Chance for a charged creeper to spawn naturally", 0.0f);
		}
	}
	
	public static class Ghast{
		public static int explosionPowerMin;
		public static int explosionPowerMax;
		
		public static void Init() {
			explosionPowerMin = Config.LoadIntProperty("ghast", "min_explosion_power", "Defines the minimum explosion power of ghast's fireballs that a ghast can spawn with. Is not affected by difficulty multiplier (Vanilla: 1)", 1);
			explosionPowerMax = Config.LoadIntProperty("ghast", "max_explosion_power", "Defines the maximum explosion power of ghast's fireballs that a ghast can spawn with. Is not affected by difficulty multiplier (Vanilla: 1)", 1);
		}
	}
	
	public static class Skeleton{
		public static String[] arrowsList;
		public static float arrowChance;
		
		public static void Init() {
			arrowsList = Config.LoadStringListProperty("skeleton", "arrows_list", "Write here, for each line, the potion effect for the tipped arrows that skeletons can spawn with.\nFormat is potion,duration,min_aplifier,max_aplifier.\nE.g. 'minecraft:slowness,10,0,2' will make skeletons have 'arrow_chance' chance to spawn with a slowness tipped arrow.\nOverwrites 'off_hand_equipment'", new String[] {});
			arrowChance = Config.LoadFloatProperty("skeleton", "arrow_chance", "Chance to give a skeleton a tipped arrow. Is affected by difficulty multiplier", 0.0f);
		}
	}
	
	public static class PigZombie {
		public static float aggroChance;
		
		public static void Init() {
			aggroChance = Config.LoadFloatProperty("pig_zombie", "aggro_chance", "Chance for a pigman to spawn permanently aggroed. (Actually is not possible to make them permanently aggroed, so in reality they are aggroed for 30 minutes since they spawn). This is affected by difficulty multiplier", 0.0f);
		}
	}
}
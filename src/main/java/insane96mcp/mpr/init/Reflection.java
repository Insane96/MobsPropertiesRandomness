package insane96mcp.mpr.init;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {
	public static Field AreaEffectCloud_effects;
		
	public static void init() {
		try {
			AreaEffectCloud_effects = ObfuscationReflectionHelper.findField(AreaEffectCloudEntity.class, "field_184503_f");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void set(Field field, Object object, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Object get(Field field, Object object) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object invoke(Method method, Object object, Object... params) {
		try {
			return method.invoke(object, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}

package net.insane96mcp.mpr.lib;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Reflection {
	public static Field EntityAreaEffectCloud_effects;
		
	public static void Init() {
		try {
			EntityAreaEffectCloud_effects = ReflectionHelper.findField(EntityAreaEffectCloud.class, "effects", "field_184503_f");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void Set(Field field, Object object, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Object Get(Field field, Object object) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object Invoke(Method method, Object object, Object... params) {
		try {
			return method.invoke(object, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}

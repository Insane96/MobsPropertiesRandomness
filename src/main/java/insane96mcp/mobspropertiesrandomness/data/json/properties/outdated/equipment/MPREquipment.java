package insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.equipment;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class MPREquipment implements IMPRObject {

	public MPRSlot head;
	public MPRSlot chest;
	public MPRSlot legs;
	public MPRSlot feet;
	@SerializedName("main_hand")
	public MPRSlot mainHand;
	@SerializedName("off_hand")
	public MPRSlot offHand;
	
	@Override
	public void validate() throws JsonValidationException {
		if (head != null)
			head.validate();
		if (chest != null)
			chest.validate();
		if (legs != null)
			legs.validate();
		if (feet != null)
			feet.validate();
		if (mainHand != null)
			mainHand.validate();
		if (offHand != null)
			offHand.validate();
	}

	public void apply(LivingEntity entity) {
		applyEquipmentToSlot(entity, this.head, EquipmentSlot.HEAD);
		applyEquipmentToSlot(entity, this.chest, EquipmentSlot.CHEST);
		applyEquipmentToSlot(entity, this.legs, EquipmentSlot.LEGS);
		applyEquipmentToSlot(entity, this.feet, EquipmentSlot.FEET);
		applyEquipmentToSlot(entity, this.mainHand, EquipmentSlot.MAINHAND);
		applyEquipmentToSlot(entity, this.offHand, EquipmentSlot.OFFHAND);
	}

	private void applyEquipmentToSlot(LivingEntity entity, MPRSlot slot, EquipmentSlot equipmentSlotType) {

	}
}

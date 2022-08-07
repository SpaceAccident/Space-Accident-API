package buildcraft.api.transport;

import java.util.Locale;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum PipeWire {
	RED,
	BLUE,
	GREEN,
	YELLOW;
	
	public static Item item;
	public static final PipeWire[] VALUES = values();
	
	private PipeWire() {
	}
	
	public PipeWire reverse() {
		switch (this) {
			case RED:
				return YELLOW;
			case BLUE:
				return GREEN;
			case GREEN:
				return BLUE;
			default:
				return RED;
		}
	}
	
	public String getTag() {
		return this.name().toLowerCase(Locale.ENGLISH) + "PipeWire";
	}
	
	public String getColor() {
		String name = this.toString().toLowerCase(Locale.ENGLISH);
		char first = Character.toUpperCase(name.charAt(0));
		return first + name.substring(1);
	}
	
	public ItemStack getStack() {
		return this.getStack(1);
	}
	
	public ItemStack getStack(int qty) {
		return item == null ? null : new ItemStack(item, qty, this.ordinal());
	}
	
	public boolean isPipeWire(ItemStack stack) {
		if (stack == null) {
			return false;
		} else if (stack.getItem() != item) {
			return false;
		} else {
			return stack.getItemDamage() == this.ordinal();
		}
	}
	
	public static PipeWire fromOrdinal(int ordinal) {
		return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : RED;
	}
}

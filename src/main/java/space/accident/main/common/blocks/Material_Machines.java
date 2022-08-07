package space.accident.main.common.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class Material_Machines extends Material {
	public Material_Machines() {
		super(MapColor.ironColor);
		setRequiresTool();
		setImmovableMobility();
		setAdventureModeExempt();
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}
}

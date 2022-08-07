package space.accident.main.mixinplugin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.Arrays;
import java.util.List;

import static space.accident.main.mixinplugin.Mixin.Side.SERVER;

public enum Mixin {

	Ic2DisabledRecipes_Mixin("Ic2DisabledCraftingRecipes_Mixin", TargetedMod.IC2),
	Ic2DisabledNEI_Mixin("Ic2DisabledNEI_Mixin", TargetedMod.IC2),
	VanilaDisabledFurnaceRecipes_Mixin("VanilaDisabledFurnaceRecipes_Mixin", TargetedMod.VANILLA),
	;
	public final String mixinClass;
	public final List<TargetedMod> targetedMods;
	private final Side side;
	
	Mixin(String mixinClass, Side side, TargetedMod... targetedMods) {
		this.mixinClass = mixinClass;
		this.targetedMods = Arrays.asList(targetedMods);
		this.side = side;
	}
	
	Mixin(String mixinClass, TargetedMod... targetedMods) {
		this.mixinClass = mixinClass;
		this.targetedMods = Arrays.asList(targetedMods);
		this.side = Side.BOTH;
	}
	
	public boolean shouldLoad(List<TargetedMod> loadedMods) {
		return (side == Side.BOTH || side == SERVER && FMLLaunchHandler.side().isServer() || side == Side.CLIENT && FMLLaunchHandler.side().isClient()) && loadedMods.containsAll(targetedMods);
	}
	
	enum Side {
		BOTH,
		CLIENT,
		SERVER;
	}
}


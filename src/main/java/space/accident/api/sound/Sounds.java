package space.accident.api.sound;

import space.accident.main.IntegrationConstants;

import java.util.Locale;

import static space.accident.api.API.sSoundList;

public class Sounds {
	
	private static final String ic2Path = IntegrationConstants.IC2.toLowerCase(Locale.ENGLISH) + ":";
	
	
	public static final int
			BREAK = 0,
			ANVIL_USE = 1,
			ANVIL_BREAK = 2,
			CLICK = 3,
			FIZZ = 4,
			EXPLODE = 5,
			FIRE_IGNITE = 6,
			WRENCH = 100,
			RUBBER_TRAMPOLINE = 101,
			PAINTER = 102,
			BATTERY_USE = 103,
			CHAINSAW_ONE = 104,
			CHAINSAW_TWO = 105,
			DRILL_SOFT = 106,
			DRILL_HARD = 107,
			OD_SCANNER = 108,
			CUTTERS = 109;
	
	
	public static void registerSounds() {
		sSoundList.put(0, "random.break");
		sSoundList.put(1, "random.anvil_use");
		sSoundList.put(2, "random.anvil_break");
		sSoundList.put(3, "random.click");
		sSoundList.put(4, "random.fizz");
		sSoundList.put(5, "random.explode");
		sSoundList.put(6, "fire.ignite");
		
		sSoundList.put(WRENCH, ic2Path + "tools.Wrench");
		sSoundList.put(RUBBER_TRAMPOLINE, ic2Path + "tools.RubberTrampoline");
		sSoundList.put(PAINTER, ic2Path + "tools.Painter");
		sSoundList.put(BATTERY_USE, ic2Path + "tools.BatteryUse");
		sSoundList.put(CHAINSAW_ONE, ic2Path + "tools.chainsaw.ChainsawUseOne");
		sSoundList.put(CHAINSAW_TWO, ic2Path + "tools.chainsaw.ChainsawUseTwo");
		sSoundList.put(DRILL_SOFT, ic2Path + "tools.drill.DrillSoft");
		sSoundList.put(DRILL_HARD, ic2Path + "tools.drill.DrillHard");
		sSoundList.put(OD_SCANNER, ic2Path + "tools.ODScanner");
		sSoundList.put(CUTTERS, ic2Path + "tools.InsulationCutters");
		
		sSoundList.put(200, ic2Path + "machines.ExtractorOp");
		sSoundList.put(201, ic2Path + "machines.MaceratorOp");
		sSoundList.put(202, ic2Path + "machines.InductionLoop");
		sSoundList.put(203, ic2Path + "machines.CompressorOp");
		sSoundList.put(204, ic2Path + "machines.RecyclerOp");
		sSoundList.put(205, ic2Path + "machines.MinerOp");
		sSoundList.put(206, ic2Path + "machines.PumpOp");
		sSoundList.put(207, ic2Path + "machines.ElectroFurnaceLoop");
		sSoundList.put(208, ic2Path + "machines.InductionLoop");
		sSoundList.put(209, ic2Path + "machines.MachineOverload");
		sSoundList.put(210, ic2Path + "machines.InterruptOne");
		sSoundList.put(211, ic2Path + "machines.KaChing");
		sSoundList.put(212, ic2Path + "machines.MagnetizerLoop");
	}
}

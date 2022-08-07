package space.accident.api.metatileentity.implementations.mutlis;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import space.accident.structurelib.StructureLibAPI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Multiblock_Tooltip_Builder {
	private static final String TAB = "   ";
	private static final String COLON = ": ";
	private static final String SEPARATOR = ", ";
	
	private final List<String> iLines;
	private final List<String> sLines;
	private final List<String> hLines;
	private final SetMultimap<Integer, String> hBlocks;
	
	private String[] iArray;
	private String[] sArray;
	private String[] hArray;
	
	//Localized tooltips
	private static final String tt_machineType = StatCollector.translateToLocal("mbtt.MachineType");
	private static final String tt_dimensions = StatCollector.translateToLocal("mbtt.Dimensions");
	private static final String tt_hollow = StatCollector.translateToLocal("mbtt.Hollow");
	private static final String tt_structure = StatCollector.translateToLocal("mbtt.Structure");
	private static final String tt_controller = StatCollector.translateToLocal("mbtt.Controller");
	private static final String tt_minimum = StatCollector.translateToLocal("mbtt.Minimum");
	private static final String tt_maintenancehatch = StatCollector.translateToLocal("mbtt.MaintenanceHatch");
	private static final String tt_energyhatch = StatCollector.translateToLocal("mbtt.EnergyHatch");
	private static final String tt_dynamohatch = StatCollector.translateToLocal("mbtt.DynamoHatch");
	private static final String tt_mufflerhatch = StatCollector.translateToLocal("mbtt.MufflerHatch");
	private static final String tt_inputbus = StatCollector.translateToLocal("mbtt.InputBus");
	private static final String tt_inputhatch = StatCollector.translateToLocal("mbtt.InputHatch");
	private static final String tt_outputbus = StatCollector.translateToLocal("mbtt.OutputBus");
	private static final String tt_outputhatch = StatCollector.translateToLocal("mbtt.OutputHatch");
	private static final String tt_causes = StatCollector.translateToLocal("mbtt.Causes");
	private static final String tt_pps = StatCollector.translateToLocal("mbtt.PPS");
	private static final String tt_hold = StatCollector.translateToLocal("mbtt.Hold");
	private static final String tt_todisplay = StatCollector.translateToLocal("mbtt.Display");
	private static final String tt_structurehint = StatCollector.translateToLocal("mbtt.StructureHint");
	private static final String tt_mod = StatCollector.translateToLocal("mbtt.Mod");
	private static final String tt_air = StatCollector.translateToLocal("mbtt.Air");
	private static final String[] tt_dots = IntStream.range(0, 16).mapToObj(i -> StatCollector.translateToLocal("structurelib.blockhint." + i + ".name")).toArray(String[]::new);
	
	public Multiblock_Tooltip_Builder() {
		iLines = new LinkedList<>();
		sLines = new LinkedList<>();
		hLines = new LinkedList<>();
		hBlocks = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
		hBlocks.put(StructureLibAPI.HINT_BLOCK_META_AIR, tt_air);
	}
	
	/**
	 * Add a line telling you what the machine type is. Usually, this will be the name of a SB version.<br>
	 * Machine Type: machine
	 *
	 * @param machine
	 * 		Name of the machine type
	 *
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addMachineType(String machine) {
		iLines.add(tt_machineType + COLON + EnumChatFormatting.YELLOW + machine + EnumChatFormatting.RESET);
		return this;
	}
	
	/**
	 * Add a basic line of information about this structure
	 *
	 * @param info
	 * 		The line to be added.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addInfo(String info) {
		iLines.add(info);
		return this;
	}
	
	/**
	 * Add a separator line like this:<br>
	 * -----------------------------------------
	 *
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addSeparator() {
		iLines.add("-----------------------------------------");
		return this;
	}
	
	/**
	 * Add a line telling how much this machine pollutes.
	 *
	 * @param pollution
	 * 		Amount of pollution per second when active
	 *
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addPollutionAmount(int pollution) {
		iLines.add(tt_causes + COLON + EnumChatFormatting.DARK_PURPLE + pollution + " " + EnumChatFormatting.GRAY + tt_pps);
		return this;
	}
	
	/**
	 * Begin adding structural information by adding a line about the structure's dimensions
	 * and then inserting a "Structure:" line.
	 *
	 * @param w
	 * 		Structure width.
	 * @param h
	 * 		Structure height.
	 * @param l
	 * 		Structure depth/length.
	 * @param hollow
	 * 		T/F, adds a (hollow) comment if true
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder beginStructureBlock(int w, int h, int l, boolean hollow) {
		if (hollow) {
			sLines.add(tt_dimensions + COLON + w + "x" + h + "x" + l + " (WxHxL) " + tt_hollow);
		}
		else {
			sLines.add(tt_dimensions + COLON + w + "x" + h + "x" + l + " (WxHxL)");
		}
		sLines.add(tt_structure + COLON);
		return this;
	}
	
	/**
	 * Begin adding structural information by adding a line about the structure's dimensions<br>
	 * and then inserting a "Structure:" line. Variable version displays min and max
	 *
	 * @param wmin
	 * 		Structure min width.
	 * @param wmax
	 * 		Structure max width.
	 * @param hmin
	 * 		Structure min height.
	 * @param hmax
	 * 		Structure max height.
	 * @param lmin
	 * 		Structure min depth/length.
	 * @param lmax
	 * 		Structure max depth/length.
	 * @param hollow
	 * 		T/F, adds a (hollow) comment if true
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder beginVariableStructureBlock(int wmin, int wmax, int hmin, int hmax, int lmin, int lmax, boolean hollow) {
		if (hollow) {
			sLines.add(tt_dimensions + COLON + wmin + "-" + wmax + "x" + hmin + "-" + hmax + "x" + lmin + "-" + lmax + " (WxHxL) " + tt_hollow);
		}
		else {
			sLines.add(tt_dimensions + COLON + wmin + "-" + wmax + "x" + hmin + "-" + hmax + "x" + lmin + "-" + lmax + " (WxHxL)");
		}
		sLines.add(tt_structure + COLON);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Controller: info
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addController(String info) {
		sLines.add(TAB + tt_controller + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)minCountx casingName (minimum)
	 * @param casingName
	 * 		Name of the Casing.
	 * @param minCount
	 * 		Minimum needed for valid structure check.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addCasingInfo(String casingName, int minCount) {
		sLines.add(TAB + minCount +"x " + casingName + " " + tt_minimum);
		return this;
	}
	
	/**
	 * Use this method to add a structural part that isn't covered by the other methods.<br>
	 * (indent)name: info
	 * @param name
	 * 		Name of the hatch or other component.
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addOtherStructurePart(String name, String info) {
		sLines.add(TAB + name + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Maintenance Hatch: info
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addMaintenanceHatch(String info) {
		sLines.add(TAB + tt_maintenancehatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Muffler Hatch: info
	 * @param info
	 * 		Location where the hatch goes
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addMufflerHatch(String info) {
		sLines.add(TAB + tt_mufflerhatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Energy Hatch: info
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addEnergyHatch(String info) {
		sLines.add(TAB + tt_energyhatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Dynamo Hatch: info
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addDynamoHatch(String info) {
		sLines.add(TAB + tt_dynamohatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Input Bus: info
	 * @param info
	 * 		Location where the bus goes
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addInputBus(String info) {
		sLines.add(TAB + tt_inputbus + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Input Hatch: info
	 * @param info
	 * 		Location where the hatch goes
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addInputHatch(String info) {
		sLines.add(TAB + tt_inputhatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Output Bus: info
	 * @param info
	 * 		Location where the bus goes
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addOutputBus(String info) {
		sLines.add(TAB + tt_outputbus + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Output Hatch: info
	 * @param info
	 * 		Location where the bus goes
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addOutputHatch(String info) {
		sLines.add(TAB + tt_outputhatch + COLON + info);
		return this;
	}
	
	/**
	 * Use this method to add a structural part that isn't covered by the other methods.<br>
	 * (indent)name: info
	 * @param name
	 * 		Name of the hatch or other component.
	 * @param info
	 * 		Positional information.
	 * @param dots
	 * 		The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addOtherStructurePart(String name, String info, int... dots) {
		sLines.add(TAB + name + COLON + info);
		for (int dot : dots) hBlocks.put(dot, name);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Maintenance Hatch: info
	 *
	 * @param info Positional information.
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addMaintenanceHatch(String info, int... dots) {
		sLines.add(TAB + tt_maintenancehatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, tt_maintenancehatch);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Muffler Hatch: info
	 *
	 * @param info Location where the hatch goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addMufflerHatch(String info, int... dots) {
		sLines.add(TAB + tt_mufflerhatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, tt_mufflerhatch);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Energy Hatch: info
	 *
	 * @param info Positional information.
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addEnergyHatch(String info, int... dots) {
		sLines.add(TAB + tt_energyhatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, tt_energyhatch);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Dynamo Hatch: info
	 *
	 * @param info Positional information.
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addDynamoHatch(String info, int... dots) {
		sLines.add(TAB + tt_dynamohatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, tt_dynamohatch);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Input Bus: info
	 *
	 * @param info Location where the bus goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addInputBus(String info, int... dots) {
		sLines.add(TAB + tt_inputbus + COLON + info);
		for (int dot : dots) hBlocks.put(dot, tt_inputbus);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Input Hatch: info
	 *
	 * @param info Location where the hatch goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addInputHatch(String info, int... dots) {
		sLines.add(TAB + tt_inputhatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, tt_inputhatch);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Output Bus: info
	 *
	 * @param info Location where the bus goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addOutputBus(String info, int... dots) {
		sLines.add(TAB + tt_outputbus + COLON + info);
		for (int dot : dots) hBlocks.put(dot, tt_outputbus);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Output Hatch: info
	 *
	 * @param info Location where the bus goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addOutputHatch(String info, int... dots) {
		sLines.add(TAB + tt_outputhatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, tt_outputhatch);
		return this;
	}
	
	/**
	 * Use this method to add non-standard structural info.<br>
	 * (indent)info
	 * @param info
	 * 		The line to be added.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addStructureInfo(String info) {
		sLines.add(TAB + info);
		return this;
	}
	
	/**
	 * Use this method to add non-standard structural hint. This info will appear before the standard structural hint.
	 * @param info
	 * 		The line to be added. This should be an entry into minecraft's localization system.
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addStructureHint(String info) {
		hLines.add(StatCollector.translateToLocal(info));
		return this;
	}
	
	/**
	 * Use this method to add an entry to standard structural hint without creating a corresponding line in structure information
	 * @param name
	 * 		The name of block This should be an entry into minecraft's localization system.
	 * @param dots
	 * 		Possible locations of this block
	 * @return Instance this method was called on.
	 */
	public Multiblock_Tooltip_Builder addStructureHint(String name, int... dots) {
		for (int dot : dots) hBlocks.put(dot, StatCollector.translateToLocal(name));
		return this;
	}
	
	/**
	 * Call at the very end.<br>
	 * Adds a final line with the mod name and information on how to display the structure guidelines.<br>
	 * Ends the building process.
	 *
	 * @param mod
	 * 		Name of the mod that adds this multiblock machine
	 */
	public void toolTipFinisher(String mod) {
		iLines.add(tt_hold + " " + EnumChatFormatting.BOLD + "[LSHIFT]" + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + " " + tt_todisplay);
		iLines.add(tt_mod + COLON + EnumChatFormatting.GREEN + mod + EnumChatFormatting.GRAY);
		hLines.add(tt_structurehint);
		iArray = iLines.toArray(new String[0]);
		sArray = sLines.toArray(new String[0]);
		// e.getKey() - 1 because 1 dot is meta 0.
		hArray = Stream.concat(hLines.stream(), hBlocks.asMap().entrySet().stream().map(e -> tt_dots[e.getKey() - 1] + COLON + String.join(SEPARATOR, e.getValue()))).toArray(String[]::new);
	}
	
	public String[] getInformation() {
		return iArray;
	}
	
	public String[] getStructureInformation() {
		return sArray;
	}
	
	public String[] getStructureHint() {
		return hArray;
	}
}

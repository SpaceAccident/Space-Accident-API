package space.accident.main.items;

import net.minecraft.item.ItemStack;
import space.accident.api.API;
import space.accident.api.enums.ToolOrePrefixes;
import space.accident.api.items.tools.MetaGenerated_Tool;
import space.accident.main.items.tools.Tool_Wrench;

public class StaticItemsToolsPage1 extends MetaGenerated_Tool {
	
	public static void register() {
		new StaticItemsToolsPage1();
	}
	
	public static StaticItemsToolsPage1 INSTANCE;
	
	public static final short WRENCH_ID = 16;

	public StaticItemsToolsPage1() {
		super("sa.metatool.01");
		INSTANCE = this;
		ItemStack tool;
		
		tool = addTool(WRENCH_ID, "Wrench", "Hold Left Click to dismantle Machines", new Tool_Wrench(), ToolOrePrefixes.craftingToolWrench);
		API.registerTool(tool, API.sWrenchList);
	}
}

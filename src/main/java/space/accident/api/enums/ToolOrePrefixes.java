package space.accident.api.enums;

public enum ToolOrePrefixes {

	craftingToolWrench,
	
	;

	public static boolean contains(String name) {
		if (!name.startsWith("craftingTool")) return false;
		for (ToolOrePrefixes tool : ToolOrePrefixes.values()) {
			if (tool.toString().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
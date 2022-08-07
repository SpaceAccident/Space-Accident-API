package space.accident.main.items;

import space.accident.api.items.MetaGeneratedItem32;

import static space.accident.api.enums.OrePrefixes.*;

public class StaticItemsPage1 extends MetaGeneratedItem32 {
	public static StaticItemsPage1 INSTANCE;
	
	public StaticItemsPage1() {
		super("metaitem.01", ingot, dust, plate);
		INSTANCE = this;
		
		int ID = 0;
		
		//TODO
		
	}
	
	public static void register() {
		new StaticItemsPage1();
	}
}
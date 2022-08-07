package space.accident.api.interfaces;

import space.accident.api.enums.Materials;

/**
 * This class only for identification tile entity
 */
public class TypeTileEntity {
	
	public interface IMetaCable {
		Materials getMaterial();
	}
	
	
	//TODO create identity
	public interface IMetaChest {}
	
	//TODO create identity
	public interface IMetaTank {}
	
	public interface IMetaPipeItem {
		Materials getMaterial();
	}
	
	public interface IMetaPipeFluid {
		Materials getMaterial();
	}
	
	//TODO create identity
	public interface IMetaFrame {
		Materials getMaterial();
	}
}
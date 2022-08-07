package buildcraft.api.statements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public interface IStatement {
	String getUniqueTag();
	
	@SideOnly(Side.CLIENT)
	IIcon getIcon();
	
	@SideOnly(Side.CLIENT)
	void registerIcons(IIconRegister var1);
	
	int maxParameters();
	
	int minParameters();
	
	String getDescription();
	
	IStatementParameter createParameter(int var1);
	
	IStatement rotateLeft();
}

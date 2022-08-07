package buildcraft.api.statements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public interface IStatementParameter {
	String getUniqueTag();
	
	@SideOnly(Side.CLIENT)
	IIcon getIcon();
	
	ItemStack getItemStack();
	
	@SideOnly(Side.CLIENT)
	void registerIcons(IIconRegister var1);
	
	String getDescription();
	
	void onClick(IStatementContainer var1, IStatement var2, ItemStack var3, StatementMouseClick var4);
	
	void readFromNBT(NBTTagCompound var1);
	
	void writeToNBT(NBTTagCompound var1);
	
	IStatementParameter rotateLeft();
}

package space.accident.structurelib.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import space.accident.structurelib.StructureLib;
import space.accident.structurelib.alignment.AlignmentUtility;

import java.util.List;

import static net.minecraft.util.StatCollector.translateToLocal;
import static space.accident.structurelib.StructureLibAPI.MOD_ID;

public class ItemFrontRotationTool extends Item {
    public ItemFrontRotationTool() {
        setMaxStackSize(1);
        setUnlocalizedName("structurelib.frontRotationTool");
        setTextureName(MOD_ID + ":itemFrontRotationTool");
        setCreativeTab(StructureLib.creativeTab);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return AlignmentUtility.handle(player, world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List aList, boolean p_77624_4_) {
        aList.add(translateToLocal("item.structurelib.frontRotationTool.desc.0"));//Triggers Front Rotation Interface
        aList.add(EnumChatFormatting.BLUE + translateToLocal("item.structurelib.frontRotationTool.desc.1"));//Rotates only the front panel,
        aList.add(EnumChatFormatting.BLUE + translateToLocal("item.structurelib.frontRotationTool.desc.2"));//which allows structure rotation.
    }
}

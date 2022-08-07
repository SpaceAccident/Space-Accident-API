package space.accident.structurelib.alignment.constructable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.structurelib.StructureLib;
import space.accident.structurelib.StructureLibAPI;
import space.accident.structurelib.alignment.IAlignment;
import space.accident.structurelib.alignment.enumerable.ExtendedFacing;

public class ConstructableUtility {
    private ConstructableUtility() {

    }

    public static boolean handle(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        StructureLibAPI.startHinting(world);
        boolean ret = handle0(stack, player, world, x, y, z, side);
        StructureLibAPI.endHinting(world);
        return ret;
    }

    private static boolean handle0(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        TileEntity tTileEntity = world.getTileEntity(x, y, z);
        if (tTileEntity == null || player instanceof FakePlayer) {
            return player instanceof EntityPlayerMP;
        }
        if (player instanceof EntityPlayerMP) {
            //struct gen
            if (player.isSneaking() && player.capabilities.isCreativeMode) {
                if (tTileEntity instanceof IConstructableProvider) {
                    IConstructable constructable = ((IConstructableProvider) tTileEntity).getConstructable();
                    if (constructable != null) {
                        constructable.construct(stack, false);
                    }
                } else if (tTileEntity instanceof IConstructable) {
                    ((IConstructable) tTileEntity).construct(stack, false);
                } else if (IMultiblockInfoContainer.contains(tTileEntity.getClass())) {
                    IMultiblockInfoContainer<TileEntity> iMultiblockInfoContainer = IMultiblockInfoContainer.get(tTileEntity.getClass());
                    if (tTileEntity instanceof IAlignment) {
                        iMultiblockInfoContainer.construct(stack, false, tTileEntity,
                            ((IAlignment) tTileEntity).getExtendedFacing());
                    } else {
                        iMultiblockInfoContainer.construct(stack, false, tTileEntity,
                            ExtendedFacing.of(ForgeDirection.getOrientation(side)));
                    }
                }
            }
            return true;
        } else if (StructureLib.isCurrentPlayer(player)) {//particles and text client side
            //if ((!player.isSneaking() || !player.capabilities.isCreativeMode)) {
            if (tTileEntity instanceof IConstructableProvider) {
                IConstructable constructable = ((IConstructableProvider) tTileEntity).getConstructable();
                if (constructable != null) {
                    constructable.construct(stack, true);
                    StructureLib.addClientSideChatMessages(constructable.getStructureDescription(stack));
                }
            } else if (tTileEntity instanceof IConstructable) {
                IConstructable constructable = (IConstructable) tTileEntity;
                constructable.construct(stack, true);
                StructureLib.addClientSideChatMessages(constructable.getStructureDescription(stack));
                return false;
            } else if (IMultiblockInfoContainer.contains(tTileEntity.getClass())) {
                IMultiblockInfoContainer<TileEntity> iMultiblockInfoContainer = IMultiblockInfoContainer.get(tTileEntity.getClass());
                if (tTileEntity instanceof IAlignment) {
                    iMultiblockInfoContainer.construct(stack, true, tTileEntity,
                        ((IAlignment) tTileEntity).getExtendedFacing());
                } else {
                    iMultiblockInfoContainer.construct(stack, true, tTileEntity,
                        ExtendedFacing.of(ForgeDirection.getOrientation(side)));
                }
                StructureLib.addClientSideChatMessages(IMultiblockInfoContainer.get(tTileEntity.getClass()).getDescription(stack));
                return false;
            }
        }
        return false;
    }
}

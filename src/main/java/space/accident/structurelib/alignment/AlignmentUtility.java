package space.accident.structurelib.alignment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class AlignmentUtility {
    private AlignmentUtility() {
    }

    public static boolean handle(EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tTileEntity = world.getTileEntity(x, y, z);
        if (tTileEntity == null || player instanceof FakePlayer) {
            return player instanceof EntityPlayerMP;
        }
        if (player instanceof EntityPlayerMP && tTileEntity instanceof IAlignmentProvider) {
            IAlignment alignment = ((IAlignmentProvider) tTileEntity).getAlignment();
            if (alignment != null) {
                if (player.isSneaking()) {
                    alignment.toolSetFlip(null);
                } else {
                    alignment.toolSetRotation(null);
                }
                return true;
            }
        }
        return false;
    }
}

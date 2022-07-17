package ic2.api.energy.tile

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection

interface IEnergyAcceptor : IEnergyTile {
    fun acceptsEnergyFrom(te: TileEntity, fd: ForgeDirection): Boolean
}

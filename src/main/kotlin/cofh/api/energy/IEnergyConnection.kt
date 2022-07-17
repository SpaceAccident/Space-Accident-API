package cofh.api.energy

import net.minecraftforge.common.util.ForgeDirection

interface IEnergyConnection {
    fun canConnectEnergy(var1: ForgeDirection): Boolean
}

package cofh.api.energy

import net.minecraftforge.common.util.ForgeDirection

interface IEnergyReceiver : IEnergyConnection {
    fun receiveEnergy(var1: ForgeDirection, var2: Int, var3: Boolean): Int
    fun getEnergyStored(var1: ForgeDirection): Int
    fun getMaxEnergyStored(var1: ForgeDirection): Int
}

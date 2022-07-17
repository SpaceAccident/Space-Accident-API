package ic2.api.energy.tile

import net.minecraftforge.common.util.ForgeDirection

interface IEnergySink : IEnergyAcceptor {
    fun getDemandedEnergy(): Double
    fun getSinkTier(): Int
    fun injectEnergy(v1: ForgeDirection, v2: Double, v3: Double): Double
}
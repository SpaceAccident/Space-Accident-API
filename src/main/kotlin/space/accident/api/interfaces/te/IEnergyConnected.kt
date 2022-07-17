package space.accident.api.interfaces.te

import cofh.api.energy.IEnergyReceiver
import ic2.api.energy.tile.IEnergySink
import net.minecraft.tileentity.TileEntity
import space.accident.api.ext.getDirection
import space.accident.api.ext.getDirectionOpposite
import space.accident.api.ext.getOppositeSide
import space.accident.api.ext.safeInt
import space.accident.main.config.Config


/**
 * Interface for getting Connected to the GregTech Energy Network.
 *
 * This is all you need to connect to the GT Network.
 * IColoredTileEntity is needed for not connecting differently coloured Blocks to each other.
 * IHasWorldObjectAndCoords is needed for the InWorld related Stuff. @BaseTileEntity does implement most of that Interface.
 */
interface IEnergyConnected : IColoredTileEntity, IHasWorldObjectAndCoords {
    /**
     * Inject Energy Call for Electricity. Gets called by EnergyEmitters to inject Energy into your Block
     *
     *
     * Note: you have to check for @inputEnergyFrom because the Network won't check for that by itself.
     *
     * @param aSide 0 - 5 = Vanilla Directions of YOUR Block the Energy gets inserted to. 6 = No specific Side (don't do Side checks for this Side)
     * @return amount of used Amperes. 0 if not accepted anything.
     */
    fun injectEnergyUnits(aSide: Byte, aVoltage: Long, aAmperage: Long): Long

    /**
     * Sided Energy Input
     */
    fun inputEnergyFrom(aSide: Byte): Boolean
    fun inputEnergyFrom(aSide: Byte, waitForActive: Boolean): Boolean {
        return inputEnergyFrom(aSide)
    }

    /**
     * Sided Energy Output
     */
    fun outputsEnergyTo(aSide: Byte): Boolean
    fun outputsEnergyTo(aSide: Byte, waitForActive: Boolean): Boolean {
        return outputsEnergyTo(aSide)
    }

    /**
     * Utility for the Network
     */
    object Util {
        /**
         * Emits Energy to the E-net. Also compatible with adjacent IC2 TileEntities.
         *
         * @return the used Amperage.
         */
        fun emitEnergyToNetwork(voltage: Long, aAmperage: Long, aEmitter: IEnergyConnected): Long {
            var rUsedAmperes: Long = 0
            var i: Byte = 0
            var j: Byte = 0
            while (i < 6 && aAmperage > rUsedAmperes) {
                if (aEmitter.outputsEnergyTo(i)) {
                    j = i.getOppositeSide()
                    val te = aEmitter.getTileEntityAtSide(i)
                    if (te is IEnergyConnected) {
                        if (aEmitter.getColorization() >= 0) {
                            val tColor = te.getColorization()
                            if (tColor >= 0 && tColor != aEmitter.getColorization()) {
                                i++
                                continue
                            }
                        }
                        rUsedAmperes += te.injectEnergyUnits(j, voltage, aAmperage - rUsedAmperes)
                    } else if (te is IEnergySink) {
                        if (te.acceptsEnergyFrom(aEmitter as TileEntity, j.getDirection())) {
                            while (aAmperage > rUsedAmperes && te.getDemandedEnergy() > 0 &&
                                te.injectEnergy(j.getDirection(), voltage.toDouble(), voltage.toDouble()) < voltage
                            ) {
                                rUsedAmperes++
                            }
                        } else if (Config.outputRF && te is IEnergyReceiver) {
                            val tDirection = i.getDirectionOpposite()
                            val rfOut: Int = (voltage * Config.eUtoRF / 100).safeInt()
                            if (te.receiveEnergy(tDirection, rfOut, true) == rfOut) {
                                te.receiveEnergy(tDirection, rfOut, false)
                                rUsedAmperes++
                            }
                        }
                    }
                    i++
                }
            }
            return rUsedAmperes
        }
    }
}
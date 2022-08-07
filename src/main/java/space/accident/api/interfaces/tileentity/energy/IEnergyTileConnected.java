package space.accident.api.interfaces.tileentity.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.interfaces.tileentity.IColoredTileEntity;
import space.accident.api.interfaces.tileentity.IWorldInteraction;

/**
 * Interface for getting Connected to the Energy Network.
 * This is all you need to connect to the Network.
 * IColoredTileEntity is needed for not connecting differently coloured Blocks to each other.
 * IWorldInteraction is needed for the InWorld related Stuff. @BaseTileEntity does implement most of that Interface.
 */
public interface IEnergyTileConnected extends IColoredTileEntity, IWorldInteraction {
	/**
	 * Inject Energy Call for Electricity. Gets called by EnergyEmitters to inject Energy into your Block
	 * <p/>
	 * Note: you have to check for @inputEnergyFrom because the Network won't check for that by itself.
	 *
	 * @param side 0 - 5 = Vanilla Directions of YOUR Block the Energy gets inserted to. 6 = No specific Side (don't do Side checks for this Side)
	 * @return amount of used Amperes. 0 if not accepted anything.
	 */
	long injectEnergyUnits(int side, long aVoltage, long aAmperage);
	
	/**
	 * Sided Energy Input
	 */
	boolean inputEnergyFrom(int side);
	
	default boolean inputEnergyFrom(int side, boolean waitForActive) {
		return inputEnergyFrom(side);
	}
	
	/**
	 * Sided Energy Output
	 */
	boolean outputsEnergyTo(int side);
	
	default boolean outputsEnergyTo(int side, boolean waitForActive) {
		return outputsEnergyTo(side);
	}
	
	/**
	 * Utility for the Network
	 */
	final class Util {
		/**
		 * Emits Energy to the Enet.
		 *
		 * @return the used Amperage.
		 */
		public static long emitEnergyToNetwork(long aVoltage, long aAmperage, IEnergyTileConnected aEmitter) {
			long rUsedAmperes = 0;
			for (int i = 0, j = 0; i < 6 && aAmperage > rUsedAmperes; i++) {
				if (aEmitter.outputsEnergyTo(i)) {
					j = ForgeDirection.getOrientation(i).getOpposite().ordinal();
					final TileEntity tTileEntity = aEmitter.getTileEntityAtSide(i);
					if (tTileEntity instanceof IEnergyTileConnected) {
						if (aEmitter.getColorization() >= 0) {
							final int tColor = ((IEnergyTileConnected) tTileEntity).getColorization();
							if (tColor >= 0 && tColor != aEmitter.getColorization()) continue;
						}
						rUsedAmperes += ((IEnergyTileConnected) tTileEntity).injectEnergyUnits(j, aVoltage, aAmperage - rUsedAmperes);
					}
				}
			}
			return rUsedAmperes;
		}
	}
}

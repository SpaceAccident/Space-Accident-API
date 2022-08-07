package space.accident.main.threads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.interfaces.tileentity.IMachineBlockUpdate;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.api.metatileentity.implementations.logistic.Cable_Electricity;
import space.accident.api.util.SpaceLog;
import space.accident.main.events.ServerEvents;

public class CableUpdateThreadThread extends TileEntityUpdateThread {
	
	protected CableUpdateThreadThread(World world, ChunkCoordinates aCoords) {
		super(world, aCoords);
	}
	
	public static void setCableUpdateValues(World world, ChunkCoordinates aCoords) {
		if (isEnabled) {
			EXECUTOR_SERVICE.submit(new CableUpdateThreadThread(world, aCoords));
		}
	}
	
	@Override
	public void run() {
		try {
			while (!tQueue.isEmpty()) {
				final ChunkCoordinates aCoords = tQueue.poll();
				final TileEntity tTileEntity;
				
				ServerEvents.TICK_LOCK.lock();
				try {
					//we dont want to go over cables that are in unloaded chunks
					//keeping the lock just to make sure no CME happens
					if (world.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) {
						tTileEntity = world.getTileEntity(aCoords.posX, aCoords.posY, aCoords.posZ);
					} else {
						tTileEntity = null;
					}
				} finally {
					ServerEvents.TICK_LOCK.unlock();
				}
				
				// See if the block itself needs an update
				if (tTileEntity instanceof IMachineBlockUpdate)
					((IMachineBlockUpdate) tTileEntity).onMachineBlockUpdate();
				
				// Now see if we should add the nearby blocks to the queue:
				// only add blocks the cable is connected to
				if (tTileEntity instanceof BaseMetaPipeEntity &&
						((BaseMetaPipeEntity) tTileEntity).getMetaTile() instanceof Cable_Electricity)
				{
					ChunkCoordinates tCoords;
					for (int i = 0;i<6;i++) {
						if (((Cable_Electricity) ((BaseMetaPipeEntity) tTileEntity).getMetaTile()).isConnectedAtSide(i)) {
							ForgeDirection offset = ForgeDirection.getOrientation(i);
							if (visited.add(tCoords = new ChunkCoordinates(aCoords.posX + offset.offsetX,
									aCoords.posY + offset.offsetY, aCoords.posZ + offset.offsetZ)))
								tQueue.add(tCoords);
						}
					}
				}
			}
		} catch (Exception e) {
			SpaceLog.FML_LOGGER.error(
					"Well this update was broken... " + mCoords + ", mWorld={" + world.getProviderName() + " @dimId " + world.provider.dimensionId + "}", e);
		}
	}
}

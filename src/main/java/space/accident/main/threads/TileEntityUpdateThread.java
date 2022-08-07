package space.accident.main.threads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import space.accident.api.API;
import space.accident.api.interfaces.tileentity.IMachineBlockUpdate;
import space.accident.api.util.SpaceLog;
import space.accident.main.events.ServerEvents;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TileEntityUpdateThread implements Runnable {
	// Threading
	private static final ThreadFactory THREAD_FACTORY = r -> {
		Thread thread = new Thread(r);
		thread.setName("GT_MachineBlockUpdate");
		return thread;
	};
	protected static ExecutorService EXECUTOR_SERVICE;
	protected static boolean isEnabled = true;
	// used by runner thread
	protected final ChunkCoordinates mCoords;
	protected final World world;
	protected final Set<ChunkCoordinates> visited = new HashSet<>(80);
	protected final Queue<ChunkCoordinates> tQueue = new LinkedList<>();
	
	// This class should never be initiated outside of this class!
	protected TileEntityUpdateThread(World world, ChunkCoordinates aCoords) {
		this.world   = world;
		this.mCoords = aCoords;
		visited.add(aCoords);
		tQueue.add(aCoords);
	}
	
	public static boolean isEnabled() {
		return isEnabled;
	}
	
	public static void setEnabled(boolean isEnabled) {
		TileEntityUpdateThread.isEnabled = isEnabled;
	}
	
	public static void setEnabled() {
		TileEntityUpdateThread.isEnabled = true;
	}
	
	public static void setDisabled() {
		TileEntityUpdateThread.isEnabled = false;
	}
	
	public static void setMachineUpdateValues(World world, ChunkCoordinates aCoords) {
		if (isEnabled) {
			EXECUTOR_SERVICE.submit(new TileEntityUpdateThread(world, aCoords));
		}
	}
	
	public static void initExecutorService() {
		EXECUTOR_SERVICE = Executors.newFixedThreadPool(Math.max(1, (Runtime.getRuntime().availableProcessors() * 2 / 3)), THREAD_FACTORY);
	}
	
	public static void shutdownExecutorService() {
		try {
			SpaceLog.FML_LOGGER.info("Shutting down Machine block update executor service");
			EXECUTOR_SERVICE.shutdown(); // Disable new tasks from being submitted
			// Wait a while for existing tasks to terminate
			if (!EXECUTOR_SERVICE.awaitTermination(60, TimeUnit.SECONDS)) {
				EXECUTOR_SERVICE.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!EXECUTOR_SERVICE.awaitTermination(60, TimeUnit.SECONDS)) {
					SpaceLog.FML_LOGGER.error("Well this didn't terminated well... TileEntityUpdateThread.shutdownExecutorService");
				}
			}
		} catch (InterruptedException ie) {
			SpaceLog.FML_LOGGER.error("Well this interruption got interrupted...", ie);
			// (Re-)Cancel if current thread also interrupted
			EXECUTOR_SERVICE.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			SpaceLog.FML_LOGGER.error("Well this didn't terminated well...", e);
			// (Re-)Cancel in case
			EXECUTOR_SERVICE.shutdownNow();
		} finally {
			SpaceLog.FML_LOGGER.info("Leaving... TileEntityUpdateThread.shutdownExecutorService");
		}
	}
	
	@Override
	public void run() {
		try {
			while (!tQueue.isEmpty()) {
				final ChunkCoordinates aCoords = tQueue.poll();
				final TileEntity tTileEntity;
				final boolean isMachineBlock;
				
				// This might load a chunk... which might load a TileEntity... which might get added to `loadedTileEntityList`... which might be in the process
				// of being iterated over during `UpdateEntities()`... which might cause a ConcurrentModificationException.  So, lock that shit.
				ServerEvents.TICK_LOCK.lock();
				try {
					tTileEntity    = world.getTileEntity(aCoords.posX, aCoords.posY, aCoords.posZ);
					isMachineBlock = API.isMachineBlock(world.getBlock(aCoords.posX, aCoords.posY, aCoords.posZ), world.getBlockMetadata(aCoords.posX, aCoords.posY, aCoords.posZ));
				} finally {
					ServerEvents.TICK_LOCK.unlock();
				}
				
				// See if the block itself needs an update
				if (tTileEntity instanceof IMachineBlockUpdate) ((IMachineBlockUpdate) tTileEntity).onMachineBlockUpdate();
				
				// Now see if we should add the nearby blocks to the queue:
				// 1) If we've visited less than 5 blocks, then yes
				// 2) If the tile says we should recursively updated (pipes don't, machine blocks do)
				// 3) If the block at the coordinates is marked as a machine block
				if (visited.size() < 5 || (tTileEntity instanceof IMachineBlockUpdate && ((IMachineBlockUpdate) tTileEntity).isMachineBlockUpdateRecursive()) || isMachineBlock) {
					ChunkCoordinates tCoords;
					
					if (visited.add(tCoords = new ChunkCoordinates(aCoords.posX + 1, aCoords.posY, aCoords.posZ))) tQueue.add(tCoords);
					if (visited.add(tCoords = new ChunkCoordinates(aCoords.posX - 1, aCoords.posY, aCoords.posZ))) tQueue.add(tCoords);
					if (visited.add(tCoords = new ChunkCoordinates(aCoords.posX, aCoords.posY + 1, aCoords.posZ))) tQueue.add(tCoords);
					if (visited.add(tCoords = new ChunkCoordinates(aCoords.posX, aCoords.posY - 1, aCoords.posZ))) tQueue.add(tCoords);
					if (visited.add(tCoords = new ChunkCoordinates(aCoords.posX, aCoords.posY, aCoords.posZ + 1))) tQueue.add(tCoords);
					if (visited.add(tCoords = new ChunkCoordinates(aCoords.posX, aCoords.posY, aCoords.posZ - 1))) tQueue.add(tCoords);
				}
			}
		} catch (Exception e) {
			SpaceLog.FML_LOGGER.error("Well this update was broken... " + mCoords + ", mWorld={" + world.getProviderName() + " @dimId " + world.provider.dimensionId + "}", e);
		}
	}
}

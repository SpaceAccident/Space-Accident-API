package space.accident.api.interfaces.metatileentity;

import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.extensions.NumberUtils;

import java.util.Map;

public interface IMetaTileEntityItemPipe extends IMetaTile {
    /**
     * @return if this Pipe can still be used.
     */
    boolean pipeCapacityCheck();
    
    /**
     * @return if this Pipe can still be used.
     */
    boolean incrementTransferCounter(int aIncrement);
    
    /**
     * Sends an ItemStack from aSender to the adjacent Blocks.
     *
     * @param aSender the BaseMetaTileEntity sending the Stack.
     * @return if it was able to send something
     */
    boolean sendItemStack(Object aSender);
    
    /**
     * Executes the Sending Code for inserting Stacks into the TileEntities.
     *
     * @param aSender the BaseMetaTileEntity sending the Stack.
     * @param side   the Side of the PIPE facing the TileEntity.
     * @return if this Side was allowed to Output into the Block.
     */
    boolean insertItemStackIntoTileEntity(Object aSender, int side);
    
    /**
     * Can be used to make flow control Pipes, like Redpowers Restriction Tubes.
     * Every normal Pipe returns a Value of 32768, so you can easily insert lower Numbers to set Routing priorities.
     * Negative Numbers to "suck" Items into a certain direction are also possible.
     */
    int getStepSize();
    
    /**
     * Utility for the Item Network
     */
    class Util {
        /**
         * @return a List of connected Item Pipes
         */
        public static Map<IMetaTileEntityItemPipe, Long> scanPipes(IMetaTileEntityItemPipe metaTile, Map<IMetaTileEntityItemPipe, Long> aMap, long aStep, boolean aSuckItems, boolean aIgnoreCapacity) {
            aStep += metaTile.getStepSize();
            if (aIgnoreCapacity || metaTile.pipeCapacityCheck()) if (aMap.get(metaTile) == null || aMap.get(metaTile) > aStep) {
                ITile baseTile = metaTile.getBaseMetaTileEntity();
                aMap.put(metaTile, aStep);
                for (int i = 0, j = 0; i < 6; i++) {
                    if (metaTile instanceof IConnectable && !((IConnectable) metaTile).isConnectedAtSide(i)) continue;
                    j = NumberUtils.getOppositeSide(i);
                    if (aSuckItems) {
                        if (baseTile.getCoverBehaviorAtSideNew(i).letsItemsIn(i, baseTile.getCoverIDAtSide(i), baseTile.getComplexCoverDataAtSide(i), -2, baseTile)) {
                            ITile tItemPipe = baseTile.getITileAtSide(i);
                            if (baseTile.getColorization() >= 0) {
                                int tColor = tItemPipe.getColorization();
                                if (tColor >= 0 && tColor != baseTile.getColorization()) {
                                    continue;
                                }
                            }
                            if (tItemPipe instanceof BaseMetaPipeEntity) {
                                IMetaTile tMetaTileEntity = tItemPipe.getMetaTile();
                                if (tMetaTileEntity instanceof IMetaTileEntityItemPipe && tItemPipe.getCoverBehaviorAtSideNew(j).letsItemsOut(j, tItemPipe.getCoverIDAtSide(j), tItemPipe.getComplexCoverDataAtSide(j), -2, tItemPipe)) {
                                    scanPipes((IMetaTileEntityItemPipe) tMetaTileEntity, aMap, aStep, aSuckItems, aIgnoreCapacity);
                                }
                            }
                        }
                    } else {
                        if (baseTile.getCoverBehaviorAtSideNew(i).letsItemsOut(i, baseTile.getCoverIDAtSide(i), baseTile.getComplexCoverDataAtSide(i), -2, baseTile)) {
                            ITile tItemPipe = baseTile.getITileAtSide(i);
                            if (tItemPipe != null) {
                                if (baseTile.getColorization() >= 0) {
                                    int tColor = tItemPipe.getColorization();
                                    if (tColor >= 0 && tColor != baseTile.getColorization()) {
                                        continue;
                                    }
                                }
                                if (tItemPipe instanceof BaseMetaPipeEntity) {
                                    IMetaTile tMetaTileEntity = tItemPipe.getMetaTile();
                                    if (tMetaTileEntity instanceof IMetaTileEntityItemPipe && tItemPipe.getCoverBehaviorAtSideNew(j).letsItemsIn(j, tItemPipe.getCoverIDAtSide(j), tItemPipe.getComplexCoverDataAtSide(j), -2, tItemPipe)) {
                                        scanPipes((IMetaTileEntityItemPipe) tMetaTileEntity, aMap, aStep, aSuckItems, aIgnoreCapacity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return aMap;
        }
    }
}

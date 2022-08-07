package space.accident.api.interfaces.tileentity;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import space.accident.api.interfaces.tileentity.energy.IEnergyTile;
import space.accident.api.interfaces.tileentity.redstone.IRedStoneTileEntity;
import space.accident.api.util.SA_CoverBehaviorBase;
import space.accident.api.util.ISerializableObject;

public interface ICoverable extends IRedStoneTileEntity, IHasInventory, IEnergyTile {
	boolean canPlaceCoverIDAtSide(int side, int id);
	
	boolean canPlaceCoverItemAtSide(int side, ItemStack aCover);
	
	boolean dropCover(int side, int aDroppedSide, boolean aForced);
	
	void setCoverDataAtSide(int side, ISerializableObject aData);
	
	void setCoverIdAndDataAtSide(int side, int id, ISerializableObject aData);
	void setCoverIDAtSide(int side, int id);
	boolean setCoverIDAtSideNoUpdate(int side, int id);
	void setCoverItemAtSide(int side, ItemStack aCover);
	
	ISerializableObject getComplexCoverDataAtSide(int side);
	
	int getCoverIDAtSide(int side);
	
	ItemStack getCoverItemAtSide(int side);
	
	SA_CoverBehaviorBase<?> getCoverBehaviorAtSideNew(int side);
	
	/**
	 * For use by the regular MetaTileEntities. Returns the Cover Manipulated input RedStone.
	 * Don't use this if you are a Cover Behavior. Only for MetaTileEntities.
	 */
	int getInternalInputRedStoneSignal(int side);
	
	/**
	 * For use by the regular MetaTileEntities. This makes it not conflict with Cover based RedStone Signals.
	 * Don't use this if you are a Cover Behavior. Only for MetaTileEntities.
	 */
	void setInternalOutputRedStoneSignal(int side, int aStrength);
	
	/**
	 * Causes a general Cover Texture update.
	 * Sends 6 Integers to Client + causes @issueTextureUpdate()
	 */
	void issueCoverUpdate(int side);
	
	/**
	 * Receiving a packet with cover data.
	 */
	void receiveCoverData(int coverSide, int coverID, int coverData);
	
	/**
	 * Receiving a packet with cover data.
	 * @param player the player who made the change
	 */
	default void receiveCoverData(int coverSide, int coverId, ISerializableObject coverData, EntityPlayerMP player) {
		if (coverData instanceof ISerializableObject.LegacyCoverData)
			receiveCoverData(coverSide, coverId, ((ISerializableObject.LegacyCoverData) coverData).get());
	}
}

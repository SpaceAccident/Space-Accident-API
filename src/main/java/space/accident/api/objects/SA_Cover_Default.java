package space.accident.api.objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.util.SA_CoverBehavior;
import space.accident.extensions.PlayerUtils;
import space.accident.extensions.StringUtils;

public class SA_Cover_Default extends SA_CoverBehavior {
	/**
	 * This is the Dummy, if there is a generic Cover without behavior
	 */
	public SA_Cover_Default() {
		super();
	}
	
	@Override
	public boolean isSimpleCover() {
		return true;
	}
	
	@Override
	public int onCoverScrewdriverclick(int side, int coverId, int aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		aCoverVariable = ((aCoverVariable + 1) & 15);
		PlayerUtils.sendChat(
				player, ((aCoverVariable & 1) != 0
						? StringUtils.trans("128", "RedStone ") : "") + ((aCoverVariable & 2) != 0
						? StringUtils.trans("129", "Energy ") : "") + ((aCoverVariable & 4) != 0
						? StringUtils.trans("130", "Fluids ") : "") + ((aCoverVariable & 8) != 0
						? StringUtils.trans("131", "Items ") : "")
		);
		return aCoverVariable;
	}
	
	@Override
	public boolean letsRedstoneGoIn(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return (aCoverVariable & 1) != 0;
	}
	
	@Override
	public boolean letsRedstoneGoOut(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return (aCoverVariable & 1) != 0;
	}
	
	@Override
	public boolean letsEnergyIn(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return (aCoverVariable & 2) != 0;
	}
	
	@Override
	public boolean letsEnergyOut(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return (aCoverVariable & 2) != 0;
	}
	
	@Override
	public boolean letsFluidIn(int side, int coverId, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return (aCoverVariable & 4) != 0;
	}
	
	@Override
	public boolean letsFluidOut(int side, int coverId, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return (aCoverVariable & 4) != 0;
	}
	
	@Override
	public boolean letsItemsIn(int side, int coverId, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return (aCoverVariable & 8) != 0;
	}
	
	@Override
	public boolean letsItemsOut(int side, int coverId, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return (aCoverVariable & 8) != 0;
	}
}

package space.accident.api.metatileentity.implementations.hathes;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.metatileentity.base.HatchBase;
import space.accident.extensions.StringUtils;

import java.util.List;

import static space.accident.api.enums.Values.V;

public class Hatch_Energy_In extends HatchBase {
	
	public Hatch_Energy_In(int id, String aNameRegional, int aTier) {
		super(id, "hatch.energy.tier." + aTier, aNameRegional, aTier, 0, StringUtils.array(
				"Energy Injector for Multiblocks",
				"Accepts up to 2 Amps")
		);
	}
	
	public Hatch_Energy_In(String name, int aTier, String aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 0, aDescription, aTextures);
	}
	
	public Hatch_Energy_In(String name, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 0, aDescription, aTextures);
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return new ITexture[]{aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI[mTier]};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		return new ITexture[]{aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI[mTier]};
	}
	
	@Override
	public boolean isSimpleMachine() {
		return true;
	}
	
	@Override
	public boolean isFacingValid(int face) {
		return true;
	}
	
	@Override
	public boolean isAccessAllowed(EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isEnetInput() {
		return true;
	}
	
	@Override
	public boolean isInputFacing(int side) {
		return side == getBaseMetaTileEntity().getFrontFace();
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return false;
	}
	
	@Override
	public long getMinimumStoredEU() {
		return 512;
	}
	
	@Override
	public long maxEUInput() {
		return V[mTier];
	}
	
	@Override
	public long maxEUStore() {
		return 512L + V[mTier] * 8L;
	}
	
	@Override
	public long maxAmperesIn() {
		return 2;
	}
	
	@Override
	public MetaTileEntity newMetaEntity(ITile aTileEntity) {
		return new Hatch_Energy_In(mName, mTier, mDescriptionArray, mTextures);
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		super.getWailaBody(itemStack, currentTip, accessor, config);
		currentTip.add(accessor.getNBTData().getLong("energy") + " EU");
	}
	
	@Override
	public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z) {
		super.getWailaNBTData(player, tile, tag, world, x, y, z);
		if (tile instanceof ITile) {
			IMetaTile mte = ((ITile) tile).getMetaTile();
			if (mte instanceof Hatch_Energy_In) {
				tag.setLong("energy", ((Hatch_Energy_In) mte).getEUVar());
			}
		}
	}
}

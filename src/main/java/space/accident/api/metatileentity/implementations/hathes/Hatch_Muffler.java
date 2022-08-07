package space.accident.api.metatileentity.implementations.hathes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.metatileentity.base.HatchBase;
import space.accident.api.render.TextureFactory;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.WorldSpawnedEventBuilder;

import java.util.Arrays;

import static space.accident.api.enums.Textures.BlockIcons.OVERLAY_MUFFLER;
import static space.accident.structurelib.util.XSTR.XSTR_INSTANCE;

public class Hatch_Muffler extends HatchBase {
	private static final String localizedDescFormat = LanguageManager.addStringLocalization(
			"sa.blockmachines.hatch.muffler.desc.format",
			"Outputs the Pollution (Might cause ... things)%n" + "DO NOT OBSTRUCT THE OUTPUT!%n" + "Reduces Pollution to %d%%%n" + "Recovers %d%% of CO2/CO/SO2"
	);
	private final int pollutionReduction = calculatePollutionReduction(100);
	private final int pollutionRecover = 100 - pollutionReduction;
	private final String[] description = String.format(localizedDescFormat, pollutionReduction, pollutionRecover).split("\\R");
	private final boolean[] facings = new boolean[ForgeDirection.VALID_DIRECTIONS.length];
	
	public Hatch_Muffler(int id, String name, String aNameRegional, int aTier) {
		super(id, name, aNameRegional, aTier, 0, "");
	}
	
	public Hatch_Muffler(String name, int aTier, String aDescription, ITexture[][][] aTextures) {
		this(name, aTier, new String[]{aDescription}, aTextures);
	}
	
	public Hatch_Muffler(String name, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 0, aDescription, aTextures);
		setInValidFacings(ForgeDirection.DOWN);
	}
	
	@Override
	public String[] getDescription() {
		return description;
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_MUFFLER)};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_MUFFLER)};
	}
	
	@Override
	public boolean isSimpleMachine() {
		return true;
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return false;
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
	public MetaTileEntity newMetaEntity(ITile aTileEntity) {
		return new Hatch_Muffler(mName, mTier, mDescriptionArray, mTextures);
	}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		super.onPostTick(baseTile, tick);
		if (baseTile.isClientSide() && this.getBaseMetaTileEntity().isActive()) {
			pollutionParticles(this.getBaseMetaTileEntity().getWorld(), "largesmoke");
		}
	}
	
	@Override
	public boolean isFacingValid(int face) {
		return facings[face];
	}
	
	@Override
	public boolean isAccessAllowed(EntityPlayer player) {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void pollutionParticles(World world, String name) {
		boolean chk1, chk2, chk3;
		float ran1 = XSTR_INSTANCE.nextFloat(), ran2, ran3;
		chk1 = ran1 * 100 < calculatePollutionReduction(100);
		/*if (GT_Pollution.getPollution(getBaseMetaTileEntity()) >= API.mPollutionSmogLimit) {
			ran2 = XSTR_INSTANCE.nextFloat();
			ran3 = XSTR_INSTANCE.nextFloat();
			chk2 = ran2 * 100 < calculatePollutionReduction(100);
			chk3 = ran3 * 100 < calculatePollutionReduction(100);
			if (!(chk1 || chk2 || chk3)) return;
		} else */
		{
			if (!chk1) return;
			ran2 = ran3 = 0.0F;
			chk2 = chk3 = false;
		}
		
		ITile aMuffler = this.getBaseMetaTileEntity();
		ForgeDirection aDir = ForgeDirection.getOrientation(aMuffler.getFrontFace());
		float xPos = aDir.offsetX * 0.76F + aMuffler.getX() + 0.25F;
		float yPos = aDir.offsetY * 0.76F + aMuffler.getY() + 0.25F;
		float zPos = aDir.offsetZ * 0.76F + aMuffler.getZ() + 0.25F;
		
		float ySpd = aDir.offsetY * 0.1F + 0.2F + 0.1F * XSTR_INSTANCE.nextFloat();
		float xSpd;
		float zSpd;
		
		if (aDir.offsetY == -1) {
			float temp = XSTR_INSTANCE.nextFloat() * 2 * (float) Math.PI;
			xSpd = (float) Math.sin(temp) * 0.1F;
			zSpd = (float) Math.cos(temp) * 0.1F;
		} else {
			xSpd = aDir.offsetX * (0.1F + 0.2F * XSTR_INSTANCE.nextFloat());
			zSpd = aDir.offsetZ * (0.1F + 0.2F * XSTR_INSTANCE.nextFloat());
		}
		
		WorldSpawnedEventBuilder.ParticleEventBuilder events = new WorldSpawnedEventBuilder.ParticleEventBuilder().setIdentifier(name).setWorld(world).setMotion(xSpd, ySpd, zSpd);
		
		if (chk1) {
			events.setPosition(xPos + ran1 * 0.5F, yPos + XSTR_INSTANCE.nextFloat() * 0.5F, zPos + XSTR_INSTANCE.nextFloat() * 0.5F).run();
		}
		if (chk2) {
			events.setPosition(xPos + ran2 * 0.5F, yPos + XSTR_INSTANCE.nextFloat() * 0.5F, zPos + XSTR_INSTANCE.nextFloat() * 0.5F).run();
		}
		if (chk3) {
			events.setPosition(xPos + ran3 * 0.5F, yPos + XSTR_INSTANCE.nextFloat() * 0.5F, zPos + XSTR_INSTANCE.nextFloat() * 0.5F).run();
		}
	}
	
	public int calculatePollutionReduction(int aPollution) {
		if (mTier < 2) {
			return aPollution;
		}
		return (int) ((float) aPollution * ((100F - 12.5F * ((float) mTier - 1F)) / 100F));
	}
	
	/**
	 * @return pollution success
	 * @deprecated replaced by {@link .polluteEnvironment(MetaTileEntity)}
	 */
	@Deprecated
	public boolean polluteEnvironment() {
		return polluteEnvironment(null);
	}
	
	/**
	 * @param mte The multi-block controller's {@link MetaTileEntity}
	 *            MetaTileEntity is passed so newer muffler hatches can do wacky things with the multis
	 * @return pollution success
	 */
	public boolean polluteEnvironment(MetaTileEntity mte) {
		//			GT_Pollution.addPollution(getBaseMetaTileEntity(), calculatePollutionReduction(10000));
		return getBaseMetaTileEntity().getAirAtSide(getBaseMetaTileEntity().getFrontFace());
	}
	
	/**
	 * @param aFacings the {@link ForgeDirection} invalid facings
	 * @apiNote API Code, BartWorks/TecTech based EBF relies on this. It's marked here, not anywhere else.
	 */
	public void setInValidFacings(ForgeDirection... aFacings) {
		Arrays.fill(facings, true);
		Arrays.stream(aFacings).forEach(face -> facings[face.ordinal()] = false);
	}
}

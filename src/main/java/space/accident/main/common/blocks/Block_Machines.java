package space.accident.main.common.blocks;

import com.cricketcraft.chisel.api.IFacade;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.API;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.IDebugableBlock;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.IDebugableTileEntity;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.items.GenericBlock;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.api.metatileentity.base.BaseMetaTileEntity;
import space.accident.api.metatileentity.base.BaseTileEntity;
import space.accident.api.metatileentity.base.CoverableTileEntity;
import space.accident.api.util.SpaceLog;
import space.accident.main.render.Renderer_Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static space.accident.api.enums.Values.SIDE_UP;
import static space.accident.api.interfaces.TypeTileEntity.IMetaChest;
import static space.accident.extensions.ItemStackUtils.isStackInList;
import static space.accident.extensions.NumberUtils.getOppositeSide;
import static space.accident.structurelib.util.XSTR.XSTR_INSTANCE;

@Optional.Interface(iface = "com.cricketcraft.chisel.api.IFacade", modid = "ChiselAPI")
public class Block_Machines extends GenericBlock implements IDebugableBlock, ITileEntityProvider, IFacade {
	
	private static final ThreadLocal<ITile> mTemporaryTileEntity = new ThreadLocal<>();
	private boolean renderAsNormalBlock;
	public Block_Machines() {
		super(Item_Machines.class, "sa.blockmachines", new Material_Machines());
		API.registerMachineBlock(this, -1);
		setHardness(1.0F);
		setResistance(10.0F);
		setStepSound(soundTypeMetal);
		this.isBlockContainer      = true;
		this.renderAsNormalBlock   = true;
		this.useNeighborBrightness = true;
	}
	
	public static void register() {
		API.sBlockMachines = new Block_Machines();
	}
	
	@Override
	public String getHarvestTool(int aMeta) {
		if (aMeta >= 8 && aMeta <= 11) {
			return "cutter";
		}
		return "wrench";
	}
	
	@Override
	public int getHarvestLevel(int aMeta) {
		return aMeta % 4;
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int aTileX, int aTileY, int aTileZ) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if ((tTileEntity instanceof BaseTileEntity)) {
			((BaseTileEntity) tTileEntity).onAdjacentBlockChange(aTileX, aTileY, aTileZ);
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if ((tTileEntity instanceof BaseMetaPipeEntity)) {
			((BaseMetaPipeEntity) tTileEntity).onNeighborBlockChange(x, y, z);
		}
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		if (API.isMachineBlock(this, world.getBlockMetadata(x, y, z))) {
			API.causeMachineUpdate(world, x, y, z);
		}
	}
	
	@Override
	public String getUnlocalizedName() {
		return "sa.blockmachines";
	}
	
	@Override
	public String getLocalizedName() {
		return StatCollector.translateToLocal(getUnlocalizedName() + ".name");
	}
	
	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 0;
	}
	
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return API.sMachineFlammable && (world.getBlockMetadata(x, y, z) == 0) ? 100 : 0;
	}
	
	@Override
	public int getRenderType() {
		if (Renderer_Block.INSTANCE == null) {
			return super.getRenderType();
		}
		return Renderer_Block.INSTANCE.mRenderID;
	}
	
	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return API.sMachineFlammable && (world.getBlockMetadata(x, y, z) == 0);
	}
	
	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return API.sMachineFlammable && (world.getBlockMetadata(x, y, z) == 0);
	}
	
	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}
	
	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean hasTileEntity(int aMeta) {
		return true;
	}
	
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return renderAsNormalBlock;
	}
	
	public Block_Machines setRenderAsNormalBlock(boolean aBool) {
		renderAsNormalBlock = aBool;
		return this;
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int aMeta) {
		return createTileEntity(world, aMeta);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess aIBlockAccess, int x, int y, int z, int side) {
		return Textures.BlockIcons.MACHINE_LV_SIDE.getIcon();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int aMeta) {
		return Textures.BlockIcons.MACHINE_LV_SIDE.getIcon();
	}
	
	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int aData1, int aData2) {
		super.onBlockEventReceived(world, x, y, z, aData1, aData2);
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		return tTileEntity != null && tTileEntity.receiveClientEvent(aData1, aData2);
	}
	
	@SuppressWarnings("unchecked") // Old API uses raw List type
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB inputAABB, List outputAABB, Entity collider) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile && ((ITile) tTileEntity).getMetaTile() != null) {
			((ITile) tTileEntity).addCollisionBoxesToList(world, x, y, z, inputAABB, outputAABB, collider);
			return;
		}
		super.addCollisionBoxesToList(world, x, y, z, inputAABB, outputAABB, collider);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile && ((ITile) tTileEntity).getMetaTile() != null) {
			return ((ITile) tTileEntity).getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile && ((ITile) tTileEntity).getMetaTile() != null) {
			return ((ITile) tTileEntity).getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override  //THIS
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		final TileEntity tTileEntity = blockAccess.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile && (((ITile) tTileEntity).getMetaTile() != null)) {
			final AxisAlignedBB bbb = ((ITile) tTileEntity).getCollisionBoundingBoxFromPool(((ITile) tTileEntity).getWorld(), 0, 0, 0);
			minX = bbb.minX; //This essentially sets block bounds
			minY = bbb.minY;
			minZ = bbb.minZ;
			maxX = bbb.maxX;
			maxY = bbb.maxY;
			maxZ = bbb.maxZ;
			return;
		}
		super.setBlockBoundsBasedOnState(blockAccess, x, y, z);
	}
	
	@Override
	public void setBlockBoundsForItemRender() {
		super.setBlockBounds(0, 0, 0, 1, 1, 1);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collider) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile && ((ITile) tTileEntity).getMetaTile() != null) {
			((ITile) tTileEntity).onEntityCollidedWithBlock(world, x, y, z, collider);
			return;
		}
		super.onEntityCollidedWithBlock(world, x, y, z, collider);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister aIconRegister) {
		if (!API.sPostloadFinished) return;
		SpaceLog.FML_LOGGER.info("Setting up Icon Register for Blocks");
		API.setBlockIconRegister(aIconRegister);
		
		SpaceLog.FML_LOGGER.info("Registering MetaTileEntity specific Textures");
		try {
			for (IMetaTile tMetaTileEntity : API.METATILEENTITIES) {
				if (tMetaTileEntity != null) {
					tMetaTileEntity.registerIcons(aIconRegister);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(SpaceLog.err);
		}
		SpaceLog.FML_LOGGER.info("Registering Crop specific Textures");
//		try {
//			for (GT_BaseCrop tCrop : GT_BaseCrop.sCropList) {
//				tCrop.registerSprites(aIconRegister);
//			}
//		} catch (Exception e) {
//			e.printStackTrace(SpaceLog.err);
//		}
		SpaceLog.FML_LOGGER.info("Starting Block Icon Load Phase");
		try {
			for (Runnable tRunnable : API.sGTBlockIconload) {
				tRunnable.run();
			}
		} catch (Exception e) {
			e.printStackTrace(SpaceLog.err);
		}
		SpaceLog.FML_LOGGER.info("Finished Block Icon Load Phase");
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		return tTileEntity instanceof BaseMetaTileEntity && ((BaseMetaTileEntity) tTileEntity).privateAccess() && !((BaseMetaTileEntity) tTileEntity).playerOwnsThis(player, true) ? -1.0F : super.getPlayerRelativeBlockHardness(player, world, x, y, z);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float aOffsetX, float aOffsetY, float aOffsetZ) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity == null) {
			return false;
		}
		if (player.isSneaking()) {
			final ItemStack tCurrentItem = player.inventory.getCurrentItem();
			if (tCurrentItem != null && !isStackInList(tCurrentItem, API.sScrewdriverList) && !isStackInList(tCurrentItem, API.sWrenchList) && !isStackInList(tCurrentItem, API.sWireCutterList) && !isStackInList(tCurrentItem, API.sSolderingToolList))
				return false;
		}
		if ((tTileEntity instanceof ITile)) {
			if (((ITile) tTileEntity).getTick() < 50L) {
				return false;
			}
			if ((!world.isRemote) && !((ITile) tTileEntity).isUseableByPlayer(player)) {
				return true;
			}
			return ((ITile) tTileEntity).onRightClick(player, side, aOffsetX, aOffsetY, aOffsetZ);
		}
		return false;
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			((ITile) tTileEntity).onLeftClick(player);
		}
	}
	
	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			return ((ITile) tTileEntity).getMetaTileID();
		}
		return 0;
	}
	
	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion aExplosion) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof BaseMetaTileEntity) {
			SpaceLog.exp.printf("Explosion at : %d | %d | %d DIMID: %s due to near explosion!%n", x, y, z, world.provider.dimensionId);
			((BaseMetaTileEntity) tTileEntity).doEnergyExplosion();
		}
		super.onBlockExploded(world, x, y, z, aExplosion);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		API.causeMachineUpdate(world, x, y, z);
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			final ITile iTile = (ITile) tTileEntity;
			mTemporaryTileEntity.set(iTile);
			if (!(iTile.getMetaTile() instanceof IMetaChest)) {
				for (int i = 0; i < iTile.getSizeInventory(); i++) {
					final ItemStack tItem = iTile.getStackInSlot(i);
					if ((tItem != null) && (tItem.stackSize > 0) && (iTile.isValidSlot(i))) {
						final EntityItem tItemEntity = new EntityItem(world, x + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F, y + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F, z + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F, new ItemStack(tItem.getItem(), tItem.stackSize, tItem.getItemDamage()));
						if (tItem.hasTagCompound()) {
							tItemEntity.getEntityItem().setTagCompound((NBTTagCompound) tItem.getTagCompound().copy());
						}
						tItemEntity.motionX = (XSTR_INSTANCE.nextGaussian() * 0.05D);
						tItemEntity.motionY = (XSTR_INSTANCE.nextGaussian() * 0.25D);
						tItemEntity.motionZ = (XSTR_INSTANCE.nextGaussian() * 0.05D);
						world.spawnEntityInWorld(tItemEntity);
						tItem.stackSize = 0;
						iTile.setInventorySlotContents(i, null);
					}
				}
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
		world.removeTileEntity(x, y, z);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int aMeta, int aFortune) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if ((tTileEntity instanceof ITile)) {
			return ((ITile) tTileEntity).getDrops();
		}
		final ITile iTile = mTemporaryTileEntity.get();
		final ArrayList<ItemStack> tDrops;
		if (iTile == null) {
			tDrops = (ArrayList<ItemStack>) Collections.<ItemStack>emptyList();
		} else {
			tDrops = iTile.getDrops();
			mTemporaryTileEntity.remove();
		}
		return tDrops;
	}
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean aWillHarvest) {
		// This delays deletion of the block until after getDrops
		return aWillHarvest || super.removedByPlayer(world, player, x, y, z, false);
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int aMeta) {
		super.harvestBlock(world, player, x, y, z, aMeta);
		world.setBlockToAir(x, y, z);
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			return ((ITile) tTileEntity).getComparatorValue(side);
		}
		return 0;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		if (side < 0 || side > 5) {
			return 0;
		}
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			return ((ITile) tTileEntity).getOutputRedStoneSignal(getOppositeSide(side));
		}
		return 0;
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		if (side < 0 || side > 5) {
			return 0;
		}
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			return ((ITile) tTileEntity).getStrongOutputRedStoneSignal(getOppositeSide(side));
		}
		return 0;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int aFortune) {
		if (!world.isRemote) {
			final TileEntity tTileEntity = world.getTileEntity(x, y, z);
			if (tTileEntity != null && (chance < 1.0F)) {
				if (tTileEntity instanceof BaseMetaTileEntity && (API.sMachineNonWrenchExplosions)) {
					SpaceLog.exp.printf("Explosion at : %d | %d | %d DIMID: %s NonWrench picking/Rain!%n", x, y, z, world.provider.dimensionId);
					((BaseMetaTileEntity) tTileEntity).doEnergyExplosion();
				}
			} else {
				super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, aFortune);
			}
		}
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		if (world.getBlockMetadata(x, y, z) == 0) {
			return true;
		}
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity != null) {
			if (tTileEntity instanceof BaseMetaTileEntity) {
				return true;
			}
			if (tTileEntity instanceof BaseMetaPipeEntity && (((BaseMetaPipeEntity) tTileEntity).mConnections & 0xFFFFFFC0) != 0) {
				return true;
			}
			return tTileEntity instanceof ICoverable && ((ICoverable) tTileEntity).getCoverIDAtSide(side.ordinal()) != 0;
		}
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube() {
		return true;
	}
	
	/**
	 * Returns the default ambient occlusion value based on block opacity
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public float getAmbientOcclusionLightValue() {
		return this.renderAsNormalBlock() ? 0.2F : 0.5F;
	}
	
	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			return ((ITile) tTileEntity).getLightOpacity();
		}
		return world.getBlockMetadata(x, y, z) == 0 ? 255 : 0;
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof BaseMetaTileEntity) {
			return ((BaseMetaTileEntity) tTileEntity).getLightValue();
		}
		return 0;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int aMeta) {
		if (aMeta < 4) {
			return API.constructBaseMetaTileEntity();
		}
		return new BaseMetaPipeEntity();
	}
	
	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			return ((ITile) tTileEntity).getBlastResistance(6);
		}
		return 10.0F;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("unchecked") // Old API uses raw List type
	public void getSubBlocks(Item item, CreativeTabs aCreativeTab, List outputSubBlocks) {
		for (int i = 1; i < API.METATILEENTITIES.length; i++) {
			if (API.METATILEENTITIES[i] != null) {
				outputSubBlocks.add(new ItemStack(item, 1, i));
			}
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (!(tTileEntity instanceof ITile)) return;
		final ITile iTile = (ITile) tTileEntity;
		iTile.setFrontFace(BaseTileEntity.getSideForPlayerPlacing(player, SIDE_UP, iTile.getValidFaces()));
	}
	
	@Override
	public ArrayList<String> getDebugInfo(EntityPlayer player, int x, int y, int z, int logLevel) {
		final TileEntity tTileEntity = player.worldObj.getTileEntity(x, y, z);
		if (tTileEntity instanceof IDebugableTileEntity) {
			return ((IDebugableTileEntity) tTileEntity).getDebugInfo(player, logLevel);
		}
		return (ArrayList<String>) Collections.<String>emptyList();
	}
	
	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int color) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) {
			if (((ITile) tTileEntity).getColorization() == ((~color) & 0xF)) {
				return false;
			}
			((ITile) tTileEntity).setColorization((~color) & 0xF);
			return true;
		}
		return false;
	}
	
	@Override
	public Block getFacade(IBlockAccess world, int x, int y, int z, int side) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof CoverableTileEntity) {
			final CoverableTileEntity tile = (CoverableTileEntity) tTileEntity;
			if (side != -1) {
				final Block facadeBlock = tile.getCoverBehaviorAtSideNew(side).getFacadeBlock(side, tile.getCoverIDAtSide(side), tile.getComplexCoverDataAtSide(side), tile);
				if (facadeBlock != null) return facadeBlock;
			} else {
				// we do not allow more than one type of facade per block, so no need to check every side
				for (int i = 0; i < 6; i++) {
					final Block facadeBlock = tile.getCoverBehaviorAtSideNew(i).getFacadeBlock(i, tile.getCoverIDAtSide(i), tile.getComplexCoverDataAtSide(i), tile);
					if (facadeBlock != null) {
						return facadeBlock;
					}
				}
			}
		}
		return Blocks.air;
	}
	
	@Override
	public int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side) {
		final TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (tTileEntity instanceof CoverableTileEntity) {
			final CoverableTileEntity tile = (CoverableTileEntity) tTileEntity;
			if (side != -1) {
				final Block facadeBlock = tile.getCoverBehaviorAtSideNew(side).getFacadeBlock(side, tile.getCoverIDAtSide(side), tile.getComplexCoverDataAtSide(side), tile);
				if (facadeBlock != null) return tile.getCoverBehaviorAtSideNew(side).getFacadeMeta(side, tile.getCoverIDAtSide(side), tile.getComplexCoverDataAtSide(side), tile);
			} else {
				for (int i = 0; i < 6; i++) {
					final Block facadeBlock = tile.getCoverBehaviorAtSideNew(i).getFacadeBlock(i, tile.getCoverIDAtSide(i), tile.getComplexCoverDataAtSide(i), tile);
					if (facadeBlock != null) {
						return tile.getCoverBehaviorAtSideNew(i).getFacadeMeta(i, tile.getCoverIDAtSide(i), tile.getComplexCoverDataAtSide(i), tile);
					}
				}
			}
		}
		return 0;
	}
}

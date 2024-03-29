package space.accident.main.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import space.accident.api.enums.MaterialType;
import space.accident.api.enums.Materials;
import space.accident.api.enums.OrePrefixes;
import space.accident.api.interfaces.ITexture;
import space.accident.api.items.GenericBlock;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.OreDictUnifier;
import space.accident.main.render.Renderer_Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static space.accident.api.API.MAX_MATERIALS;
import static space.accident.api.API.sGeneratedMaterials;
import static space.accident.api.enums.MaterialType.*;
import static space.accident.api.items.MetaGeneratedItem.MAX_COUNT_AUTOGENERATED_ITEMS;

public abstract class Meta_Block_Abstract extends GenericBlock implements ITileEntityProvider {
	
	public static boolean FUCKING_LOCK = false;
	public static ThreadLocal<TileEntity_MetaBlocks> mTemporaryTileEntity = new ThreadLocal<>();
	
	private static final List<MaterialType> BLOCKS = new ArrayList<>();
	
	static {
		BLOCKS.addAll(Arrays.asList(BLOCK_DEFAULT, BLOCK_METAL));
	}
	
	private static final String DOT_NAME = ".name";
	
	
	protected Meta_Block_Abstract(String aUnlocalizedName, int aOreMetaCount, Material aMaterial) {
		super(Item_MetaBlocks.class, aUnlocalizedName, aMaterial);
		setStepSound(soundTypeStone);
		if (aOreMetaCount > 8 || aOreMetaCount < 0) aOreMetaCount = 8;
		for (int i = 1; i < sGeneratedMaterials.length; i++) {
			if (sGeneratedMaterials[i] != null) {
				for (int j = 0; j < aOreMetaCount; j++) {
					LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + (j * MAX_MATERIALS)) + DOT_NAME, getLocalizedNameFormat(sGeneratedMaterials[i]));
					LanguageManager.addStringLocalization(getUnlocalizedName() + "." + ((i + MAX_COUNT_AUTOGENERATED_ITEMS / 2) + (j * MAX_MATERIALS)) + DOT_NAME,
							"Small " + (getLocalizedNameFormat(sGeneratedMaterials[i])));
					if (containsType(sGeneratedMaterials[i])) {
						OreDictUnifier.registerOre(OrePrefixes.block.get(sGeneratedMaterials[i]), new ItemStack(this, 1, i + (j * MAX_MATERIALS)));
					}
				}
			}
		}
	}
	
	public int getBaseBlockHarvestLevel(int aMeta) {
		return 0;
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return null;
	}
	
	public String getLocalizedNameFormat(Materials aMaterial) {
		return "%material" + OrePrefixes.block.postfix;
	}
	
	public String getLocalizedName(Materials aMaterial) {
		return aMaterial.getDefaultLocalizedNameForItem(getLocalizedNameFormat(aMaterial));
	}
	
	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return (!(entity instanceof EntityDragon)) && (super.canEntityDestroy(world, x, y, z, entity));
	}
	
	@Override
	public String getHarvestTool(int aMeta) {
		return aMeta < 8 ? "pickaxe" : "shovel";
	}
	
	@Override
	public int getHarvestLevel(int aMeta) {
		return aMeta == 5 || aMeta == 6 ? 2 : aMeta % 8;
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return 1.0F + getHarvestLevel(world.getBlockMetadata(x, y, z)) * 1.0F;
	}
	
	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return 1.0F + getHarvestLevel(world.getBlockMetadata(x, y, z)) * 1.0F;
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	@Override
	public abstract String getUnlocalizedName();
	
	public abstract Block getDroppedBlock();
	
	public abstract ITexture[] getTextureSet(); //Must have 16 entries.
	
	@Override
	public String getLocalizedName() {
		return StatCollector.translateToLocal(getUnlocalizedName() + DOT_NAME);
	}
	
	@Override
	public int getRenderType() {
		if (Renderer_Block.INSTANCE == null) {
			return super.getRenderType();
		}
		return Renderer_Block.INSTANCE.mRenderID;
	}
	
	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return true;
	}
	
	@Override
	public boolean hasTileEntity(int aMeta) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int aMeta) {
		return new TileEntity_MetaBlocks();
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int aTileX, int aTileY, int aTileZ) {
		if (!FUCKING_LOCK) {
			FUCKING_LOCK = true;
			TileEntity tTileEntity = world.getTileEntity(x, y, z);
			if ((tTileEntity instanceof TileEntity_MetaBlocks)) {
				((TileEntity_MetaBlocks) tTileEntity).onUpdated();
			}
		}
		FUCKING_LOCK = false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!FUCKING_LOCK) {
			FUCKING_LOCK = true;
			TileEntity tTileEntity = world.getTileEntity(x, y, z);
			if ((tTileEntity instanceof TileEntity_MetaBlocks)) {
				((TileEntity_MetaBlocks) tTileEntity).onUpdated();
			}
		}
		FUCKING_LOCK = false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess aIBlockAccess, int x, int y, int z, int side) {
		return Blocks.stone.getIcon(0, 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int aMeta) {
		return Blocks.stone.getIcon(0, 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister aIconRegister) {
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		Renderer_Block.addHitEffects(effectRenderer, this, worldObj, target.blockX, target.blockY, target.blockZ, target.sideHit);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		Renderer_Block.addDestroyEffects(effectRenderer, this, world, x, y, z);
		return true;
	}
	
	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if (((tTileEntity instanceof TileEntity_MetaBlocks))) {
			return ((TileEntity_MetaBlocks) tTileEntity).getMetaData();
		}
		return 0;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if ((tTileEntity instanceof TileEntity_MetaBlocks)) {
			mTemporaryTileEntity.set((TileEntity_MetaBlocks) tTileEntity);
		}
		super.breakBlock(world, x, y, z, block, meta);
		world.removeTileEntity(x, y, z);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int aMeta, int aFortune) {
		TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if ((tTileEntity instanceof TileEntity_MetaBlocks)) {
			return ((TileEntity_MetaBlocks) tTileEntity).getDrops(getDroppedBlock());
		}
		return mTemporaryTileEntity.get() == null ? new ArrayList<>() : mTemporaryTileEntity.get().getDrops(getDroppedBlock());
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item aItem, CreativeTabs aTab, List aList) {
		for (int i = 0; i < sGeneratedMaterials.length; i++) {
			Materials tMaterial = sGeneratedMaterials[i];
			if ((tMaterial != null) && (containsType(tMaterial))) {
				for (int meta = i; meta < MAX_COUNT_AUTOGENERATED_ITEMS - MAX_MATERIALS + i; meta += MAX_MATERIALS) {
					if (!(new ItemStack(aItem, 1, meta).getDisplayName().contains(DOT_NAME))) {
						aList.add(new ItemStack(aItem, 1, meta));
					}
				}
			}
		}
	}
	
	private static boolean containsType(Materials mat) {
		for (MaterialType type : mat.types) {
			if (BLOCKS.contains(type)) {
				return true;
			}
		}
		return false;
	}
	
}

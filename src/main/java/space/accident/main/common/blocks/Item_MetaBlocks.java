package space.accident.main.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import space.accident.api.enums.Materials;

import static space.accident.api.API.MAX_MATERIALS;

public class Item_MetaBlocks extends ItemBlock {
	public Item_MetaBlocks(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return false;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.field_150939_a.getUnlocalizedName() + "." + getDamage(stack);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String name = super.getItemStackDisplayName(stack);
		if (this.field_150939_a instanceof Meta_Block_Abstract) {
			name = Materials.getLocalizedNameForItem(name, stack.getItemDamage() % MAX_MATERIALS);
		}
		return name;
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int aMeta) {
		short tDamage = (short) getDamage(stack);
		if (tDamage > 0) {
			if (!world.setBlock(x, y, z, this.field_150939_a, TileEntity_MetaBlocks.getHarvestData(tDamage,
					((Meta_Block_Abstract) field_150939_a).getBaseBlockHarvestLevel(aMeta % 16000 / 1000)), 3)) {
				return false;
			}
			TileEntity_MetaBlocks tTileEntity = (TileEntity_MetaBlocks) world.getTileEntity(x, y, z);
			tTileEntity.mMetaData = tDamage;
			tTileEntity.mNatural = false;
		} else if (!world.setBlock(x, y, z, this.field_150939_a, 0, 3)) {
			return false;
		}
		if (world.getBlock(x, y, z) == this.field_150939_a) {
			this.field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
			this.field_150939_a.onPostBlockPlaced(world, x, y, z, tDamage);
		}
		return true;
	}
}


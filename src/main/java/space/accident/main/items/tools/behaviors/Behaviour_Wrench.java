package space.accident.main.items.tools.behaviors;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.API;
import space.accident.api.items.MetaBaseItem;
import space.accident.api.items.tools.MetaGenerated_Tool;
import space.accident.api.sound.Sounds;
import space.accident.api.util.LanguageManager;

import java.util.Arrays;
import java.util.List;

import static space.accident.api.util.ModHandler.canUseElectricItem;
import static space.accident.api.util.Utility.determineWrenchingSide;
import static space.accident.api.util.Utility.sendSoundToPlayers;
import static space.accident.extensions.ItemStackUtils.isElectricItem;

public class Behaviour_Wrench extends Behaviour_None {
	
	private final int costs;
	private final String mTooltip = LanguageManager.addStringLocalization("sa.behaviour.wrench", "Rotates Blocks on Right Click");
	
	public Behaviour_Wrench(int costs) {
		this.costs = costs;
	}
	
	@Override
	public boolean onItemUseFirst(MetaBaseItem aItem, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		Block block = world.getBlock(x, y, z);
		if (block == null) {
			return false;
		}
		int aMeta = world.getBlockMetadata(x, y, z);
		int aTargetSide = determineWrenchingSide(side, hitX, hitY, hitZ);
		TileEntity aTileEntity = world.getTileEntity(x, y, z);
		try {
			if (((aTileEntity instanceof IWrenchable))) {
				if (((IWrenchable) aTileEntity).wrenchCanSetFacing(player, aTargetSide)) {
					if ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs))) {
						((IWrenchable) aTileEntity).setFacing((short) aTargetSide);
						sendSoundToPlayers(world, API.sSoundList.get(Sounds.WRENCH), 1.0F, -1.0F, x, y, z);
					}
					return true;
				}
				if (((IWrenchable) aTileEntity).wrenchCanRemove(player)) {
					int tDamage = ((IWrenchable) aTileEntity).getWrenchDropRate() < 1.0F ? 10 : 3;
					if ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, (long) tDamage * this.costs))) {
						ItemStack tOutput = ((IWrenchable) aTileEntity).getWrenchDrop(player);
						for (ItemStack tStack : block.getDrops(world, x, y, z, aMeta, 0)) {
							if (tOutput == null) {
								world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, tStack));
							} else {
								world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, tOutput));
								tOutput = null;
							}
						}
						world.setBlockToAir(x, y, z);
						sendSoundToPlayers(world, API.sSoundList.get(Sounds.WRENCH), 1.0F, -1.0F, x, y, z);
					}
					return true;
				}
				return true;
			}
		} catch (Throwable ignored) {
		}
		if ((block == Blocks.log) || (block == Blocks.log2) || (block == Blocks.hay_block)) {
			if ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs))) {
				world.setBlockMetadataWithNotify(x, y, z, (aMeta + 4) % 12, 3);
				sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
			}
			return true;
		}
		int calc = aMeta / 4 * 4 + (aMeta % 4 + 1) % 4;
		if ((block == Blocks.powered_repeater) || (block == Blocks.unpowered_repeater)) {
			if ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs))) {
				world.setBlockMetadataWithNotify(x, y, z, calc, 3);
				sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
			}
			return true;
		}
		if ((block == Blocks.powered_comparator) || (block == Blocks.unpowered_comparator)) {
			if ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs))) {
				world.setBlockMetadataWithNotify(x, y, z, calc, 3);
				sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
			}
			return true;
		}
		if ((block == Blocks.crafting_table) || (block == Blocks.bookshelf)) {
			if ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs))) {
				world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, new ItemStack(block, 1, aMeta)));
				world.setBlockToAir(x, y, z);
				sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
			}
			return true;
		}
		if (aMeta == aTargetSide) {
			if ((block == Blocks.pumpkin) || (block == Blocks.lit_pumpkin) || (block == Blocks.piston) || (block == Blocks.sticky_piston) || (block == Blocks.dispenser) || (block == Blocks.dropper) || (block == Blocks.furnace) || (block == Blocks.lit_furnace) || (block == Blocks.chest) || (block == Blocks.trapped_chest) || (block == Blocks.ender_chest) || (block == Blocks.hopper)) {
				if ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs))) {
					world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, new ItemStack(block, 1, 0)));
					world.setBlockToAir(x, y, z);
					sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
				}
				return true;
			}
		} else {
			if ((block == Blocks.piston) || (block == Blocks.sticky_piston) || (block == Blocks.dispenser) || (block == Blocks.dropper)) {
				if ((aMeta < 6) && ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs)))) {
					world.setBlockMetadataWithNotify(x, y, z, aTargetSide, 3);
					sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
				}
				return true;
			}
			if ((block == Blocks.pumpkin) || (block == Blocks.lit_pumpkin) || (block == Blocks.furnace) || (block == Blocks.lit_furnace) || (block == Blocks.chest) || (block == Blocks.ender_chest) || (block == Blocks.trapped_chest)) {
				if ((aTargetSide > 1) && ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs)))) {
					world.setBlockMetadataWithNotify(x, y, z, aTargetSide, 3);
					sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
				}
				return true;
			}
			if (block == Blocks.hopper) {
				if ((aTargetSide != 1) && ((player.capabilities.isCreativeMode) || (((MetaGenerated_Tool) aItem).doDamage(stack, this.costs)))) {
					world.setBlockMetadataWithNotify(x, y, z, aTargetSide, 3);
					sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
				}
				return true;
			}
		}
		if ((Arrays.asList(block.getValidRotations(world, x, y, z)).contains(ForgeDirection.getOrientation(aTargetSide))) && ((player.capabilities.isCreativeMode) || (!isElectricItem(stack)) || (canUseElectricItem(stack, this.costs))) && (block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(aTargetSide)))) {
			if (!player.capabilities.isCreativeMode) {
				((MetaGenerated_Tool) aItem).doDamage(stack, this.costs);
			}
			sendSoundToPlayers(world, API.sSoundList.get(100), 1.0F, -1.0F, x, y, z);
		}
		return false;
	}
	
	@Override
	public List<String> getAdditionalToolTips(MetaBaseItem aItem, List<String> aList, ItemStack stack) {
		aList.add(this.mTooltip);
		return aList;
	}
}

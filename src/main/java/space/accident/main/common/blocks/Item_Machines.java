package space.accident.main.common.blocks;


import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import space.accident.api.enums.Materials;
import space.accident.api.interfaces.ISecondaryDescribable;
import space.accident.api.interfaces.TypeTileEntity.*;
import space.accident.api.interfaces.metatileentity.IConnectable;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.CoverableTileEntity;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.SpaceLog;

import javax.annotation.Nullable;
import java.util.List;

import static space.accident.api.API.METATILEENTITIES;
import static space.accident.api.API.sPostloadFinished;
import static space.accident.api.enums.Values.VN;
import static space.accident.extensions.ItemStackUtils.isStackInvalid;
import static space.accident.extensions.NumberUtils.*;
import static space.accident.extensions.StringUtils.isStringValid;

public class Item_Machines extends ItemBlock implements IFluidContainerItem {
	public Item_Machines(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	
	public static IMetaTile getMetaTileEntity(ItemStack stack) {
		if (isStackInvalid(stack)) return null;
		if (!(stack.getItem() instanceof Item_Machines)) return null;
		if (stack.getItemDamage() < 0 || stack.getItemDamage() > METATILEENTITIES.length) return null;
		return METATILEENTITIES[stack.getItemDamage()];
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer _null, List aList, boolean aF3_H) {
		try {
			final int tDamage = getDamage(stack);
			if ((tDamage <= 0) || (tDamage >= METATILEENTITIES.length)) {
				return;
			}
			
			if (METATILEENTITIES[tDamage] != null) {
				final ITile tTileEntity = METATILEENTITIES[tDamage].getBaseMetaTileEntity();
				if (!sPostloadFinished && tTileEntity.getMetaTile() instanceof ISecondaryDescribable) {
					final String[] tSecondaryDescription = ((ISecondaryDescribable) tTileEntity.getMetaTile()).getSecondaryDescription();
					addDescription(null, tSecondaryDescription, tDamage, "_Secondary", true);
				}
				{
					final IMetaTile tMetaTileEntity = tTileEntity.getMetaTile();
					final String tSuffix = (tMetaTileEntity instanceof ISecondaryDescribable && ((ISecondaryDescribable) tMetaTileEntity).isDisplaySecondaryDescription()) ? "_Secondary" : "";
					addDescription(aList, tTileEntity.getDescription(), tDamage, tSuffix, !sPostloadFinished);
				}
				if (tTileEntity.getEUCapacity() > 0L) {
					if (tTileEntity.getInputVoltage() > 0L) {
						final int inputTier = getTier(tTileEntity.getInputVoltage());
						aList.add(LanguageManager.addStringLocalization("TileEntity_EUp_IN", "Voltage IN: ", !sPostloadFinished) + EnumChatFormatting.GREEN + format(tTileEntity.getInputVoltage()) + " (" + VN[inputTier] + EnumChatFormatting.GREEN + ")" + EnumChatFormatting.GRAY);
					}
					if (tTileEntity.getOutputVoltage() > 0L) {
						final int outputTier = getTier(tTileEntity.getOutputVoltage());
						aList.add(LanguageManager.addStringLocalization("TileEntity_EUp_OUT", "Voltage OUT: ", !sPostloadFinished) + EnumChatFormatting.GREEN + format(tTileEntity.getOutputVoltage()) + " (" + VN[outputTier] + EnumChatFormatting.GREEN + ")" + EnumChatFormatting.GRAY);
					}
					if (tTileEntity.getOutputAmperage() > 1L) {
						aList.add(LanguageManager.addStringLocalization("TileEntity_EUp_AMOUNT", "Amperage: ", !sPostloadFinished) + EnumChatFormatting.YELLOW + format(tTileEntity.getOutputAmperage()) + EnumChatFormatting.GRAY);
					}
					aList.add(LanguageManager.addStringLocalization("TileEntity_EUp_STORE", "Capacity: ", !sPostloadFinished) + EnumChatFormatting.BLUE + format(tTileEntity.getEUCapacity()) + EnumChatFormatting.GRAY);
				}
				if (METATILEENTITIES[tDamage] instanceof IMetaTank) {
					if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("mFluid")) {
						final FluidStack tContents = FluidStack.loadFluidStackFromNBT(stack.stackTagCompound.getCompoundTag("mFluid"));
						if (tContents != null && tContents.amount > 0) {
							aList.add(LanguageManager.addStringLocalization("TileEntity_TANK_INFO", "Contains Fluid: ", !sPostloadFinished) + EnumChatFormatting.YELLOW + tContents.getLocalizedName() + EnumChatFormatting.GRAY);
							aList.add(LanguageManager.addStringLocalization("TileEntity_TANK_AMOUNT", "Fluid Amount: ", !sPostloadFinished) + EnumChatFormatting.GREEN + format(tContents.amount) + " L" + EnumChatFormatting.GRAY);
						}
					}
				}
				if (METATILEENTITIES[tDamage] instanceof IMetaChest) {
					if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("mItemStack")) {
						final ItemStack tContents = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("mItemStack"));
						final int tSize = stack.stackTagCompound.getInteger("mItemCount");
						if (tContents != null && tSize > 0) {
							aList.add(LanguageManager.addStringLocalization("TileEntity_CHEST_INFO", "Contains Item: ", !sPostloadFinished) + EnumChatFormatting.YELLOW + tContents.getDisplayName() + EnumChatFormatting.GRAY);
							aList.add(LanguageManager.addStringLocalization("TileEntity_CHEST_AMOUNT", "Item Amount: ", !sPostloadFinished) + EnumChatFormatting.GREEN + format(tSize) + EnumChatFormatting.GRAY);
						}
					}
				}
			}
			final NBTTagCompound nbt = stack.getTagCompound();
			if (nbt != null) {
				if (nbt.getBoolean("mMuffler")) {
					aList.add(LanguageManager.addStringLocalization("GT_TileEntity_MUFFLER", "has Muffler Upgrade", !sPostloadFinished));
				}
				if (nbt.getBoolean("mSteamConverter")) {
					aList.add(LanguageManager.addStringLocalization("GT_TileEntity_STEAMCONVERTER", "has Steam Upgrade", !sPostloadFinished));
				}
				int tAmount = 0;
				if ((tAmount = nbt.getInteger("mSteamTanks")) > 0) {
					aList.add(tAmount + " " + LanguageManager.addStringLocalization("GT_TileEntity_STEAMTANKS", "Steam Tank Upgrades", !sPostloadFinished));
				}
				
				CoverableTileEntity.addInstalledCoversInformation(nbt, aList);
			}
		} catch (Throwable e) {
			SpaceLog.FML_LOGGER.error("addInformation", e);
		}
	}
	
	private void addDescription(@Nullable List<String> aList, @Nullable String[] aDescription, int aDamage, String aSuffix, boolean aWriteIntoLangFile) {
		if (aDescription == null) return;
		for (int i = 0, tLength = aDescription.length; i < tLength; i++) {
			String tDescLine = aDescription[i];
			if (!isStringValid(tDescLine)) continue;
			
			String tKey = String.format("TileEntity_DESCRIPTION_%05d%s_Index_%02d", aDamage, aSuffix, i);
			if (tDescLine.contains("%%%")) {
				final String[] tSplitStrings = tDescLine.split("%%%");
				final StringBuilder tBuffer = new StringBuilder();
				final String[] tRep = new String[tSplitStrings.length / 2];
				for (int j = 0; j < tSplitStrings.length; j++)
					if (j % 2 == 0) tBuffer.append(tSplitStrings[j]);
					else {
						tBuffer.append(" %s");
						tRep[j / 2] = tSplitStrings[j];
					}
				final String tTranslated = String.format(LanguageManager.addStringLocalization(tKey, tBuffer.toString(), aWriteIntoLangFile), (Object[]) tRep);
				if (aList != null) aList.add(tTranslated);
			} else {
				String tTranslated = LanguageManager.addStringLocalization(tKey, tDescLine, aWriteIntoLangFile);
				if (aList != null) aList.add(tTranslated.equals("") ? tDescLine : tTranslated);
			}
		}
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return false;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		final short tDamage = (short) getDamage(stack);
		if ((tDamage < 0) || (tDamage >= METATILEENTITIES.length)) {
			return "";
		}
		if (METATILEENTITIES[tDamage] != null) {
			return getUnlocalizedName() + "." + METATILEENTITIES[tDamage].getMetaName();
		}
		return "";
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String name = super.getItemStackDisplayName(stack);
		final short aDamage = (short) getDamage(stack);
		if (aDamage >= 0 && aDamage < METATILEENTITIES.length && METATILEENTITIES[aDamage] != null) {
			Materials aMaterial = null;
			if (METATILEENTITIES[aDamage] instanceof IMetaPipeItem) {
				aMaterial = ((IMetaPipeItem) METATILEENTITIES[aDamage]).getMaterial();
			} else if (METATILEENTITIES[aDamage] instanceof IMetaPipeFluid) {
				aMaterial = ((IMetaPipeFluid) METATILEENTITIES[aDamage]).getMaterial();
			} else if (METATILEENTITIES[aDamage] instanceof IMetaCable) {
				aMaterial = ((IMetaCable) METATILEENTITIES[aDamage]).getMaterial();
			} else if (METATILEENTITIES[aDamage] instanceof IMetaFrame) {
				aMaterial = ((IMetaFrame) METATILEENTITIES[aDamage]).getMaterial();
			}
			if (aMaterial != null) {
				name = aMaterial.getLocalizedNameForItem(name);
			}
		}
		return name;
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		super.onCreated(stack, world, player);
		final short tDamage = (short) getDamage(stack);
		if ((tDamage < 0) || ((tDamage >= METATILEENTITIES.length) && (METATILEENTITIES[tDamage] != null))) {
			METATILEENTITIES[tDamage].onCreated(stack, world, player);
		}
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int tSide, float hitX, float hitY, float hitZ, int aMeta) {
		final short tDamage = (short) getDamage(stack);
		if (tDamage > 0) {
			if (METATILEENTITIES[tDamage] == null) {
				return false;
			}
			final int tMetaData = METATILEENTITIES[tDamage].getTileEntityBaseType();
			if (!world.setBlock(x, y, z, this.field_150939_a, tMetaData, 3)) {
				return false;
			}
			if (world.getBlock(x, y, z) != this.field_150939_a) {
				throw new RuntimeException("Failed to place Block even though World.setBlock returned true. It COULD be MCPC/Bukkit causing that. In case you really have that installed, don't report this Bug to me, I don't know how to fix it.");
			}
			if (world.getBlockMetadata(x, y, z) != tMetaData) {
				throw new RuntimeException("Failed to set the MetaValue of the Block even though World.setBlock returned true. It COULD be MCPC/Bukkit causing that. In case you really have that installed, don't report this Bug to me, I don't know how to fix it.");
			}
			final ITile tTileEntity = (ITile) world.getTileEntity(x, y, z);
			if (tTileEntity != null) {
				tTileEntity.setInitialValuesAsNBT(tTileEntity.isServerSide() ? stack.getTagCompound() : null, tDamage);
				if (player != null) {
					tTileEntity.setOwnerName(player.getDisplayName());
					tTileEntity.setOwnerUuid(player.getUniqueID());
				}
				tTileEntity.getMetaTile().initDefaultModes(stack.getTagCompound());
				final int side = getOppositeSide(tSide);
				if (tTileEntity.getMetaTile() instanceof IConnectable) {
					// If we're connectable, try connecting to whatever we're up against
					((IConnectable) tTileEntity.getMetaTile()).connect(side);
				} else if (player != null && player.isSneaking()) {
					// If we're being placed against something that is connectable, try telling it to connect to us
					final ITile aTileEntity = tTileEntity.getITileAtSide(side);
					if (aTileEntity != null && aTileEntity.getMetaTile() instanceof IConnectable) {
						((IConnectable) aTileEntity.getMetaTile()).connect(side);
					}
				}
			}
		} else if (!world.setBlock(x, y, z, this.field_150939_a, tDamage, 3)) {
			return false;
		}
		if (world.getBlock(x, y, z) == this.field_150939_a) {
			this.field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
			this.field_150939_a.onPostBlockPlaced(world, x, y, z, tDamage);
		}
		return true;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity player, int aTimer, boolean aIsInHand) {
		super.onUpdate(stack, world, player, aTimer, aIsInHand);
		final short tDamage = (short) getDamage(stack);
		final EntityLivingBase tPlayer = (EntityPlayer) player;
		if (METATILEENTITIES[tDamage] instanceof IMetaTank || METATILEENTITIES[tDamage] instanceof IMetaChest) {
			final NBTTagCompound tNBT = stack.stackTagCompound;
			if (tNBT == null) return;
			if (tNBT.hasNoTags()) {
				stack.setTagCompound(null);
				return;
			}
			if ((tNBT.hasKey("mItemCount") && tNBT.getInteger("mItemCount") > 0) || (tNBT.hasKey("mFluid") && FluidStack.loadFluidStackFromNBT(tNBT.getCompoundTag("mFluid")).amount > 64000)) {
				final FluidStack tFluid = FluidStack.loadFluidStackFromNBT(tNBT.getCompoundTag("mFluid"));
				int tLasing = 1200;
				if (tFluid != null) {
					final double tFluidAmount = tFluid.amount;
					final double tMiddlePoint = 4096000;
					final double tSmoothingCoefficient = 2000000;
					final int tMaxLastingTime = 12000;
					final double tmp = (tFluidAmount - tMiddlePoint) / tSmoothingCoefficient;
					tLasing = (int) (Math.exp(tmp) / (Math.exp(tmp) + Math.exp(-tmp)) * tMaxLastingTime);
				}
				tPlayer.addPotionEffect(new PotionEffect(Potion.hunger.id, tLasing, 1));
				tPlayer.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, tLasing, 1));
				tPlayer.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, tLasing, 1));
				tPlayer.addPotionEffect(new PotionEffect(Potion.weakness.id, tLasing, 1));
			}
		}
	}
	
	@Override
	public FluidStack getFluid(ItemStack container) {
		if (container != null) {
			final NBTTagCompound tNBT = container.stackTagCompound;
			if (tNBT != null && tNBT.hasKey("mFluid", 10)) {
				return FluidStack.loadFluidStackFromNBT(tNBT.getCompoundTag("mFluid"));
			}
		}
		return null;
	}
	
	@Override
	public int getCapacity(ItemStack container) {
		if (container != null) {
			final int tDamage = container.getItemDamage();
			final IMetaTile tMetaTile = METATILEENTITIES[tDamage];
			if (tMetaTile != null) return tMetaTile.getCapacity();
		}
		return 0;
	}
	
	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		if (container != null && resource != null) {
			final int tDamage = container.getItemDamage();
			final IMetaTile tMetaTile = METATILEENTITIES[tDamage];
			if (!(tMetaTile instanceof IMetaTank)) {
				return 0;
			}
			if (container.stackTagCompound == null) container.stackTagCompound = new NBTTagCompound();
			final FluidStack tStoredFluid = getFluid(container);
			final int tCapacity = getCapacity(container);
			if (tCapacity <= 0) return 0;
			if (tStoredFluid != null && tStoredFluid.isFluidEqual(resource)) {
				final int tAmount = Math.min(tCapacity - tStoredFluid.amount, resource.amount);
				if (doFill) {
					final FluidStack tNewFluid = new FluidStack(tStoredFluid, tAmount + tStoredFluid.amount);
					container.stackTagCompound.setTag("mFluid", tNewFluid.writeToNBT(new NBTTagCompound()));
				}
				return tAmount;
			}
			if (tStoredFluid == null) {
				final int tAmount = Math.min(tCapacity, resource.amount);
				if (doFill) {
					final FluidStack tNewFluid = new FluidStack(resource, tAmount);
					container.stackTagCompound.setTag("mFluid", tNewFluid.writeToNBT(new NBTTagCompound()));
				}
				return tAmount;
			}
		}
		return 0;
	}
	
	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		if (container != null && container.hasTagCompound()) {
			final int tDamage = container.getItemDamage();
			final IMetaTile tMetaTile = METATILEENTITIES[tDamage];
			if (!(tMetaTile instanceof IMetaTank)) {
				return null;
			}
			final FluidStack tStoredFluid = getFluid(container);
			if (tStoredFluid != null) {
				final int tAmount = Math.min(maxDrain, tStoredFluid.amount);
				final FluidStack tNewFluid = new FluidStack(tStoredFluid, tStoredFluid.amount - tAmount);
				final FluidStack tOutputFluid = new FluidStack(tStoredFluid, tAmount);
				if (doDrain) {
					if (tNewFluid.amount <= 0) {
						container.stackTagCompound.removeTag("mFluid");
						if (container.stackTagCompound.hasNoTags()) container.setTagCompound(null);
					} else {
						container.stackTagCompound.setTag("mFluid", tNewFluid.writeToNBT(new NBTTagCompound()));
					}
				}
				return tOutputFluid;
			}
		}
		return null;
	}
}

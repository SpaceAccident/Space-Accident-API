package space.accident.api.items.tools;

import appeng.api.implementations.items.IAEWrench;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.api.tool.ITool;
import mods.railcraft.api.core.items.IToolCrowbar;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import space.accident.api.damagesources.EnchRadioactivity;
import space.accident.api.enums.MaterialList;
import space.accident.api.enums.Materials;
import space.accident.api.enums.ToolOrePrefixes;
import space.accident.api.interfaces.IDamagableItem;
import space.accident.api.interfaces.tools.IToolStats;
import space.accident.api.interfaces.tools.IToolTurbine;
import space.accident.api.items.MetaBaseItem;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.OreDictUnifier;
import space.accident.main.events.ServerEvents;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static net.minecraft.enchantment.EnchantmentHelper.*;
import static net.minecraft.util.EnumChatFormatting.*;
import static space.accident.api.util.Utility.*;
import static space.accident.main.IntegrationConstants.*;

@Optional.InterfaceList(value = {
		@Optional.Interface(iface = "mods.railcraft.api.core.items.IToolCrowbar", modid = RC),
		@Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = BC),
		@Optional.Interface(iface = "appeng.api.implementations.items", modid = AE2),
		@Optional.Interface(iface = "crazypants.enderio.api.tool.ITool", modid = EIO)
})
public abstract class MetaGenerated_Tool extends MetaBaseItem implements IDamagableItem, IToolCrowbar, IToolWrench, IAEWrench, ITool {
	
	/**
	 * All instances of this Item Class are listed here.
	 * This gets used to register the Renderer to all Items of this Type, if useStandardMetaItemRenderer() returns true.
	 * <p/>
	 * You can also use the unlocalized Name gotten from getUnlocalizedName() as Key if you want to get a specific Item.
	 */
	public static final ConcurrentHashMap<String, MetaGenerated_Tool> sInstances = new ConcurrentHashMap<>();
	public static final String
			NBT_SA_TOOL_IDENTITY = "SA.ToolsStats",
			NBT_PRIMARY_MATERIAL = "PrimaryMaterial",
			NBT_SECONDARY_MATERIAL = "SecondaryMaterial",
			NBT_MAX_DAMAGE = "MaxDamage",
			NBT_DAMAGE = "Damage",
			NBT_ELECTRIC = "Electric",
			NBT_MAX_CHARGE = "MaxCharge",
			NBT_VOLTAGE = "Voltage",
			NBT_TIER = "Tier",
			NBT_SPECIAL_DATA = "SpecialData",
			NBT_ENCHANTED = "ench",
			NBT_HEAT = "Heat",
			NBT_HEAT_TIME = "HeatTime";
	
	public final ConcurrentHashMap<Short, IToolStats> mToolStats = new ConcurrentHashMap<>();
	
	/**
	 * Creates the Item using these Parameters.
	 *
	 * @param unlocalName The Unlocalized Name of this Item.
	 */
	public MetaGenerated_Tool(String unlocalName) {
		super(unlocalName);
		setMaxStackSize(1);
		sInstances.put(getUnlocalizedName(), this);
	}
	
	public static Materials getPrimaryMaterial(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			nbt = nbt.getCompoundTag(NBT_SA_TOOL_IDENTITY);
			if (nbt != null) return Materials.getRealMaterial(nbt.getString(NBT_PRIMARY_MATERIAL));
		}
		return MaterialList._NULL;
	}
	
	public static Materials getSecondaryMaterial(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			nbt = nbt.getCompoundTag(NBT_SA_TOOL_IDENTITY);
			if (nbt != null) return Materials.getRealMaterial(nbt.getString(NBT_SECONDARY_MATERIAL));
		}
		return MaterialList._NULL;
	}
	
	public static long getToolMaxDamage(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			nbt = nbt.getCompoundTag(NBT_SA_TOOL_IDENTITY);
			if (nbt != null) return nbt.getLong(NBT_MAX_DAMAGE);
		}
		return 0;
	}
	
	public static long getToolDamage(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			nbt = nbt.getCompoundTag(NBT_SA_TOOL_IDENTITY);
			if (nbt != null) return nbt.getLong(NBT_DAMAGE);
		}
		return 0;
	}
	
	public static boolean setToolDamage(ItemStack stack, long aDamage) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			nbt = nbt.getCompoundTag(NBT_SA_TOOL_IDENTITY);
			if (nbt != null) {
				nbt.setLong(NBT_DAMAGE, aDamage);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This adds a Custom Item to the ending Range.
	 *
	 * @param id        The Id of the assigned Tool Class [0 - 32765] (only even Numbers allowed! Uneven ID's are empty electric Items)
	 * @param english   The Default Localized Name of the created Item
	 * @param toolTip   The Default ToolTip of the created Item, you can also insert null for having no ToolTip
	 * @param toolStats The Food Value of this Item. Can be null as well.
	 * @param oreDict   The OreDict Names you want to give the Item.
	 * @return An ItemStack containing the newly created Item, but without specific Stats.
	 */
	public final ItemStack addTool(int id, String english, String toolTip, IToolStats toolStats, List<String> oreDict) {
		if (toolTip == null) toolTip = "";
		if (id >= 0 && id < 32766 && id % 2 == 0) {
			LanguageManager.addStringLocalization(getUnlocalizedName() + "." + id + ".name", english);
			LanguageManager.addStringLocalization(getUnlocalizedName() + "." + id + ".tooltip", toolTip);
			LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (id + 1) + ".name", english + " (Empty)");
			LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (id + 1) + ".tooltip", "You need to recharge it");
			mToolStats.put((short) id, toolStats);
			mToolStats.put((short) (id + 1), toolStats);
			toolStats.onStatsAddedToTool(this, id);
			ItemStack rStack = new ItemStack(this, 1, id);
			for (Object tOreDictNameOrAspect : oreDict) {
				OreDictUnifier.registerOre(tOreDictNameOrAspect, rStack);
			}
			return rStack;
		}
		return null;
	}
	
	public final ItemStack addTool(int id, String english, String toolTip, IToolStats toolStats, String... oreDict) {
		return addTool(id, english, toolTip, toolStats, Arrays.asList(oreDict));
	}
	
	public final ItemStack addTool(int id, String english, String toolTip, IToolStats toolStats, ToolOrePrefixes... oreDict) {
		return addTool(id, english, toolTip, toolStats, Arrays.stream(oreDict).map(Enum::name).collect(Collectors.toList()));
	}
	
	/**
	 * This Function gets an ItemStack Version of this Tool
	 *
	 * @param aToolID            the ID of the Tool Class
	 * @param amount            Amount of Items (well normally you only need 1)
	 * @param aPrimaryMaterial   Primary Material of this Tool
	 * @param aSecondaryMaterial Secondary (Rod/Handle) Material of this Tool
	 * @param aElectricArray     The Electric Stats of this Tool (or null if not electric)
	 */
	public final ItemStack getToolWithStats(int aToolID, int amount, Materials aPrimaryMaterial, Materials aSecondaryMaterial, long[] aElectricArray) {
		ItemStack rStack = new ItemStack(this, amount, aToolID);
		IToolStats tToolStats = getToolStats(rStack);
		if (tToolStats != null) {
			NBTTagCompound tMainNBT = new NBTTagCompound(), tToolNBT = new NBTTagCompound();
			if (aPrimaryMaterial != null) {
				tToolNBT.setString(NBT_PRIMARY_MATERIAL, aPrimaryMaterial.name);
				tToolNBT.setLong(NBT_MAX_DAMAGE, 100L * (long) (aPrimaryMaterial.durability * tToolStats.getMaxDurabilityMultiplier()));
			}
			if (aSecondaryMaterial != null) tToolNBT.setString(NBT_SECONDARY_MATERIAL, aSecondaryMaterial.name);
			
			if (aElectricArray != null) {
				tToolNBT.setBoolean(NBT_ELECTRIC, true);
				tToolNBT.setLong(NBT_MAX_CHARGE, aElectricArray[0]);
				tToolNBT.setLong(NBT_VOLTAGE, aElectricArray[1]);
				tToolNBT.setLong(NBT_TIER, aElectricArray[2]);
				tToolNBT.setLong(NBT_SPECIAL_DATA, aElectricArray[3]);
			}
			
			tMainNBT.setTag(NBT_SA_TOOL_IDENTITY, tToolNBT);
			rStack.setTagCompound(tMainNBT);
		}
		isItemStackUsable(rStack);
		return rStack;
	}
	
	/**
	 * Called by the Block Harvesting Event within the GT_Proxy
	 */
	@Mod.EventHandler
	public void onHarvestBlockEvent(ArrayList<ItemStack> aDrops, ItemStack stack, EntityPlayer player, Block block, int x, int y, int z, byte meta, int aFortune, boolean aSilkTouch, BlockEvent.HarvestDropsEvent aEvent) {
		IToolStats tStats = getToolStats(stack);
		if (isItemStackUsable(stack) && getDigSpeed(stack, block, meta) > 0.0F) {
			doDamage(stack, (long) tStats.convertBlockDrops(aDrops, stack, player, block, x, y, z, meta, aFortune, aSilkTouch, aEvent) * tStats.getToolDamagePerDropConversion());
		}
	}
	
	@Mod.EventHandler
	public float onBlockBreakSpeedEvent(float aDefault, ItemStack stack, EntityPlayer player, Block block, int x, int y, int z, byte meta, PlayerEvent.BreakSpeed aEvent) {
		IToolStats tStats = getToolStats(stack);
		return tStats == null ? aDefault : tStats.getMiningSpeed(block, meta, aDefault, player, player.worldObj, x, y, z);
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		if (player.worldObj.isRemote) {
			return false;
		}
		IToolStats tStats = getToolStats(stack);
		Block block = player.worldObj.getBlock(x, y, z);
		if (tStats.isChainsaw() && (block instanceof IShearable)) {
			IShearable target = (IShearable) block;
			if ((target.isShearable(stack, player.worldObj, x, y, z))) {
				ArrayList<ItemStack> drops = target.onSheared(stack, player.worldObj, x, y, z, getEnchantmentLevel(Enchantment.fortune.effectId, stack));
				for (ItemStack is : drops) {
					float f = 0.7F;
					double d = itemRand.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = itemRand.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = itemRand.nextFloat() * f + (1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, x + d, y + d1, z + d2, is);
					entityitem.delayBeforeCanPickup = 10;
					player.worldObj.spawnEntityInWorld(entityitem);
				}
				player.addStat(net.minecraft.stats.StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
				onBlockDestroyed(stack, player.worldObj, block, x, y, z, player);
			}
			return false;
		}
		return super.onBlockStartBreak(stack, x, y, z, player);
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		IToolStats tStats = getToolStats(stack);
		if (tStats == null || !isItemStackUsable(stack)) return true;
		doSoundAtClient(tStats.getEntityHitSound(), 1, 1.0F);
		if (super.onLeftClickEntity(stack, player, entity)) return true;
		if (entity.canAttackWithItem() && !entity.hitByEntity(player)) {
			float tMagicDamage = tStats.getMagicDamageAgainstEntity(entity instanceof EntityLivingBase ? getEnchantmentModifierLiving(player, (EntityLivingBase) entity) : 0.0F, entity, stack, player), tDamage = tStats.getNormalDamageAgainstEntity((float) player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() + getToolCombatDamage(stack), entity, stack, player);
			if (tDamage + tMagicDamage > 0.0F) {
				
				boolean tCriticalHit = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && entity instanceof EntityLivingBase;
				
				if (tCriticalHit && tDamage > 0.0F) tDamage *= 1.5F;
				tDamage += tMagicDamage;
				if (entity.attackEntityFrom(tStats.getDamageSource(player, entity), tDamage)) {
					
					if (entity instanceof EntityLivingBase) {
						entity.setFire(getFireAspectModifier(player) * 4);
					}
					
					int tKnockcack = (player.isSprinting() ? 1 : 0) + (entity instanceof EntityLivingBase ? getKnockbackModifier(player, (EntityLivingBase) entity) : 0);
					if (tKnockcack > 0) {
						entity.addVelocity(-MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F) * tKnockcack * 0.5F, 0.1D, MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F) * tKnockcack * 0.5F);
						player.motionX *= 0.6D;
						player.motionZ *= 0.6D;
						player.setSprinting(false);
					}
					if (tCriticalHit) {
						player.onCriticalHit(entity);
					}
					if (tMagicDamage > 0.0F) {
						player.onEnchantmentCritical(entity);
					}
					if (tDamage >= 18.0F) {
						player.triggerAchievement(AchievementList.overkill);
					}
					player.setLastAttacker(entity);
					if (entity instanceof EntityLivingBase) {
						func_151384_a((EntityLivingBase) entity, player);
					}
					func_151385_b(player, entity);
					if (entity instanceof EntityLivingBase) {
						player.addStat(StatList.damageDealtStat, Math.round(tDamage * 10.0F));
					}
					entity.hurtResistantTime = Math.max(1, tStats.getHurtResistanceTime(entity.hurtResistantTime, entity));
					player.addExhaustion(0.3F);
					doDamage(stack, tStats.getToolDamagePerEntityAttack());
				}
			}
		}
		if (stack.stackSize <= 0) player.destroyCurrentEquippedItem();
		return true;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		IToolStats tStats = getToolStats(stack);
		if (tStats != null && tStats.canBlock()) player.setItemInUse(stack, 72000);
		return super.onItemRightClick(stack, world, player);
	}
	
	@Override
	public final int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}
	
	@Override
	public final EnumAction getItemUseAction(ItemStack stack) {
		IToolStats tStats = getToolStats(stack);
		if (tStats != null && tStats.canBlock()) return EnumAction.block;
		return EnumAction.none;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public final void getSubItems(Item aItem, CreativeTabs aCreativeTab, List aList) {
		for (int i = 0; i < 32766; i += 2) {
			if (getToolStats(new ItemStack(this, 1, i)) != null) {
				ItemStack tStack = new ItemStack(this, 1, i);
				isItemStackUsable(tStack);
				aList.add(tStack);
				aList.add(getToolWithStats(i, 1, MaterialList.COBALT, MaterialList.COBALT, null));
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister aIconRegister) {
		//
	}
	
	@Override
	public final IIcon getIconFromDamage(int meta) {
		return null;
	}
	
	@Override
	@SuppressWarnings({"PointlessArithmeticExpression"})
	public void addToolTip(List<String> aList, ItemStack stack, EntityPlayer player) {
		long maxDmg = getToolMaxDamage(stack);
		Materials tMaterial = getPrimaryMaterial(stack);
		IToolStats tStats = getToolStats(stack);
		
		int ind = getElectricStats(stack) != null ? 2 : 1;
		if (tStats != null) {
			int harvestLevel = getHarvestLevel(stack, "");
			float combatDamage = getToolCombatDamage(stack, tMaterial);
			long toolDmg = getToolDamage(stack);
			if (tStats instanceof IToolTurbine) {
				((IToolTurbine) tStats).addToolTip(aList, ind, tStats, tMaterial, toolDmg, maxDmg, combatDamage, harvestLevel);
			} else {
				aList.add(ind + 0, WHITE + String.format(transItem("001", "Durability: %s/%s"), "" + GREEN + (maxDmg - toolDmg) + " ", " " + maxDmg) + GRAY);
				aList.add(ind + 1, WHITE + String.format(transItem("002", "%s lvl %s"), tMaterial.localName + YELLOW, "" + harvestLevel) + GRAY);
				aList.add(ind + 2, WHITE + String.format(transItem("003", "Attack Damage: %s"), "" + BLUE + combatDamage) + GRAY);
				aList.add(ind + 3, WHITE + String.format(transItem("004", "Mining Speed: %s"), "" + GOLD + Math.max(Float.MIN_NORMAL, tStats.getSpeedMultiplier() * tMaterial.mToolSpeed)) + GRAY);
				
				NBTTagCompound nbt = stack.getTagCompound();
				if (nbt != null) {
					nbt = nbt.getCompoundTag(NBT_SA_TOOL_IDENTITY);
					if (nbt != null && nbt.hasKey(NBT_HEAT)) {
						int tHeat = nbt.getInteger(NBT_HEAT);
						long tWorldTime = player.getEntityWorld().getWorldTime();
						if (nbt.hasKey(NBT_HEAT_TIME)) {
							long tHeatTime = nbt.getLong(NBT_HEAT_TIME);
							if (tWorldTime > (tHeatTime + 10)) {
								tHeat = (int) (tHeat - ((tWorldTime - tHeatTime) / 10));
								if (tHeat < 300 && tHeat > -10000) tHeat = 300;
							}
							nbt.setLong(NBT_HEAT_TIME, tWorldTime);
							if (tHeat > -10000) {
								nbt.setInteger(NBT_HEAT, tHeat);
							}
						}
						aList.add(ind + 3, RED + "Heat: " + nbt.getInteger(NBT_HEAT) + " K" + GRAY);
					}
				}
			}
		}
	}
	
	@Override
	public Long[] getFluidContainerStats(ItemStack stack) {
		return null;
	}
	
	@Override
	public Long[] getElectricStats(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			nbt = nbt.getCompoundTag(NBT_SA_TOOL_IDENTITY);
			if (nbt != null && nbt.getBoolean(NBT_ELECTRIC)) return new Long[]{nbt.getLong(NBT_MAX_CHARGE), nbt.getLong(NBT_VOLTAGE), nbt.getLong(NBT_TIER), nbt.getLong(NBT_SPECIAL_DATA)};
		}
		return null;
	}
	
	public float getToolCombatDamage(ItemStack stack) {
		IToolStats tStats = getToolStats(stack);
		if (tStats == null) return 0;
		return tStats.getBaseDamage() + getPrimaryMaterial(stack).mToolQuality;
	}
	
	public float getToolCombatDamage(ItemStack stack, Materials mat) {
		IToolStats tStats = getToolStats(stack);
		if (tStats == null) return 0;
		return tStats.getBaseDamage() + mat.mToolQuality;
	}
	
	@Override
	public final boolean doDamageToItem(ItemStack stack, int dmg) {
		return doDamage(stack, dmg * 100L);
	}
	
	public final boolean doDamage(ItemStack stack, long amount) {
		if (!isItemStackUsable(stack)) return false;
		Long[] tElectric = getElectricStats(stack);
		
		if (tElectric == null) {
			incStackSizeByDamage(stack, amount);
			return true;
		}
		if (use(stack, (int) amount, null)) {
			if (ThreadLocalRandom.current().nextInt(0, 25) == 0) {
				incStackSizeByDamage(stack, amount);
			}
			return true;
		}
		return false;
	}
	
	private void incStackSizeByDamage(ItemStack stack, long amount) {
		long tNewDamage = getToolDamage(stack) + amount;
		setToolDamage(stack, tNewDamage);
		if (tNewDamage >= getToolMaxDamage(stack)) {
			IToolStats tStats = getToolStats(stack);
			if (tStats == null || setStack(stack, tStats.getBrokenItem(stack)) == null) {
				if (tStats != null) {
					doSoundAtClient(tStats.getBreakingSound(), 1, 1.0F);
				}
				if (stack.stackSize > 0) stack.stackSize--;
			}
		}
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		if (!isItemStackUsable(stack)) return 0.0F;
		IToolStats tStats = getToolStats(stack);
		if (tStats == null || Math.max(0, getHarvestLevel(stack, "")) < block.getHarvestLevel(meta)) return 0.0F;
		return tStats.isMinableBlock(block, meta) ? Math.max(Float.MIN_NORMAL, tStats.getSpeedMultiplier() * getPrimaryMaterial(stack).mToolSpeed) : 0.0F;
	}
	
	@Override
	public final boolean canHarvestBlock(Block block, ItemStack stack) {
		return getDigSpeed(stack, block, 0) > 0.0F;
	}
	
	@Override
	public final int getHarvestLevel(ItemStack stack, String aToolClass) {
		IToolStats tStats = getToolStats(stack);
		return tStats == null ? -1 : tStats.getBaseQuality() + getPrimaryMaterial(stack).mToolQuality;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase player) {
		if (!isItemStackUsable(stack)) return false;
		IToolStats tStats = getToolStats(stack);
		if (tStats == null) return false;
		doSoundAtClient(tStats.getMiningSound(), 1, 1.0F);
		doDamage(stack, (int) Math.max(1, block.getBlockHardness(world, x, y, z) * tStats.getToolDamagePerBlockBreak()));
		return getDigSpeed(stack, block, world.getBlockMetadata(x, y, z)) > 0.0F;
	}
	
	@Override
	public final ItemStack getContainerItem(ItemStack stack) {
		return getContainerItem(stack, true);
	}
	
	@Override
	public final boolean hasContainerItem(ItemStack stack) {
		return getContainerItem(stack, false) != null;
	}
	
	private ItemStack getContainerItem(ItemStack is, boolean playSound) {
		if (!isItemStackUsable(is)) return null;
		is = copyAmount(1, is);
		IToolStats tStats = getToolStats(is);
		if (tStats == null) return null;
		doDamage(is, tStats.getToolDamagePerContainerCraft());
		is = is != null && is.stackSize > 0 ? is : null;
		if (playSound && ServerEvents.INSTANCE.mTicksUntilNextCraftSound <= 0) {
			ServerEvents.INSTANCE.mTicksUntilNextCraftSound = 10;
			String sound = (is == null) ? tStats.getBreakingSound() : tStats.getCraftingSound();
			doSoundAtClient(sound, 1, 1.0F);
		}
		return is;
	}
	
	public IToolStats getToolStats(ItemStack stack) {
		isItemStackUsable(stack);
		return getToolStatsInternal(stack);
	}
	
	private IToolStats getToolStatsInternal(ItemStack stack) {
		return stack == null ? null : mToolStats.get((short) stack.getItemDamage());
	}
	
	@Override
	public boolean canWhack(EntityPlayer player, ItemStack stack, int x, int y, int z) {
		if (!isItemStackUsable(stack)) return false;
		IToolStats tStats = getToolStats(stack);
		return tStats != null && tStats.isCrowbar();
	}
	
	@Override
	public void onWhack(EntityPlayer player, ItemStack stack, int x, int y, int z) {
		IToolStats tStats = getToolStats(stack);
		if (tStats != null) doDamage(stack, tStats.getToolDamagePerEntityAttack());
	}
	
	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {
		if (player == null) return false;
		if (player.getCurrentEquippedItem() == null) return false;
		if (!isItemStackUsable(player.getCurrentEquippedItem())) return false;
		IToolStats tStats = getToolStats(player.getCurrentEquippedItem());
		return tStats != null && tStats.isWrench();
	}
	
	@Override
	public boolean canWrench(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		return canWrench(player, x, y, z);
	}
	
	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
		if (player == null) return;
		if (player.getCurrentEquippedItem() == null) return;
		IToolStats tStats = getToolStats(player.getCurrentEquippedItem());
		if (tStats != null) doDamage(player.getCurrentEquippedItem(), tStats.getToolDamagePerEntityAttack());
	}
	
	@Override
	public boolean canUse(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		return canWrench(player, x, y, z);
	}
	
	@Override
	public void used(ItemStack stack, EntityPlayer player, int x, int y, int z) {
		wrenchUsed(player, x, y, z);
	}
	
	@Override
	public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
		if (player == null) return false;
		if (player.getCurrentEquippedItem() == null) return false;
		if (!isItemStackUsable(player.getCurrentEquippedItem())) return false;
		IToolStats tStats = getToolStats(player.getCurrentEquippedItem());
		return tStats.isWrench();
	}
	
	@Override
	public boolean canLink(EntityPlayer player, ItemStack stack, EntityMinecart cart) {
		if (!isItemStackUsable(stack)) return false;
		IToolStats tStats = getToolStats(stack);
		return tStats != null && tStats.isCrowbar();
	}
	
	@Override
	public void onLink(EntityPlayer player, ItemStack stack, EntityMinecart cart) {
		IToolStats tStats = getToolStats(stack);
		if (tStats != null) doDamage(stack, tStats.getToolDamagePerEntityAttack());
	}
	
	@Override
	public boolean canBoost(EntityPlayer player, ItemStack stack, EntityMinecart cart) {
		if (!isItemStackUsable(stack)) return false;
		IToolStats tStats = getToolStats(stack);
		return tStats != null && tStats.isCrowbar();
	}
	
	@Override
	public void onBoost(EntityPlayer player, ItemStack stack, EntityMinecart cart) {
		IToolStats tStats = getToolStats(stack);
		if (tStats != null) doDamage(stack, tStats.getToolDamagePerEntityAttack());
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		IToolStats tStats = getToolStats(stack);
		if (tStats != null && player != null) tStats.onToolCrafted(stack, player);
		super.onCreated(stack, world, player);
	}
	
	@Override
	public final boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
		return false;
	}
	
	@Override
	public final int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public boolean isFull3D() {
		return true;
	}
	
	@Override
	public boolean isItemStackUsable(ItemStack stack) {
		IToolStats tStats = getToolStatsInternal(stack);
		if (stack.getItemDamage() % 2 != 0 || tStats == null) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt != null) nbt.removeTag(NBT_ENCHANTED);
			return false;
		}
		Materials aMaterial = getPrimaryMaterial(stack);
		HashMap<Integer, Integer> tMap = new HashMap<>(), tResult = new HashMap<>();
		if (aMaterial.enchantmentTools != null) {
			tMap.put(aMaterial.enchantmentTools.effectId, (int) aMaterial.enchantmentToolsLevel);
			if (aMaterial.enchantmentTools == Enchantment.fortune) tMap.put(Enchantment.looting.effectId, (int) aMaterial.enchantmentToolsLevel);
			if (aMaterial.enchantmentTools == Enchantment.knockback) tMap.put(Enchantment.power.effectId, (int) aMaterial.enchantmentToolsLevel);
			if (aMaterial.enchantmentTools == Enchantment.fireAspect) tMap.put(Enchantment.flame.effectId, (int) aMaterial.enchantmentToolsLevel);
		}
		Enchantment[] tEnchants = tStats.getEnchantments(stack);
		int[] tLevels = tStats.getEnchantmentLevels(stack);
		for (int i = 0; i < tEnchants.length; i++)
			if (tLevels[i] > 0) {
				Integer tLevel = tMap.get(tEnchants[i].effectId);
				tMap.put(tEnchants[i].effectId, tLevel == null ? tLevels[i] : tLevel == tLevels[i] ? tLevel + 1 : Math.max(tLevel, tLevels[i]));
			}
		for (Map.Entry<Integer, Integer> tEntry : tMap.entrySet()) {
			if (tEntry.getKey() == 33 || (tEntry.getKey() == 20 && tEntry.getValue() > 2) || tEntry.getKey() == EnchRadioactivity.INSTANCE.effectId) tResult.put(tEntry.getKey(), tEntry.getValue());
			else switch (Enchantment.enchantmentsList[tEntry.getKey()].type) {
				case weapon:
					if (tStats.isWeapon()) tResult.put(tEntry.getKey(), tEntry.getValue());
					break;
				case all:
					tResult.put(tEntry.getKey(), tEntry.getValue());
					break;
				case armor:
				case armor_feet:
				case armor_head:
				case armor_legs:
				case armor_torso:
				case breakable:
				case fishing_rod:
					break;
				case bow:
					if (tStats.isRangedWeapon()) tResult.put(tEntry.getKey(), tEntry.getValue());
					break;
				case digger:
					if (tStats.isMiningTool()) tResult.put(tEntry.getKey(), tEntry.getValue());
					break;
			}
		}
		EnchantmentHelper.setEnchantments(tResult, stack);
		return true;
	}
	
	@Override
	public short getChargedMetaData(ItemStack stack) {
		return (short) (stack.getItemDamage() - (stack.getItemDamage() % 2));
	}
	
	@Override
	public short getEmptyMetaData(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) nbt.removeTag(NBT_ENCHANTED);
		return (short) (stack.getItemDamage() + 1 - (stack.getItemDamage() % 2));
	}
	
	@Override
	public int getItemEnchantability() {
		return 0;
	}
	
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack aBook) {
		return false;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack aMaterial) {
		return false;
	}
}

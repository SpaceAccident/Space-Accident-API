package space.accident.api.interfaces.tools;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.items.tools.MetaGenerated_Tool;

import java.util.List;

/**
 * The Stats for Tools. Not including any Material Modifiers.
 * <p/>
 * And this is supposed to not have any ItemStack Parameters as these are generic Stats.
 */
public interface IToolStats {
	/**
	 * Called when player crafts this Tool
	 */
	void onToolCrafted(ItemStack stack, EntityPlayer player);
	
	/**
	 * Called when this gets added to a Tool Item
	 */
	void onStatsAddedToTool(MetaGenerated_Tool aItem, int id);
	
	/**
	 * @return Damage the Tool receives when breaking a Block. 100 is one Damage Point (or 100 EU).
	 */
	int getToolDamagePerBlockBreak();
	
	/**
	 * @return Damage the Tool receives when converting the drops of a Block. 100 is one Damage Point (or 100 EU).
	 */
	int getToolDamagePerDropConversion();
	
	/**
	 * @return Damage the Tool receives when being used as Container Item. 100 is one use, however it is usually 8 times more than normal.
	 */
	int getToolDamagePerContainerCraft();
	
	/**
	 * @return Damage the Tool receives when being used as Weapon, 200 is the normal Value, 100 for actual Weapons.
	 */
	int getToolDamagePerEntityAttack();
	
	/**
	 * @return Basic Quality of the Tool, 0 is normal. If increased, it will increase the general quality of all Tools of this Type. Decreasing is also possible.
	 */
	int getBaseQuality();
	
	/**
	 * @return The Damage Bonus for this Type of Tool against Mobs. 1.0F is normal punch.
	 */
	float getBaseDamage();
	
	/**
	 * @return This gets the Hurt Resistance time for Entities getting hit. (always does 1 as minimum)
	 */
	int getHurtResistanceTime(int aOriginalHurtResistance, Entity entity);
	
	/**
	 * @return This is a multiplier for the Tool Speed. 1.0F = no special Speed.
	 */
	float getSpeedMultiplier();
	
	/**
	 * @return This is a multiplier for the Tool Speed. 1.0F = no special Durability.
	 */
	float getMaxDurabilityMultiplier();
	
	DamageSource getDamageSource(EntityLivingBase player, Entity entity);
	
	String getMiningSound();
	
	String getCraftingSound();
	
	String getEntityHitSound();
	
	String getBreakingSound();
	
	Enchantment[] getEnchantments(ItemStack stack);
	
	int[] getEnchantmentLevels(ItemStack stack);
	
	/**
	 * @return If this Tool can be used for blocking Damage like a Sword.
	 */
	boolean canBlock();
	
	/**
	 * @return If this Tool can be used as an RC Crowbar.
	 */
	boolean isCrowbar();
	
	
	boolean isChainsaw();
	
	/**
	 * @return If this Tool can be used as an BC Wrench.
	 */
	boolean isWrench();
	
	/**
	 * @return If this Tool can be used as Weapon i.e. if that is the main purpose.
	 */
	boolean isWeapon();
	
	/**
	 * @return If this Tool is a Ranged Weapon. Return false at isWeapon unless you have a Blade attached to your Bow/Gun or something
	 */
	boolean isRangedWeapon();
	
	/**
	 * @return If this Tool can be used as Weapon i.e. if that is the main purpose.
	 */
	boolean isMiningTool();
	
	/**
	 * block.getHarvestTool(meta) can return the following Values for example.
	 * "axe", "pickaxe", "sword", "shovel", "hoe", "grafter", "saw", "wrench", "crowbar", "file", "hammer", "plow", "plunger", "scoop", "screwdriver", "sense", "scythe", "softhammer", "cutter", "plasmatorch"
	 *
	 * @return If this is a minable Block. Tool Quality checks (like Diamond Tier or something) are separate from this check.
	 */
	boolean isMinableBlock(Block block, int meta);
	
	/**
	 * This lets you modify the Drop List, when this type of Tool has been used.
	 *
	 * @return the Amount of modified Items.
	 */
	int convertBlockDrops(List<ItemStack> aDrops, ItemStack stack, EntityPlayer player, Block block,
						  int x, int y, int z, int meta, int aFortune, boolean aSilkTouch, BlockEvent.HarvestDropsEvent aEvent);
	
	/**
	 * @return Returns a broken Version of the Item.
	 */
	ItemStack getBrokenItem(ItemStack stack);
	
	/**
	 * @return the Damage actually done to the Mob.
	 */
	float getNormalDamageAgainstEntity(float aOriginalDamage, Entity entity, ItemStack stack, EntityPlayer player);
	
	/**
	 * @return the Damage actually done to the Mob.
	 */
	float getMagicDamageAgainstEntity(float aOriginalDamage, Entity entity, ItemStack stack, EntityPlayer player);
	
	IIconContainer getIcon(boolean aIsToolHead, ItemStack stack);
	
	int[] getRGBa(boolean aIsToolHead, ItemStack stack);
	
	float getMiningSpeed(Block block, int meta, float aDefault, EntityPlayer player, World worldObj, int x, int y, int z);
}

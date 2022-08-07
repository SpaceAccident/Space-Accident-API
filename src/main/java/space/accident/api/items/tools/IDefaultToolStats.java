package space.accident.api.items.tools;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import space.accident.api.API;
import space.accident.api.damagesources.DamageSources;
import space.accident.api.interfaces.tools.IToolStats;
import space.accident.api.sound.Sounds;

import java.util.List;

public interface IDefaultToolStats extends IToolStats {
	
	public static Enchantment[] FORTUNE_ENCHANTMENT = {Enchantment.fortune};
	public static Enchantment[] LOOTING_ENCHANTMENT = {Enchantment.looting};
	public static Enchantment[] ZERO_ENCHANTMENTS = new Enchantment[0];
	public static int[] ZERO_ENCHANTMENT_LEVELS = new int[0];
	
	@Override
	default int getToolDamagePerBlockBreak() {
		return 100;
	}
	
	@Override
	default int getToolDamagePerDropConversion() {
		return 100;
	}
	
	@Override
	default int getToolDamagePerContainerCraft() {
		return 800;
	}
	
	@Override
	default int getToolDamagePerEntityAttack() {
		return 200;
	}
	
	@Override
	default float getSpeedMultiplier() {
		return 1.0F;
	}
	
	@Override
	default float getMaxDurabilityMultiplier() {
		return 1.0F;
	}
	
	@Override
	default int getHurtResistanceTime(int aOriginalHurtResistance, Entity entity) {
		return aOriginalHurtResistance;
	}
	
	@Override
	default String getMiningSound() {
		return null;
	}
	
	@Override
	default String getCraftingSound() {
		return null;
	}
	
	@Override
	default String getEntityHitSound() {
		return null;
	}
	
	@Override
	default String getBreakingSound() {
		return API.sSoundList.get(Sounds.BREAK);
	}
	
	@Override
	default int getBaseQuality() {
		return 0;
	}
	
	@Override
	default boolean canBlock() {
		return false;
	}
	
	@Override
	default boolean isCrowbar() {
		return false;
	}
	
	@Override
	default boolean isChainsaw() {
		return false;
	}
	
	@Override
	default boolean isWrench() {
		return false;
	}
	
	@Override
	default boolean isWeapon() {
		return false;
	}
	
	@Override
	default boolean isRangedWeapon() {
		return false;
	}
	
	@Override
	default boolean isMiningTool() {
		return true;
	}
	
	@Override
	default DamageSource getDamageSource(EntityLivingBase player, Entity entity) {
		return DamageSources.getCombatDamage((player instanceof EntityPlayer) ? "player" : "mob", player, (entity instanceof EntityLivingBase) ? getDeathMessage(player, (EntityLivingBase) entity) : null);
	}
	
	default IChatComponent getDeathMessage(EntityLivingBase player, EntityLivingBase entity) {
		return new EntityDamageSource((player instanceof EntityPlayer) ? "player" : "mob", player).func_151519_b(entity);
	}
	
	@Override
	default int convertBlockDrops(List<ItemStack> aDrops, ItemStack stack, EntityPlayer player, Block block, int x, int y, int z, int meta, int aFortune, boolean aSilkTouch, BlockEvent.HarvestDropsEvent aEvent) {
		return 0;
	}
	
	@Override
	default ItemStack getBrokenItem(ItemStack stack) {
		return null;
	}
	
	@Override
	default Enchantment[] getEnchantments(ItemStack stack) {
		return ZERO_ENCHANTMENTS;
	}
	
	@Override
	default int[] getEnchantmentLevels(ItemStack stack) {
		return ZERO_ENCHANTMENT_LEVELS;
	}
	
	@Override
	default void onToolCrafted(ItemStack stack, EntityPlayer player) {
		player.triggerAchievement(AchievementList.openInventory);
		player.triggerAchievement(AchievementList.mineWood);
		player.triggerAchievement(AchievementList.buildWorkBench);
	}
	
	@Override
	default void onStatsAddedToTool(MetaGenerated_Tool aItem, int id) {
	}
	
	@Override
	default float getNormalDamageAgainstEntity(float aOriginalDamage, Entity entity, ItemStack stack, EntityPlayer player) {
		return aOriginalDamage;
	}
	
	@Override
	default float getMagicDamageAgainstEntity(float aOriginalDamage, Entity entity, ItemStack stack, EntityPlayer player) {
		return aOriginalDamage;
	}
	
	@Override
	default float getMiningSpeed(Block block, int meta, float aDefault, EntityPlayer player, World worldObj, int x, int y, int z) {
		return aDefault;
	}
}

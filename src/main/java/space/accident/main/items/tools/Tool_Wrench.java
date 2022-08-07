package space.accident.main.items.tools;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import space.accident.api.API;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.items.tools.MetaGenerated_Tool;
import space.accident.api.items.tools.IDefaultToolStats;
import space.accident.api.sound.Sounds;
import space.accident.main.items.tools.behaviors.Behaviour_Wrench;

import static space.accident.api.util.Utility.*;


public class Tool_Wrench implements IDefaultToolStats {
	
	@Override
	public int getToolDamagePerBlockBreak() {
		return 50;
	}
	
	@Override
	public int getToolDamagePerDropConversion() {
		return 100;
	}
	
	@Override
	public int getToolDamagePerContainerCraft() {
		return 800;
	}
	
	@Override
	public int getToolDamagePerEntityAttack() {
		return 200;
	}
	
	@Override
	public int getBaseQuality() {
		return 0;
	}
	
	@Override
	public float getBaseDamage() {
		return 3.0F;
	}
	
	@Override
	public int getHurtResistanceTime(int aOriginalHurtResistance, Entity entity) {
		return aOriginalHurtResistance * 2;
	}
	
	@Override
	public float getSpeedMultiplier() {
		return 1.0F;
	}
	
	@Override
	public float getMaxDurabilityMultiplier() {
		return 1.0F;
	}
	
	@Override
	public String getCraftingSound() {
		return API.sSoundList.get(Sounds.WRENCH);
	}
	
	@Override
	public String getEntityHitSound() {
		return null;
	}
	
	@Override
	public String getBreakingSound() {
		return API.sSoundList.get(Sounds.BREAK);
	}
	
	@Override
	public String getMiningSound() {
		return API.sSoundList.get(Sounds.WRENCH);
	}
	
	@Override
	public boolean canBlock() {
		return false;
	}
	
	@Override
	public boolean isCrowbar() {
		return false;
	}
	
	@Override
	public boolean isWrench() {
		return true;
	}
	
	@Override
	public boolean isMinableBlock(Block block, int meta) {
		return isAppropriateTool(block, meta, "wrench")
				|| isAppropriateMaterial(block, Material.piston)
				|| isSpecialBlock(block, Blocks.hopper, Blocks.dispenser, Blocks.dropper);
	}
	
	@Override
	public ItemStack getBrokenItem(ItemStack stack) {
		return null;
	}
	
	@Override
	public IIconContainer getIcon(boolean aIsToolHead, ItemStack stack) {
		return aIsToolHead ? Textures.ItemIcons.WRENCH : null;
	}
	
	@Override
	public int[] getRGBa(boolean aIsToolHead, ItemStack stack) {
		return aIsToolHead ? MetaGenerated_Tool.getPrimaryMaterial(stack).mRGBa : null;
	}
	
	@Override
	public void onStatsAddedToTool(MetaGenerated_Tool aItem, int id) {
		aItem.addItemBehavior(id, new Behaviour_Wrench(100));
	}
	
	@Override
	public IChatComponent getDeathMessage(EntityLivingBase player, EntityLivingBase entity) {
		return new ChatComponentText(EnumChatFormatting.GREEN + player.getCommandSenderName() + EnumChatFormatting.WHITE + " threw a Monkey Wrench into the Plans of " + EnumChatFormatting.RED + entity.getCommandSenderName() + EnumChatFormatting.WHITE);
	}
}
package space.accident.api.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import space.accident.api.util.GT_LanguageManager;
import space.accident.extensions.ItemStackUtils;
import space.accident.extensions.StringUtils;

import java.util.List;

import static space.accident.api.enums.Values.RES_PATH_ITEM;
import static space.accident.main.Tags.MODID;

public class GenericItem extends Item {
	
	protected final String name, tooltip;
	
	@SideOnly(Side.CLIENT)
	protected IIcon icon;
	
	public GenericItem(String unLocalName, String englishName, String englishTooltip) {
		this(unLocalName, englishName, englishTooltip, true);
	}
	
	public GenericItem(String unLocalName, String englishName, String englishTooltip, boolean needTooltipToLang) {
		super();
		name = "sa." + unLocalName;
		GT_LanguageManager.addStringLocalization(name + ".name", englishName);
		if (StringUtils.isStringValid(englishTooltip)) {
			GT_LanguageManager.addStringLocalization(tooltip = name + ".tooltip_main", englishTooltip, needTooltipToLang);
		} else {
			tooltip = null;
		}
		GameRegistry.registerItem(this, name, MODID);
	}
	
	@Override
	public final Item setUnlocalizedName(String aName) {
		return this;
	}
	
	@Override
	public final String getUnlocalizedName() {
		return name;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getHasSubtypes() ? name + "." + getDamage(stack) : name;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister aIconRegister) {
		icon = aIconRegister.registerIcon(RES_PATH_ITEM + name);
	}
	
	@Override
	public boolean doesSneakBypassUse(World world, int aX, int aY, int aZ, EntityPlayer player) {
		return true;
	}
	
	@Override
	public IIcon getIconFromDamage(int meta) {
		return icon;
	}
	
	public int getTier(ItemStack stack) {
		return 0;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean aF3_H) {
		if (getMaxDamage() > 0 && !getHasSubtypes()) tooltips.add((stack.getMaxDamage() - getDamage(stack)) + " / " + stack.getMaxDamage());
		if (tooltip != null) {
			tooltips.add(GT_LanguageManager.getTranslation(tooltip));
		}
		if (ItemStackUtils.isElectricItem(stack)) {
			tooltips.add(String.format(GT_LanguageManager.getTranslation("Item_DESCRIPTION_Index_019"), getTier(stack) + ""));
		}
		addToolTip(tooltips, stack, player);
	}
	
	protected void addToolTip(List<String> tooltips, ItemStack stack, EntityPlayer player) {}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		isItemStackUsable(stack);
	}
	
	public boolean isItemStackUsable(ItemStack stack) {
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return null;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return getContainerItem(stack) != null;
	}
	
	public String transItem(String key, String english) {
		return GT_LanguageManager.addStringLocalization("Item_DESCRIPTION_Index_" + key, english, false);
	}
}
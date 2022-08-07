package space.accident.main.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import space.accident.api.enums.MaterialList;
import space.accident.api.enums.Materials;
import space.accident.api.items.GenericItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static space.accident.api.API.isDebug;
import static space.accident.api.util.Utility.getContainersFromFluid;
import static space.accident.api.util.Utility.getFluidDisplayStack;
import static space.accident.extensions.FluidUtils.getFluidName;
import static space.accident.extensions.NumberUtils.format;

@SuppressWarnings("ALL")
public class FluidDisplayItem extends GenericItem {
	
	private static final Map<Fluid, String> sFluidTooltips = new HashMap<>();
	
	public FluidDisplayItem() {
		super("fluid_display", "Fluid Display", null);
		ItemList.Display_Fluid.set(this);
	}
	
	public static void register() {
		new FluidDisplayItem();
	}
	
	public static boolean isCell(ItemStack tItemStack) {
		for (int tOreDict : OreDictionary.getOreIDs(tItemStack)) {
			String tOreDictName = OreDictionary.getOreName(tOreDict);
			if (tOreDictName.startsWith("cell")) return true;
		}
		return false;
	}
	
	public static Materials getMaterialFromCell(ItemStack tItemStack) {
		for (int tOreDict : OreDictionary.getOreIDs(tItemStack)) {
			String tOreDictName = OreDictionary.getOreName(tOreDict);
			if (tOreDictName.startsWith("cell")) {
				return Materials.getRealMaterial(
						tOreDictName.replace("cell", "")
								.replace("Molten", "")
								.replace("Plasma", "")
				);
			}
		}
		return MaterialList._NULL;
	}
	
	@Override
	protected void addToolTip(List aList, ItemStack stack, EntityPlayer player) {
		if (FluidRegistry.getFluid(stack.getItemDamage()) != null) {
			String tChemicalFormula = getChemicalFormula(new FluidStack(FluidRegistry.getFluid(stack.getItemDamage()), 1));
			if (!tChemicalFormula.isEmpty()) aList.add(EnumChatFormatting.YELLOW + tChemicalFormula + EnumChatFormatting.RESET);
		}
		NBTTagCompound nbt = stack.getTagCompound();
		if (isDebug) {
			Fluid tFluid = FluidRegistry.getFluid(stack.getItemDamage());
			if (tFluid != null) {
				aList.add("Registry: " + tFluid.getName());
			}
		}
		if (nbt != null) {
			long tToolTipAmount = nbt.getLong("mFluidDisplayAmount");
			if (tToolTipAmount > 0L) {
				aList.add(EnumChatFormatting.BLUE + "Amount: " + format(tToolTipAmount) + " L" + EnumChatFormatting.GRAY);
			}
			aList.add(EnumChatFormatting.RED + "Temperature: " + format(nbt.getLong("mFluidDisplayHeat")) + " K" + EnumChatFormatting.GRAY);
			aList.add(EnumChatFormatting.GREEN + String.format(transItem("018", "State: %s"), nbt.getBoolean("mFluidState") ? "Gas" : "Liquid") + EnumChatFormatting.GRAY);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister aIconRegister) {
	}
	
	@Override
	public IIcon getIconFromDamage(int meta) {
		return Stream.of(FluidRegistry.getFluid(meta), FluidRegistry.WATER)
				.filter(Objects::nonNull)
				.map(Fluid::getStillIcon)
				.filter(Objects::nonNull)
				.findFirst()
				.orElseThrow(IllegalStateException::new);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int aRenderPass) {
		Fluid tFluid = FluidRegistry.getFluid(stack.getItemDamage());
		return tFluid == null ? 16777215 : tFluid.getColor();
	}
	
	@Override
	public int getSpriteNumber() {
		return 0;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack != null) {
			return getFluidName(FluidRegistry.getFluid(stack.getItemDamage()), false);
		}
		return "";
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (stack != null) {
			return getFluidName(FluidRegistry.getFluid(stack.getItemDamage()), true);
		}
		return "";
	}
	
	@SideOnly(Side.CLIENT)
	public String getChemicalFormula(FluidStack aRealFluid) {
		return sFluidTooltips.computeIfAbsent(
				aRealFluid.getFluid(),
				fluid -> {
					for (ItemStack tContainer : getContainersFromFluid(aRealFluid)) {
						if (isCell(tContainer)) {
							Materials tMaterial = getMaterialFromCell(tContainer);
							if (!tMaterial.equals(MaterialList._NULL)) {
								if (tMaterial.chemicalFormula.equals("?")) {
									return "";
								} else {
									return tMaterial.chemicalFormula;
								}
							} else {
								List tTooltip = tContainer.getTooltip(null, true);
								for (Object tInfo : tTooltip) {
									if (!((String) tInfo).contains(" ") && !((String) tInfo).contains(":") && tTooltip.indexOf(tInfo) != 0) {
										return (String) tInfo;
									}
								}
							}
						}
					}
					return "";
				}
		);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item aItem, CreativeTabs aTab, List aList) {
		if (isDebug) {
			int i = 0;
			for (int j = FluidRegistry.getMaxID(); i < j; i++) {
				ItemStack tStack = getFluidDisplayStack(FluidRegistry.getFluid(i));
				if (tStack != null) {
					aList.add(tStack);
				}
			}
		}
	}
}

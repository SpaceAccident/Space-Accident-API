package space.accident.main.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import space.accident.api.enums.MaterialList;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.interfaces.tools.IToolStats;
import space.accident.api.items.tools.MetaGenerated_Tool;

import static space.accident.extensions.ItemStackUtils.isStackInvalid;
import static space.accident.main.render.RenderUtil.renderItemIcon;

public class MetaGeneratedToolRenderer implements IItemRenderer {
	
	public MetaGeneratedToolRenderer() {
		for (MetaGenerated_Tool tItem : MetaGenerated_Tool.sInstances.values()) {
			if (tItem != null) {
				MinecraftForgeClient.registerItemRenderer(tItem, this);
			}
		}
	}
	
	@Override
	public boolean handleRenderType(ItemStack stack, IItemRenderer.ItemRenderType aType) {
		if ((isStackInvalid(stack)) || (stack.getItemDamage() < 0)) {
			return false;
		}
		return (aType == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) || (aType == IItemRenderer.ItemRenderType.INVENTORY) || (aType == IItemRenderer.ItemRenderType.EQUIPPED) || (aType == IItemRenderer.ItemRenderType.ENTITY);
	}
	
	@Override
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType aType, ItemStack stack, IItemRenderer.ItemRendererHelper aHelper) {
		if (isStackInvalid(stack)) {
			return false;
		}
		return aType == IItemRenderer.ItemRenderType.ENTITY;
	}
	
	@Override
	public void renderItem(IItemRenderer.ItemRenderType aType, ItemStack stack, Object... data) {
		if (isStackInvalid(stack)) {
			return;
		}
		MetaGenerated_Tool aItem = (MetaGenerated_Tool) stack.getItem();
		GL11.glEnable(3042);
		if (aType == IItemRenderer.ItemRenderType.ENTITY) {
			if (RenderItem.renderInFrame) {
				GL11.glScalef(0.85F, 0.85F, 0.85F);
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslated(-0.5D, -0.42D, 0.0D);
			} else {
				GL11.glTranslated(-0.5D, -0.42D, 0.0D);
			}
		}
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		
		IToolStats tToolStats = aItem.getToolStats(stack);
		if (tToolStats != null) {
			IIconContainer aIcon = tToolStats.getIcon(false, stack);
			if (aIcon != null) {
				IIcon tIcon = aIcon.getIcon();
				IIcon tOverlay = aIcon.getOverlayIcon();
				if (tIcon != null) {
					Minecraft.getMinecraft().renderEngine.bindTexture(aIcon.getTextureFile());
					GL11.glBlendFunc(770, 771);
					int[] tModulation = tToolStats.getRGBa(false, stack);
					GL11.glColor3f(tModulation[0] / 255.0F, tModulation[1] / 255.0F, tModulation[2] / 255.0F);
					if (aType.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
						renderItemIcon(tIcon, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
					} else {
						ItemRenderer.renderItemIn2D(Tessellator.instance, tIcon.getMaxU(), tIcon.getMinV(), tIcon.getMinU(), tIcon.getMaxV(), tIcon.getIconWidth(), tIcon.getIconHeight(), 0.0625F);
					}
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
				}
				if (tOverlay != null) {
					Minecraft.getMinecraft().renderEngine.bindTexture(aIcon.getTextureFile());
					GL11.glBlendFunc(770, 771);
					if (aType.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
						renderItemIcon(tOverlay, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
					} else {
						ItemRenderer.renderItemIn2D(Tessellator.instance, tOverlay.getMaxU(), tOverlay.getMinV(), tOverlay.getMinU(), tOverlay.getMaxV(), tOverlay.getIconWidth(), tOverlay.getIconHeight(), 0.0625F);
					}
				}
			}
			aIcon = tToolStats.getIcon(true, stack);
			if (aIcon != null) {
				IIcon tIcon = aIcon.getIcon();
				IIcon tOverlay = aIcon.getOverlayIcon();
				if (tIcon != null) {
					Minecraft.getMinecraft().renderEngine.bindTexture(aIcon.getTextureFile());
					GL11.glBlendFunc(770, 771);
					int[] tModulation = tToolStats.getRGBa(true, stack);
					GL11.glColor3f(tModulation[0] / 255.0F, tModulation[1] / 255.0F, tModulation[2] / 255.0F);
					if (aType.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
						renderItemIcon(tIcon, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
					} else {
						ItemRenderer.renderItemIn2D(Tessellator.instance, tIcon.getMaxU(), tIcon.getMinV(), tIcon.getMinU(), tIcon.getMaxV(), tIcon.getIconWidth(), tIcon.getIconHeight(), 0.0625F);
					}
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
				}
				if (tOverlay != null) {
					Minecraft.getMinecraft().renderEngine.bindTexture(aIcon.getTextureFile());
					GL11.glBlendFunc(770, 771);
					if (aType.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
						renderItemIcon(tOverlay, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
					} else {
						ItemRenderer.renderItemIn2D(Tessellator.instance, tOverlay.getMaxU(), tOverlay.getMinV(), tOverlay.getMinU(), tOverlay.getMaxV(), tOverlay.getIconWidth(), tOverlay.getIconHeight(), 0.0625F);
					}
				}
			}
			if ((aType == IItemRenderer.ItemRenderType.INVENTORY) && (MetaGenerated_Tool.getPrimaryMaterial(stack) != MaterialList._NULL)) {
				long tDamage = MetaGenerated_Tool.getToolDamage(stack);
				long tMaxDamage = MetaGenerated_Tool.getToolMaxDamage(stack);
				if (tDamage <= 0L) {
					aIcon = Textures.ItemIcons.DURABILITY_BAR[8];
				} else if (tDamage >= tMaxDamage) {
					aIcon = Textures.ItemIcons.DURABILITY_BAR[0];
				} else {
					aIcon = Textures.ItemIcons.DURABILITY_BAR[((int) java.lang.Math.max(0L, java.lang.Math.min(7L, (tMaxDamage - tDamage) * 8L / tMaxDamage)))];
				}
				if (aIcon != null) {
					IIcon tIcon = aIcon.getIcon();
					IIcon tOverlay = aIcon.getOverlayIcon();
					if (tIcon != null) {
						Minecraft.getMinecraft().renderEngine.bindTexture(aIcon.getTextureFile());
						GL11.glBlendFunc(770, 771);
						if (aType.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
							renderItemIcon(tIcon, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
						} else {
							ItemRenderer.renderItemIn2D(Tessellator.instance, tIcon.getMaxU(), tIcon.getMinV(), tIcon.getMinU(), tIcon.getMaxV(), tIcon.getIconWidth(), tIcon.getIconHeight(), 0.0625F);
						}
					}
					if (tOverlay != null) {
						Minecraft.getMinecraft().renderEngine.bindTexture(aIcon.getTextureFile());
						GL11.glBlendFunc(770, 771);
						if (aType.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
							renderItemIcon(tOverlay, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
						} else {
							ItemRenderer.renderItemIn2D(Tessellator.instance, tOverlay.getMaxU(), tOverlay.getMinV(), tOverlay.getMinU(), tOverlay.getMaxV(), tOverlay.getIconWidth(), tOverlay.getIconHeight(), 0.0625F);
						}
					}
				}
				Long[] tStats = aItem.getElectricStats(stack);
				if ((tStats != null) && (tStats[3] < 0L)) {
					long tCharge = aItem.getRealCharge(stack);
					if (tCharge <= 0L) {
						aIcon = Textures.ItemIcons.ENERGY_BAR[0];
					} else if (tCharge >= tStats[0]) {
						aIcon = Textures.ItemIcons.ENERGY_BAR[8];
					} else {
						aIcon = Textures.ItemIcons.ENERGY_BAR[(7 - (int) java.lang.Math.max(0L, java.lang.Math.min(6L, (tStats[0] - tCharge) * 7L / tStats[0])))];
					}
				} else {
					aIcon = null;
				}
				if (aIcon != null) {
					IIcon tIcon = aIcon.getIcon();
					IIcon tOverlay = aIcon.getOverlayIcon();
					if (tIcon != null) {
						Minecraft.getMinecraft().renderEngine.bindTexture(aIcon.getTextureFile());
						GL11.glBlendFunc(770, 771);
						if (aType.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
							renderItemIcon(tIcon, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
						} else {
							ItemRenderer.renderItemIn2D(Tessellator.instance, tIcon.getMaxU(), tIcon.getMinV(), tIcon.getMinU(), tIcon.getMaxV(), tIcon.getIconWidth(), tIcon.getIconHeight(), 0.0625F);
						}
					}
					if (tOverlay != null) {
						Minecraft.getMinecraft().renderEngine.bindTexture(aIcon.getTextureFile());
						GL11.glBlendFunc(770, 771);
						if (aType.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
							renderItemIcon(tOverlay, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
						} else {
							ItemRenderer.renderItemIn2D(Tessellator.instance, tOverlay.getMaxU(), tOverlay.getMinV(), tOverlay.getMinU(), tOverlay.getMaxV(), tOverlay.getIconWidth(), tOverlay.getIconHeight(), 0.0625F);
						}
					}
				}
			}
		}
		GL11.glDisable(3042);
	}
}
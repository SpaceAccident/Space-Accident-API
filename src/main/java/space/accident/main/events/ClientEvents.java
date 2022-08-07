package space.accident.main.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import space.accident.api.API;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.ITurn;
import space.accident.api.objects.ItemStackData;

import java.util.Arrays;
import java.util.List;

import static space.accident.extensions.ItemStackUtils.isStackInList;
import static space.accident.main.render.RenderUtil.drawGrid;

public class ClientEvents {
	
	public static final int CHANGE_COMMON_DATA = 0;
	public static final int CHANGE_CUSTOM_DATA = 1;
	public static final int CHANGE_COLOR = 2;
	public static final int CHANGE_REDSTONE_OUTPUT = 3;
	public static final int DO_SOUND = 4;
	public static final int START_SOUND_LOOP = 5;
	public static final int STOP_SOUND_LOOP = 6;
	public static final int CHANGE_LIGHT = 7;
	private static final List<Block> ROTATABLE_VANILLA_BLOCKS = Arrays.asList(Blocks.piston, Blocks.sticky_piston, Blocks.furnace, Blocks.lit_furnace, Blocks.dropper, Blocks.dispenser, Blocks.chest, Blocks.trapped_chest, Blocks.ender_chest, Blocks.hopper, Blocks.pumpkin, Blocks.lit_pumpkin);
	public static int hideValue = 0;
	
	/**
	 * <p>Client tick counter that is set to 5 on hiding pipes and covers.</p>
	 * <p>It triggers a texture update next client tick when reaching 4, with provision for 3 more update tasks,
	 * spreading client change detection related work and network traffic on different ticks, until it reaches 0.</p>
	 */
	public static int changeDetected = 0;
	
	public static void register() {
		ClientEvents events = new ClientEvents();
		FMLCommonHandler.instance().bus().register(events);
		MinecraftForge.EVENT_BUS.register(events);
	}
	
	private static int shouldHeldItemHideThings() {
		try {
			final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if (player == null) return 0;
			final ItemStack tCurrentItem = player.getCurrentEquippedItem();
			if (tCurrentItem == null) return 0;
			final int[] ids = OreDictionary.getOreIDs(tCurrentItem);
			int hide = 0;
			for (int i : ids) {
				if (OreDictionary.getOreName(i).equals("craftingToolSolderingIron")) {
					hide |= 0x1;
					break;
				}
			}
			if (isStackInList(tCurrentItem, API.sWrenchList) || isStackInList(tCurrentItem, API.sScrewdriverList) || isStackInList(tCurrentItem, API.sHardHammerList) || isStackInList(tCurrentItem, API.sSoftHammerList) || isStackInList(tCurrentItem, API.sWireCutterList) || isStackInList(tCurrentItem, API.sSolderingToolList) || isStackInList(tCurrentItem, API.sCrowbarList) || API.sCovers.containsKey(new ItemStackData(tCurrentItem))) {
				hide |= 0x2;
			}
			return hide;
		} catch (Exception e) {
			return 0;
		}
	}
	
	@SubscribeEvent
	public void onClientTickEvent(cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent aEvent) {
		if (aEvent.phase == cpw.mods.fml.common.gameevent.TickEvent.Phase.END) {
			if (changeDetected > 0) changeDetected--;
			final int newHideValue = shouldHeldItemHideThings();
			if (newHideValue != hideValue) {
				hideValue = newHideValue;
				changeDetected = 5;
			}
		}
	}
	
	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent aEvent) {
		final Block block = aEvent.player.worldObj.getBlock(aEvent.target.blockX, aEvent.target.blockY, aEvent.target.blockZ);
		final TileEntity aTileEntity = aEvent.player.worldObj.getTileEntity(aEvent.target.blockX, aEvent.target.blockY, aEvent.target.blockZ);
		
		if (isStackInList(aEvent.currentItem, API.sWrenchList)) {
			if (aTileEntity instanceof ITurn || ROTATABLE_VANILLA_BLOCKS.contains(block) || aTileEntity instanceof IWrenchable) drawGrid(aEvent, false, true, aEvent.player.isSneaking());
			return;
		}
		
		if (!(aTileEntity instanceof ICoverable)) return;
		
		if (isStackInList(aEvent.currentItem, API.sWireCutterList) || isStackInList(aEvent.currentItem, API.sSolderingToolList)) {
			if (((ICoverable) aTileEntity).getCoverIDAtSide(aEvent.target.sideHit) == 0) drawGrid(aEvent, false, false, aEvent.player.isSneaking());
			return;
		}
		
		if ((aEvent.currentItem == null && aEvent.player.isSneaking()) || isStackInList(aEvent.currentItem, API.sCrowbarList) || isStackInList(aEvent.currentItem, API.sScrewdriverList)) {
			if (((ICoverable) aTileEntity).getCoverIDAtSide(aEvent.target.sideHit) == 0) for (int i = 0; i < 6; i++)
				if (((ICoverable) aTileEntity).getCoverIDAtSide(i) > 0) {
					drawGrid(aEvent, true, false, true);
					return;
				}
			return;
		}
		
		if (isStackInList(aEvent.currentItem, API.sCovers.keySet())) {
			if (((ICoverable) aTileEntity).getCoverIDAtSide(aEvent.target.sideHit) == 0) drawGrid(aEvent, true, false, aEvent.player.isSneaking());
		}

//		if (areStacksEqual(ItemList.Tool_Cover_Copy_Paste.get(1), aEvent.currentItem, true)) {
//			if (((ICoverable) aTileEntity).getCoverIDAtSide((int) aEvent.target.sideHit) == 0)
//				drawGrid(aEvent, true, false, aEvent.player.isSneaking());
//		}
	}
	
}

package space.accident.api.util;

import buildcraft.api.transport.IPipeTile;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.transport.IItemDuct;
import com.google.auto.value.AutoValue;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import space.accident.api.API;
import space.accident.api.damagesources.EnchRadioactivity;
import space.accident.api.damagesources.DamageSources;
import space.accident.api.enums.Materials;
import space.accident.api.items.GenericItem;
import space.accident.api.objects.ItemStackData;
import space.accident.api.objects.ItemData;
import space.accident.main.items.ItemList;
import space.accident.main.network.Packet_Sound;
import space.accident.main.threads.SoundThread;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static space.accident.api.API.*;
import static space.accident.api.enums.Values.V;
import static space.accident.api.enums.Values.W;
import static space.accident.api.util.ModHandler.canUseElectricItem;
import static space.accident.api.util.ModHandler.damageOrDechargeItem;
import static space.accident.extensions.ItemStackUtils.*;
import static space.accident.extensions.NumberUtils.getOppositeSide;
import static space.accident.extensions.NumberUtils.getTier;
import static space.accident.extensions.StringUtils.isStringInvalid;
import static space.accident.main.SpaceAccidentApi.NETWORK;
import static space.accident.main.SpaceAccidentApi.proxy;

@SuppressWarnings("ALL")
public class Utility {
	
	private static final List<FluidContainerRegistry.FluidContainerData> sFluidContainerList = new ArrayList<>();
	private static final Map<String, Fluid> sFluidUnlocalizedNameToFluid = new HashMap<>();
	private static final Map<Fluid, List<ItemStack>> sFluidToContainers = new HashMap<>();
	private static final Map<ItemStackData, Map<Fluid, FluidContainerRegistry.FluidContainerData>> sEmptyContainerToFluidToData = new HashMap<>();
	private static final Map<ItemStackData, FluidContainerRegistry.FluidContainerData> sFilledContainerToData = new HashMap<>();
	public static Map<PlayedSound, Integer> sPlayedSoundMap = new HashMap<>();
	private static final Field isDrawingField = ReflectionHelper.findField(Tessellator.class, "isDrawing", "field_78415_z");
	
	public static UUID defaultUuid = null;
	
	public static ItemStack setStack(ItemStack aSetStack, ItemStack aToStack) {
		if (isStackInvalid(aSetStack) || isStackInvalid(aToStack)) return null;
		((ItemStack) aSetStack).func_150996_a(((ItemStack) aToStack).getItem());
		((ItemStack) aSetStack).stackSize = ((ItemStack) aToStack).stackSize;
		Items.feather.setDamage((ItemStack) aSetStack, Items.feather.getDamage((ItemStack) aToStack));
		((ItemStack) aSetStack).setTagCompound(((ItemStack) aToStack).getTagCompound());
		return (ItemStack) aSetStack;
	}
	
	public static ItemStack getFluidDisplayStack(Fluid aFluid) {
		return aFluid == null ? null : getFluidDisplayStack(new FluidStack(aFluid, 0), false);
	}
	
	public static ItemStack getFluidDisplayStack(FluidStack aFluid, boolean aUseStackSize) {
		return getFluidDisplayStack(aFluid, aUseStackSize, false);
	}
	
	public static ItemStack getFluidDisplayStack(FluidStack aFluid, boolean aUseStackSize, boolean aHideStackSize) {
		if (aFluid == null || aFluid.getFluid() == null) return null;
		int tmp = 0;
		try {
			tmp = aFluid.getFluid().getID();
		} catch (Exception e) {
			System.err.println(e);
		}
		ItemStack rStack = ItemList.Display_Fluid.getWithDamage(1, tmp);
		NBTTagCompound tNBT = new NBTTagCompound();
		tNBT.setLong("mFluidDisplayAmount", aUseStackSize ? aFluid.amount : 0);
		tNBT.setLong("mFluidDisplayHeat", aFluid.getFluid().getTemperature(aFluid));
		tNBT.setBoolean("mFluidState", aFluid.getFluid().isGaseous(aFluid));
		tNBT.setBoolean("mHideStackSize", aHideStackSize);
		rStack.setTagCompound(tNBT);
		return rStack;
	}
	public static FluidStack getFluidFromDisplayStack(ItemStack aDisplayStack) {
		if (!isStackValid(aDisplayStack) ||
				aDisplayStack.getItem() != ItemList.Display_Fluid.getItem() ||
				!aDisplayStack.hasTagCompound())
			return null;
		Fluid tFluid = FluidRegistry.getFluid(ItemList.Display_Fluid.getItem().getDamage(aDisplayStack));
		return new FluidStack(tFluid, (int) aDisplayStack.getTagCompound().getLong("mFluidDisplayAmount"));
	}
	
	/**
	 * Add an itemstack to player inventory, or drop on ground if full.
	 * Can be called on client but it probably won't work very well.
	 */
	public static void addItemToPlayerInventory(EntityPlayer player, ItemStack stack) {
		if (isStackInvalid(stack)) return;
		if (!player.inventory.addItemStackToInventory(stack) && !player.worldObj.isRemote) {
			EntityItem dropItem = player.entityDropItem(stack, 0);
			dropItem.delayBeforeCanPickup = 0;
		}
	}
	
	/**
	 * Converts a Number to a String
	 */
	public static String parseNumberToString(int aNumber) {
		boolean temp = true, negative = false;
		if (aNumber < 0) {
			aNumber *= -1;
			negative = true;
		}
		StringBuilder tStringB = new StringBuilder();
		for (int i = 1000000000; i > 0; i /= 10) {
			int tDigit = (aNumber / i) % 10;
			if (temp && tDigit != 0) temp = false;
			if (!temp) {
				tStringB.append(tDigit);
				if (i != 1) for (int j = i; j > 0; j /= 1000) if (j == 1) tStringB.append(",");
			}
		}
		String tString = tStringB.toString();
		if (tString.equals("")) tString = "0";
		return negative ? "-" + tString : tString;
	}
	
	public static ItemStack[] copyStackArray(ItemStack... aStacks) {
		ItemStack[] rStacks = new ItemStack[aStacks.length];
		for (int i = 0; i < aStacks.length; i++) rStacks[i] = copy(aStacks[i]);
		return rStacks;
	}
	
	public static FluidStack[] copyFluidArray(FluidStack... aStacks) {
		FluidStack[] rStacks = new FluidStack[aStacks.length];
		for (int i = 0; i < aStacks.length; i++) if (aStacks[i] != null) rStacks[i] = aStacks[i].copy();
		return rStacks;
	}
	
	public static ItemStack updateItemStack(ItemStack stack) {
		if (isStackValid(stack) && stack.getItem() instanceof GenericItem)
			((GenericItem) stack.getItem()).isItemStackUsable(stack);
		return stack;
	}
	
	public static <X, Y> Map<X, Y> reMap(Map<X, Y> aMap) {
		Map<X, Y> tMap = new HashMap<>(aMap);
		aMap.clear();
		aMap.putAll(tMap);
		return aMap;
	}
	
	@SuppressWarnings("ForLoopReplaceableByForEach")
	public static <T> T[] withoutNulls(T[] array, IntFunction<T[]> arrayFactory) {
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				count++;
			}
		}
		
		T[] newArr = arrayFactory.apply(count);
		if (count == 0) return newArr;
		
		int j = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				newArr[j] = array[i];
				j++;
			}
		}
		
		return newArr;
	}
	
	public static <T> T[] withoutTrailingNulls(T[] array, IntFunction<T[]> arrayFactory) {
		int firstNull = -1;
		for (int i = array.length - 1; i >= 0; i--) {
			if (array[i] == null) {
				firstNull = i;
			} else {
				break;
			}
		}
		
		if (firstNull == -1) {
			T[] newArray = arrayFactory.apply(array.length);
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		} else if (firstNull == 0) {
			return arrayFactory.apply(0);
		} else {
			T[] newArray = arrayFactory.apply(firstNull);
			System.arraycopy(array, 0, newArray, 0, firstNull);
			return newArray;
		}
	}
	
	public static boolean areUnificationsEqual(ItemStack aStack1, ItemStack aStack2) {
		return areUnificationsEqual(aStack1, aStack2, false);
	}
	
	public static boolean areUnificationsEqual(ItemStack aStack1, ItemStack aStack2, boolean aIgnoreNBT) {
		return areStacksEqual(OreDictUnifier.get_nocopy(aStack1), OreDictUnifier.get_nocopy(aStack2), aIgnoreNBT);
	}
	
	public static ItemStack fillFluidContainer(FluidStack aFluid, ItemStack stack, boolean aRemoveFluidDirectly, boolean aCheckIFluidContainerItems) {
		if (isStackInvalid(stack) || aFluid == null) return null;
//		if (ModHandler.isWater(aFluid) && ItemList.Bottle_Empty.isStackEqual(stack)) {
//			if (aFluid.amount >= 250) {
//				if (aRemoveFluidDirectly) aFluid.amount -= 250;
//				return new ItemStack(Items.potionitem, 1, 0);
//			}
//			return null;
//		}
		if (aCheckIFluidContainerItems && stack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) stack.getItem()).getFluid(stack) == null && ((IFluidContainerItem) stack.getItem()).getCapacity(stack) <= aFluid.amount) {
			if (aRemoveFluidDirectly)
				aFluid.amount -= ((IFluidContainerItem) stack.getItem()).fill(stack = copyAmount(1, stack), aFluid, true);
			else
				((IFluidContainerItem) stack.getItem()).fill(stack = copyAmount(1, stack), aFluid, true);
			return stack;
		}
		Map<Fluid, FluidContainerRegistry.FluidContainerData> tFluidToContainer = sEmptyContainerToFluidToData.get(new ItemStackData(stack));
		if (tFluidToContainer == null) return null;
		FluidContainerRegistry.FluidContainerData tData = tFluidToContainer.get(aFluid.getFluid());
		if (tData == null || tData.fluid.amount > aFluid.amount) return null;
		if (aRemoveFluidDirectly) aFluid.amount -= tData.fluid.amount;
		return copyAmount(1, tData.filledContainer);
	}
	
	/**
	 * Get general container item, not only fluid container but also non-consumable item.
	 * getContainerForFilledItem works better for fluid container.
	 */
	public static ItemStack getContainerItem(ItemStack stack, boolean aCheckIFluidContainerItems) {
		if (isStackInvalid(stack)) return null;
		if (stack.getItem().hasContainerItem(stack)) {
			return stack.getItem().getContainerItem(stack);
		}
		
		// These are all special Cases, in which it is intended to have only Blocks outputting those Container Items
//		if (ItemList.Cell_Empty.isStackEqual(stack, false, true)) return null;
//		if (ItemList.IC2_Fuel_Can_Filled.isStackEqual(stack, false, true)) return ItemList.IC2_Fuel_Can_Empty.get(1);
//		if (stack.getItem() == Items.potionitem || stack.getItem() == Items.experience_bottle || ItemList.TF_Vial_FieryBlood.isStackEqual(stack) || ItemList.TF_Vial_FieryTears.isStackEqual(stack))
//			return ItemList.Bottle_Empty.get(1);
		
		if (aCheckIFluidContainerItems && stack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) stack.getItem()).getCapacity(stack) > 0) {
			ItemStack tStack = copyAmount(1, stack);
			((IFluidContainerItem) stack.getItem()).drain(tStack, Integer.MAX_VALUE, true);
			if (!areStacksEqual(stack, tStack)) return tStack;
			return null;
		}

//		int tCapsuleCount = ModHandler.getCapsuleCellContainerCount(stack);
//		if (tCapsuleCount > 0) return ItemList.Cell_Empty.get(tCapsuleCount);
//
//		if (ItemList.IC2_ForgeHammer.isStackEqual(stack) || ItemList.IC2_WireCutter.isStackEqual(stack))
//			return copyMetaData(Items.feather.getDamage(stack) + 1, stack);
		return null;
	}
	
	public static List<ItemStack> getContainersFromFluid(FluidStack tFluidStack) {
		if (tFluidStack != null) {
			List<ItemStack> tContainers = sFluidToContainers.get(tFluidStack.getFluid());
			if (tContainers == null) return new ArrayList<>();
			return tContainers;
		}
		return new ArrayList<>();
	}
	
	public static FluidStack getFluidForFilledItem(ItemStack stack, boolean aCheckIFluidContainerItems) {
		if (isStackInvalid(stack)) return null;
		if (aCheckIFluidContainerItems && stack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) stack.getItem()).getCapacity(stack) > 0)
			return ((IFluidContainerItem) stack.getItem()).drain(copyAmount(1, stack), Integer.MAX_VALUE, true);
		FluidContainerRegistry.FluidContainerData tData = sFilledContainerToData.get(new ItemStackData(stack));
		return tData == null ? null : tData.fluid.copy();
	}
	
	/**
	 * Get empty fluid container from filled one.
	 */
	public static ItemStack getContainerForFilledItem(ItemStack stack, boolean aCheckIFluidContainerItems) {
		if (isStackInvalid(stack)) return null;
		FluidContainerRegistry.FluidContainerData tData = sFilledContainerToData.get(new ItemStackData(stack));
		if (tData != null) return copyAmount(1, tData.emptyContainer);
		if (aCheckIFluidContainerItems && stack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) stack.getItem()).getCapacity(stack) > 0) {
			((IFluidContainerItem) stack.getItem()).drain(stack = copyAmount(1, stack), Integer.MAX_VALUE, true);
			return stack;
		}
		return null;
	}
	
	public static ItemStack copy(ItemStack... aStacks) {
		for (ItemStack tStack : aStacks) if (isStackValid(tStack)) return tStack.copy();
		return null;
	}
	
	@Nullable
	public static ItemStack copyOrNull(@Nullable ItemStack stack) {
		if (isStackValid(stack)) return stack.copy();
		return null;
	}
	
	public static ItemStack copyAmount(long amount, ItemStack... aStacks) {
		ItemStack rStack = copy(aStacks);
		if (isStackInvalid(rStack)) return null;
		if (amount > 64) amount = 64;
		else if (amount == -1) amount = 111;
		else if (amount < 0) amount = 0;
		rStack.stackSize = (int) amount;
		return rStack;
	}
	
	public static ItemStack copyAmountUnsafe(long amount, ItemStack... aStacks) {
		ItemStack rStack = copy(aStacks);
		if (isStackInvalid(rStack)) return null;
		if (amount > Integer.MAX_VALUE) amount = Integer.MAX_VALUE;
		else if (amount < 0) amount = 0;
		rStack.stackSize = (int) amount;
		return rStack;
	}
	
	public static ItemStack copyMetaData(long meta, ItemStack... aStacks) {
		ItemStack rStack = copy(aStacks);
		if (isStackInvalid(rStack)) return null;
		Items.feather.setDamage(rStack, (short) meta);
		return rStack;
	}
	
	public static ItemStack copyAmountAndMetaData(long amount, long meta, ItemStack... aStacks) {
		ItemStack rStack = copyAmount(amount, aStacks);
		if (isStackInvalid(rStack)) return null;
		Items.feather.setDamage(rStack, (short) meta);
		return rStack;
	}
	
	public static int stackToInt(ItemStack stack) {
		if (isStackInvalid(stack)) return 0;
		return itemToInt(stack.getItem(), Items.feather.getDamage(stack));
	}
	
	public static int itemToInt(Item aItem, int aMeta) {
		return Item.getIdFromItem(aItem) | (aMeta << 16);
	}
	
	public static ItemStack intToStack(int stack) {
		int tID = stack & (~0 >>> 16), tMeta = stack >>> 16;
		Item tItem = Item.getItemById(tID);
		if (tItem != null) return new ItemStack(tItem, 1, tMeta);
		return null;
	}
	
	public static boolean areFluidsEqual(FluidStack aFluid1, FluidStack aFluid2) {
		return areFluidsEqual(aFluid1, aFluid2, false);
	}
	
	public static boolean areFluidsEqual(FluidStack aFluid1, FluidStack aFluid2, boolean aIgnoreNBT) {
		return aFluid1 != null && aFluid2 != null && aFluid1.getFluid() == aFluid2.getFluid() && (aIgnoreNBT || ((aFluid1.tag == null) == (aFluid2.tag == null)) && (aFluid1.tag == null || aFluid1.tag.equals(aFluid2.tag)));
	}
	
	public static boolean areStacksEqual(ItemStack aStack1, ItemStack aStack2) {
		return areStacksEqual(aStack1, aStack2, false);
	}
	
	public static boolean areStacksEqual(ItemStack aStack1, ItemStack aStack2, boolean aIgnoreNBT) {
		return aStack1 != null && aStack2 != null && aStack1.getItem() == aStack2.getItem()
				&& (aIgnoreNBT || (((aStack1.getTagCompound() == null) == (aStack2.getTagCompound() == null)) && (aStack1.getTagCompound() == null || aStack1.getTagCompound().equals(aStack2.getTagCompound()))))
				&& (Items.feather.getDamage(aStack1) == Items.feather.getDamage(aStack2) || Items.feather.getDamage(aStack1) == W || Items.feather.getDamage(aStack2) == W);
	}
	
	public static <X, Y extends Comparable<Y>> LinkedHashMap<X, Y> sortMapByValuesAcending(Map<X, Y> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(entriesToMap(LinkedHashMap::new));
	}
	
	/**
	 * Returns a merge function, suitable for use in
	 * {@link Map#merge(Object, Object, BiFunction) Map.merge()} or
	 * {@link Collectors#toMap(Function, Function, BinaryOperator) toMap()}, which always
	 * throws {@code IllegalStateException}.  This can be used to enforce the
	 * assumption that the elements being collected are distinct.
	 *
	 * @param <T> the type of input arguments to the merge function
	 * @return a merge function which always throw {@code IllegalStateException}
	 */
	public static <T> BinaryOperator<T> throwingMerger() {
		return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
	}
	
	public static <K, V, E extends Map.Entry<K, V>, M extends Map<K, V>> Collector<E, ?, M> entriesToMap(Supplier<M> mapSupplier) {
		return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, throwingMerger(), mapSupplier);
	}
	
	public static String toSubscript(long no) {
		char[] chars = Long.toString(no).toCharArray();
		for (int i = 0; i < chars.length; i++) {
			chars[i] += 8272;
		}
		return new String(chars);
	}
	
	public static String getLocalizedNameForItem(String aFormat, int aMaterialID) {
		if (aMaterialID >= 0 && aMaterialID < MAX_MATERIALS) {
			Materials aMaterial = API.sGeneratedMaterials[aMaterialID];
			if (aMaterial != null)
				return aMaterial.getLocalizedNameForItem(aFormat);
		}
		return aFormat;
	}
	
	public static boolean doSoundAtClient(String aSoundName, int aTimeUntilNextSound, float aSoundStrength) {
		return doSoundAtClient(aSoundName, aTimeUntilNextSound, aSoundStrength, proxy.getPlayer());
	}
	
	public static boolean doSoundAtClient(String aSoundName, int aTimeUntilNextSound, float aSoundStrength, Entity entity) {
		if (entity == null) return false;
		return doSoundAtClient(aSoundName, aTimeUntilNextSound, aSoundStrength, entity.posX, entity.posY, entity.posZ);
	}
	
	public static boolean doSoundAtClient(String aSoundName, int aTimeUntilNextSound, float aSoundStrength, double x, double y, double z) {
		return doSoundAtClient(aSoundName, aTimeUntilNextSound, aSoundStrength, 1.01818028F, x, y, z);
	}
	
	public static boolean doSoundAtClient(String aSoundName, int aTimeUntilNextSound, float aSoundStrength, float aSoundModulation, double x, double y, double z) {
		if (isStringInvalid(aSoundName) || !FMLCommonHandler.instance().getEffectiveSide().isClient() || proxy.getPlayer() == null || !proxy.getPlayer().worldObj.isRemote)
			return false;
		if (API.sMultiThreadedSounds)
			new Thread(new SoundThread(proxy.getPlayer().worldObj, MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), aTimeUntilNextSound, aSoundName, aSoundStrength, aSoundModulation), "Sound Effect").start();
		else
			new SoundThread(proxy.getPlayer().worldObj, MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), aTimeUntilNextSound, aSoundName, aSoundStrength, aSoundModulation).run();
		return true;
	}
	
	public static boolean sendSoundToPlayers(World world, String aSoundName, float aSoundStrength, float aSoundModulation, int x, int y, int z) {
		if (isStringInvalid(aSoundName) || world == null || world.isRemote) return false;
		NETWORK.sendPacketToAllPlayersInRange(world, new Packet_Sound(aSoundName, aSoundStrength, aSoundModulation, x, (short) y, z), x, z);
		return true;
	}
	
	public static int safeInt(long number, int margin) {
		return number > Integer.MAX_VALUE - margin ? Integer.MAX_VALUE - margin : (int) number;
	}
	
	public static int safeInt(long number) {
		return number > V[V.length - 1] ? safeInt(V[V.length - 1], 1) : number < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) number;
	}
	
	public static boolean isDrawing(Tessellator tess) {
		try {
			return isDrawingField.getBoolean(tess);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<String> getTooltip(ItemStack stack, boolean aGuiStyle) {
		try {
			List<String> tooltip = stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
			if (aGuiStyle) {
				tooltip.set(0, (stack.getRarity() == null ? EnumRarity.common : stack.getRarity()).rarityColor +tooltip.get(0));
				for (int i = 1; i < tooltip.size(); i++) {
					tooltip.set(i, EnumChatFormatting.GRAY + tooltip.get(i));
				}
			}
			return tooltip;
		} catch (RuntimeException e) {
			// Collections.singletonList() can not be added to. we don't want that
			if (aGuiStyle)
				return Lists.newArrayList((stack.getRarity() == null ? EnumRarity.common : stack.getRarity()).rarityColor + stack.getDisplayName());
			return Lists.newArrayList(stack.getDisplayName());
		}
	}
	
	/**
	 * Loads an ItemStack properly.
	 */
	public static ItemStack loadItem(NBTTagCompound nbt, String aTagName) {
		return loadItem(nbt.getCompoundTag(aTagName));
	}
	
	public static FluidStack loadFluid(NBTTagCompound nbt, String aTagName) {
		return loadFluid(nbt.getCompoundTag(aTagName));
	}
	
	/**
	 * Loads an ItemStack properly.
	 */
	public static ItemStack loadItem(NBTTagCompound nbt) {
		if (nbt == null) return null;
		return OreDictUnifier.get(true, ItemStack.loadItemStackFromNBT(nbt));
	}
	
	/**
	 * Loads an FluidStack properly.
	 */
	public static FluidStack loadFluid(NBTTagCompound nbt) {
		if (nbt == null) return null;
		return FluidStack.loadFluidStackFromNBT(nbt);
	}
	
	/**
	 * This Function determines the direction a Block gets when being Wrenched.
	 * returns -1 if invalid. Even though that could never happen.
	 */
	public static int determineWrenchingSide(int side, float x, float y, float z) {
		int tBack = getOppositeSide(side);
		switch (side) {
			case 0:
			case 1:
				if (x < 0.25) {
					if (z < 0.25) return tBack;
					if (z > 0.75) return tBack;
					return 4;
				}
				if (x > 0.75) {
					if (z < 0.25) return tBack;
					if (z > 0.75) return tBack;
					return 5;
				}
				if (z < 0.25) return 2;
				if (z > 0.75) return 3;
				return side;
			case 2:
			case 3:
				if (x < 0.25) {
					if (y < 0.25) return tBack;
					if (y > 0.75) return tBack;
					return 4;
				}
				if (x > 0.75) {
					if (y < 0.25) return tBack;
					if (y > 0.75) return tBack;
					return 5;
				}
				if (y < 0.25) return 0;
				if (y > 0.75) return 1;
				return side;
			case 4:
			case 5:
				if (z < 0.25) {
					if (y < 0.25) return tBack;
					if (y > 0.75) return tBack;
					return 2;
				}
				if (z > 0.75) {
					if (y < 0.25) return tBack;
					if (y > 0.75) return tBack;
					return 3;
				}
				if (y < 0.25) return 0;
				if (y > 0.75) return 1;
				return side;
		}
		return -1;
	}
	
	public static float getStrength(long aExplosionPower) {
		return aExplosionPower < V[0] ? 1.0F : aExplosionPower < V[1] ? 2.0F : aExplosionPower < V[2] ? 3.0F : aExplosionPower < V[3] ? 4.0F
				: aExplosionPower < V[4] ? 5.0F : aExplosionPower < V[4] * 2 ? 6.0F : aExplosionPower < V[5] ? 7.0F : aExplosionPower < V[6] ? 8.0F
				: aExplosionPower < V[7] ? 9.0F : aExplosionPower < V[8] ? 10.0F : aExplosionPower < V[8] * 2 ? 11.0F : aExplosionPower < V[9] ? 12.0F
				: aExplosionPower < V[10] ? 13.0F : aExplosionPower < V[11] ? 14.0F : aExplosionPower < V[12] ? 15.0F : aExplosionPower < V[12] * 2 ? 16.0F
				: aExplosionPower < V[13] ? 17.0F : aExplosionPower < V[14] ? 18.0F : aExplosionPower < V[15] ? 19.0F : 20.0F;
	}
	
	public static boolean applyHeatDamage(EntityLivingBase entity, float damage) {
		return applyHeatDamage(entity, damage, DamageSources.getHeatDamage());
	}
	
	public static boolean applyHeatDamageFromItem(EntityLivingBase entity, float damage, ItemStack item) {
		return applyHeatDamage(entity, damage, new DamageSources.DamageSourceHotItem(item));
	}
	
	private static boolean applyHeatDamage(EntityLivingBase entity, float aDamage, DamageSource source) {
		if (aDamage > 0 && entity != null && entity.getActivePotionEffect(Potion.fireResistance) == null && !isWearingFullHeatHazmat(entity)) {
			entity.attackEntityFrom(source, aDamage);
			return true;
		}
		return false;
	}
	
	public static boolean applyFrostDamage(EntityLivingBase entity, float aDamage) {
		if (aDamage > 0 && entity != null && !isWearingFullFrostHazmat(entity)) {
			entity.attackEntityFrom(DamageSources.getFrostDamage(), aDamage);
			return true;
		}
		return false;
	}
	
	public static boolean applyElectricityDamage(EntityLivingBase entity, long aVoltage, long aAmperage) {
		long aDamage = getTier(aVoltage) * aAmperage * 4;
		if (aDamage > 0 && entity != null && !isWearingFullElectroHazmat(entity)) {
			entity.attackEntityFrom(DamageSources.getElectricDamage(), aDamage);
			return true;
		}
		return false;
	}
	
	public static boolean applyRadioactivity(EntityLivingBase entity, int aLevel, int aAmountOfItems) {
		if (aLevel > 0 && entity != null && entity.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD && entity.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD && !isWearingFullRadioHazmat(entity)) {
			PotionEffect tEffect = null;
			entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, aLevel * 140 * aAmountOfItems + Math.max(0, ((tEffect = entity.getActivePotionEffect(Potion.moveSlowdown)) == null ? 0 : tEffect.getDuration())), Math.max(0, (5 * aLevel) / 7)));
			entity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, aLevel * 150 * aAmountOfItems + Math.max(0, ((tEffect = entity.getActivePotionEffect(Potion.digSlowdown)) == null ? 0 : tEffect.getDuration())), Math.max(0, (5 * aLevel) / 7)));
			entity.addPotionEffect(new PotionEffect(Potion.confusion.id, aLevel * 130 * aAmountOfItems + Math.max(0, ((tEffect = entity.getActivePotionEffect(Potion.confusion)) == null ? 0 : tEffect.getDuration())), Math.max(0, (5 * aLevel) / 7)));
			entity.addPotionEffect(new PotionEffect(Potion.weakness.id, aLevel * 150 * aAmountOfItems + Math.max(0, ((tEffect = entity.getActivePotionEffect(Potion.weakness)) == null ? 0 : tEffect.getDuration())), Math.max(0, (5 * aLevel) / 7)));
			entity.addPotionEffect(new PotionEffect(Potion.hunger.id, aLevel * 130 * aAmountOfItems + Math.max(0, ((tEffect = entity.getActivePotionEffect(Potion.hunger)) == null ? 0 : tEffect.getDuration())), Math.max(0, (5 * aLevel) / 7)));
			entity.addPotionEffect(new PotionEffect(24 /* IC2 Radiation */, aLevel * 180 * aAmountOfItems + Math.max(0, ((tEffect = entity.getActivePotionEffect(Potion.potionTypes[24])) == null ? 0 : tEffect.getDuration())), Math.max(0, (5 * aLevel) / 7)));
			return true;
		}
		return false;
	}
	
	public static boolean isWearingFullFrostHazmat(EntityLivingBase entity) {
		for (int i = 1; i < 5; i++)
			if (!isStackInList(entity.getEquipmentInSlot(i), API.sFrostHazmatList)) return false;
		return true;
	}
	
	public static boolean isWearingFullHeatHazmat(EntityLivingBase entity) {
		for (int i = 1; i < 5; i++)
			if (!isStackInList(entity.getEquipmentInSlot(i), API.sHeatHazmatList)) return false;
		return true;
	}
	
	public static boolean isWearingFullBioHazmat(EntityLivingBase entity) {
		for (int i = 1; i < 5; i++)
			if (!isStackInList(entity.getEquipmentInSlot(i), API.sBioHazmatList)) return false;
		return true;
	}
	
	public static boolean isWearingFullRadioHazmat(EntityLivingBase entity) {
		for (int i = 1; i < 5; i++)
			if (!isStackInList(entity.getEquipmentInSlot(i), API.sRadioHazmatList)) return false;
		return true;
	}
	
	public static boolean isWearingFullElectroHazmat(EntityLivingBase entity) {
		for (int i = 1; i < 5; i++)
			if (!isStackInList(entity.getEquipmentInSlot(i), API.sElectroHazmatList)) return false;
		return true;
	}
	
	public static boolean isWearingFullGasHazmat(EntityLivingBase entity) {
		for (int i = 1; i < 5; i++)
			if (!isStackInList(entity.getEquipmentInSlot(i), API.sGasHazmatList)) return false;
		return true;
	}
	
	public static int getRadioactivityLevel(ItemStack stack) {
		ItemData tData = OreDictUnifier.getItemData(stack);
		if (tData != null && tData.hasValidMaterialData()) {
			if (tData.mMaterial.mMaterial.enchantmentArmors instanceof EnchRadioactivity)
				return tData.mMaterial.mMaterial.enchantmentArmorsLevel;
			if (tData.mMaterial.mMaterial.enchantmentTools instanceof EnchRadioactivity)
				return tData.mMaterial.mMaterial.enchantmentToolsLevel;
		}
		return EnchantmentHelper.getEnchantmentLevel(EnchRadioactivity.INSTANCE.effectId, stack);
	}
	
	/**
	 * Uses a Soldering Iron from player or external inventory
	 */
	public static boolean useSolderingIron(ItemStack stack, EntityLivingBase player, IInventory aExternalInventory) {
		if (player == null || stack == null) return false;
		if (isStackInList(stack, sSolderingToolList)) {
			if (player instanceof EntityPlayer) {
				EntityPlayer tPlayer = (EntityPlayer) player;
				if (tPlayer.capabilities.isCreativeMode) return true;
				if (isElectricItem(stack) && ic2.api.item.ElectricItem.manager.getCharge(stack) > 1000.0d) {
					if (consumeSolderingMaterial(tPlayer)
							|| (aExternalInventory != null && consumeSolderingMaterial(aExternalInventory))) {
						if (canUseElectricItem(stack, 10000)) {
							return ModHandler.useElectricItem(stack, 10000, (EntityPlayer) player);
						}
						ModHandler.useElectricItem(stack, (int) ic2.api.item.ElectricItem.manager.getCharge(stack), (EntityPlayer) player);
						return false;
					}
				}
			} else {
				damageOrDechargeItem(stack, 1, 1000, player);
				return true;
			}
		}
		return false;
	}
	
	public static boolean useSolderingIron(ItemStack stack, EntityLivingBase player) {
		return useSolderingIron(stack, player, null);
	}
	
	
	public static boolean consumeSolderingMaterial(EntityPlayer player) {
		if (player.capabilities.isCreativeMode) return true;
		if (consumeSolderingMaterial(player.inventory)) {
			if (player.inventoryContainer != null) {
				player.inventoryContainer.detectAndSendChanges();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Consumes soldering material from given inventory
	 */
	public static boolean consumeSolderingMaterial(IInventory aInventory) {
		for (int i = 0; i < aInventory.getSizeInventory(); i++) {
			ItemStack tStack = aInventory.getStackInSlot(i);
			if (isStackInList(tStack, sSolderingMetalList)) {
				if (tStack.stackSize < 1) return false;
				if (tStack.stackSize == 1) {
					tStack = null;
				} else {
					tStack.stackSize--;
				}
				aInventory.setInventorySlotContents(i, tStack);
				aInventory.markDirty();
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAllowedToTakeFromSlot(IInventory aTileEntity, int aSlot, int side, ItemStack stack) {
		if (ForgeDirection.getOrientation(side) == ForgeDirection.UNKNOWN) {
			return isAllowedToTakeFromSlot(aTileEntity, aSlot, (int) 0, stack)
					|| isAllowedToTakeFromSlot(aTileEntity, aSlot, (int) 1, stack)
					|| isAllowedToTakeFromSlot(aTileEntity, aSlot, (int) 2, stack)
					|| isAllowedToTakeFromSlot(aTileEntity, aSlot, (int) 3, stack)
					|| isAllowedToTakeFromSlot(aTileEntity, aSlot, (int) 4, stack)
					|| isAllowedToTakeFromSlot(aTileEntity, aSlot, (int) 5, stack);
		}
		if (aTileEntity instanceof ISidedInventory)
			return ((ISidedInventory) aTileEntity).canExtractItem(aSlot, stack, side);
		return true;
	}
	
	public static boolean isAllowedToPutIntoSlot(IInventory aTileEntity, int aSlot, int side, ItemStack stack, int aMaxStackSize) {
		ItemStack tStack = aTileEntity.getStackInSlot(aSlot);
		if (tStack != null && (!areStacksEqual(tStack, stack) || tStack.stackSize >= tStack.getMaxStackSize()))
			return false;
		if (ForgeDirection.getOrientation(side) == ForgeDirection.UNKNOWN) {
			return isAllowedToPutIntoSlot(aTileEntity, aSlot, (int) 0, stack, aMaxStackSize)
					|| isAllowedToPutIntoSlot(aTileEntity, aSlot, (int) 1, stack, aMaxStackSize)
					|| isAllowedToPutIntoSlot(aTileEntity, aSlot, (int) 2, stack, aMaxStackSize)
					|| isAllowedToPutIntoSlot(aTileEntity, aSlot, (int) 3, stack, aMaxStackSize)
					|| isAllowedToPutIntoSlot(aTileEntity, aSlot, (int) 4, stack, aMaxStackSize)
					|| isAllowedToPutIntoSlot(aTileEntity, aSlot, (int) 5, stack, aMaxStackSize);
		}
		if (aTileEntity instanceof ISidedInventory && !((ISidedInventory) aTileEntity).canInsertItem(aSlot, stack, side))
			return false;
		return aSlot < aTileEntity.getSizeInventory() && aTileEntity.isItemValidForSlot(aSlot, stack);
	}
	
	public static boolean listContainsItem(Collection<ItemStack> aList, ItemStack stack, boolean aTIfListEmpty, boolean aInvertFilter) {
		if (stack == null || stack.stackSize < 1) return false;
		if (aList == null) return aTIfListEmpty;
		boolean tEmpty = true;
		for (ItemStack tStack : aList) {
			if (tStack != null) {
				tEmpty = false;
				if (areStacksEqual(stack, tStack)) {
					return !aInvertFilter;
				}
			}
		}
		return tEmpty ? aTIfListEmpty : aInvertFilter;
	}
	
	/**
	 * moves multiple stacks from Inv-Side to Inv-Side
	 *
	 * @return the Amount of moved Items
	 */
	
	public static int moveMultipleItemStacks(Object aTileEntity1, Object aTileEntity2, int aGrabFrom, int aPutTo, List<ItemStack> aFilter, boolean aInvertFilter, int aMaxTargetStackSize, int aMinTargetStackSize, int aMaxMoveAtOnce, int aMinMoveAtOnce,int aStackAmount) {
		if (aTileEntity1 instanceof  IInventory)
			return moveMultipleItemStacks((IInventory) aTileEntity1, aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce,aStackAmount, true);
		return 0;
	}
	
	public static int moveMultipleItemStacks(IInventory aTileEntity1, Object aTileEntity2, int aGrabFrom, int aPutTo, List<ItemStack> aFilter, boolean aInvertFilter, int aMaxTargetStackSize, int aMinTargetStackSize, int aMaxMoveAtOnce, int aMinMoveAtOnce,int aMaxStackTransfer, boolean aDoCheckChests) {
		if (aTileEntity1 == null || aMaxTargetStackSize <= 0 || aMinTargetStackSize <= 0 || aMaxMoveAtOnce <= 0 || aMinTargetStackSize > aMaxTargetStackSize || aMinMoveAtOnce > aMaxMoveAtOnce || aMaxStackTransfer == 0)
			return 0;
		
		// find where to take from
		int[] tGrabSlots = new int[aTileEntity1.getSizeInventory()];
		int tGrabSlotsSize = 0;
		if (aTileEntity1 instanceof ISidedInventory) {
			for(int i : ((ISidedInventory) aTileEntity1).getAccessibleSlotsFromSide(aGrabFrom)) {
				ItemStack s = aTileEntity1.getStackInSlot(i);
				if (s == null || !isAllowedToTakeFromSlot(aTileEntity1, i, aGrabFrom, s) || s.stackSize < aMinMoveAtOnce || !listContainsItem(aFilter, s, true, aInvertFilter))
					continue;
				tGrabSlots[tGrabSlotsSize++] = i;
			}
		}
		else {
			for (int i = 0; i < tGrabSlots.length; i++)
			{
				ItemStack s = aTileEntity1.getStackInSlot(i);
				if (s == null || s.stackSize < aMinMoveAtOnce || !listContainsItem(aFilter, s, true, aInvertFilter))
					continue;
				tGrabSlots[tGrabSlotsSize++] = i;
			}
		}
		
		// no source, bail out
		if(tGrabSlotsSize == 0) {
			// maybe source is a double chest. check it
			if (aDoCheckChests && aTileEntity1 instanceof TileEntityChest)
				return moveFromAdjacentChests((TileEntityChest) aTileEntity1, aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer);
			return 0;
		}
		
		// if target is an inventory, e.g. chest, machine, drawers...
		if (aTileEntity2 instanceof IInventory) {
			IInventory tPutInventory = (IInventory) aTileEntity2;
			
			// partially filled slot spare space mapping.
			// value is the sum of all spare space left not counting completely empty slot
			HashMap<ItemId, Integer> tPutItems = new HashMap<>(tPutInventory.getSizeInventory());
			// partially filled slot contents
			HashMap<ItemId, List<ItemStack>> tPutItemStacks = new HashMap<>(tPutInventory.getSizeInventory());
			// completely empty slots
			List<Integer> tPutFreeSlots = new ArrayList<>(tPutInventory.getSizeInventory());
			
			// find possible target slots
			int[] accessibleSlots = null;
			if (aTileEntity2 instanceof ISidedInventory)
				accessibleSlots = ((ISidedInventory) tPutInventory).getAccessibleSlotsFromSide(aPutTo);
			for (int i = 0; i < tPutInventory.getSizeInventory(); i++) {
				int slot = i;
				if(accessibleSlots != null)
				{
					if(accessibleSlots.length <= i)
						break;
					slot = accessibleSlots[slot];
				}
				ItemStack s = tPutInventory.getStackInSlot(slot);
				if(s == null) {
					tPutFreeSlots.add(slot);
				} else if((s.stackSize < s.getMaxStackSize() && s.stackSize < tPutInventory.getInventoryStackLimit()) && aMinMoveAtOnce <= s.getMaxStackSize() - s.stackSize) {
					ItemId sID = ItemId.createNoCopy(s);
					tPutItems.merge(sID, (Math.min(s.getMaxStackSize(), tPutInventory.getInventoryStackLimit()) - s.stackSize), Integer::sum);
					tPutItemStacks.computeIfAbsent(sID, k -> new ArrayList<>()).add(s);
				}
			}
			
			// target completely filled, bail out
			if(tPutItems.isEmpty() && tPutFreeSlots.isEmpty()) {
				// maybe target is a double chest. check it.
				if (aDoCheckChests && aTileEntity2 instanceof TileEntityChest)
					return moveToAdjacentChests(aTileEntity1, (TileEntityChest) aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer);
				return 0;
			}
			
			// go over source stacks one by one
			int tStacksMoved = 0,tTotalItemsMoved = 0;
			for (int j = 0; j < tGrabSlotsSize; j++) {
				int grabSlot = tGrabSlots[j];
				int tMovedItems;
				int tStackSize;
				do {
					tMovedItems = 0;
					ItemStack tGrabStack = aTileEntity1.getStackInSlot(grabSlot);
					if (tGrabStack == null)
						break;
					tStackSize = tGrabStack.stackSize;
					ItemId sID = ItemId.createNoCopy(tGrabStack);
					
					if (tPutItems.containsKey(sID)) {
						// there is a partially filled slot, try merging
						int canPut = Math.min(tPutItems.get(sID), aMaxMoveAtOnce);
						if (canPut >= aMinMoveAtOnce) {
							List<ItemStack> putStack = tPutItemStacks.get(sID);
							if (!putStack.isEmpty()) {
								// can move, do merge
								int toPut = Math.min(canPut, tStackSize);
								tMovedItems = toPut;
								for (int i = 0; i < putStack.size(); i++) {
									ItemStack s = putStack.get(i);
									int sToPut = Math.min(Math.min(Math.min(toPut, s.getMaxStackSize() - s.stackSize), tPutInventory.getInventoryStackLimit() - s.stackSize), aMaxTargetStackSize - s.stackSize);
									if (sToPut <= 0)
										continue;
									if (sToPut < aMinMoveAtOnce)
										continue;
									if (s.stackSize + sToPut < aMinTargetStackSize)
										continue;
									toPut -= sToPut;
									s.stackSize += sToPut;
									if (s.stackSize == s.getMaxStackSize() || s.stackSize == tPutInventory.getInventoryStackLimit()) {
										// this slot is full. remove this stack from candidate list
										putStack.remove(i);
										i--;
									}
									if (toPut == 0)
										break;
								}
								tMovedItems -= toPut;
								if (tMovedItems > 0) {
									tStackSize -= tMovedItems;
									tTotalItemsMoved += tMovedItems;
									// deduct spare space
									tPutItems.merge(sID, tMovedItems, (a, b) -> a.equals(b) ? null : a - b);
									
									if (tStackSize == 0)
										aTileEntity1.setInventorySlotContents(grabSlot, null);
									else
										tGrabStack.stackSize = tStackSize;
									
									aTileEntity1.markDirty();
									tPutInventory.markDirty();
								}
							}
						}
					}
					// still stuff to move & have completely empty slots
					if (tStackSize > 0 && !tPutFreeSlots.isEmpty()) {
						for (int i = 0; i < tPutFreeSlots.size(); i++) {
							int tPutSlot = tPutFreeSlots.get(i);
							if (isAllowedToPutIntoSlot(tPutInventory, tPutSlot, aPutTo, tGrabStack, (int) 64)) {
								// allowed, now do moving
								int tMoved = moveStackFromSlotAToSlotB(aTileEntity1, tPutInventory, grabSlot, tPutSlot, aMaxTargetStackSize, aMinTargetStackSize, (int) (aMaxMoveAtOnce - tMovedItems), aMinMoveAtOnce);
								if (tMoved > 0) {
									ItemStack s = tPutInventory.getStackInSlot(tPutSlot);
									if (s != null) {
										// s might be null if tPutInventory is very special, e.g. infinity chest
										// if s is null, we will not mark this slot as target candidate for anything
										int spare = Math.min(s.getMaxStackSize(), tPutInventory.getInventoryStackLimit()) - s.stackSize;
										if (spare > 0) {
											ItemId ssID = ItemId.createNoCopy(s);
											// add back to spare space count
											tPutItems.merge(ssID, spare, Integer::sum);
											// add to partially filled slot list
											tPutItemStacks.computeIfAbsent(ssID, k -> new ArrayList<>()).add(s);
										}
										// this is no longer free
										tPutFreeSlots.remove(i);
										i--;
									}
									// else -> noop
									// this is still a free slot. no need to do anything.
									tTotalItemsMoved += tMoved;
									tMovedItems += tMoved;
									tStackSize -= tMoved;
									if (tStackSize == 0)
										break;
								}
							}
						}
					}
					
					if (tMovedItems > 0) {
						// check if we have moved enough stacks
						if (++tStacksMoved >= aMaxStackTransfer)
							return tTotalItemsMoved;
					}
				} while (tMovedItems > 0 && tStackSize > 0); //support inventories that store more than a stack in a slot
			}
			
			// check if source is a double chest, if yes, try move from the adjacent as well
			if (aDoCheckChests && aTileEntity1 instanceof TileEntityChest) {
				int tAmount = moveFromAdjacentChests((TileEntityChest) aTileEntity1, aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer - tStacksMoved);
				if (tAmount != 0) return tAmount+tTotalItemsMoved;
			}
			
			// check if target is a double chest, if yes, try move to the adjacent as well
			if (aDoCheckChests && aTileEntity2 instanceof TileEntityChest) {
				int tAmount = moveToAdjacentChests(aTileEntity1, (TileEntityChest) aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer - tStacksMoved);
				if (tAmount != 0) return tAmount+tTotalItemsMoved;
			}
			
			return tTotalItemsMoved;
		}
		// there should be a function to transfer more than 1 stack in a pipe
		// however I do not see any ways to improve it. too much work for what it is worth
		int tTotalItemsMoved = 0;
		int tGrabInventorySize = tGrabSlots.length;
		for (int i = 0; i < tGrabInventorySize; i++) {
			int tMoved = moveStackIntoPipe(aTileEntity1, aTileEntity2, tGrabSlots, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aDoCheckChests);
			if (tMoved == 0)
				return tTotalItemsMoved;
			else
				tTotalItemsMoved += tMoved;
		}
		return  0;
	}
	
	
	/**
	 * Moves Stack from Inv-Slot to Inv-Slot, without checking if its even allowed. (useful for internal Inventory Operations)
	 *
	 * @return the Amount of moved Items
	 */
	public static int moveStackFromSlotAToSlotB(IInventory aTileEntity1, IInventory aTileEntity2, int aGrabFrom, int aPutTo, int aMaxTargetStackSize, int aMinTargetStackSize, int aMaxMoveAtOnce, int aMinMoveAtOnce) {
		if (aTileEntity1 == null || aTileEntity2 == null || aMaxTargetStackSize <= 0 || aMinTargetStackSize <= 0 || aMinTargetStackSize > aMaxTargetStackSize || aMaxMoveAtOnce <= 0 || aMinMoveAtOnce > aMaxMoveAtOnce)
			return 0;
		
		ItemStack tStack1 = aTileEntity1.getStackInSlot(aGrabFrom), tStack2 = aTileEntity2.getStackInSlot(aPutTo), tStack3 = null;
		if (tStack1 != null) {
			if (tStack2 != null && !areStacksEqual(tStack1, tStack2)) return 0;
			tStack3 = copyOrNull(tStack1);
			aMaxTargetStackSize = (int) Math.min(aMaxTargetStackSize, Math.min(tStack3.getMaxStackSize(), Math.min(tStack2 == null ? Integer.MAX_VALUE : tStack2.getMaxStackSize(), aTileEntity2.getInventoryStackLimit())));
			tStack3.stackSize = Math.min(tStack3.stackSize, aMaxTargetStackSize - (tStack2 == null ? 0 : tStack2.stackSize));
			if (tStack3.stackSize > aMaxMoveAtOnce) tStack3.stackSize = aMaxMoveAtOnce;
			if (tStack3.stackSize + (tStack2 == null ? 0 : tStack2.stackSize) >= Math.min(tStack3.getMaxStackSize(), aMinTargetStackSize) && tStack3.stackSize >= aMinMoveAtOnce) {
				tStack3 = aTileEntity1.decrStackSize(aGrabFrom, tStack3.stackSize);
				aTileEntity1.markDirty();
				if (tStack3 != null) {
					if (tStack2 == null) {
						aTileEntity2.setInventorySlotContents(aPutTo, copyOrNull(tStack3));
					} else {
						tStack2.stackSize += tStack3.stackSize;
					}
					aTileEntity2.markDirty();
					return (int) tStack3.stackSize;
				}
			}
		}
		return 0;
	}
	
	private static int moveToAdjacentChests(IInventory aTileEntity1, TileEntityChest aTargetChest, int aGrabFrom, int aPutTo, List<ItemStack> aFilter, boolean aInvertFilter, int aMaxTargetStackSize, int aMinTargetStackSize, int aMaxMoveAtOnce, int aMinMoveAtOnce, int aMaxStackTransfer) {
		if (aTargetChest.adjacentChestChecked) {
			if (aTargetChest.adjacentChestXNeg != null) {
				return moveMultipleItemStacks(aTileEntity1, aTargetChest.adjacentChestXNeg, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer, false);
			} else if (aTargetChest.adjacentChestZNeg != null) {
				return moveMultipleItemStacks(aTileEntity1, aTargetChest.adjacentChestZNeg, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer, false);
			} else if (aTargetChest.adjacentChestXPos != null) {
				return moveMultipleItemStacks(aTileEntity1, aTargetChest.adjacentChestXPos, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer, false);
			} else if (aTargetChest.adjacentChestZPos != null) {
				return moveMultipleItemStacks(aTileEntity1, aTargetChest.adjacentChestZPos, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer, false);
			}
		}
		return 0;
	}
	
	private static int moveFromAdjacentChests(TileEntityChest aChest, Object aTileEntity2, int aGrabFrom, int aPutTo, List<ItemStack> aFilter, boolean aInvertFilter, int aMaxTargetStackSize, int aMinTargetStackSize, int aMaxMoveAtOnce, int aMinMoveAtOnce, int aMaxStackTransfer) {
		if (aChest.adjacentChestXNeg != null) {
			return moveMultipleItemStacks(aChest.adjacentChestXNeg, aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer, false);
		} else if (aChest.adjacentChestZNeg != null) {
			return moveMultipleItemStacks(aChest.adjacentChestZNeg, aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer, false);
		} else if (aChest.adjacentChestXPos != null) {
			return moveMultipleItemStacks(aChest.adjacentChestXPos, aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer, false);
		} else if (aChest.adjacentChestZPos != null) {
			return moveMultipleItemStacks(aChest.adjacentChestZPos, aTileEntity2, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, aMaxStackTransfer, false);
		}
		return 0;
	}
	
	
	public static boolean isConnectableNonInventoryPipe(Object aTileEntity, int side) {
		if (aTileEntity == null) return false;
		checkAvailabilities();
		if (TE_CHECK && aTileEntity instanceof IItemDuct) return true;
		if (BC_CHECK && aTileEntity instanceof IPipeTile)
			return ((IPipeTile) aTileEntity).isPipeConnected(ForgeDirection.getOrientation(side));
		return false;
	}
	/**
	 * Moves Stack from Inv-Slot to Inv-Slot, without checking if its even allowed.
	 *
	 * @return the Amount of moved Items
	 */
	public static int moveStackIntoPipe(IInventory aTileEntity1, Object aTileEntity2, int[] aGrabSlots, int aGrabFrom, int aPutTo, List<ItemStack> aFilter, boolean aInvertFilter, int aMaxTargetStackSize, int aMinTargetStackSize, int aMaxMoveAtOnce, int aMinMoveAtOnce) {
		return moveStackIntoPipe(aTileEntity1, aTileEntity2, aGrabSlots, aGrabFrom, aPutTo, aFilter, aInvertFilter, aMaxTargetStackSize, aMinTargetStackSize, aMaxMoveAtOnce, aMinMoveAtOnce, true);
	}
	
	public static boolean TE_CHECK = false, BC_CHECK = false, CHECK_ALL = true, RF_CHECK = false;
	public static void checkAvailabilities() {
		if (CHECK_ALL) {
			try {
				Class tClass = IItemDuct.class;
				tClass.getCanonicalName();
				TE_CHECK = true;
			} catch (Throwable e) {/**/}
			try {
				Class tClass = IPipeTile.class;
				tClass.getCanonicalName();
				BC_CHECK = true;
			} catch (Throwable e) {/**/}
			try {
				Class tClass = IEnergyReceiver.class;
				tClass.getCanonicalName();
				RF_CHECK = true;
			} catch (Throwable e) {/**/}
			CHECK_ALL = false;
		}
	}
	
	/**
	 * Moves Stack from Inv-Slot to Inv-Slot, without checking if its even allowed.
	 *
	 * @return the Amount of moved Items
	 */
	public static int moveStackIntoPipe(IInventory aTileEntity1, Object aTileEntity2, int[] aGrabSlots, int aGrabFrom, int aPutTo, List<ItemStack> aFilter, boolean aInvertFilter, int aMaxTargetStackSize, int aMinTargetStackSize, int aMaxMoveAtOnce, int aMinMoveAtOnce, boolean dropItem) {
		if (aTileEntity1 == null || aMaxTargetStackSize <= 0 || aMinTargetStackSize <= 0 || aMinTargetStackSize > aMaxTargetStackSize || aMaxMoveAtOnce <= 0 || aMinMoveAtOnce > aMaxMoveAtOnce)
			return 0;
		if (aTileEntity2 != null) {
			checkAvailabilities();
			if (TE_CHECK && aTileEntity2 instanceof IItemDuct) {
				for (int aGrabSlot : aGrabSlots) {
					if (listContainsItem(aFilter, aTileEntity1.getStackInSlot(aGrabSlot), true, aInvertFilter)) {
						if (isAllowedToTakeFromSlot(aTileEntity1, aGrabSlot, (int) aGrabFrom, aTileEntity1.getStackInSlot(aGrabSlot))) {
							if (Math.max(aMinMoveAtOnce, aMinTargetStackSize) <= aTileEntity1.getStackInSlot(aGrabSlot).stackSize) {
								ItemStack tStack = copyAmount(Math.min(aTileEntity1.getStackInSlot(aGrabSlot).stackSize, Math.min(aMaxMoveAtOnce, aMaxTargetStackSize)), aTileEntity1.getStackInSlot(aGrabSlot));
								ItemStack rStack = ((IItemDuct) aTileEntity2).insertItem(ForgeDirection.getOrientation(aPutTo), copyOrNull(tStack));
								int tMovedItemCount = (int) (tStack.stackSize - (rStack == null ? 0 : rStack.stackSize));
								if (tMovedItemCount >= 1/*Math.max(aMinMoveAtOnce, aMinTargetStackSize)*/) {
									//((cofh.api.transport.IItemConduit)aTileEntity2).insertItem(ForgeDirection.getOrientation(aPutTo), copyAmount(tMovedItemCount, tStack), F);
									aTileEntity1.decrStackSize(aGrabSlot, tMovedItemCount);
									aTileEntity1.markDirty();
									return tMovedItemCount;
								}
							}
						}
					}
				}
				return 0;
			}
			if (BC_CHECK && aTileEntity2 instanceof IPipeTile) {
				for (int aGrabSlot : aGrabSlots) {
					if (listContainsItem(aFilter, aTileEntity1.getStackInSlot(aGrabSlot), true, aInvertFilter)) {
						if (isAllowedToTakeFromSlot(aTileEntity1, aGrabSlot, (int) aGrabFrom, aTileEntity1.getStackInSlot(aGrabSlot))) {
							if (Math.max(aMinMoveAtOnce, aMinTargetStackSize) <= aTileEntity1.getStackInSlot(aGrabSlot).stackSize) {
								ItemStack tStack = copyAmount(Math.min(aTileEntity1.getStackInSlot(aGrabSlot).stackSize, Math.min(aMaxMoveAtOnce, aMaxTargetStackSize)), aTileEntity1.getStackInSlot(aGrabSlot));
								int tMovedItemCount = (int) ((IPipeTile) aTileEntity2).injectItem(copyOrNull(tStack), false, ForgeDirection.getOrientation(aPutTo));
								if (tMovedItemCount >= Math.max(aMinMoveAtOnce, aMinTargetStackSize)) {
									tMovedItemCount = (int) (((IPipeTile) aTileEntity2).injectItem(copyAmount(tMovedItemCount, tStack), true, ForgeDirection.getOrientation(aPutTo)));
									aTileEntity1.decrStackSize(aGrabSlot, tMovedItemCount);
									aTileEntity1.markDirty();
									return tMovedItemCount;
								}
							}
						}
					}
				}
				return 0;
			}
		}
		
		ForgeDirection tDirection = ForgeDirection.getOrientation(aGrabFrom);
		if (aTileEntity1 instanceof TileEntity && tDirection != ForgeDirection.UNKNOWN && tDirection.getOpposite() == ForgeDirection.getOrientation(aPutTo)) {
			int tX = ((TileEntity) aTileEntity1).xCoord + tDirection.offsetX, tY = ((TileEntity) aTileEntity1).yCoord + tDirection.offsetY, tZ = ((TileEntity) aTileEntity1).zCoord + tDirection.offsetZ;
			if (!hasBlockHitBox(((TileEntity) aTileEntity1).getWorldObj(), tX, tY, tZ) && dropItem) {
				for (int aGrabSlot : aGrabSlots) {
					if (listContainsItem(aFilter, aTileEntity1.getStackInSlot(aGrabSlot), true, aInvertFilter)) {
						if (isAllowedToTakeFromSlot(aTileEntity1, aGrabSlot, (int) aGrabFrom, aTileEntity1.getStackInSlot(aGrabSlot))) {
							if (Math.max(aMinMoveAtOnce, aMinTargetStackSize) <= aTileEntity1.getStackInSlot(aGrabSlot).stackSize) {
								ItemStack tStack = copyAmount(Math.min(aTileEntity1.getStackInSlot(aGrabSlot).stackSize, Math.min(aMaxMoveAtOnce, aMaxTargetStackSize)), aTileEntity1.getStackInSlot(aGrabSlot));
								EntityItem tEntity = new EntityItem(((TileEntity) aTileEntity1).getWorldObj(), tX + 0.5, tY + 0.5, tZ + 0.5, tStack);
								tEntity.motionX = tEntity.motionY = tEntity.motionZ = 0;
								((TileEntity) aTileEntity1).getWorldObj().spawnEntityInWorld(tEntity);
								aTileEntity1.decrStackSize(aGrabSlot, tStack.stackSize);
								aTileEntity1.markDirty();
								return (int) tStack.stackSize;
							}
						}
					}
				}
			}
		}
		return 0;
	}
	
	public static boolean hasBlockHitBox(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).getCollisionBoundingBoxFromPool(world, x, y, z) != null;
	}
	
	
	public static boolean isAppropriateTool(Block block, int meta, String... tTools) {
		
		if (block == null || tTools == null) {
			return false;
		}
		String targetTool = block.getHarvestTool(meta);
		return !isStringEmpty(targetTool) && isArrayContains(targetTool, tTools);
	}
	
	public static boolean isAppropriateMaterial(Block block, Material... tMats) {
		if (block == null || tMats == null) {
			return false;
		}
		return isArrayContains(block.getMaterial(), tMats);
	}
	
	
	public static boolean isSpecialBlock(Block block, Block... tBlocks) {
		if (block == null || tBlocks == null) {
			return false;
		}
		return isArrayContains(block, tBlocks);
	}
	
	
	public static <T> boolean isArrayContains(T obj, T[] list) {
		
		if (obj == null || list == null) {
			return false;
		}
		
		for (T iObj : list) {
			if (obj == iObj || obj.equals(iObj)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isStringEmpty(String s) {
		return s == null || s.length() == 0;
	}
	
	public static boolean hasNull(Object... obj) {
		for (Object iObj : obj) {
			if (iObj == null) {
				return true;
			}
		}
		return false;
	}
	
	public static Fluid getFluidFromUnlocalizedName(String aUnlocalizedName) {
		return sFluidUnlocalizedNameToFluid.get(aUnlocalizedName);
	}
	
	public static void reInit() {
		sFilledContainerToData.clear();
		sEmptyContainerToFluidToData.clear();
		sFluidToContainers.clear();
		sFluidUnlocalizedNameToFluid.clear();
		for (FluidContainerRegistry.FluidContainerData tData : sFluidContainerList) {
			sFilledContainerToData.put(new ItemStackData(tData.filledContainer), tData);
			Map<Fluid, FluidContainerRegistry.FluidContainerData> tFluidToContainer = sEmptyContainerToFluidToData.get(new ItemStackData(tData.emptyContainer));
			List<ItemStack> tContainers = sFluidToContainers.get(tData.fluid.getFluid());
			if (tFluidToContainer == null) {
				sEmptyContainerToFluidToData.put(new ItemStackData(tData.emptyContainer), tFluidToContainer = new /*Concurrent*/HashMap<>());
				API.sFluidMappings.add(tFluidToContainer);
			}
			tFluidToContainer.put(tData.fluid.getFluid(), tData);
			if (tContainers == null) {
				tContainers = new ArrayList<>();
				tContainers.add(tData.filledContainer);
				sFluidToContainers.put(tData.fluid.getFluid(), tContainers);
			}
			else tContainers.add(tData.filledContainer);
		}
		for (Fluid tFluid : FluidRegistry.getRegisteredFluids().values()) {
			sFluidUnlocalizedNameToFluid.put(tFluid.getUnlocalizedName(), tFluid);
		}
	}
	
	@AutoValue
	public abstract static class ItemId {
		/** This method copies NBT, as it is mutable. */
		public static ItemId create(ItemStack itemStack) {
			NBTTagCompound nbt = itemStack.getTagCompound();
			if (nbt != null) {
				nbt = (NBTTagCompound) nbt.copy();
			}
			
			return new AutoValue_Utility_ItemId(
					itemStack.getItem(), itemStack.getItemDamage(), nbt);
		}
		
		/** This method does not copy NBT in order to save time. Make sure not to mutate it! */
		public static ItemId createNoCopy(ItemStack itemStack) {
			return new AutoValue_Utility_ItemId(
					itemStack.getItem(), itemStack.getItemDamage(), itemStack.getTagCompound());
		}
		
		protected abstract Item item();
		protected abstract int metaData();
		
		@Nullable
		protected abstract NBTTagCompound nbt();
	}
}

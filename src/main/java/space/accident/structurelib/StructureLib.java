package space.accident.structurelib;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import space.accident.structurelib.block.BlockHint;
import space.accident.structurelib.item.ItemBlockHint;
import space.accident.structurelib.item.ItemConstructableTrigger;
import space.accident.structurelib.item.ItemFrontRotationTool;
import space.accident.structurelib.net.AlignmentMessage;
import space.accident.structurelib.proxy.CommonProxy;
import space.accident.structurelib.util.XSTR;

/**
 * This class does not contain a stable API. Refrain from using this class.
 */
@Mod(modid = StructureLibAPI.MOD_ID, name = "StructureLib", version = "GRADLETOKEN_VERSION", acceptableRemoteVersions = "*", guiFactory = "space.accident.structurelib.GuiFactory")
public class StructureLib {
    public static boolean DEBUG_MODE = Boolean.getBoolean("structurelib.debug");
    public static Logger LOGGER = LogManager.getLogger("StructureLib");

    @SidedProxy(serverSide = "space.accident.structurelib.proxy.CommonProxy", clientSide = "space.accident.structurelib.proxy.ClientProxy")
    static CommonProxy proxy;
    static SimpleNetworkWrapper net = NetworkRegistry.INSTANCE.newSimpleChannel(StructureLibAPI.MOD_ID);

    static {
        net.registerMessage(AlignmentMessage.ServerHandler.class, AlignmentMessage.AlignmentQuery.class, 0, Side.SERVER);
        net.registerMessage(AlignmentMessage.ClientHandler.class, AlignmentMessage.AlignmentData.class, 1, Side.CLIENT);
    }

    public static final XSTR RANDOM = new XSTR();

    static Block blockHint;
    static Item itemBlockHint;
    static Item itemFrontRotationTool;
    static Item itemConstructableTrigger;
    public static final CreativeTabs creativeTab = new CreativeTabs("structurelib") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return StructureLibAPI.getItemBlockHint();
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        ConfigurationHandler.INSTANCE.init(e.getSuggestedConfigurationFile());
        GameRegistry.registerBlock(blockHint = new BlockHint(), ItemBlockHint.class, "blockhint");
        itemBlockHint = ItemBlock.getItemFromBlock(StructureLibAPI.getBlockHint());
        GameRegistry.registerItem(itemFrontRotationTool = new ItemFrontRotationTool(), itemFrontRotationTool.getUnlocalizedName());
        GameRegistry.registerItem(itemConstructableTrigger = new ItemConstructableTrigger(), itemConstructableTrigger.getUnlocalizedName());
        proxy.preInit(e);
    }

    public static void addClientSideChatMessages(String... messages) {
        proxy.addClientSideChatMessages(messages);
    }

    public static EntityPlayer getCurrentPlayer() {
        return proxy.getCurrentPlayer();
    }

    public static boolean isCurrentPlayer(EntityPlayer player) {
        return proxy.isCurrentPlayer(player);
    }
}

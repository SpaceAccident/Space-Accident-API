package space.accident.main

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.*
import space.accident.main.proxy.CommonProxy
import java.util.*

@Mod(
    modid = MODID,
    version = VERSION,
    name = MODNAME,
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:forgelin;"
)
object SpaceAccidentApi {

    @SidedProxy(clientSide = "$GROUPNAME.proxy.ClientProxy", serverSide = "$GROUPNAME.proxy.CommonProxy")
    lateinit var proxy: CommonProxy

    /**
     * Do not use before the start of the server
     */
    val random = Random()

    @JvmStatic
    @Mod.Instance(MODID)
    val instance = SpaceAccidentApi

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        proxy.postInit(event)
    }

    @Mod.EventHandler
    fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
        proxy.serverAboutToStart(event)
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        proxy.serverStarting(event)
    }

    @Mod.EventHandler
    fun serverStarted(event: FMLServerStartedEvent) {
        proxy.serverStarted(event)
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        proxy.serverStopping(event)
    }

    @Mod.EventHandler
    fun serverStopped(event: FMLServerStoppedEvent) {
        proxy.serverStopped(event)
    }
}
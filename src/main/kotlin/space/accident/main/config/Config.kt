package space.accident.main.config

import net.minecraftforge.common.config.Configuration
import java.io.File

object Config {

    //Category
    private const val GENERAL = "general"

    var outputRF = false
    var eUtoRF = 4

    private inline fun onPostCreate(configFile: File?, crossinline action: (Configuration) -> Unit) {
        Configuration(configFile).let { config ->
            config.load()
            action(config)
            if (config.hasChanged()) {
                config.save()
            }
        }
    }

    fun createConfig(configFile: File?) {
        val config = File(File(configFile, "SpaceAccident"), "main.cfg")
        onPostCreate(config) { cfg ->

        }
    }
}
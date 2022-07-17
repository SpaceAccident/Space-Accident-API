package space.accident.api.interfaces.te

interface IUpgradableMachine : IMachineProgress {
    /**
     * Accepts Upgrades. Some Machines have an Upgrade Limit.
     */
    fun isUpgradable(): Boolean

    /**
     * Accepts Muffler Upgrades
     */
    fun isMufflerUpgradable(): Boolean

    /**
     * Accepts Steam-Converter Upgrades
     */
    fun isSteamEngineUpgradable(): Boolean

    /**
     * Adds Muffler Upgrade
     */
    fun addMufflerUpgrade(): Boolean

    /**
     * Adds MJ-Converter Upgrade
     */
    fun addSteamEngineUpgrade(): Boolean

    /**
     * Does this Machine have an Muffler
     */
    fun hasMufflerUpgrade(): Boolean

    /**
     * Does this Machine have a Steam-Converter
     */
    fun hasSteamEngineUpgrade(): Boolean
}
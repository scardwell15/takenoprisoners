package takenoprisoners.config

import org.magiclib.util.MagicSettings

class FactionConfigurationLoader {
    companion object {
        const val SETTINGS_PATH = "data/config/takenoprisonersFactionConfig/%s.json"
        private val inst = FactionConfigurationLoader()

        @JvmStatic
        fun getFactionConfig(factionId: String): FactionConfiguration? {
            return inst.factionMap[factionId.lowercase()]
        }

        @JvmStatic
        fun load() {
            inst.loadFactionConfigs()
        }
    }

    private var factionMap: MutableMap<String, FactionConfiguration> = HashMap()

    private fun loadFactionConfigs() {
        factionMap.clear()

        MagicSettings.getList("TakeNoPrisoners", "factionsWithConfigs")
            .map { FactionConfiguration(it) }
            .forEach {
                factionMap[it.factionId.lowercase()] = it
            }
    }
}
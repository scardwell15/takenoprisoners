package captureofficers.config

import data.scripts.util.MagicSettings

class FactionConfigLoader {
    companion object {
        const val SETTINGS_PATH = "data/config/takenoprisonersFactionConfig/%s.json"
        private val inst = FactionConfigLoader()

        @JvmStatic
        fun getFactionConfig(factionId: String): FactionConfig? {
            return inst.factionMap[factionId.lowercase()]
        }

        @JvmStatic
        fun load() {
            inst.loadFactionConfigs()
        }
    }

    private var factionMap: MutableMap<String, FactionConfig> = HashMap()

    private fun loadFactionConfigs() {
        factionMap.clear()

        MagicSettings.getList("TakeNoPrisoners", "factionsWithConfigs")
            .map { FactionConfig(it) }
            .forEach {
                factionMap[it.factionId] = it
            }
    }
}
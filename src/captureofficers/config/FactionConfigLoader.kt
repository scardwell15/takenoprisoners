package captureofficers.config

import com.fs.starfarer.api.characters.PersonAPI
import data.scripts.util.MagicSettings

class FactionConfigLoader {
    companion object {
        const val SETTINGS_PATH = "data/config/takenoprisonersFactionConfig/%s.json"
        private var inst = FactionConfigLoader()

        @JvmStatic
        fun getFactionConfig(factionId: String): FactionConfig? {
            return inst.factionMap[factionId.lowercase()]
        }

        @JvmStatic
        fun load() {
            inst.loadFactionConfigs()
        }
    }

    private var factionMap: MutableMap<String, FactionConfig> = mutableMapOf()

    private fun loadFactionConfigs() {
        factionMap.clear()

        MagicSettings.getList("TakeNoPrisoners", "factionsWithConfigs")
                .map { FactionConfig(it) }
                .forEach {
                    factionMap[it.factionId] = it
                }

    }
}

fun PersonAPI.getFactionConfig(): FactionConfig? = FactionConfigLoader.getFactionConfig(this.faction.id)
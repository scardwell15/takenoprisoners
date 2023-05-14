package takenoprisoners.config

import com.fs.starfarer.api.Global
import org.apache.log4j.Logger
import org.json.JSONException
import org.json.JSONObject
import org.lazywizard.lazylib.ext.json.optFloat
import java.io.IOException

class FactionConfiguration(var factionId: String, loadFromJson: Boolean) {
    constructor(factionId: String) : this(factionId, true)

    companion object {
        private val log: Logger = Logger.getLogger(FactionConfiguration::class.java)
    }

    var capturesOfficers = true
    var capturesCommanders = true
    var acceptsRansoms = true
    var captureChance = 0.5f
    var sendsVengeanceFleets = true
    var vengeancePointThreshold = 8
    var vengeanceFleetStrength = 0.75f
    var commanderForcedCapture = true

    init {
        factionId = factionId.lowercase()
        if (loadFromJson) {
            try {
                var settings: JSONObject = Global.getSettings()
                    .getMergedJSONForMod(
                        FactionConfigurationLoader.SETTINGS_PATH.format(factionId),
                        "presmattdamon_takenoprisoners"
                    )
                initSettings(settings)
                log.info("Loaded exotica faction config for faction $factionId")
            } catch (ex: IOException) {
                throw Exception("Failed to load exotica faction config for faction $factionId", ex)
            } catch (ex: JSONException) {
                throw Exception("The Exotica faction config for $factionId is incorrect.", ex)
            }
        }
    }

    fun initSettings(settings: JSONObject) {
        capturesOfficers = settings.optBoolean("capturesOfficers", capturesOfficers)
        capturesCommanders = settings.optBoolean("capturesCommanders", capturesCommanders)
        commanderForcedCapture = settings.optBoolean("commanderForcedCapture", commanderForcedCapture)
        acceptsRansoms = settings.optBoolean("acceptsRansoms", acceptsRansoms)
        captureChance = settings.optFloat("captureChance", captureChance)
        sendsVengeanceFleets = settings.optBoolean("sendsVengeanceFleets", sendsVengeanceFleets)
        vengeancePointThreshold = settings.optInt("vengeancePointThreshold", vengeancePointThreshold)
        vengeanceFleetStrength = settings.optFloat("vengeanceFleetStrength", vengeanceFleetStrength)
    }
}
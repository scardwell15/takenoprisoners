package takenoprisoners.campaign

import takenoprisoners.config.FactionConfiguration
import takenoprisoners.config.FactionConfigurationLoader
import takenoprisoners.utils.NeverendingChaseScript
import takenoprisoners.utils.StringUtils
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetAssignment
import com.fs.starfarer.api.impl.campaign.ids.Abilities
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.util.Misc
import org.magiclib.campaign.MagicFleetBuilder
import java.awt.Color

object VengeanceFleetHandler {
    val VENGEANCE_KEY = "TNP_VengeancePointsMap"
    fun loadVengeancePoints(): MutableMap<String, Int> {
        var vengeanceMap: MutableMap<String, Int>? = Global.getSector().persistentData[VENGEANCE_KEY] as MutableMap<String, Int>?
        if (vengeanceMap == null) {
            vengeanceMap = mutableMapOf()
            Global.getSector().persistentData[VENGEANCE_KEY] = vengeanceMap
        }
        return vengeanceMap
    }

    fun addVengeancePoint(factionId: String) {
        var currPoints = (loadVengeancePoints()[factionId] ?: 0) + 1

        var factionConfig = FactionConfigurationLoader.getFactionConfig(factionId)
        if (factionConfig != null && factionConfig.vengeancePointThreshold <= currPoints) {
            currPoints = 0

            var didSomething = false
            if (factionConfig.sendsVengeanceFleets) {
                spawnVengeanceFleet(factionId, factionConfig)
                didSomething = true
            }

            if (didSomething) {
                Global.getSector().campaignUI.addMessage(
                    StringUtils.getTranslation("VengeanceFleet", "NotificationText")
                        .format("factionId", Global.getSector().getFaction(factionId).displayName)
                        .toStringNoFormats(), Color(255, 0, 0)
                )
            }
        }

        loadVengeancePoints()[factionId] = currPoints
    }

    fun spawnVengeanceFleet(factionId: String, factionConfig: FactionConfiguration?): CampaignFleetAPI {
        val playerFleet = Global.getSector().playerFleet
        val fleetPoints: Int = (playerFleet.fleetPoints * (factionConfig?.vengeanceFleetStrength ?: 0.75f)).toInt()

        val justice = MagicFleetBuilder()
            .setFleetFaction(factionId)
            .setFleetType(FleetTypes.TASK_FORCE)
            .setMinFP(fleetPoints)
            .setAssignment(FleetAssignment.INTERCEPT)
            .setAssignmentTarget(playerFleet)
            .setIsImportant(true)
            .create()

        justice.containingLocation = Global.getSector().hyperspace
        val hunterLoc = Misc.getPointAtRadius(playerFleet.locationInHyperspace, 500f)
        justice.setLocation(hunterLoc.x, hunterLoc.y)

        justice.memoryWithoutUpdate[MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE] = true
        justice.memoryWithoutUpdate[MemFlags.MEMORY_KEY_MAKE_HOSTILE] = true

        NeverendingChaseScript.makeFleetPursuePlayer(justice)

        justice.memoryWithoutUpdate["\$coff_vengeanceFleet"] = true

        justice.addScript(NeverendingChaseScript(justice))

        val eb = justice.getAbility(Abilities.EMERGENCY_BURN)
        eb?.activate()

        return justice
    }
}
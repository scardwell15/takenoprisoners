package captureofficers.campaign.rulecmd

import captureofficers.campaign.VengeanceFleetHandler
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.util.Misc

class COFF_AddVengeancePoint: BaseCommandPlugin() {
    override fun execute(
        ruleId: String,
        dialog: InteractionDialogAPI,
        params: MutableList<Misc.Token>,
        memoryMap: MutableMap<String, MemoryAPI>
    ): Boolean {
        val factionId = dialog.interactionTarget.activePerson.faction.id
        VengeanceFleetHandler.addVengeancePoint(factionId)
        return true
    }
}
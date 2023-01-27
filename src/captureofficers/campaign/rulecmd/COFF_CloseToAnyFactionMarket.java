package captureofficers.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class COFF_CloseToAnyFactionMarket extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        PersonAPI person = Global.getSector().getPlayerFleet().getActivePerson();
        String factionId = person.getFaction().getId();
        for (MarketAPI market : Misc.getFactionMarkets(factionId)) {
            if (market.isInvalidMissionTarget()) continue;
            if (Misc.getDistanceToPlayerLY(market.getLocationInHyperspace()) < 0.25f) {
                return true;
            }
        }

        return false;
    }
}

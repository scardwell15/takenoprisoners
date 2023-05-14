package takenoprisoners.campaign.rulecmd;

import takenoprisoners.campaign.Searcher;
import takenoprisoners.utils.Strings;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class TNP_CloseToMarket extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = Searcher.INST.getMarket(memoryMap.get(MemKeys.LOCAL).getString(Strings.MARKET_MEMKEY));
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        if (Misc.getDistanceToPlayerLY(market.getLocationInHyperspace()) == 0) {
            float dist = 100f;
            try {
                dist = params.get(0).getFloat(memoryMap);
            } catch (Exception ex) {
                //donothing, value may not have existed.
            }

            float currDist = Misc.getDistance(market.getPrimaryEntity(), playerFleet);
            if (currDist < dist) {
                return true;
            }
        }

        return false;
    }
}

package takenoprisoners.campaign.rulecmd;

import takenoprisoners.campaign.Searcher;
import takenoprisoners.utils.Strings;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class TNP_RecalcMarketDistIfSet extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = Searcher.INST.getMarket(memoryMap.get(MemKeys.LOCAL).getString(Strings.MARKET_MEMKEY));
        if (market != null) {
            float dist = Misc.getDistanceToPlayerLY(market.getLocationInHyperspace());
            memoryMap.get(MemKeys.LOCAL).set(Strings.MARKET_MEMKEY, market.getId());
            memoryMap.get(MemKeys.LOCAL).set(Strings.MARKET_NAME_MEMKEY, market.getName());
            memoryMap.get(MemKeys.LOCAL).set(Strings.MARKET_DIST_MEMKEY, Misc.getRoundedValueMaxOneAfterDecimal(dist));
            memoryMap.get(MemKeys.LOCAL).set(Strings.MARKET_COLOR_MEMKEY, market.getTextColorForFactionOrPlanet());
        }
        return true;
    }
}

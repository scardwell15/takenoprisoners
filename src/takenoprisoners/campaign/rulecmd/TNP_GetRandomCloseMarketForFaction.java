package takenoprisoners.campaign.rulecmd;

import takenoprisoners.utils.Strings;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.util.List;
import java.util.Map;

public class TNP_GetRandomCloseMarketForFaction extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String factionId = params.get(0).getString(memoryMap);

        MarketAPI pickedMarket = get(factionId);
        if (pickedMarket != null) {
            float dist = Misc.getDistanceToPlayerLY(pickedMarket.getLocationInHyperspace());
            memoryMap.get(MemKeys.LOCAL).set(Strings.MARKET_MEMKEY, pickedMarket.getId());
            memoryMap.get(MemKeys.LOCAL).set(Strings.MARKET_NAME_MEMKEY, pickedMarket.getName());
            memoryMap.get(MemKeys.LOCAL).set(Strings.MARKET_DIST_MEMKEY, Misc.getRoundedValueMaxOneAfterDecimal(dist));
            memoryMap.get(MemKeys.LOCAL).set(Strings.MARKET_COLOR_MEMKEY, pickedMarket.getTextColorForFactionOrPlanet());

            return true;
        }
        return false;
    }

    public static MarketAPI get(String factionId) {
        WeightedRandomPicker<MarketAPI> marketPicker = new WeightedRandomPicker<>();
        for (MarketAPI market : Misc.getFactionMarkets(factionId)) {
            if (market.isInvalidMissionTarget()) continue;
            float dist = Math.max(Misc.getDistanceToPlayerLY(market.getLocationInHyperspace()), 0.25f);
            marketPicker.add(market, 1000 / (dist * dist));
        }

        MarketAPI pickedMarket = marketPicker.pick();
        return pickedMarket;
    }
}

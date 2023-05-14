package takenoprisoners.campaign.actions.definitions.steps;

import takenoprisoners.campaign.actions.definitions.StepExecutor;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;

public class NearFactionMarket implements StepExecutor.Step {
    public NearFactionMarket() {
    }

    @Override
    public boolean canShow(PersonAPI person) {
        return true;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        String factionId = person.getFaction().getId();
        for (MarketAPI market : Misc.getFactionMarkets(factionId)) {
            if (market.isInvalidMissionTarget()) continue;
            if (Misc.getDistanceToPlayerLY(market.getLocationInHyperspace()) < 0.25f) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute(PersonAPI person, StepExecutor executor) {
    }
}

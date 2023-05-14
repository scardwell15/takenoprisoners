package takenoprisoners.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;

public class Searcher extends HubMissionWithSearch {
    public static Searcher INST = new Searcher();

    public MarketAPI getMarket(String id) {
        if (id == null) {
            return null;
        }

        return Global.getSector().getEconomy().getMarket(id);
    }

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        return false;
    }
}

package takenoprisoners.campaign.rulecmd;

import takenoprisoners.campaign.VengeanceFleetHandler;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class TNP_AddVengeancePoint extends TNPCommandPlugin {
    @Override
    public boolean execute(
            String ruleId,
            InteractionDialogAPI dialog,
            List<Misc.Token> params,
            Map<String, MemoryAPI> memoryMap) {
        String factionId = getActivePerson(dialog).getFaction().getId();
        VengeanceFleetHandler.INSTANCE.addVengeancePoint(factionId);
        return true;
    }
}
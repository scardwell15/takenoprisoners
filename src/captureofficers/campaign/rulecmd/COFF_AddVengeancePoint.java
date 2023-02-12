package captureofficers.campaign.rulecmd;

import captureofficers.campaign.VengeanceFleetHandler;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class COFF_AddVengeancePoint extends BaseCommandPlugin {
    @Override
    public boolean execute(
            String ruleId,
            InteractionDialogAPI dialog,
            List<Misc.Token> params,
            Map<String, MemoryAPI> memoryMap) {
        String factionId = dialog.getInteractionTarget().getActivePerson().getFaction().getId();
        VengeanceFleetHandler.INSTANCE.addVengeancePoint(factionId);
        return true;
    }
}
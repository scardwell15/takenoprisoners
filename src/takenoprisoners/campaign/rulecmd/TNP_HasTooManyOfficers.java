package takenoprisoners.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class TNP_HasTooManyOfficers extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        boolean retVal = Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().size() < Global.getSector().getPlayerStats().getOfficerNumber().getModifiedInt();

        try {
            String optionId = params.get(0).getString(memoryMap);
            dialog.getOptionPanel().setEnabled(optionId, retVal);

            if (!retVal) {
                String optionTooltip = params.get(1).getString(memoryMap);
                if (optionTooltip == null) {
                    optionTooltip = "You have too many officers.";
                }
                dialog.getOptionPanel().setTooltip(optionId, optionTooltip);
            }
        } catch (Exception ex) {
        }

        return retVal;
    }
}

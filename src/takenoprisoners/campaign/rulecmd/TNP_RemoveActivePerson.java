package takenoprisoners.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import takenoprisoners.campaign.actions.definitions.steps.RemovePerson;

import java.util.List;
import java.util.Map;

public class TNP_RemoveActivePerson extends TNPCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        new RemovePerson().execute(getActivePerson(dialog), null);

        return true;
    }
}

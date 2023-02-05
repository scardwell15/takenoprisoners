package captureofficers.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class COFF_LevelUp extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        dialog.getInteractionTarget().getActivePerson().getStats().setLevel(dialog.getInteractionTarget().getActivePerson().getStats().getLevel() + 1);
        dialog.getInteractionTarget().getActivePerson().getStats().setPoints(dialog.getInteractionTarget().getActivePerson().getStats().getPoints() + 1);
        return true;
    }
}

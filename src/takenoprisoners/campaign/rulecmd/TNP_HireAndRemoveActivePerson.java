package takenoprisoners.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import takenoprisoners.TakeNoPrisonersModPlugin;
import takenoprisoners.campaign.actions.definitions.HireAction;
import takenoprisoners.campaign.actions.definitions.steps.RemovePerson;
import takenoprisoners.ui.PrisonersDialogDelegate;

import java.util.List;
import java.util.Map;

public class TNP_HireAndRemoveActivePerson extends TNPCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        PersonAPI person = getActivePerson(dialog);

        HireAction action = new HireAction(dialog);
        action.execute(person);
        new RemovePerson().execute(person, action);

        return true;
    }
}

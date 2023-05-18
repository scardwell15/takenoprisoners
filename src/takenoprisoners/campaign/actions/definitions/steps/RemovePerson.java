package takenoprisoners.campaign.actions.definitions.steps;

import takenoprisoners.TakeNoPrisonersModPlugin;
import takenoprisoners.campaign.actions.definitions.StepExecutor;
import takenoprisoners.ui.PrisonersDialogDelegate;
import com.fs.starfarer.api.characters.PersonAPI;

public class RemovePerson implements StepExecutor.Step {
    @Override
    public void execute(PersonAPI person, StepExecutor executor) {
        TakeNoPrisonersModPlugin.removePerson(person);

        if (PrisonersDialogDelegate.getInst() != null) {
            PrisonersDialogDelegate.getInst().removePerson(person, executor);
        }
    }


    @Override
    public boolean canShow(PersonAPI person) {
        return true;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        return true;
    }
}

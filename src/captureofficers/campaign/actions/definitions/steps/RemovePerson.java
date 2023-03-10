package captureofficers.campaign.actions.definitions.steps;

import captureofficers.campaign.actions.definitions.StepExecutor;
import captureofficers.ui.PrisonersDialogDelegate;
import com.fs.starfarer.api.characters.PersonAPI;

public class RemovePerson implements StepExecutor.Step {
    @Override
    public void execute(PersonAPI person, StepExecutor executor) {
        PrisonersDialogDelegate.getInst().removePerson(person, executor);
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

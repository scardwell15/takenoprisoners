package takenoprisoners.campaign.actions.definitions;

import takenoprisoners.campaign.actions.definitions.steps.FireAllTrigger;
import takenoprisoners.campaign.actions.definitions.steps.RemovePerson;
import takenoprisoners.utils.StringUtils;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.LinkedHashSet;
import java.util.Set;

public class ReleaseAction extends StepExecutor {

    public ReleaseAction(InteractionDialogAPI dialog) {
        super(dialog);
    }

    @Override
    public String getId() {
        return "release";
    }

    @Override
    public Set<Step> getSteps(PersonAPI person) {
        Set<Step> definition = new LinkedHashSet<>();
        definition.add(new FireAllTrigger(dialog, "COFF_ReleasedPrisoner"));
        definition.add(new RemovePerson());
        return definition;
    }

    @Override
    public boolean mustBeAllowed() {
        return false;
    }

    @Override
    public String getButtonText() {
        return StringUtils.getString("ReleaseMethod", "ButtonText");
    }

    @Override
    public void generatedButton(TooltipMakerAPI buttonHolder, ButtonAPI button, PersonAPI person) {
    }

    @Override
    public String getAfterClickDisplayText(PersonAPI person) {
        return StringUtils.getString("ReleaseMethod", "OutputText");
    }
}

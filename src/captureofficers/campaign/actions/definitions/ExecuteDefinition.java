package captureofficers.campaign.actions.definitions;

import captureofficers.campaign.actions.definitions.steps.FireAllTrigger;
import captureofficers.campaign.actions.definitions.steps.RemovePerson;
import captureofficers.utils.StringUtils;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class ExecuteDefinition extends StepExecutor {
    public ExecuteDefinition(InteractionDialogAPI dialog) {
        super(dialog);
    }

    @Override
    public String getId() {
        return "execute";
    }

    @Override
    public Set<Step> getSteps(PersonAPI person) {
        Set<Step> definition = new LinkedHashSet<>();
        definition.add(new FireAllTrigger(dialog, "COFF_ExecutedPrisoner"));
        definition.add(new RemovePerson());
        return definition;
    }

    @Override
    public boolean mustBeAllowed() {
        return false;
    }

    @Override
    public String getButtonText() {
        return StringUtils.getString("ExecuteMethod", "ButtonText");
    }

    @Override
    public void generatedButton(TooltipMakerAPI buttonHolder, ButtonAPI button, PersonAPI person) {
    }

    @Override
    public Color getAfterClickRowColor(PersonAPI person) {
        return new Color(180,100,100);
    }

    @Override
    public String getAfterClickDisplayText(PersonAPI person) {
        return StringUtils.getString("ExecuteMethod", "OutputText");
    }
}

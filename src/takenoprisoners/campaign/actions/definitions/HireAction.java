package takenoprisoners.campaign.actions.definitions;

import takenoprisoners.campaign.actions.definitions.steps.FireAllTrigger;
import takenoprisoners.campaign.actions.definitions.steps.HirePerson;
import takenoprisoners.campaign.actions.definitions.steps.RemovePerson;
import takenoprisoners.utils.StringUtils;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class HireAction extends StepExecutor {
    public HireAction(InteractionDialogAPI dialog) {
        super(dialog);
    }

    @Override
    public String getId() {
        return "hire";
    }

    @Override
    public Set<Step> getSteps(PersonAPI person) {
        Set<Step> definition = new LinkedHashSet<>();
        definition.add(new FireAllTrigger(dialog, "COFF_HiredActivePerson"));
        definition.add(new HirePerson());
        definition.add(new RemovePerson());

        return definition;
    }

    @Override
    public boolean canShow(PersonAPI person) {
        return true;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        return true;
    }

    @Override
    public boolean mustBeAllowed() {
        return true;
    }

    @Override
    public String getButtonText() {
        return StringUtils.getString("HireMethod", "ButtonText");
    }

    @Override
    public void generatedButton(TooltipMakerAPI buttonHolder, ButtonAPI button, PersonAPI person) {
    }

    @Override
    public Color getAfterClickRowColor(PersonAPI person) {
        return new Color(100,140,180);
    }

    @Override
    public String getAfterClickDisplayText(PersonAPI person) {
        return StringUtils.getString("HireMethod", "OutputText");
    }
}

package takenoprisoners.campaign.actions.definitions;

import takenoprisoners.campaign.actions.definitions.steps.HirePerson;
import takenoprisoners.campaign.actions.definitions.steps.RemovePerson;
import takenoprisoners.campaign.actions.definitions.steps.TakeCredits;
import takenoprisoners.utils.StringUtils;
import takenoprisoners.utils.Strings;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class BribeAction extends StepExecutor {
    private static final float PER_LEVEL_CREDITS = 1500f;

    public BribeAction(InteractionDialogAPI dialog) {
        super(dialog);
    }

    @Override
    public String getId() {
        return "bribe";
    }

    @Override
    public Set<Step> getSteps(PersonAPI person) {
        Set<Step> definition = new LinkedHashSet<>();
        definition.add(new TakeCredits(Strings.BRIBE_PRICE_MEMKEY, getDefaultCreditLoss(person)));
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
        return StringUtils.getString("BribeMethod", "ButtonText");
    }

    @Override
    public void generatedButton(TooltipMakerAPI buttonHolder, ButtonAPI button, PersonAPI person) {
    }


    private int getCreditLoss(PersonAPI person) {
        if (person.getMemoryWithoutUpdate().contains(Strings.BRIBE_PRICE_MEMKEY)) {
            return ((Number) person.getMemoryWithoutUpdate().get(Strings.BRIBE_PRICE_MEMKEY)).intValue();
        }
        return getDefaultCreditLoss(person);
    }

    private int getDefaultCreditLoss(PersonAPI person) {
        return ((Number) (person.getStats().getLevel() * PER_LEVEL_CREDITS)).intValue();
    }

    @Override
    public Color getAfterClickRowColor(PersonAPI person) {
        return new Color(180,168,100);
    }

    @Override
    public String getAfterClickDisplayText(PersonAPI person) {
        int credits = getCreditLoss(person);
        return StringUtils.getTranslation("BribeMethod", "OutputText")
                .format("credits", credits)
                .toStringNoFormats();
    }
}

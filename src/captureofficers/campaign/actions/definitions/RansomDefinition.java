package captureofficers.campaign.actions.definitions;

import captureofficers.campaign.actions.definitions.steps.NearFactionMarket;
import captureofficers.campaign.actions.definitions.steps.RemovePerson;
import captureofficers.campaign.actions.definitions.steps.TakeCredits;
import captureofficers.config.FactionConfig;
import captureofficers.config.FactionConfigLoader;
import captureofficers.utils.AllowedActions;
import captureofficers.utils.StringUtils;
import captureofficers.utils.Strings;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class RansomDefinition extends StepExecutor {
    private static final float PER_LEVEL_CREDITS = 750f;

    public RansomDefinition(InteractionDialogAPI dialog) {
        super(dialog);
    }

    @Override
    public String getId() {
        return "ransom";
    }

    @Override
    public Set<Step> getSteps(PersonAPI person) {
        Set<Step> definition = new LinkedHashSet<>();
        definition.add(new NearFactionMarket());
        definition.add(new TakeCredits(Strings.RANSOM_PRICE_MEMKEY, getDefaultCreditGain(person), true));
        definition.add(new RemovePerson());

        return definition;
    }

    @Override
    public boolean canShow(PersonAPI person) {
        FactionConfig config = FactionConfigLoader.getFactionConfig(person.getFaction().getId());
        if ((config == null || config.getAcceptsRansoms()) && AllowedActions.contains(person, this)) {
            return super.canShow(person);
        }
        return false;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        return new NearFactionMarket().canUse(person);
    }

    @Override
    public boolean mustBeAllowed() {
        return false;
    }

    @Override
    public String getButtonText() {
        return StringUtils.getString("RansomMethod", "ButtonText");
    }

    @Override
    public void generatedButton(TooltipMakerAPI buttonHolder, ButtonAPI button, PersonAPI person) {
    }

    private int getCreditGain(PersonAPI person) {
        if (person.getMemoryWithoutUpdate().contains(Strings.RANSOM_PRICE_MEMKEY)) {
            return ((Number) person.getMemoryWithoutUpdate().get(Strings.RANSOM_PRICE_MEMKEY)).intValue();
        }
        return getDefaultCreditGain(person);
    }

    private int getDefaultCreditGain(PersonAPI person) {
        return ((Number) (person.getStats().getLevel() * PER_LEVEL_CREDITS)).intValue();
    }

    @Override
    public Color getAfterClickRowColor(PersonAPI person) {
        return new Color(180, 168, 100);
    }

    @Override
    public String getAfterClickDisplayText(PersonAPI person) {
        int credits = getCreditGain(person);

        return StringUtils.getTranslation("BribeMethod", "OutputText")
                .format("credits", credits)
                .toStringNoFormats();
    }
}

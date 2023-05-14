package takenoprisoners.campaign.actions.definitions;

import takenoprisoners.campaign.listeners.ReopenUIEveryFrameScript;
import takenoprisoners.config.FactionConfiguration;
import takenoprisoners.config.FactionConfigurationLoader;
import takenoprisoners.ui.PrisonersDialogDelegate;
import takenoprisoners.utils.StringUtils;
import takenoprisoners.utils.Strings;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.PersonalityAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class TalkAction extends PrisonersDialogDelegate.ActionDefinitionImpl {

    public TalkAction(InteractionDialogAPI dialog) {
        super(dialog);
    }

    @Override
    public String getId() {
        return "talk";
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
        return false;
    }

    @Override
    public String getButtonText() {
        return StringUtils.getString("TalkMethod", "ButtonText");
    }

    @Override
    public void execute(PersonAPI person) {
        Global.getSector().getCampaignUI().getCurrentInteractionDialog().dismiss();

        FactionConfiguration config = FactionConfigurationLoader.getFactionConfig(person.getFaction().getId());

        person.getMemoryWithoutUpdate().set(Strings.IS_PRISONER_MEMKEY, true);
        person.getMemoryWithoutUpdate().set(Strings.PERSONALITY_DEGREE_MEMKEY, getPersonalityDegree(person.getPersonalityAPI()));
        person.getMemoryWithoutUpdate().set(Strings.RANSOMABLE, config != null && config.getAcceptsRansoms());
        Global.getSector().addScript(new EndedConvoEveryFrameScript(person));
    }

    @Override
    public void generatedButton(TooltipMakerAPI buttonHolder, ButtonAPI button, PersonAPI person) {
    }

    public static class EndedConvoEveryFrameScript extends ReopenUIEveryFrameScript {
        private final PersonAPI person;
        public EndedConvoEveryFrameScript(PersonAPI person) {
            this.person = person;
        }

        @Override
        public void exitedMenu() {
            // runcode com.fs.starfarer.api.impl.campaign.DebugFlags.PRINT_RULES_DEBUG_INFO = true
            person.getMemoryWithoutUpdate().set(Strings.IS_PRISONER_MEMKEY, true);

            RuleBasedInteractionDialogPluginImpl plugin = new PrisonerInteractionDialogPluginImpl(person);
            Global.getSector().getCampaignUI().showInteractionDialog(plugin, Global.getSector().getPlayerFleet());
            Global.getSector().getCampaignUI().getCurrentInteractionDialog().getVisualPanel().showPersonInfo(person);
        }

        @Override
        public void reopen() {
            if (person.getMemoryWithoutUpdate().contains(Strings.POST_DIALOG_STEPS_MEMKEY)) {
                Object data = person.getMemoryWithoutUpdate().get(Strings.POST_DIALOG_STEPS_MEMKEY);

                Set<String> stepTags = new LinkedHashSet<>();
                if (data instanceof String) {
                    String text = data.toString();

                    if (text.contains(",")) {
                        stepTags.addAll(Arrays.asList(text.split(",")));
                    } else {
                        stepTags.add(text);
                    }
                } else if (data instanceof Set) {
                    stepTags = (Set<String>) data;
                } else {
                    throw new RuntimeException(String.format("Values in %s memory key must be a String or set of Strings that correspond to valid CaptureOfficers Step tags. The person who threw is %s.", Strings.POST_DIALOG_STEPS_MEMKEY, person.getNameString()));
                }

                StepExecutor.executeTags(person, stepTags);
                person.getMemoryWithoutUpdate().unset(Strings.POST_DIALOG_STEPS_MEMKEY);
            }

            person.getMemoryWithoutUpdate().unset(Strings.IS_PRISONER_MEMKEY);

            super.reopen();
        }
    }

    public static class PrisonerInteractionDialogPluginImpl extends RuleBasedInteractionDialogPluginImpl {
        private PersonAPI person;
        public PrisonerInteractionDialogPluginImpl(PersonAPI person) {
            this.person = person;
        }

        @Override
        public void init(InteractionDialogAPI dialog) {
            if (person != null) {
                dialog.getInteractionTarget().setActivePerson(person);
                person = null;
            }

            super.init(dialog);
        }
    }

    private static int getPersonalityDegree(PersonalityAPI personality) {
        if (personality.getId().equals(Personalities.TIMID))
            return -2;
        else if (personality.getId().equals(Personalities.CAUTIOUS))
            return -1;
        else if (personality.getId().equals(Personalities.STEADY))
            return 0;
        else if (personality.getId().equals(Personalities.AGGRESSIVE))
            return 1;
        else if (personality.getId().equals(Personalities.RECKLESS))
            return 2;
        return 0;
    }
}

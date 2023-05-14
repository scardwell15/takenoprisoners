package takenoprisoners.campaign.actions.definitions;

import takenoprisoners.campaign.actions.definitions.steps.HirePerson;
import takenoprisoners.campaign.actions.definitions.steps.RemovePerson;
import takenoprisoners.ui.PrisonersDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.characters.PersonAPI;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Easily execute a series of pre-defined actions.
 */
public abstract class StepExecutor extends PrisonersDialogDelegate.ActionDefinitionImpl {
    public StepExecutor(InteractionDialogAPI dialog) {
        super(dialog);
    }

    public abstract Set<Step> getSteps(PersonAPI person);

    @Override
    public boolean canShow(PersonAPI person) {
        for (Step action : getSteps(person)) {
            if (!action.canShow(person)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        for (Step action : getSteps(person)) {
            if (!action.canUse(person)) {
                return false;
            }
        }
        return true;
    }

    public void execute(PersonAPI person) {
        for (Step action : getSteps(person)) {
            action.execute(person, this);
        }
    }

    public enum Type {
        HIRE("hire", HirePerson.class),
        REMOVE("remove", RemovePerson.class);

        public String tag;
        public Class<? extends Step> stepClass;
        Type(String tag, Class<? extends Step> stepClass) {
            this.tag = tag;
            this.stepClass = stepClass;
        }
    }

    public interface Step {
        boolean canShow(PersonAPI person);
        boolean canUse(PersonAPI person);
        void execute(PersonAPI person, StepExecutor executor);
    }

    public static Set<Step> getStepsFromList(Set<String> tags) {
        Set<Step> steps = new LinkedHashSet<>();
        for (String tag : tags) {
            for (Type type : Type.values()) {
                if (type.tag.equals(tag)) {
                    try {
                        steps.add(type.stepClass.newInstance());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return steps;
    }

    public static void executeTags(PersonAPI person, Set<String> tags) {
        Set<Step> steps = getStepsFromList(tags);
        for (Step action : steps) {
            action.execute(person, null);
        }
    }
}

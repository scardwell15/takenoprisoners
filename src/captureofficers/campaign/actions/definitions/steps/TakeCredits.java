package captureofficers.campaign.actions.definitions.steps;

import captureofficers.campaign.actions.definitions.StepExecutor;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;

public class TakeCredits implements StepExecutor.Step {
    private String memKey;
    private int defaultCredits;
    public TakeCredits(String memKey) {
        this(memKey, 0);
    }
    public TakeCredits(String memKey, int defaultCredits) {
        this.memKey = memKey;
        this.defaultCredits = defaultCredits;
    }

    @Override
    public boolean canShow(PersonAPI person) {
        return true;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        return getCredits(person) <= 0 || Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= getCredits(person);
    }

    @Override
    public void execute(PersonAPI person, StepExecutor executor) {
        int credits = getCredits(person);

        if (credits > 0) {
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(credits);
        } else if (credits < 0) {
            Global.getSector().getPlayerFleet().getCargo().getCredits().add(Math.abs(credits));
        }
    }

    public int getCredits(PersonAPI person) {
        if (person.getMemoryWithoutUpdate().contains(this.memKey)) {
            return (int) person.getMemoryWithoutUpdate().get(this.memKey);
        } else if (defaultCredits != 0) {
            return defaultCredits;
        }
        throw new IllegalArgumentException("Could not get valid credits amount.");
    }
}

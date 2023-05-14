package takenoprisoners.campaign.actions.definitions.steps;

import takenoprisoners.campaign.actions.definitions.StepExecutor;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;

public class TakeCredits implements StepExecutor.Step {
    private String memKey;
    private int defaultCredits;
    private boolean giveCredits;

    public TakeCredits(String memKey) {
        this(memKey, 0, false);
    }

    public TakeCredits(String memKey, boolean giveCredits) {
        this(memKey, 0, giveCredits);
    }

    public TakeCredits(String memKey, int defaultCredits) {
        this(memKey, defaultCredits, false);
    }

    public TakeCredits(String memKey, int defaultCredits, boolean giveCredits) {
        this.memKey = memKey;
        this.defaultCredits = defaultCredits;
        this.giveCredits = giveCredits;
    }

    @Override
    public boolean canShow(PersonAPI person) {
        return true;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        if (giveCredits) {
            return getCredits(person) != 0;
        }

        return Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= getCredits(person);
    }

    @Override
    public void execute(PersonAPI person, StepExecutor executor) {
        int credits = Math.abs(getCredits(person));

        if (giveCredits) {
            Global.getSector().getPlayerFleet().getCargo().getCredits().add(credits);
        } else {
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(credits);
        }
    }

    public int getCredits(PersonAPI person) {
        if (person.getMemoryWithoutUpdate().contains(this.memKey)) {
            return ((Number) person.getMemoryWithoutUpdate().get(this.memKey)).intValue();
        } else if (defaultCredits != 0) {
            return defaultCredits;
        }
        throw new IllegalArgumentException("Could not get valid credits amount.");
    }
}

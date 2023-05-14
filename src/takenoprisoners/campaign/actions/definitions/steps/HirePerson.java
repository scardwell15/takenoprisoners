package takenoprisoners.campaign.actions.definitions.steps;

import takenoprisoners.campaign.actions.definitions.StepExecutor;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;

public class HirePerson implements StepExecutor.Step {
    private final float credits;

    public HirePerson() {
        credits = 0f;
    }

    public HirePerson(float credits) {
        this.credits = credits;
    }

    @Override
    public boolean canShow(PersonAPI person) {
        return true;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        return Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().size() < Global.getSector().getPlayerStats().getOfficerNumber().getModifiedInt();
    }

    @Override
    public void execute(PersonAPI person, StepExecutor executor) {
        if (this.credits > 0) {
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(this.credits);
        }

        person.setFaction(Global.getSector().getPlayerFaction().getId());
        Global.getSector().getPlayerFleet().getFleetData().addOfficer(person);
    }
}

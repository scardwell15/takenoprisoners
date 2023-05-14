package takenoprisoners.campaign.actions.definitions.steps;

import takenoprisoners.campaign.actions.definitions.StepExecutor;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.ReputationActionResponsePlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;

public class AdjustFactionRep implements StepExecutor.Step {
    protected RepLevel limit;
    protected int adjust;

    public AdjustFactionRep(RepLevel limit, int adjust) {
        this.limit = limit;
        this.adjust = adjust;
    }

    @Override
    public void execute(PersonAPI person, StepExecutor executor) {
        CoreReputationPlugin.CustomRepImpact impact = new CoreReputationPlugin.CustomRepImpact();
        impact.limit = limit;
        impact.delta = adjust * 0.01f;
        ReputationActionResponsePlugin.ReputationAdjustmentResult result = Global.getSector().adjustPlayerReputation(
                new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM, impact,
                        null, null, true),
                person.getFaction().getId());
    }


    @Override
    public boolean canShow(PersonAPI person) {
        return true;
    }

    @Override
    public boolean canUse(PersonAPI person) {
        return true;
    }
}

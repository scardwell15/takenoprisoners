package takenoprisoners.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;

public abstract class TNPCommandPlugin extends BaseCommandPlugin {
    public PersonAPI getActivePerson() {
        return getActivePerson(null);
    }

    public PersonAPI getActivePerson(InteractionDialogAPI dialog) {
        SectorEntityToken token = Global.getSector().getPlayerFleet();
        if (dialog != null && dialog.getInteractionTarget() != null && dialog.getInteractionTarget().getActivePerson() != null) {
            token = dialog.getInteractionTarget();
        }

        return token.getActivePerson();
    }
}

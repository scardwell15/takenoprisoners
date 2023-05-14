package takenoprisoners.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;

import java.util.List;

public class ViewPrisonersUI {
    public static final float PRISONER_ROW_SIZE = 64;

    public static void showPrisonersPanel(List<PersonAPI> members) {
        Global.getSector().getCampaignUI().showInteractionDialog(new PrisonersPanelCreatorPlugin(members), Global.getSector().getPlayerFleet());
    }
}

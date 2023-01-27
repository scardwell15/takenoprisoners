package captureofficers.campaign.listeners;

import captureofficers.CaptureOfficers;
import captureofficers.ui.ViewPrisonersUI;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;

public class ReopenUIEveryFrameScript implements EveryFrameScript {
    private boolean started = false;

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        boolean showingDialog = Global.getSector().getCampaignUI().isShowingDialog();
        boolean isShowingMenu = Global.getSector().getCampaignUI().isShowingMenu();

        if (Global.getSector().getCampaignUI().isShowingDialog() || Global.getSector().getCampaignUI().isShowingMenu()) {
            if (!started) {
                started = true;
            }
            return;
        } else if (!started) {
            exitedMenu();
            return;
        }

        Global.getSector().removeScript(this);
        reopen();
    }

    public void exitedMenu() {
    }

    public void reopen() {
        ViewPrisonersUI.showPrisonersPanel(CaptureOfficers.getPersons());
    }
}
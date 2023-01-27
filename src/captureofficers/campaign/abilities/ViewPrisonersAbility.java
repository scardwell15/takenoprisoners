package captureofficers.campaign.abilities;

import captureofficers.CaptureOfficers;
import captureofficers.ui.ViewPrisonersUI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;

public class ViewPrisonersAbility extends BaseDurationAbility {
    @Override
    protected void activateImpl() {
        //show menu
        ViewPrisonersUI.showPrisonersPanel(CaptureOfficers.getPersons());
    }

    @Override
    protected void applyEffect(float amount, float level) {
    }

    @Override
    protected void deactivateImpl() {
    }

    @Override
    protected void cleanupImpl() {
    }
}

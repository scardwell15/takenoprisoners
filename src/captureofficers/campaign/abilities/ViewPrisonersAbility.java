package captureofficers.campaign.abilities;

import captureofficers.CaptureOfficers;
import captureofficers.ui.ViewPrisonersUI;
import captureofficers.utils.StringUtils;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

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

    @Override
    public boolean hasTooltip() {
        return true;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        LabelAPI title = tooltip.addTitle(spec.getName());
        title.setHighlightColor(Misc.getGrayColor());
        tooltip.addPara(StringUtils.getString("PrisonerUI", "AbilityDesc"), 10f);
    }
}

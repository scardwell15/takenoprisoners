package takenoprisoners.ui;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class StringTooltip extends BaseTooltipCreator {
    private final TooltipMakerAPI tooltip;
    private final String description;

    public StringTooltip(TooltipMakerAPI tooltip, String description) {
        this.tooltip = tooltip;
        this.description = description;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return Math.min(tooltip.computeStringWidth(description), 300f);
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addPara(description, 3f);
    }

    public static void addToTooltip(TooltipMakerAPI tooltip, String text) {
        tooltip.addTooltipToPrevious(new StringTooltip(tooltip, text), TooltipMakerAPI.TooltipLocation.BELOW);
    }
}

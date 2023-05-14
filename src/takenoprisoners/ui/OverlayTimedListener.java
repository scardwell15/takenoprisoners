package takenoprisoners.ui;

import takenoprisoners.utils.RenderUtils;
import com.fs.starfarer.api.ui.PositionAPI;

import java.awt.*;

public class OverlayTimedListener implements TimedUIPlugin.Listener {
    protected Color color = Color.yellow;

    public OverlayTimedListener() {}

    public OverlayTimedListener(Color color) {
        this.color = color;
    }

    @Override
    public void end() {
    }

    @Override
    public void render(PositionAPI pos, float alphaMult, float currLife, float endLife) {
    }

    @Override
    public void renderBelow(PositionAPI pos, float alphaMult, float currLife, float endLife) {
        RenderUtils.pushUIRenderingStack();

        float panelX = pos.getX();
        float panelY = pos.getY();
        float panelW = pos.getWidth();
        float panelH = pos.getHeight();
        RenderUtils.renderBox(panelX, panelY, panelW, panelH, color, alphaMult * (endLife - currLife) / endLife);

        RenderUtils.popUIRenderingStack();
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

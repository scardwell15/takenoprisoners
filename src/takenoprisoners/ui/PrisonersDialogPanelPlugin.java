package takenoprisoners.ui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;

import java.util.List;

public class PrisonersDialogPanelPlugin implements CustomUIPanelPlugin {
    private final PrisonersDialogDelegate delegate;

    public PrisonersDialogPanelPlugin(PrisonersDialogDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void positionChanged(PositionAPI position) {
    }

    @Override
    public void renderBelow(float alphaMult) {
    }

    @Override
    public void render(float alphaMult) {
    }

    @Override
    public void advance(float amount) {
        delegate.advance(amount);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}

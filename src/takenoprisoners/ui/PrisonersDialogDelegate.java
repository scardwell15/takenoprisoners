package takenoprisoners.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import takenoprisoners.TakeNoPrisonersModPlugin;
import takenoprisoners.campaign.actions.definitions.*;
import takenoprisoners.utils.AllowedActions;
import takenoprisoners.utils.StringUtils;
import takenoprisoners.utils.Strings;

import java.awt.*;
import java.util.List;
import java.util.*;

public class PrisonersDialogDelegate implements CustomDialogDelegate {
    private static Logger log = Logger.getLogger(PrisonersDialogDelegate.class);
    private static PrisonersDialogDelegate inst = null;

    public static PrisonersDialogDelegate getInst() {
        return inst;
    }

    protected final float panelWidth;
    protected final float panelHeight;
    protected final float allRowsHeight;

    protected final List<PersonAPI> members;
    protected final List<ButtonData> buttons = new ArrayList<>();

    @Getter
    protected final InteractionDialogAPI dialog;
    protected final PrisonersDialogPanelPlugin plugin;
    protected CustomPanelAPI panel = null;
    protected TooltipMakerAPI parentTooltip = null;
    private boolean invalidateLayout = false;


    public PrisonersDialogDelegate(InteractionDialogAPI dialog, List<PersonAPI> members, float panelWidth, float panelHeight, float allRowsHeight) {
        this.dialog = dialog;
        this.members = members;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.allRowsHeight = allRowsHeight;
        this.plugin = new PrisonersDialogPanelPlugin(this);
        inst = this;
    }

    @Override
    public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
        TooltipMakerAPI tt = panel.createUIElement(panelWidth, panelHeight - 16, true);
        this.parentTooltip = tt;

        String headerStr = StringUtils.getString("PrisonerUI", "Title");
        tt.addSectionHeading(headerStr, Alignment.MID, 0);

        CustomPanelAPI custom = panel.createCustomPanel(panelWidth, allRowsHeight, null);
        this.panel = custom;

        //TooltipMakerAPI outer = custom.createUIElement(panelWidth, panelHeight - 16, true);

        int currYOffset = 3;
        for (PersonAPI member : members) {
            TooltipMakerAPI currRow = addRow(custom, member);
            custom.addUIElement(currRow).inTL(3, currYOffset);
            currYOffset += (ViewPrisonersUI.PRISONER_ROW_SIZE + 3);
        }

        //custom.addUIElement(outer).inTL(0, 0);
        tt.addCustom(custom, 0);
        panel.addUIElement(tt).inTL(0, 0);
    }

    /**
     * Adds a row for the specified fleet member's info.
     */
    public TooltipMakerAPI addRow(CustomPanelAPI outer, PersonAPI member) {
        float pad = 3;
        float opad = 10;
        float textWidth = 240;
        Color f = member.getFaction().getBaseUIColor();

        TooltipMakerAPI rowTooltip = outer.createUIElement(panelWidth, ViewPrisonersUI.PRISONER_ROW_SIZE, false);
        CustomPanelAPI contentHolder = outer.createCustomPanel(panelWidth, ViewPrisonersUI.PRISONER_ROW_SIZE, null);

        // Ship image with tooltip of the ship class
        TooltipMakerAPI portraitHolder = contentHolder.createUIElement(ViewPrisonersUI.PRISONER_ROW_SIZE, ViewPrisonersUI.PRISONER_ROW_SIZE, false);
        portraitHolder.addImage(member.getPortraitSprite(), ViewPrisonersUI.PRISONER_ROW_SIZE, 0);
        contentHolder.addUIElement(portraitHolder).inTL(0, 0);

        // Ship name, class, bandwidth
        TooltipMakerAPI textHolder = contentHolder.createUIElement(textWidth, ViewPrisonersUI.PRISONER_ROW_SIZE, false);

        FactionAPI faction = member.getFaction();
        String factionName = faction.getDisplayNameOverride() != null
                ? faction.getDisplayNameOverride()
                : faction.getDisplayName();

        if (factionName.equals("pirates")) {
            factionName = "Pirate";
        }

        textHolder.addPara(member.getNameString(), f, 0);
        StringUtils.getTranslation("PrisonerUI", "LevelText")
                .format("level", member.getStats().getLevel())
                .format("personality", member.getPersonalityAPI().getDisplayName())
                .addToTooltip(textHolder);

        StringUtils.getTranslation("PrisonerUI", "FactionText")
                .format("factionName", factionName)
                .format("rank", member.getRank())
                .addToTooltip(textHolder);

        contentHolder.addUIElement(textHolder).rightOfMid(portraitHolder, pad);

        List<MutableCharacterStatsAPI.SkillLevelAPI> skills = new ArrayList<>();
        for (MutableCharacterStatsAPI.SkillLevelAPI skillLevel : member.getStats().getSkillsCopy()) {
            SkillSpecAPI skill = skillLevel.getSkill();
            if (skill.isAptitudeEffect()) continue;
            if (skill.isAdminSkill()) continue;
            if (skill.isAdmiralSkill()) continue;

            skills.add(skillLevel);
        }

        float iconSize = (float) (56f - 8f * Math.min(Math.floor(skills.size() / 8f), 3));

        UIComponentAPI lastSkillHolder = null;
        for (MutableCharacterStatsAPI.SkillLevelAPI skillLevel : skills) {
            SkillSpecAPI skill = skillLevel.getSkill();
            TooltipMakerAPI skillHolder = contentHolder.createUIElement(iconSize, iconSize, false);

            skillHolder.addImage(skill.getSpriteName(), iconSize, 0);

            if (skillLevel.getLevel() > 1) {
                String eliteImage = String.format("graphics/icons/skills/elite_%s.png", skill.getGoverningAptitudeId());
                SpriteAPI sprite = Global.getSettings().getSprite(eliteImage);
                if (sprite == null || sprite.getWidth() == 0) {
                    eliteImage = "graphics/icons/skills/elite_industry.png";
                }
                skillHolder.addImage(eliteImage, iconSize, -iconSize);
            }

            SkillTooltip.addToTooltip(skillHolder, member, skill, skillLevel.getLevel());

            if (lastSkillHolder == null) {
                contentHolder.addUIElement(skillHolder).rightOfMid(textHolder, 3);
            } else {
                contentHolder.addUIElement(skillHolder).rightOfMid(lastSkillHolder, 3);
            }

            lastSkillHolder = skillHolder;
        }

        int columnLength = 2; //number of buttons
        float columnXOffset = 0;
        float columnYOffset = 0;
        int index = 0;

        Set<ActionDefinition> actions = getActions(member);
        float buttonHolderWidth = (float) (Math.ceil((float) actions.size() / (float) columnLength) * 64f);
        TooltipMakerAPI buttonHolder = contentHolder.createUIElement(buttonHolderWidth, ViewPrisonersUI.PRISONER_ROW_SIZE, false);


        for (ActionDefinition choice : actions) {
            index++;

            CutStyle cutStyle = CutStyle.TL_BR;
            if (index == columnLength || columnXOffset != 0) {
                cutStyle = CutStyle.BL_TR;
            }

            ButtonAPI button = buttonHolder.addButton(choice.getButtonText(), null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, cutStyle, 64, 22, 3);
            button.getPosition().inTL(columnXOffset, columnYOffset);
            button.setEnabled(choice.canUse(member));
            choice.generatedButton(buttonHolder, button, member);

            buttons.add(new ButtonData(dialog, choice, member, button, rowTooltip));

            if (index >= columnLength) {
                columnXOffset += (64 + 3);
                columnYOffset = 0;
                index = 0;
            } else {
                columnYOffset += (22 + 3);
            }
        }

        contentHolder.addUIElement(buttonHolder).inTR(pad * 6, pad);

        // done, add row to TooltipMakerAPI
        rowTooltip.addCustom(contentHolder, opad);
        return rowTooltip;
    }

    public void removePerson(PersonAPI person, ActionDefinition displayedAction) {
        members.remove(person);

        ButtonData deletedData = null;
        Iterator<ButtonData> buttonIterator = buttons.iterator();
        while (buttonIterator.hasNext()) {
            ButtonData data = buttonIterator.next();
            if (data.getPerson().equals(person)) {
                if (displayedAction != null && deletedData == null) {
                    deletedData = data;
                } else {
                    buttonIterator.remove();
                }
                removeRow(data.getRow());
            }
        }

        if (deletedData != null) {
            OfficerRemoveOverlayListener listener = new OfficerRemoveOverlayListener(deletedData);
            listener.setColor(displayedAction.getAfterClickRowColor(person));

            TooltipMakerAPI rowTooltip = panel.createUIElement(panelWidth, ViewPrisonersUI.PRISONER_ROW_SIZE, false);
            CustomPanelAPI pluginHolder = panel.createCustomPanel(panelWidth, ViewPrisonersUI.PRISONER_ROW_SIZE, new TimedUIPlugin(1.25f, listener));
            TooltipMakerAPI textHolder = pluginHolder.createUIElement(panelWidth, ViewPrisonersUI.PRISONER_ROW_SIZE, false);

            textHolder.addPara(displayedAction.getAfterClickDisplayText(person), 0).getPosition().inMid();

            pluginHolder.addUIElement(textHolder).inTL(0, 0);
            rowTooltip.addCustom(pluginHolder, 0).getPosition().inTL(3, 12);
            panel.addUIElement(rowTooltip).inTL(3, 0);

            deletedData.setRow(rowTooltip);
            deletedData.setButton(null);
        }
    }

    private void removeRow(TooltipMakerAPI row) {
        panel.removeComponent(row);
        invalidateLayout = true;
    }

    public void advance(float amount) {
        for (ButtonData data : buttons) {
            ButtonAPI button = data.getButton();

            if (button != null && button.isChecked() && button.isEnabled()) {
                button.setChecked(false);
                data.execute();
                break;
            }
        }

        if (invalidateLayout) {
            invalidateLayout = false;

            Set<TooltipMakerAPI> rows = new LinkedHashSet<>();
            for (ButtonData data : buttons) {
                rows.add(data.getRow());
            }

            int currYOffset = 3;
            for (TooltipMakerAPI row : rows) {
                row.getPosition().inTL(3, currYOffset);
                currYOffset += (ViewPrisonersUI.PRISONER_ROW_SIZE + 3);
            }
        }
    }

    @Override
    public boolean hasCancelButton() {
        return false;
    }

    @Override
    public String getConfirmText() {
        return null;
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public void customDialogConfirm() {
        dialog.dismiss();
    }

    @Override
    public void customDialogCancel() {
        dialog.dismiss();
    }

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return plugin;
    }

    public List<ButtonData> getButtons() {
        return buttons;
    }

    public boolean isInvalidateLayout() {
        return invalidateLayout;
    }

    public void setInvalidateLayout(boolean invalidateLayout) {
        this.invalidateLayout = invalidateLayout;
    }

    public Set<ActionDefinition> getActions(PersonAPI person) {
        log.info(String.format("name = %s allowedActions = %s", person.getNameString(), person.getMemoryWithoutUpdate().getString(Strings.ALLOWED_ACTIONS_MEMKEY)));

        Set<ActionDefinition> defs = new LinkedHashSet<>();
        defs.add(new TalkAction(dialog));
        defs.add(new HireAction(dialog));
        defs.add(new BribeAction(dialog));
        defs.add(new RansomAction(dialog));
        defs.add(new ReleaseAction(dialog));
        defs.add(new ExecuteAction(dialog));

        Iterator<ActionDefinition> defIterator = defs.iterator();
        while (defIterator.hasNext()) {
            ActionDefinition def = defIterator.next();

            boolean allowed = AllowedActions.contains(person, def, def.mustBeAllowed());
            boolean canShow = def.canShow(person);

            log.info(String.format("allowed = %s canShow = %s", allowed, canShow));

            if (!allowed || !canShow) {
                defIterator.remove();
            }
        }

        return defs;
    }

    public interface ActionDefinition {
        String getId();

        boolean canShow(PersonAPI person);

        boolean canUse(PersonAPI person);

        boolean mustBeAllowed();

        String getButtonText();

        void execute(PersonAPI person);

        void generatedButton(TooltipMakerAPI buttonHolder, ButtonAPI button, PersonAPI person);

        Color getAfterClickRowColor(PersonAPI person);

        String getAfterClickDisplayText(PersonAPI person);
    }

    @RequiredArgsConstructor
    public static abstract class ActionDefinitionImpl implements ActionDefinition {
        protected final InteractionDialogAPI dialog;


        @Override
        public Color getAfterClickRowColor(PersonAPI person) {
            return new Color(200, 200, 200, 200);
        }

        @Override
        public String getAfterClickDisplayText(PersonAPI person) {
            return "";
        }
    }

    public static class ButtonData {
        private final InteractionDialogAPI dialog;
        private final ActionDefinition action;
        private final PersonAPI person;
        private ButtonAPI button = null;
        private TooltipMakerAPI row = null;

        public ButtonData(InteractionDialogAPI dialog, ActionDefinition action, PersonAPI person) {
            this.dialog = dialog;
            this.action = action;
            this.person = person;
        }

        public ButtonData(InteractionDialogAPI dialog, ActionDefinition action, PersonAPI person, ButtonAPI button) {
            this.dialog = dialog;
            this.action = action;
            this.person = person;
            this.button = button;
        }

        public ButtonData(InteractionDialogAPI dialog, ActionDefinition action, PersonAPI person, TooltipMakerAPI row) {
            this.dialog = dialog;
            this.action = action;
            this.person = person;
            this.row = row;
        }

        public ButtonData(InteractionDialogAPI dialog, ActionDefinition action, PersonAPI person, ButtonAPI button, TooltipMakerAPI row) {
            this.dialog = dialog;
            this.action = action;
            this.person = person;
            this.button = button;
            this.row = row;
        }

        public ActionDefinition getAction() {
            return action;
        }

        public PersonAPI getPerson() {
            return person;
        }

        public ButtonAPI getButton() {
            return button;
        }

        public void setButton(ButtonAPI button) {
            this.button = button;
        }

        public TooltipMakerAPI getRow() {
            return row;
        }

        public void setRow(TooltipMakerAPI row) {
            this.row = row;
        }

        public void execute() {
            PersonAPI activePerson = this.dialog.getInteractionTarget().getActivePerson();
            this.dialog.getInteractionTarget().setActivePerson(person);
            this.action.execute(person);
            this.dialog.getInteractionTarget().setActivePerson(activePerson);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ButtonData that = (ButtonData) o;

            if (action != that.action) return false;
            return person.equals(that.person);
        }

        @Override
        public int hashCode() {
            int result = action.hashCode();
            result = 31 * result + person.hashCode();
            return result;
        }
    }

    public class OfficerRemoveOverlayListener extends OverlayTimedListener {
        private final ButtonData officerData;

        public OfficerRemoveOverlayListener(ButtonData officerData) {
            this.officerData = officerData;
        }

        @Override
        public void end() {
            Iterator<ButtonData> buttonIterator = buttons.iterator();
            while (buttonIterator.hasNext()) {
                ButtonData data = buttonIterator.next();
                if (data.getPerson().equals(officerData.getPerson())) {
                    buttonIterator.remove();
                    removeRow(data.getRow());
                }
            }
        }
    }
}

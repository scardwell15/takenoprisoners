package takenoprisoners.ui;

import takenoprisoners.utils.SkillsUtil;
import takenoprisoners.utils.StringUtils;
import com.fs.starfarer.api.characters.*;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkillTooltip extends BaseTooltipCreator {
    private final TooltipMakerAPI tooltip;
    private final PersonAPI person;
    private final SkillSpecAPI skill;
    private final List<List<Object>> effects;
    private final float level;

    public SkillTooltip(TooltipMakerAPI tooltip, PersonAPI person, SkillSpecAPI skillSpec, float level) {
        this.tooltip = tooltip;
        this.person = person;
        this.skill = skillSpec;
        this.effects = SkillsUtil.getLevelEffects(skillSpec.getId());
        this.level = level;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 400f;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.setParaInsigniaLarge();
        tooltip.addPara(skill.getName(), getSkillTitleColor(skill), 0f);
        tooltip.setParaFontDefault();

        if (skill.getScopeStr() != null) {
            tooltip.addPara(skill.getScopeStr(), 0);
        }

        if (skill.getScopeStr2() != null) {
            tooltip.addPara(skill.getScopeStr2(), 0);
        }

        Set<Class> printed = new HashSet<>();
        UIComponentAPI prev = tooltip.getPrev();
        boolean eliteLabel = false;
        for (int i = 0; i < effects.size(); i++) {
            List<Object> effectGroup = effects.get(i);

            if (i > 0 && !eliteLabel) {
                eliteLabel = true;
                tooltip.addTitle(StringUtils.getString("PrisonerUI", "EliteSkill"), Misc.getStoryOptionColor()).getPosition().belowLeft(prev, 3);
                prev = tooltip.getPrev();
            }

            for (Object effect : effectGroup) {
                if (printed.contains(effect.getClass())) continue;
                printed.add(effect.getClass());

                if (effect instanceof CustomSkillDescription) {
                    CustomSkillDescription desc = ((CustomSkillDescription) effect);
                    if (desc.hasCustomDescription()) {
                        desc.createCustomDescription(person.getStats(), skill, tooltip, getTooltipWidth(null));

                        prev = tooltip.getPrev();
                        continue;
                    }
                }

                if (effect instanceof LevelBasedEffect) {
                    LevelBasedEffect levelEff = ((LevelBasedEffect) effect);
                    tooltip.addPara(levelEff.getEffectDescription(level), Misc.getHighlightColor(), 0).getPosition().belowLeft(prev, 1);
                } else if (effect instanceof DescriptionSkillEffect) {
                    DescriptionSkillEffect desc = ((DescriptionSkillEffect) effect);
                    LabelAPI label = tooltip.addPara(desc.getString(), desc.getTextColor(), 0);
                    label.setHighlight(desc.getHighlights());
                    label.setHighlightColors(desc.getHighlightColors());
                    label.getPosition().belowLeft(prev, 1);
                } else {
                    throw new RuntimeException("Unexpected effect class " + effect + " " + effect.getClass());
                }

                prev = tooltip.getPrev();
            }
        }
    }

    public static void addToTooltip(TooltipMakerAPI tooltip, PersonAPI person, SkillSpecAPI skillSpec, float level) {
        tooltip.addTooltipToPrevious(new SkillTooltip(tooltip, person, skillSpec, level), TooltipMakerAPI.TooltipLocation.BELOW);
    }

    private static Color getSkillTitleColor(SkillSpecAPI skill) {
        if (skill.getGoverningAptitudeId().equals(Skills.APT_COMBAT)) {
            return new Color(208, 124, 118);
        } else if (skill.getGoverningAptitudeId().equals(Skills.APT_INDUSTRY)) {
            return new Color(176, 160, 94);
        } else if (skill.getGoverningAptitudeId().equals(Skills.APT_LEADERSHIP)) {
            return new Color(89, 155, 80);
        } else if (skill.getGoverningAptitudeId().equals(Skills.APT_TECHNOLOGY)) {
            return new Color(115, 133, 208);
        } else {
            return skill.getGoverningAptitudeColor().brighter();
        }
    }
}

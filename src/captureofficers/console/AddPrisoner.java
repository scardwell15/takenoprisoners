package captureofficers.console;

import captureofficers.CaptureOfficers;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.util.Random;

public class AddPrisoner implements BaseCommand {
    @Override
    public CommandResult runCommand(String argsString, CommandContext context) {
        if ( context.isInCampaign() )
        {
            String[] args = argsString.split(" ");
            String factionId = args[0];
            FactionAPI faction = Global.getSector().getFaction(factionId);

            PersonAPI person = faction.createRandomPerson();
            person.getStats().setLevel(new Random().nextInt(8) + 1);

            WeightedRandomPicker<SkillSpecAPI> skillIdPicker = new WeightedRandomPicker<>();
            for (String skillId : Global.getSettings().getSkillIds()) {
                SkillSpecAPI skillSpec = Global.getSettings().getSkillSpec(skillId);
                if (skillSpec.isAptitudeEffect()) continue;
                if (!skillSpec.isCombatOfficerSkill()) continue;

                skillIdPicker.add(skillSpec);
            }

            for (int j = 0; j < person.getStats().getLevel(); j++) {
                SkillSpecAPI skillSpec = skillIdPicker.pick();
                String skillId = skillSpec.getId();
                if (person.getStats().getSkillLevel(skillId) > 0) {
                    j--;
                    continue;
                }
                person.getStats().increaseSkill(skillId);

                //make elite
                if (Math.random() > 0.5) {
                    person.getStats().increaseSkill(skillId);
                }
            }

            CaptureOfficers.addPerson(person);
            return CommandResult.SUCCESS;
        } else {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
    }
}

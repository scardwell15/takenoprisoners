package captureofficers.campaign.listeners;

import captureofficers.CaptureOfficers;
import captureofficers.utils.Settings;
import captureofficers.utils.Strings;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.EngagementResultForFleetAPI;
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin;
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.FleetMemberData;
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.Status;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j
public class CampaignListener extends BaseCampaignEventListener {

    public CampaignListener(boolean permaRegister) {
        super(permaRegister);
    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        EngagementResultForFleetAPI loserSide = result.getLoserResult();

        if (loserSide.isPlayer()) {
            loserSide = result.getWinnerResult();
        }

        if (loserSide.getFleet().getMemoryWithoutUpdate().getBoolean("$ziggurat")) {
            return;
        }

        if (loserSide.getAllEverDeployedCopy() == null) return;

        log.info("Capture - Engagement ended. Processing enemy fleet now.");

        PersonAPI fleetCommander = loserSide.getFleet().getCommander();
        if (canCapture(fleetCommander, true)) {
            log.info("Capture - Added enemy fleet commander as prisoner.");
            CaptureOfficers.addPerson(fleetCommander);
        }

        if (loserSide.getFleet().getMemoryWithoutUpdate().contains(Strings.ENEMY_FLEET_PRISONERS_MEMKEY)) {
            List<PersonAPI> persons = (List<PersonAPI>) loserSide.getFleet().getMemoryWithoutUpdate().get(Strings.ENEMY_FLEET_PRISONERS_MEMKEY);

            for (PersonAPI person : persons) {
                CaptureOfficers.addPerson(person, true);
            }

            loserSide.getFleet().getMemoryWithoutUpdate().unset(Strings.ENEMY_FLEET_PRISONERS_MEMKEY);
        }

        for (DeployedFleetMemberAPI deployedMember : loserSide.getAllEverDeployedCopy()) {
            FleetMemberAPI member = deployedMember.getMember();
            if (member == null) continue;

            PersonAPI captain = member.getCaptain();

            if (!captain.equals(fleetCommander)) {
                if (canCapture(captain, false)) {

                    float recoveryChance = Settings.getBaseCaptureChance();
                    if (loserSide.getDestroyed().contains(member)) {
                        recoveryChance *= 0.5f;
                    } else if (loserSide.getRetreated().contains(member)) {
                        recoveryChance *= 0f;
                    }

                    if (Math.random() <= recoveryChance) {
                        log.info("Capture - Added enemy officer as prisoner.");
                        CaptureOfficers.addPerson(captain);
                    }
                }
            }
        }
    }

    private boolean canCapture(PersonAPI person, boolean commander) {
        if (person != null) {
            if (person.isDefault()) {
                return false;
            }

            if (person.equals(Global.getSector().getPlayerPerson())) {
                return false;
            }

            if (person.hasTag(Strings.NO_CAPTURE_TAG)) {
                log.info("Capture - Person has blocking capture tag.%n");
                return false;
            }

            if (person.hasTag(Strings.FORCE_CAPTURE_TAG)) {
                log.info("Capture - Person has force capture tag.%n");
                return true;
            }

            if (commander) {
                if (!Settings.isCommanderCapturable(person.getFaction().getId())) {
                    return false;
                }
            } else if (!Settings.isFactionCapturable(person.getFaction().getId())) {
                return false;
            }

            if (Global.getSector().getImportantPeople().getPerson(person.getId()) != null) {
                log.info("Capture - Person was important.%n");
                return false;
            }

            if (person.isAICore()) {
                return false;
            }

            return true;
        }
        return false;
    }
}

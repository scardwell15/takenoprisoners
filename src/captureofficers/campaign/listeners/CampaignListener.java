package captureofficers.campaign.listeners;

import captureofficers.CaptureOfficers;
import captureofficers.config.FactionConfig;
import captureofficers.config.FactionConfigLoader;
import captureofficers.utils.Strings;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.EngagementResultForFleetAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import lombok.extern.log4j.Log4j;

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
            return;
        }

        if (loserSide.getFleet().getMemoryWithoutUpdate().getBoolean("$ziggurat")) {
            return;
        }

        if (loserSide.getAllEverDeployedCopy() == null) return;

        log.info("Capture - Engagement ended. Processing enemy fleet now.");

        FactionConfig config = FactionConfigLoader.getFactionConfig(loserSide.getFleet().getFaction().getId());

        PersonAPI fleetCommander = loserSide.getFleet().getCommander();
        if (config != null && config.getCommanderForcedCapture()) {
            if (canCapture(fleetCommander, config, true)) {
                log.info("Capture - Added enemy fleet commander as prisoner.");
                CaptureOfficers.addPerson(fleetCommander);
            }
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

            boolean isCommander = captain.equals(fleetCommander);
            if (config == null || (config.getCommanderForcedCapture() && !isCommander)) {
                if (canCapture(captain, config, isCommander)) {

                    float recoveryChance = 0.1f;
                    if (config != null) {
                        recoveryChance = config.getCaptureChance();
                    }
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

    private boolean canCapture(PersonAPI person, FactionConfig config, boolean isCommander) {
        if (person != null) {
            if (person.isDefault()) {
                return false;
            }

            if (person.equals(Global.getSector().getPlayerPerson())) {
                return false;
            }

            if (person.hasTag(Strings.NO_CAPTURE_TAG)) {
                log.info("Capture - Person has blocking capture tag.");
                return false;
            }

            if (person.hasTag(Strings.FORCE_CAPTURE_TAG)) {
                log.info("Capture - Person has force capture tag.");
                return true;
            }

            if (Global.getSector().getImportantPeople().getPerson(person.getId()) != null) {
                log.info("Capture - Person was important.");
                return false;
            }

            if (person.isAICore()) {
                return false;
            }

            return config != null && ((isCommander && config.getCapturesCommanders()) || config.getCapturesOfficers());
        }
        return false;
    }
}

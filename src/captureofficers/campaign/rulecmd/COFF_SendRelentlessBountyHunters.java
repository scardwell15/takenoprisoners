package captureofficers.campaign.rulecmd;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicCampaign;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.campaign.MagicFleetBuilder;

import java.util.List;
import java.util.Map;

public class COFF_SendRelentlessBountyHunters extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String factionId = params.get(0).getString(memoryMap);

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        CampaignFleetAPI hunter = new MagicFleetBuilder()
                .setFleetFaction(factionId)
                .setFleetType(FleetTypes.MERC_BOUNTY_HUNTER)
                .setMinFP(Global.getSector().getPlayerFleet().getFleetPoints()) //full FP
                .setAssignment(FleetAssignment.INTERCEPT)
                .setAssignmentTarget(Global.getSector().getPlayerFleet())
                .setIsImportant(true)
                .create();

        hunter.setContainingLocation(Global.getSector().getHyperspace());
        Vector2f hunterLoc = Misc.getPointAtRadius(playerFleet.getLocationInHyperspace(), 500f);
        hunter.setLocation(hunterLoc.x, hunterLoc.y);

        hunter.setFaction(Factions.INDEPENDENT, true);

        Misc.makeNoRepImpact(hunter, "bountyHunter");

        hunter.getAI().addAssignment(FleetAssignment.INTERCEPT, playerFleet, 1000000f, null);

        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, true);
        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE, true);
        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PURSUE_PLAYER, true);
        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);

        hunter.getMemoryWithoutUpdate().set("$ttli_bountyHunter", true);

        String hisOrHer = Math.random() > 0.5f ? "her" : "his";
        hunter.getMemoryWithoutUpdate().set("$ttli_hisOrHer", hisOrHer);

        hunter.addScript(new NeverendingChaseScript(hunter));

        AbilityPlugin eb = hunter.getAbility(Abilities.EMERGENCY_BURN);
        if (eb != null) eb.activate();

        return true;
    }

    public class NeverendingChaseScript implements EveryFrameScript {
        protected CampaignFleetAPI hunter;
        protected float elapsed = 0f;

        public NeverendingChaseScript(CampaignFleetAPI fleet) {
            this.hunter = fleet;
        }

        public void advance(float amount) {
            CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

            if (!hunter.isInCurrentLocation()) {
                elapsed += Global.getSector().getClock().convertToDays(amount);
                if (elapsed > 7f && hunter.getBattle() == null) {


                    Vector2f hunterLoc = Misc.getPointAtRadius(playerFleet.getLocationInHyperspace(), 500f);
                    hunter.setLocation(hunterLoc.x, hunterLoc.y);

                    hunter.getAI().addAssignment(FleetAssignment.ATTACK_LOCATION, playerFleet, 1000f, null);

                    AbilityPlugin eb = hunter.getAbility(Abilities.EMERGENCY_BURN);
                    if (eb != null) eb.activate();

                    elapsed = -1;
                }
            } else {
                if (hunter.getAI().getCurrentAssignment() == null
                    || hunter.getAI().getCurrentAssignment().getTarget() == null
                    || hunter.getAI().getCurrentAssignment().getTarget() != playerFleet) {

                    hunter.getAI().addAssignment(FleetAssignment.ATTACK_LOCATION, playerFleet, 1000f, null);

                    AbilityPlugin eb = hunter.getAbility(Abilities.EMERGENCY_BURN);
                    if (eb != null) eb.activate();
                }
                elapsed = 0f;
            }
        }

        public boolean isDone() {
            return elapsed < 0;
        }

        public boolean runWhilePaused() {
            return false;
        }
    }
}

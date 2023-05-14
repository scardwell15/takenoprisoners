package takenoprisoners.utils;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.ai.FleetAIFlags;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

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

                makeFleetPursuePlayer(hunter);

                AbilityPlugin eb = hunter.getAbility(Abilities.EMERGENCY_BURN);
                if (eb != null) eb.activate();
            }
        } else {
            if (hunter.getAI().getCurrentAssignment() == null
                    || hunter.getAI().getCurrentAssignment().getTarget() == null
                    || hunter.getAI().getCurrentAssignment().getTarget() != playerFleet) {

                makeFleetPursuePlayer(hunter);

                AbilityPlugin eb = hunter.getAbility(Abilities.EMERGENCY_BURN);
                if (eb != null) eb.activate();
            }
            elapsed = 0f;
        }
    }

    public boolean isDone() {
        return hunter == null || !hunter.isAlive() || hunter.isEmpty() || hunter.isDespawning();
    }

    public boolean runWhilePaused() {
        return false;
    }
    
    public static void makeFleetPursuePlayer(CampaignFleetAPI hunter) {
        hunter.getAI().clearAssignments();

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        Vector2f playerLoc = playerFleet.getLocation();

        hunter.getMemoryWithoutUpdate().set(FleetAIFlags.PLACE_TO_LOOK_FOR_TARGET, playerLoc);
        hunter.getMemoryWithoutUpdate().set(FleetAIFlags.TRAVEL_DEST, playerLoc);
        hunter.getMemoryWithoutUpdate().set(FleetAIFlags.MOVE_DEST, playerLoc);

        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, true);
        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE, true);
        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PURSUE_PLAYER, true);
        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_STICK_WITH_PLAYER_IF_ALREADY_TARGET, true);

        hunter.getAI().addAssignment(FleetAssignment.INTERCEPT, playerFleet, 10000f, null);
    }
}

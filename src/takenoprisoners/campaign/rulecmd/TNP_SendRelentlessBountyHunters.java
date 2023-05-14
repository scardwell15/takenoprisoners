package takenoprisoners.campaign.rulecmd;

import takenoprisoners.utils.NeverendingChaseScript;
import takenoprisoners.utils.Strings;
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
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.campaign.MagicFleetBuilder;

import java.util.List;
import java.util.Map;

public class TNP_SendRelentlessBountyHunters extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String factionId = params.get(0).getString(memoryMap);

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        CampaignFleetAPI hunter = new MagicFleetBuilder()
                .setFleetFaction(factionId)
                .setFleetType(FleetTypes.MERC_BOUNTY_HUNTER)
                .setMinFP(playerFleet.getFleetPoints()) //full FP
                .setAssignment(FleetAssignment.INTERCEPT)
                .setAssignmentTarget(playerFleet)
                .setIsImportant(true)
                .create();

        hunter.setContainingLocation(Global.getSector().getHyperspace());
        Vector2f hunterLoc = Misc.getPointAtRadius(playerFleet.getLocationInHyperspace(), 500f);
        hunter.setLocation(hunterLoc.x, hunterLoc.y);

        hunter.setFaction(Factions.INDEPENDENT, true);

        Misc.makeNoRepImpact(hunter, "bountyHunter");

        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        hunter.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);

        NeverendingChaseScript.makeFleetPursuePlayer(hunter);

        hunter.getMemoryWithoutUpdate().set("$coff_hunter", true);
        hunter.getMemoryWithoutUpdate().set("$coff_hunttrigger", dialog.getInteractionTarget().getActivePerson().getMemoryWithoutUpdate().getString(Strings.DIALOG_TRIGGER_MEMKEY));

        hunter.getMemoryWithoutUpdate().set("$ttli_bountyHunter", true);

        String hisOrHer = Math.random() > 0.5f ? "her" : "his";
        hunter.getMemoryWithoutUpdate().set("$ttli_hisOrHer", hisOrHer);

        hunter.addScript(new NeverendingChaseScript(hunter));

        AbilityPlugin eb = hunter.getAbility(Abilities.EMERGENCY_BURN);
        if (eb != null) eb.activate();

        return true;
    }
}

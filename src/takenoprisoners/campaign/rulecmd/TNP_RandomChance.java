package takenoprisoners.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class TNP_RandomChance extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        try {
            float chance = params.get(0).getFloat(memoryMap);
            return new Random().nextInt(100) + 1 < chance;
        } catch (Exception ex) {
            //do nothing (there was either no float, or an invalid value.)
        }

        return false;
    }
}

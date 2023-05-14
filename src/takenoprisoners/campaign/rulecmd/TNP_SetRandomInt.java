package takenoprisoners.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class TNP_SetRandomInt extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String memKey = params.get(0).getString(memoryMap);
        int min = params.get(1).getInt(memoryMap);
        int max = min;
        try {
            max = params.get(2).getInt(memoryMap);
        } catch (Exception ex) {
            //param 1 treated as max instead
        }

        String target = MemKeys.LOCAL;
        if (memKey.contains(".")) {
            target = memKey.substring(0, memKey.indexOf("."));

            if (memoryMap.containsKey(target)) {
                memKey = memKey.substring(memKey.indexOf(".") + 1);
            } else {
                target = MemKeys.LOCAL;
            }
        }

        if (!memKey.startsWith("$")) {
            memKey = "$" + memKey;
        }

        int random;
        if (min == max) {
            random = new Random().nextInt(min);
        } else {
            random = min + new Random().nextInt(max - min);
        }

        memoryMap.get(target).set(memKey, random);
        return true;
    }
}

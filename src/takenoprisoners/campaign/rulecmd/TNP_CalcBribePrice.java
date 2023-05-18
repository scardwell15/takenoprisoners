package takenoprisoners.campaign.rulecmd;

import takenoprisoners.utils.Strings;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class TNP_CalcBribePrice extends TNPCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        PersonAPI person = getActivePerson(dialog);

        float creditCost = getCreditCost(person);

        try {
            float mult = params.get(0).getFloat(memoryMap);
            creditCost *= mult;
        } catch (Exception ex) {
            //do nothing (there was either no float, or an invalid value.)
        }

        person.getMemoryWithoutUpdate().set(Strings.BRIBE_PRICE_MEMKEY, creditCost);
        person.getMemoryWithoutUpdate().set(Strings.BRIBE_PRICE_STRING_MEMKEY, Misc.getDGSCredits(creditCost));

        return true;
    }

    //vengeful faction costs 5x more than cooperative
    private float getCreditCost(PersonAPI person) {
        float rel = person.getFaction().getRelToPlayer().getRel();
        float relMult = 1.5f - rel;
        return person.getStats().getLevel() * 2500f * relMult;
    }
}

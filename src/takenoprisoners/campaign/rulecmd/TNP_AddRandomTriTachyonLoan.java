package takenoprisoners.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.TriTachLoanBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.TriTachLoanIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class TNP_AddRandomTriTachyonLoan extends TNPCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MarketAPI targetMarket = TNP_GetRandomCloseMarketForFaction.get(Factions.TRITACHYON);

        TriTachLoanEvent event = new TriTachLoanEvent();
        event.publicRegen(targetMarket);
        event.setLoanAmount(200000 + Misc.random.nextInt(6) * 10000);
        event.setRepaymentAmount((int) (event.getLoanAmount() * (250 + Misc.random.nextInt(250)) / 100f));
        event.setRepaymentDays(400 - Misc.random.nextInt(50));

        TriTachLoanIntel intel = new TriTachLoanIntel(event, targetMarket);
        Global.getSector().getIntelManager().addIntel(intel, false, dialog.getTextPanel());

        getActivePerson(dialog).getMemoryWithoutUpdate().set("$coff_loanIntel", intel);
        return true;
    }

    private static class TriTachLoanEvent extends TriTachLoanBarEvent {
        public void publicRegen(MarketAPI market) {
            //this method creates a bunch of stuff that the intel needs, but it is protected.
            regen(market);
        }
    }
}

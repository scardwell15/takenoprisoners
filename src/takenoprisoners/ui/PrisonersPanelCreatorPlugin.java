package takenoprisoners.ui;

import takenoprisoners.utils.Strings;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static takenoprisoners.ui.ViewPrisonersUI.PRISONER_ROW_SIZE;

public class PrisonersPanelCreatorPlugin implements InteractionDialogPlugin {
    private final List<PersonAPI> persons;
    private PrisonersDialogDelegate delegate;

    private InteractionDialogAPI dialog;
    private Map<String, MemoryAPI> memoryMap = null;
    private MemoryAPI memory;

    public PrisonersPanelCreatorPlugin(List<PersonAPI> persons) {
        this.persons = persons;
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;

        updateMemory();

        for (PersonAPI person : this.persons) {
            if (!person.getMemoryWithoutUpdate().contains("$coff_dialogtrigger")) {
                dialog.getInteractionTarget().setActivePerson(person);
                updateMemory();
                FireBest.fire(null, dialog, memoryMap, "PickPrisonerDialog");

                if (Global.getSector().getImportantPeople().containsPerson(person) && !person.getMemoryWithoutUpdate().contains(Strings.FORCE_CAPTURE_TAG)) {
                    person.getMemoryWithoutUpdate().set(Strings.BRIBE_PRICE_MEMKEY, String.valueOf(person.getMemoryWithoutUpdate().get(Strings.BRIBE_PRICE_MEMKEY)));
                }
            }
        }

        float screenWidth = Global.getSettings().getScreenWidth();
        float screenHeight = Global.getSettings().getScreenHeight();

        float allRowsHeight = (PRISONER_ROW_SIZE + 10) * persons.size() + 3;

        float panelHeight = Math.min(allRowsHeight + 20 + 16, screenHeight * 0.65f);
        float panelWidth = screenWidth * 0.65f;

        delegate = new PrisonersDialogDelegate(dialog, persons, panelWidth, panelHeight, allRowsHeight);
        dialog.showCustomDialog(panelWidth, panelHeight, delegate);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {

    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {

    }

    @Override
    public void advance(float amount) {
    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return memoryMap;
    }

    public void updateMemory() {
        if (memoryMap == null) {
            memoryMap = new HashMap<>();
        } else {
            memoryMap.clear();
        }

        memory = dialog.getInteractionTarget().getMemory();

        memoryMap.put(MemKeys.LOCAL, memory);
        if (dialog.getInteractionTarget().getFaction() != null) {
            memoryMap.put(MemKeys.FACTION, dialog.getInteractionTarget().getFaction().getMemory());
        } else {
            memoryMap.put(MemKeys.FACTION, Global.getFactory().createMemory());
        }
        memoryMap.put(MemKeys.GLOBAL, Global.getSector().getMemory());
        memoryMap.put(MemKeys.PLAYER, Global.getSector().getCharacterData().getMemory());

        if (dialog.getInteractionTarget().getMarket() != null) {
            memoryMap.put(MemKeys.MARKET, dialog.getInteractionTarget().getMarket().getMemory());
        }

        if (memory.contains(MemFlags.MEMORY_KEY_SOURCE_MARKET)) {
            String marketId = memory.getString(MemFlags.MEMORY_KEY_SOURCE_MARKET);
            MarketAPI market = Global.getSector().getEconomy().getMarket(marketId);
            if (market != null) {
                memoryMap.put(MemKeys.SOURCE_MARKET, market.getMemory());
            }
        }

        updatePersonMemory();
    }

    private void updatePersonMemory() {
        PersonAPI person = dialog.getInteractionTarget().getActivePerson();
//		if (person != null) {
//			memoryMap.put(MemKeys.PERSON, person.getMemory());
//		} else {
//			memoryMap.remove(MemKeys.PERSON);
//		}
        if (person != null) {
            memory = person.getMemory();
            memoryMap.put(MemKeys.LOCAL, memory);
            memoryMap.put(MemKeys.PERSON_FACTION, person.getFaction().getMemory());
            memoryMap.put(MemKeys.ENTITY, dialog.getInteractionTarget().getMemory());
        } else {
            memory = dialog.getInteractionTarget().getMemory();
            memoryMap.put(MemKeys.LOCAL, memory);
            memoryMap.remove(MemKeys.ENTITY);
            memoryMap.remove(MemKeys.PERSON_FACTION);

        }
    }
}

package takenoprisoners;

import takenoprisoners.campaign.VengeanceFleetHandler;
import takenoprisoners.campaign.listeners.CampaignEventListener;
import takenoprisoners.utils.Settings;
import takenoprisoners.utils.Strings;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TakeNoPrisonersModPlugin extends BaseModPlugin {
    private static final Logger log = Logger.getLogger(TakeNoPrisonersModPlugin.class);
    private CampaignEventListener listener = null;

    @Override
    public void onNewGameAfterEconomyLoad() {
        registerListener();
        checkAbility();
    }

    @Override
    public void beforeGameSave() {
        removeListener();
    }

    @Override
    public void afterGameSave() {
        registerListener();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Settings.reloadSettings();
        registerListener();
        VengeanceFleetHandler.INSTANCE.loadVengeancePoints();

        checkAbility();
    }

    public void checkAbility() {
        if (!Global.getSector().getCharacterData().getMemoryWithoutUpdate().contains("$ability:viewprisoners")) {
            Global.getSector().getCharacterData().getMemoryWithoutUpdate().set("$ability:viewprisoners", true, 0);
        }

        if (Global.getSector().getPlayerFleet() == null) return;

        if (!Global.getSector().getCharacterData().getAbilities().contains("viewprisoners")) {
            Global.getSector().getCharacterData().addAbility("viewprisoners");
        }

        if (!Global.getSector().getPlayerFleet().getAbilities().containsKey("viewprisoners")) {
            Global.getSector().getPlayerFleet().addAbility("viewprisoners");
        }
    }

    private void registerListener() {
        if (listener == null || !Global.getSector().getListenerManager().hasListener(CampaignEventListener.class)) {
            listener = new CampaignEventListener(false);
            Global.getSector().addListener(listener);
        }
    }

    private void removeListener() {
        if (listener != null) {
            Global.getSector().removeListener(listener);
            listener = null;
        }
    }

    public static String getPersistentDataId() {
        return Strings.CAPTURED_PERSONS_PERSISTENT_KEY;
    }

    public static List<PersonAPI> getPersons() {
        if (!Global.getSector().getPersistentData().containsKey(getPersistentDataId())) {
            Global.getSector().getPersistentData().put(getPersistentDataId(), new ArrayList<>());
        }

        List<PersonAPI> persons = (List<PersonAPI>) Global.getSector().getPersistentData().get(getPersistentDataId());

        boolean killList = false;
        for (PersonAPI person : persons) {
            if (person.getFleet() != null) {
                log.info(String.format("%s still had a fleet.", person.getNameString()));
                killList = true;
                person.setFleet(null);
            }

            if (person.getStats() != null
                    && person.getStats().getFleet() != null) {
                log.info(String.format("%s still had a stats fleet.", person.getNameString()));
                killList = true;
                person.getStats().setFleet(null);
            }
        }

        if (killList) {
            Global.getSector().getPersistentData().remove(getPersistentDataId());

            List<PersonAPI> newPersons = new ArrayList<>();
            newPersons.addAll(persons);
            Global.getSector().getPersistentData().put(getPersistentDataId(), newPersons);

            persons = newPersons;
        }

        return persons;
    }

    public static void addPerson(PersonAPI person, boolean forceCapture) {
        List<PersonAPI> persons = getPersons();

        if (!persons.contains(person)) {
            if (forceCapture || getPersons().size() < Settings.getMaxPrisoners()) {
                person.setFleet(null);
                persons.add(person);

                if (Global.getSector().getImportantPeople().containsPerson(person) && !forceCapture) {
                    person.setName(null);
                }
            }
        }
    }

    public static void addPerson(PersonAPI person) {
        addPerson(person, person.hasTag(Strings.FORCE_CAPTURE_TAG));
    }

    public static void removePerson(PersonAPI person) {
        List<PersonAPI> persons = getPersons();
        Global.getSector().getPersistentData().remove(getPersistentDataId());

        persons.remove(person);

        Global.getSector().getPersistentData().put(getPersistentDataId(), persons);
    }

    public static void generateTestOfficers() {
        for (int i = 0; i < new Random().nextInt(5) + 3; i++) {
            FactionAPI faction = Global.getSector().getAllFactions().get(new Random().nextInt(Global.getSector().getAllFactions().size()));
            while (!faction.isShowInIntelTab()) {
                faction = Global.getSector().getAllFactions().get(new Random().nextInt(Global.getSector().getAllFactions().size()));
            }

            PersonAPI person = faction.createRandomPerson();
            person.getStats().setLevel(new Random().nextInt(8) + 1);

            WeightedRandomPicker<SkillSpecAPI> skillIdPicker = new WeightedRandomPicker<>();
            for (String skillId : Global.getSettings().getSkillIds()) {
                SkillSpecAPI skillSpec = Global.getSettings().getSkillSpec(skillId);
                if (skillSpec.isAptitudeEffect()) continue;
                if (!skillSpec.isCombatOfficerSkill()) continue;

                skillIdPicker.add(skillSpec);
            }

            for (int j = 0; j < person.getStats().getLevel(); j++) {
                SkillSpecAPI skillSpec = skillIdPicker.pick();
                String skillId = skillSpec.getId();
                if (person.getStats().getSkillLevel(skillId) > 0) {
                    j--;
                    continue;
                }
                person.getStats().increaseSkill(skillId);

                //make elite
                if (Math.random() > 0.5) {
                    person.getStats().increaseSkill(skillId);
                }
            }

            addPerson(person);
        }
    }
}

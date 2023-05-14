package takenoprisoners.utils;

import takenoprisoners.ui.PrisonersDialogDelegate;
import com.fs.starfarer.api.characters.PersonAPI;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class AllowedActions {
    public static Set<String> get(PersonAPI person) {
        Set<String> allowedActionsList = new LinkedHashSet<>();

        if (person.getMemoryWithoutUpdate().contains(Strings.ALLOWED_ACTIONS_MEMKEY)) {
            Object acts = person.getMemoryWithoutUpdate().get(Strings.ALLOWED_ACTIONS_MEMKEY);

            if (acts != null) {
                if (acts instanceof String) {
                    String allowedActionsString = acts.toString();
                    if (allowedActionsString.contains(",")) {
                        allowedActionsList.addAll(Arrays.asList(allowedActionsString.split(",")));
                    } else {
                        allowedActionsList.add(allowedActionsString);
                    }
                } else if (acts instanceof Set) {
                    allowedActionsList = (Set<String>) acts;
                }
            }
        }

        return allowedActionsList;
    }

    public static void add(PersonAPI person, PrisonersDialogDelegate.ActionDefinition def) {
        add(person, def.getId());
    }

    public static void add(PersonAPI person, String... defNames) {
        for (String defName : defNames) {
            add(person, defName);
        }
    }

    public static void add(PersonAPI person, String defName) {
        defName = defName.toLowerCase();

        Set<String> allowedActions = get(person);
        allowedActions.add(defName);

        StringBuilder sb = new StringBuilder();
        boolean appendComma = false;
        for (String act : allowedActions) {
            if (!appendComma) {
                appendComma = true;
            } else {
                sb.append(",");
            }
            sb.append(act);
        }
        String allowedActionsString = sb.toString();

        person.getMemoryWithoutUpdate().set(Strings.ALLOWED_ACTIONS_MEMKEY, allowedActionsString);
    }

    public static boolean contains(PersonAPI person, String defName, boolean mustContain) {
        if (person.getMemoryWithoutUpdate().contains(Strings.ALLOWED_ACTIONS_MEMKEY)) {
            Set<String> allowedActions = get(person);
            if (allowedActions.contains(defName.toLowerCase())) {
                return true;
            }
            return false;
        }

        return !mustContain;
    }

    public static boolean contains(PersonAPI person, PrisonersDialogDelegate.ActionDefinition def, boolean mustContain) {
        return contains(person, def.getId(), mustContain);
    }

    public static boolean contains(PersonAPI person, String defName) {
        return contains(person, defName, false);
    }

    public static boolean contains(PersonAPI person, PrisonersDialogDelegate.ActionDefinition def) {
        return contains(person, def.getId(), false);
    }
}

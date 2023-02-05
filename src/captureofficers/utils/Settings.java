package captureofficers.utils;

import data.scripts.util.MagicSettings;

import java.util.*;

public class Settings {
    private static final Map<String, Capturability> factionCapturability = new HashMap<>();
    private static final Set<String> factionRansomBlacklist = new HashSet<>();
    private static int maxPrisoners = -1;
    private static float captureChance = 0.5f;

    public static boolean isFactionCapturable(String factionId) {
        factionId = factionId.toLowerCase();
        return factionCapturability.containsKey(factionId);
    }

    public static boolean isCommanderCapturable(String factionId) {
        return factionCapturability.containsKey(factionId) && factionCapturability.get(factionId).equals(Capturability.ALL);
    }

    public static boolean doesFactionRansom(String factionId) {
        return !factionRansomBlacklist.contains(factionId);
    }

    public static int getMaxPrisoners() {
        return maxPrisoners;
    }

    public static float getBaseCaptureChance() {
        return captureChance;
    }

    public static void reloadCapturability() {
        factionCapturability.clear();

        Map<String, String> jsonMap = MagicSettings.getStringMap("TakeNoPrisoners", "factionCapturability");
        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            try {
                factionCapturability.put(entry.getKey(), Capturability.valueOf(entry.getValue()));
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException(String.format("TakeNoPrisoners configuration is incorrect. The faction %s has an incorrect capturability rating.", entry.getKey()));
            }
        }
    }

    public static void reloadRansomBlacklist() {
        factionRansomBlacklist.clear();

        List<String> jsonArray = MagicSettings.getList("TakeNoPrisoners", "factionRansomBlacklist");
        factionRansomBlacklist.addAll(jsonArray);
    }

    public static void reloadMaxPrisoners() {
        maxPrisoners = MagicSettings.getInteger("TakeNoPrisoners", "maxPrisoners");
    }

    public static void reloadCaptureChance() {
        captureChance = MagicSettings.getFloat("TakeNoPrisoners", "baseCaptureChance");
    }

    public static void reloadSettings() {
        Settings.reloadMaxPrisoners();
        Settings.reloadCapturability();
        Settings.reloadRansomBlacklist();
        Settings.reloadCaptureChance();
    }

    public enum Capturability {
        NONE,
        OFFICERS_ONLY,
        ALL
    }
}

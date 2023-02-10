package captureofficers.utils;

import captureofficers.config.FactionConfigLoader;
import data.scripts.util.MagicSettings;

public class Settings {
    private static int maxPrisoners = -1;

    public static int getMaxPrisoners() {
        return maxPrisoners;
    }


    public static void reloadMaxPrisoners() {
        maxPrisoners = MagicSettings.getInteger("TakeNoPrisoners", "maxPrisoners");
    }

    public static void reloadSettings() {
        Settings.reloadMaxPrisoners();
        FactionConfigLoader.load();
    }
}

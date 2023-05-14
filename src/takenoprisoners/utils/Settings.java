package takenoprisoners.utils;

import takenoprisoners.config.FactionConfigurationLoader;
import org.magiclib.util.MagicSettings;

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
        FactionConfigurationLoader.load();
    }
}

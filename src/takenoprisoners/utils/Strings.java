package takenoprisoners.utils;

public class Strings {
    /**
     * List<PersonAPI>. The list of persons captured by the player fleet.
     */
    public static String CAPTURED_PERSONS_PERSISTENT_KEY = "CaptureOfficers_OfficerList";
    public static String CAPTURED_PERSONS_FIXED_LEAK = "CaptureOfficers_FixedLeak";

    /**
     * Set<String>. a list of steps to execute after dialog. see StepExecutor.Type for list of valid actions.
     * If none are set then no action is executed, this memkey isn't required for anything.
     * As of 1.0.0 the valid actions are "hire" and "remove".
     * To set this in rules.csv, use commas (e.g. $coff_postdialogsteps = "hire,remove"
     */
    public static String POST_DIALOG_STEPS_MEMKEY = "$coff_postdialogsteps";

    /**
     * Set<String>. a list of allowed actions. if none are specified then all generated actions are allowed, usually
     * based on faction.
     * As of 1.0.0 the valid actions are "execute", "hire", "release", and "talk".
     * To set this in rules.csv, use commas (e.g. $coff_allowedactions = "hire,release,execute")
     */
    public static String ALLOWED_ACTIONS_MEMKEY = "$coff_allowedactions";

    /**
     * List<PersonAPI>. a list of PersonAPIs held by the enemy fleet.
     * they will always be captured.
     */
    public static String ENEMY_FLEET_PRISONERS_MEMKEY = "$coff_prisoners";

    /**
     * boolean. if set, the person is a prisoner.
     */
    public static String IS_PRISONER_MEMKEY = "$coff_isprisoner";

    /**
     * set as tag on PersonAPI. forces the person to be captured.
     */
    public static String FORCE_CAPTURE_TAG = "coff_forcecapture";

    /**
     * set as tag on PersonAPI. forces the person to not be captured.
     * this takes precedence over force tag.
     */
    public static String NO_CAPTURE_TAG = "coff_nocapture";

    public static String BRIBE_PRICE_STRING_MEMKEY = "$coff_bribeprice_text";
    public static String BRIBE_PRICE_MEMKEY = "$coff_bribeprice";
    public static String RANSOM_PRICE_STRING_MEMKEY = "$coff_ransomprice_text";
    public static String RANSOM_PRICE_MEMKEY = "$coff_ransomprice";


    public static String MARKET_MEMKEY = "$coff_market_id";
    public static String MARKET_NAME_MEMKEY = "$coff_market_name";
    public static String MARKET_DIST_MEMKEY = "$coff_market_dist";
    public static String MARKET_COLOR_MEMKEY = "$coff_market_color";

    public static String DIALOG_TRIGGER_MEMKEY = "$coff_dialogtrigger";

    //-2 timid, -1 cautious, 0 steady, 1 aggressive, 2 reckless
    public static String PERSONALITY_DEGREE_MEMKEY = "$coff_personalityDegree";

    public static String RANSOMABLE = "$coff_ransomable";
}

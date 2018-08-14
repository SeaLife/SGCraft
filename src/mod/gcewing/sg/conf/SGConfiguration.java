package gcewing.sg.conf;

import gcewing.sg.BaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SGConfiguration {

    private static Logger log = LoggerFactory.getLogger(SGConfiguration.class);

    public static boolean DEBUG_STATE            = false;
    public static boolean DEBUG_ENERGY_USE       = false;
    public static boolean DEBUG_CONNECT          = false;
    public static boolean DEBUG_TRANSIENT_DAMAGE = false;
    public static boolean DEBUG_TELEPORT         = false;
    public static boolean DEBUG_ZPM              = false;
    public static boolean DEBUG_MERGE            = false;
    public static boolean DEBUG_LAVA             = false;
    public static boolean DEBUG_RANDOM           = false;
    public static int     DEBUG_LEVEL            = 0;
    public static boolean DEBUG_STRUCTURE        = false;

    public static double  maxEnergyBuffer          = 1000;
    public static double  energyPerFuelItem        = 96000;
    public static double  distanceFactorMultiplier = 1.0;
    public static double  interDimensionMultiplier = 4.0;
    public static int     gateOpeningsPerFuelItem  = 24;
    public static int     minutesOpenPerFuelItem   = 80;
    public static int     secondsToStayOpen        = 5 * 60;
    public static boolean oneWayTravel             = true;
    public static boolean closeFromEitherEnd       = true;
    public static int     chunkLoadingRange        = 1;
    public static boolean logStargateEvents        = false;
    public static boolean preserveInventory        = false;
    public static float   soundVolume              = 1F;
    public static boolean variableChevronPositions = true;
    public static boolean zpmUsage                 = true;

    public static boolean augmentStructures           = true;
    public static boolean spawnTokra                  = true;
    public static int     structureAugmentationChance = 25;
    public static int     zpmChestChance              = 15;
    public static int     chevronUpgradeChance        = 25;

    public static int genUnderLavaOdds  = 1;
    public static int maxNodesUnderLava = 16;
    public static int genIsolatedOdds   = 1;
    public static int maxIsolatedNodes  = 4;

    public static int     explosionRadius = 10;
    public static boolean fieryExplosion  = true;
    public static boolean smokyExplosion  = true;

    public static double  energyToOpen;
    public static double  energyUsePerTick;
    public static int     ticksToStayOpen;
    public static boolean transparency = true;

    public static boolean ccAllowImmediate = false;

    private static final String CONFIG_CAT_STARGATE = "stargate";
    private static final String CONFIG_CAT_OPTIONS  = "options";
    private static final String CONFIG_CAT_IRIS     = "iris";
    private static final String CONFIG_CAT_DEBUG    = "debug";
    private static final String CONFIG_CAT_NAQUADAH = "naquadah";


    public static void configure(BaseConfiguration cfg) {
        log.info("Loading Configuration for SGCraft ...");

        energyPerFuelItem = cfg.getDouble(CONFIG_CAT_STARGATE, "energyPerFuelItem", energyPerFuelItem);
        gateOpeningsPerFuelItem = cfg.getInteger(CONFIG_CAT_STARGATE, "gateOpeningsPerFuelItem", gateOpeningsPerFuelItem);
        minutesOpenPerFuelItem = cfg.getInteger(CONFIG_CAT_STARGATE, "minutesOpenPerFuelItem", minutesOpenPerFuelItem);
        secondsToStayOpen = cfg.getInteger(CONFIG_CAT_STARGATE, "secondsToStayOpen", secondsToStayOpen);
        oneWayTravel = cfg.getBoolean(CONFIG_CAT_STARGATE, "oneWayTravel", oneWayTravel);
        closeFromEitherEnd = cfg.getBoolean(CONFIG_CAT_STARGATE, "closeFromEitherEnd", closeFromEitherEnd);
        maxEnergyBuffer = cfg.getDouble(CONFIG_CAT_STARGATE, "maxEnergyBuffer", maxEnergyBuffer);
        distanceFactorMultiplier = cfg.getDouble(CONFIG_CAT_STARGATE, "distanceFactorMultiplier", distanceFactorMultiplier);
        interDimensionMultiplier = cfg.getDouble(CONFIG_CAT_STARGATE, "interDimensionMultiplier", interDimensionMultiplier);
        transparency = cfg.getBoolean(CONFIG_CAT_STARGATE, "transparency", transparency);
        soundVolume = (float) cfg.getDouble(CONFIG_CAT_STARGATE, "soundVolume", soundVolume);
        variableChevronPositions = cfg.getBoolean(CONFIG_CAT_STARGATE, "variableChevronPositions", variableChevronPositions);
        zpmUsage = cfg.getBoolean(CONFIG_CAT_STARGATE, "zpmUsage", zpmUsage);
        logStargateEvents = cfg.getBoolean(CONFIG_CAT_OPTIONS, "logStargateEvents", logStargateEvents);
        chunkLoadingRange = cfg.getInteger(CONFIG_CAT_OPTIONS, "chunkLoadingRange", chunkLoadingRange);
        preserveInventory = cfg.getBoolean(CONFIG_CAT_IRIS, "preserveInventory", preserveInventory);

        explosionRadius = cfg.getInteger(CONFIG_CAT_STARGATE, "explosionRadius", explosionRadius);
        fieryExplosion = cfg.getBoolean(CONFIG_CAT_STARGATE, "explosionFlame", fieryExplosion);
        smokyExplosion = cfg.getBoolean(CONFIG_CAT_STARGATE, "explosionSmoke", smokyExplosion);

        genUnderLavaOdds = cfg.getInteger(CONFIG_CAT_NAQUADAH, "genUnderLavaOdds", genUnderLavaOdds);
        maxNodesUnderLava = cfg.getInteger(CONFIG_CAT_NAQUADAH, "maxNodesUnderLava", maxNodesUnderLava);
        genIsolatedOdds = cfg.getInteger(CONFIG_CAT_NAQUADAH, "genIsolatedOdds", genIsolatedOdds);
        maxIsolatedNodes = cfg.getInteger(CONFIG_CAT_NAQUADAH, "maxIsolatedNodes", maxIsolatedNodes);
        DEBUG_LAVA = cfg.getBoolean(CONFIG_CAT_NAQUADAH, "DEBUG_LAVA", DEBUG_LAVA);
        DEBUG_RANDOM = cfg.getBoolean(CONFIG_CAT_NAQUADAH, "DEBUG_RANDOM", DEBUG_RANDOM);
        DEBUG_LEVEL = cfg.getInteger(CONFIG_CAT_NAQUADAH, "DEBUG_LEVEL", DEBUG_LEVEL);

        augmentStructures = cfg.getBoolean(CONFIG_CAT_OPTIONS, "augmentStructures", augmentStructures);
        structureAugmentationChance = cfg.getInteger(CONFIG_CAT_OPTIONS, "structureAugmentationChance", structureAugmentationChance);
        zpmChestChance = cfg.getInteger(CONFIG_CAT_OPTIONS, "zpmChestChance", zpmChestChance);
        chevronUpgradeChance = cfg.getInteger(CONFIG_CAT_OPTIONS, "chevronUpgradeChance", chevronUpgradeChance);
        DEBUG_STRUCTURE = cfg.getBoolean(CONFIG_CAT_OPTIONS, "DEBUG_STRUCTURE", DEBUG_STRUCTURE);
        spawnTokra = cfg.getBoolean(CONFIG_CAT_OPTIONS, "spawnTokraWithPyramidStargate", spawnTokra);

        DEBUG_CONNECT = cfg.getBoolean(CONFIG_CAT_DEBUG, "connect", DEBUG_CONNECT);
        DEBUG_ENERGY_USE = cfg.getBoolean(CONFIG_CAT_DEBUG, "energyUse", DEBUG_ENERGY_USE);
        DEBUG_STATE = cfg.getBoolean(CONFIG_CAT_DEBUG, "state", DEBUG_STATE);
        DEBUG_TELEPORT = cfg.getBoolean(CONFIG_CAT_DEBUG, "teleport", DEBUG_TELEPORT);
        DEBUG_MERGE = cfg.getBoolean(CONFIG_CAT_DEBUG, "merge", DEBUG_MERGE);

        // enable debug
        DEBUG_ENERGY_USE = true;
        DEBUG_CONNECT = true;
        DEBUG_STRUCTURE = true;
        DEBUG_MERGE = true;
        DEBUG_TELEPORT = true;


        ticksToStayOpen = 20 * secondsToStayOpen;
        energyToOpen = energyPerFuelItem / gateOpeningsPerFuelItem;
        energyUsePerTick = energyPerFuelItem / (minutesOpenPerFuelItem * 60 * 20);

        if (DEBUG_ENERGY_USE) {
            log.info("[DEBUG] energyPerFuelItem = {}", energyPerFuelItem);
            log.info("[DEBUG] energyToOpen      = {}", energyToOpen);
            log.info("[DEBUG] energyUsePerTick  = {}", energyUsePerTick);
        }
    }
}

package survivalblock.thiocyanate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import survivalblock.thiocyanate.cyanide.platform.XPlatform;

import java.util.Locale;

public abstract class Thiocyanate implements XPlatform {
	public static final String MOD_ID = "thiocyanate";
    public static final String FORMATTED_MOD_ID;
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static Thiocyanate instance;

    public static IllegalStateException createException(String originalMessage) {
        return new IllegalStateException("[" + FORMATTED_MOD_ID + "] "  + originalMessage);
    }

    public static Thiocyanate getInstance() {
        return instance;
    }

    public static void setInstance(Thiocyanate platform) {
        if (instance != null) {
            Thiocyanate.LOGGER.warn("Setting a new instance ({}) of Thiocyanate when an existing one ({}) is present!", platform, instance);
        }
        instance = platform;
    }

    static {
        String modId = MOD_ID.replace("_", " ");
        FORMATTED_MOD_ID = modId.substring(0, 1).toUpperCase(Locale.ROOT) + modId.substring(1);
    }
}
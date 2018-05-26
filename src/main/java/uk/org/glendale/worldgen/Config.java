/**
 * Config.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.worldgen.exceptions.InvalidConfigurationException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Provides access to application wide configuration that isn't stored in the
 * database. This is generally low level options that aren't expected to change.
 * This is a singleton class.
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static Config configuration;

    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;

    private int    httpPort;

    private int mapDensityMin;
    private int mapDensityMax;

    private boolean useRealStarColours;
    private int planetMapFaceSize;
    private int planetResolution;

    private int simFrequency;
    private boolean skipDowntime;

    private String getString(ResourceBundle bundle, String key) {
        try {
            String value = bundle.getString(key);
            if (value == null || value.trim().length() == 0) {
                throw new InvalidConfigurationException(key, value);
            }
            return value;
        } catch (MissingResourceException e) {
            throw new InvalidConfigurationException(key);
        }
    }

    private String getString(ResourceBundle bundle, String key, String value) {
        try {
            return getString(bundle, key);
        } catch (InvalidConfigurationException e) {
            return value;
        }
    }

    private int getInt(ResourceBundle bundle, String key) {
        String str = getString(bundle, key);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException(key, str);
        }
    }

    private int getInt(ResourceBundle bundle, String key, int value) {
        String str = null;
        try {
            str = getString(bundle, key);
            return Integer.parseInt(str);
        } catch (InvalidConfigurationException e) {
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException(key, str);
        }
    }

    private boolean getBoolean(ResourceBundle bundle, String key) {
        String str = getString(bundle, key);

        return Boolean.parseBoolean(str);
    }

    private boolean getBoolean(ResourceBundle bundle, String key, boolean value) {
        try {
            return getBoolean(bundle, key);
        } catch (InvalidConfigurationException e) {
            return value;
        }
    }

    private Config(ResourceBundle bundle) {
        Enumeration<String> keys =  bundle.getKeys();

        setDatabaseURL(getString(bundle, "database.url"));
        setDatabaseUsername(getString(bundle, "database.username"));
        setDatabasePassword(getString(bundle, "database.password"));

        setHttpPort(getInt(bundle, "server.port", 4567));

        setDensityMinimum(getInt(bundle,"map.density.min", 1));
        setDensityMaximum(getInt(bundle,"map.density.max", 90));

        setUseRealStarColours(getBoolean(bundle, "style.useRealStarColours", false));

        setPlanetMapFaceSize(getInt(bundle, "planet.map.faceSize", 12));
        setPlanetMapResolution(getInt(bundle, "planet.map.resolution", 2048));

        setSimFrequency(getInt(bundle, "sim.frequency", 60));
        setSkipDowntime(getBoolean(bundle, "sim.skipDowntime", false));
    }

    public static Config getConfiguration() {
        if (configuration == null) {

            if (System.getProperty("worldgen.config") != null) {
                String configFile = System.getProperty("worldgen.config");
                logger.info(String.format("Reading configuration from [%s]", configFile));

                try (FileInputStream fis = new FileInputStream(configFile)) {
                    configuration = new Config(new PropertyResourceBundle(fis));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ResourceBundle bundle = ResourceBundle.getBundle("worldgen");
                configuration = new Config(bundle);
            }
        }

        return configuration;
    }

    private void setDatabaseURL(String url) {
        if (url == null || url.trim().length() == 0) {
            throw new InvalidConfigurationException("database.url");
        }
        logger.info(String.format("DatabaseURL [%s]", url));
        this.databaseUrl = url;
    }

    /**
     * Gets the database URL to connect to.
     *
     * @return  Database URL.
     */
    public String getDatabaseURL() {
        return databaseUrl;
    }

    private void setDatabaseUsername(String username) {
        if (username == null || username.trim().length() == 0) {
            throw new InvalidConfigurationException("database.username");
        }
        logger.info(String.format("DatabaseUsername [%s]", username));
        this.databaseUsername = username;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    private void setDatabasePassword(String password) {
        if (password == null || password.length() == 0) {
            throw new InvalidConfigurationException("database.password");
        }
        logger.info(String.format("DatabasePassword [%s]", "********"));
        this.databasePassword = password;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    private void setDensityMinimum(int density) {
        if (density < 0 || density > 100) {
            throw new InvalidConfigurationException("map.density.min", ""+density);
        }
        logger.info(String.format("densityMin [%d]", density));
        this.mapDensityMin = density;
    }

    /**
     * Gets the minimum allowable density of a density map. This defaults to 1 if
     * not set. This is the percentage chance of a star system being present in any
     * given hex in a sector.
     *
     * @return  Minimum map density, from 0 to 100.
     */
    public int getDensityMinimum() {
        return mapDensityMin;
    }

    private void setDensityMaximum(int density) {
        if (density < 0 || density > 100) {
            throw new InvalidConfigurationException("map.density.max", ""+density);
        }
        logger.info(String.format("densityMax [%d]", density));
        this.mapDensityMax = density;
    }

    /**
     * Gets the maximum allowable density of a density map. This defaults to 90 if
     * not set. This is the percentage chance of a star system being present in any
     * given hex in a sector.
     *
     * @return  Maximum map density, from 0 to 100.
     */
    public int getDensityMaximum() {
        return mapDensityMax;
    }

    private void setUseRealStarColours(boolean useRealStarColours) {
        this.useRealStarColours = useRealStarColours;
    }

    /**
     * Gets whether star maps should use real star colours rather than false colours.
     * False colours make it clearer what the spectral type of the star is, but real
     * colours are... more real. Defaults to false.
     *
     * @return      True if maps should show stars using real colours.
     */
    public boolean getUseRealStarColours() {
        return useRealStarColours;
    }


    private void setPlanetMapFaceSize(int planetMapFaceSize) {
        this.planetMapFaceSize = planetMapFaceSize;
    }

    /**
     * Gets the size of each face on a planetary map. Defaults to 12 if not set.
     *
     * @return      Size of each face on a planetary map.
     */
    public int getMapFaceSize() {
        return planetMapFaceSize;
    }

    private void setPlanetMapResolution(int planetResolution) {
        this.planetResolution = planetResolution;
    }

    /**
     * Gets the resolution (width, in pixels) of a planetary map. Defaults to 2048 if not set.
     * This doesn't affect the number of tiles on the map, just how big the final image is.
     *
     * @return Width of planet maps in pixels.
     */
    public int getPlanetMapResolution() {
        return planetResolution;
    }


    private void setSimFrequency(int frequency) {
        if (frequency < 1) {
            throw new InvalidConfigurationException("Sim Schedule must be greater than zero");
        }
        this.simFrequency = frequency;
    }

    /**
     * Gets the frequency (in seconds) at which the simulation updates events. This
     * is normally every minute (60 seconds) unless set to a different value.
     *
     * @return      Frequency of the simulation events, in seconds.
     */
    public int getSimFrequency() {
        return simFrequency;
    }


    private void setSkipDowntime(boolean skipDowntime) {
        this.skipDowntime = skipDowntime;
    }

    public boolean getSkipDowntime() {
        return skipDowntime;
    }


    private void setHttpPort(int port) {
        if (port < 1) {
            throw new InvalidConfigurationException("Server Port must be greater than zero");
        }
        this.httpPort = port;
    }

    /**
     * Gets the port the HTTP server should listen on. This defaults to 4567.
     *
     * @return      HTTP Port the server listens on.
     */
    public int getHttpPort() {
        return httpPort;
    }
}

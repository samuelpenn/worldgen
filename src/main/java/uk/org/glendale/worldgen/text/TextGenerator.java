/**
 * TextGenerator.java
 *
 * Copyright (c) 2007, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.codes.Government;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetGroup;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.codes.StarPort;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemCode;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.FacilityType;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Create a random description for a planet. Descriptions are made up of phrases
 * which are read from a resource file, allowing easy changing. The phrases
 * support a very simple form of flow control, allowing a phrase to consist of
 * random elements, to reference other elements, and to choose based on planet
 * values.
 *
 * [a|b|c] Select random one of a, b or c {a} Insert phrase referenced by key a
 * {a|b|c} Select random one of a, b or c and reference that key. (50?phrase)
 * 50% chance of displaying phrase (25?a|b) 25% chance of a, otherwise b
 * (Prop>20?a:b) Get property of planet, compare with value, then a otherwise b.
 * $Prop Value of property for planet.
 *
 *
 * @author Samuel Penn
 */
public class TextGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TextGenerator.class);
    private StringBuffer buffer = new StringBuffer();
    private Planet planet = null;
    private Facility facility = null;
    private StarSystem system = null;

    private Properties phrases = null;

    /**
     * Get the phrase for the given key from the resource bundle. Some keys will
     * have a number of possible options (in the form key, key.1, key.2 etc). If
     * a key has several options, one will be selected randomly.
     *
     * @param key
     *            Key to use to find a phrase.
     * @return The selected phrase, or null if none found.
     */
    private String getPhrase(String key) {
        String text = null;

        text = phrases.getProperty(key);
        if (text != null) {
            int i = 0;
            while (phrases.getProperty(key + "." + (i + 1)) != null) {
                i++;
            }
            if (i > 0) {
                int choice = Die.rollZero(i + 1);
                if (choice != 0)
                    text = phrases.getProperty(key + "." + choice);
            }
        }

        return text;
    }

    /**
     * Gets whether the named phrase has been defined. Only looks for the exact key,
     * doesn't try to find other associated keys.
     *
     * @param key   Key to look for.
     * @return      True iff the exact property exists.
     */
    private boolean hasPhrase(String key) {
        return phrases.getProperty(key) != null;
    }

    private static final String RESOURCE_BASE = "text.planets.";
    private static final String FACILITY_BASE = "text.facilities.";
    private static final String SYSTEM_BASE = "text.systems.";

    private void readResource(String bundleName) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
            Enumeration<String> e = bundle.getKeys();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
                String value = bundle.getString(key);
                phrases.setProperty(key, value);
            }
        } catch (MissingResourceException e) {
            logger.warn(String.format("Unable to find text bundle for [%s]", bundleName));
        }

    }

    /**
     * Finds all the resources for this type of planet generator. Resources are
     * named according to the planet type and group.
     */
    private void readResources() {
        String type = planet.getType().name();
        String group = planet.getType().getGroup().name();

        phrases = new Properties();

        readResource(RESOURCE_BASE + "all");
        readResource(RESOURCE_BASE + group);
        readResource(RESOURCE_BASE + group + "." + type);
    }

    public TextGenerator(final StarSystem system, final String resource) {
        this.system = system;
        phrases = new Properties();
        readResource(SYSTEM_BASE + resource);
    }

    public TextGenerator(final Planet planet) {
        this.planet = planet;
        if (this.planet == null) {
            throw new IllegalStateException(
                    "Planet builder has not been correctly initiated");
        }
        readResources();
    }

    public TextGenerator(final Planet planet, final Facility facility) {
        this.planet = planet;
        this.facility = facility;

        if (this.planet == null || this.facility == null) {
            throw new IllegalStateException(
                    "Planet builder has not been correctly initiated");
        }
        phrases = new Properties();

        String type = facility.getType().name().toLowerCase();
        String name = facility.getName();

        readResource(FACILITY_BASE + type + "." + name);
    }

    /**
     * Get a random phrase from the text fragment. The text may be bounded by [
     * ] and will be delimited by '|'. For example, [hello|hi|greetings] will
     * return one of 'hello', 'hi' or 'greetings'.
     *
     * @param text
     *            Text to choose from.
     * @return One of the phrases from the list of phrases.
     */
    private String random(String text) {
        text = text.replaceAll("^[\\[\\{]", "");
        text = text.replaceAll("[\\}\\]]$", "");
        StringTokenizer tokens = new StringTokenizer(text, "|");
        int count = tokens.countTokens();
        String token = null;
        int choice = (int) (Math.random() * count);

        for (int i = 0; i <= choice; i++) {
            token = tokens.nextToken();
        }

        return token;
    }

    /**
     * Parse a line of text which has flow control sections. Currently supported
     * flow control consists of: [a|b|c] - Choose one of a, b or c and display
     * that. {a|b|c} - Choose one of a, b or c and look it up as a key phrase,
     * then parse that before displaying it.
     *
     * @param line
     *            Line to be parsed.
     * @return Result instance of the parsed line.
     */
    private String parse(String line) {
        if (line == null)
            return "";

        try {
            // Replace any property variables, and change enums to lower case.
            while (line.contains("$$")) {
                String prop = line.substring(line.indexOf("$$") + 2);
                String value = "";

                prop = prop.replaceAll("[^A-Za-z0-9].*", "");
                // System.out.println(prop);

                value = getProperty(prop).replaceAll("([A-Z])", " $1").toLowerCase().trim();
                line = line.replaceFirst("\\$\\$" + prop, value);
            }

            // Replace any property variables.
            while (line.contains("$")) {
                String prop = line.substring(line.indexOf("$") + 1);
                String value = "";

                prop = prop.replaceAll("[^A-Za-z0-9].*", "");
                // System.out.println(prop);

                value = getProperty(prop);
                line = line.replaceFirst("\\$" + prop, value);
            }

            // Switch statement.
            // (VARIABLE|VALUE=a|VALUE=b|VALUE=c|DEFAULT)
            // If the VARIABLE is equal to a VALUE, display the option for that
            // value.
            // If VALUE> or VALUE< is used (instead of VALUE=), select the
            // option if the
            // VALUE is greater than or less than the VARIABLE.
            while (line.contains("(") && line.contains(")")) {
                String options = line.substring(line.indexOf("(") + 1,
                        line.indexOf(")") + 1);
                String[] tokens = options.split("\\|");
                String option = "";
                String value = tokens[0];

                for (int i = 1; i < tokens.length; i++) {
                    String test = tokens[i].replaceAll("[=<>].*", "");
                    option = tokens[i].replaceAll(".*[=<>]", "");

                    if (tokens[i].contains("=")) {
                        if (test.equals(value)) {
                            break;
                        }
                    } else if (tokens[i].contains("<")) {
                        try {
                            Long v = Long.parseLong(value);
                            Long t = Long.parseLong(test);
                            if (t < v) {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            if (value.compareToIgnoreCase(test) < 0)
                                break;
                        }

                    } else if (tokens[i].contains(">")) {
                        // Is the tested value greater than this case?
                        try {
                            Long v = Long.parseLong(value);
                            Long t = Long.parseLong(test);
                            if (t > v) {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            if (value.compareToIgnoreCase(test) > 0)
                                break;
                        }
                    } else {
                        // Default value.
                        break;
                    }
                }

                line = line.replaceFirst("\\(.*?\\)", option);
                line = line.replaceAll("\\)", "");
            }

            // Choosen a random option.
            while (line.contains("[") && line.contains("]")) {
                int start = line.indexOf("[");
                int end = line.indexOf("]") + 1;
                if (end < start) {
                    // Malformed text string. Try to repair and continue.
                    logger.warn(String.format("Text string <<%s>> is malformed, probably spurious ']'", line));
                    line = line.replaceFirst("\\]", "");
                    continue;
                }

                String options = line.substring(start, end);
                String option = random(options);

                line = line.replaceFirst("\\[.*?\\]", option);
            }

            // Replace a random option with the substituted phrase.
            while (line.contains("{") && line.contains("}")) {
                String options = line.substring(line.indexOf("{"),
                        line.indexOf("}") + 1);
                String option = random(options);
                // System.out.println("Replacing ["+option+"] in ["+line+"]...");
                option = parse(getPhrase(option));
                // System.out.println("...with ["+option+"]");

                line = line.replaceFirst("\\{.*?\\}", option);
            }
        } catch (Throwable e) {
            System.out.println("Unable to parse [" + line + "] ("
                    + e.getMessage() + ")");
            e.printStackTrace();
        }
        return line;
    }

    /**
     * Get the named property from the Planet object. Uses reflection to call
     * the right getter on the Planet. If no such property is found, then the
     * empty string is returned. Result is always a string. If the contents
     * looks like a number, then it will be formatted and truncated to 1dp if
     * necessary.
     *
     * If the property name is of the form xDy, then a dice roll is actually
     * done instead, rolling x dice of size y. e.g., $3D6 translates as roll 3
     * six sided dice, and return the result.
     *
     * @param name
     *            Name of property to fetch.
     * @return Value of the property, or empty string.
     */
    private String getProperty(String name) {
        String value = "";
        if (planet == null && system == null) {
            return value;
        }

        // Possible to make die rolls.
        if (name.matches("[0-9]D[0-9]")) {
            int number = Integer.parseInt(name.replaceAll("([0-9]+)D[0-9]+",
                    "$1"));
            int type = Integer.parseInt(name
                    .replaceAll("[0-9]+D([0-9])+", "$1"));

            return "" + Die.die(type, number);
        }

        try {
            if (planet != null) {
                Method method = planet.getClass().getMethod("get" + name);
                Object result = method.invoke(planet);
                value = "" + result;
            } else if (system != null) {
                Method method = system.getClass().getMethod("get" + name);
                Object result = method.invoke(system);
                value = "" + result;
            }

            try {
                double i = Double.parseDouble(value);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(1);
                value = format.format(i);
            } catch (NumberFormatException e) {
                // Do nothing.
            }
        } catch (Throwable e) {
            System.out.println("getProperty: Cannot find method for [" + name
                    + "]");
            value = "";
        }

        return value;
    }

    private void addText(StringBuffer buffer, String key, int percentChance) {
        if (Die.d100() > percentChance)
            return;

        String text = getPhrase(key);
        if (text == null)
            return;

        if (buffer.length() > 0) {
            buffer.append(" ");
        }
        buffer.append(parse(text));
    }

    /**
     * Gets the full description for this planet. As well as getting a basic description based on the
     * type of the planet, it also returns text describing the planet's temperature, atmosphere and
     * biosphere if these are available.
     *
     * @return          String containing the full description of this planet.
     */
    public String getFullDescription() {
        return getFullDescription("planet." + planet.getType());
    }

    /**
     * Gets the full description for this planet. As well as getting a basic description based on the
     * type of the planet, it also returns text describing the planet's temperature, atmosphere and
     * biosphere if these are available.
     *
     * There are certain planet features, that if defined, will override the normal descriptions.
     * If a planet has such a feature, the description for this feature will be returned instead.
     *
     * @param rootKey   This is the planet type.
     * @return          String containing the full description of this planet.
     */
    private String getFullDescription(String rootKey) {
        buffer = new StringBuffer();

        // Look for features definitions of form planet.<type>.<feature>
        for (PlanetFeature feature : planet.getFeatures()) {
            String key = String.format("%s.%s", rootKey, feature.toString());
            if (hasPhrase(key)) {
                addText(buffer, key, 100);
                return buffer.toString().replaceAll(" +", " ").trim();
            }
        }

        addText(buffer, rootKey, 100);
        addText(buffer, rootKey + ".temperature." + planet.getTemperature(),
                100);
        addText(buffer, rootKey + ".atmosphere." + planet.getAtmosphere(), 100);
        addText(buffer, rootKey + ".pressure." + planet.getPressure(), 75);
        addText(buffer, rootKey + ".biosphere." + planet.getLife(), 100);

        for (PlanetFeature feature : planet.getFeatures()) {
            String key = String.format("%s.feature.%s", rootKey, feature.toString());
            if (phrases.containsKey(key)) {
                addText(buffer, key, 100);
            } else {
                logger.error("MISSING KEY " + key);
            }
        }

        // Add description for any trade codes.
		/*
		 * for (String code : planet.getTradeCodes()) { String key =
		 * rootKey+".trade."+code; if (phrases.getProperty(key) != null) {
		 * addText(buffer, key, 100); } else { key = "trade."+code; if
		 * (phrases.getProperty(key) != null) { addText(buffer, key, 100); } } }
		 */
        return buffer.toString().replaceAll(" +", " ").trim();
    }

    public String getFacilityDescription() {
        String rootKey = facility.getName();

        buffer = new StringBuffer();

        addText(buffer, rootKey, 100);

        return buffer.toString().replaceAll(" +", " ").trim();
    }

    public String getSystemDescription(String rootKey) {
        buffer = new StringBuffer();

        addText(buffer, "system." + rootKey, 100);

        for (StarSystemCode code : system.getTradeCodes()) {
            addText(buffer, "system." + rootKey + "." + code.name(), code.getNotability());
        }

        return buffer.toString().replaceAll(" +", " ").trim();
    }

    public static void main(String[] args) {
        Planet p = new Planet();
        Facility f = new Facility();

        p.setGovernment(Government.Anarchy);
        p.setStarPort(StarPort.Do);
        f.setName("RamshackleDocks");
        f.setType(FacilityType.STARPORT);

        TextGenerator tg = new TextGenerator(p, f);

        System.out.println(tg.getFacilityDescription());
    }
}

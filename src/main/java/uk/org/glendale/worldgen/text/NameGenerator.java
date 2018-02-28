/**
 * NameGenerator.java
 *
 * Copyright (c) 2011, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import uk.org.glendale.utils.rpg.Die;

/**
 * Random name generator.
 *
 * @author Samuel Penn
 *
 */
public class NameGenerator {
    private StringBuffer buffer = new StringBuffer();

    private Properties names = null;
    private ArrayList<String> roots = new ArrayList<String>();

    public NameGenerator(String resource) {
        String bundleName = "text." + resource;

        ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
        names = new Properties();

        Enumeration keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            names.setProperty(key, bundle.getString(key));

            if (key.matches("[a-z]*\\.format")) {
                roots.add(key.replaceAll("\\.format", ""));
            }

        }
    }

    private void getResource(URL url) {
        throw new UnsupportedOperationException("URLs are not yet supported");
    }

    /**
     * Get the phrase for the given key from the resource bundle. Some keys will
     * have a number of possible options (in the form key, key.1, key.2 etc). If
     * a key has several options, one will be selected randomly.
     *
     * @param key
     *            Key to use to find a phrase.
     * @return The selected phrase, or null if none found.
     */
    private String getRules(String key) {
        String text = null;

        text = names.getProperty(key);
        if (text != null) {
            int i = 0;
            while (names.getProperty(key + "." + (i + 1)) != null)
                i++;
            if (i > 0) {
                int choice = (int) (Math.random() * (i + 1));
                // System.out.println("Going for choice "+choice+" out of "+i);
                if (choice != 0)
                    text = names.getProperty(key + "." + choice);
            }
        }
        // System.out.println("Got ["+key+"] ["+text+"]");

        return text;
    }

    private String get(String style, String modifier, String key) {
        String list = null;

        if (modifier != null)
            list = getRules(key + "." + modifier);
        if (list == null)
            list = getRules(key);

        // System.out.println("get["+modifier+","+key+"]: ["+list+"]");

        String[] tokens = list.split(" +");
        String rule = tokens[Die.rollZero(tokens.length)];
        String word = "";

        for (int i = 0; i < rule.length(); i++) {
            char c = rule.charAt(i);
            if (Character.isUpperCase(c)) {
                word += get(style, modifier, style + "." + c);
            } else {
                word += c;
            }
        }

        return word;
    }

    private String getName(String style, String modifier) {
        String format = null;

        if (modifier != null)
            format = getRules(style + "." + modifier + ".format");
        if (format == null)
            format = getRules(style + ".format");

        if (format == null) {
            return "[" + style + ".format]";
        }

        String[] roots = format.split(" ");
        String name = "";

        for (String f : roots) {
            String n = get(style, modifier, f);
            name += n.substring(0, 1).toUpperCase() + n.substring(1) + " ";
        }
        name = name.replaceAll(" '", "'");
        name = name.replaceAll("_", " ");

        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ' ' && i < name.length() - 2) {
                name = name.substring(0, i + 1)
                        + name.substring(i + 1, i + 2).toUpperCase()
                        + name.substring(i + 2);
            }
        }

        return name.trim();
    }

    public String[] getRoots() {
        return roots.toArray(new String[0]);
    }

    /**
     * Gets a name generated according to the default rule set. If a default ('root')
     * ruleset hasn't been defined, get one based on 'standard'.
     *
     * @return  Randomly generated name.
     */
    public String generateName() {
        return getName(names.getProperty("root", "standard"), null);
    }

    /**
     * Gets a name generated according to the specified ruleset.
     *
     * @param root  Ruleset to use.
     * @return  Randomly generated name.
     */
    public String generateName(String root) {
        return getName(root, null);
    }

    /**
     * Gets a name generated according to the specified ruleset and modifier.
     *
     * @param root      Ruleset to use.
     * @param modifier  Modifier to use.
     *
     * @throws Exception
     */
    public String generateName(String root, String modifier) {
        String name = getName(root, modifier);

        int softMax = 6;
        int hardMax = 8;
        try {
            softMax = Integer.parseInt(names.getProperty(root+".soft.max", "6"));
            hardMax = Integer.parseInt(names.getProperty(root+".hard.max", "8"));
        } catch (NumberFormatException e) {
            // Broken config.
        }

        // If the name length is greater than the soft maximum, retry once.
        // Forbid names longer than the hard maximum.
        int max = softMax;
        while (name.length() > max) {
            name = getName(root, modifier);
            max = hardMax;
        }

        return name;
    }


    public static void main(String[] args) throws Exception {
        NameGenerator name = new NameGenerator("systems");

        for (String r : name.getRoots()) {
            System.out.println(r);
        }

        for (int i = 0; i < 5; i++) {
            System.out.println(name.generateName("standard"));
        }
    }
}

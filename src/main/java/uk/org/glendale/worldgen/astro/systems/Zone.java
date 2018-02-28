package uk.org.glendale.worldgen.astro.systems;

/**
 * Defines the Zone of a star system, either Green, Amber or Red. This determines how dangerous the system
 * is for travellers. Most systems will be Green. Caution is recommended for Amber systems, and Red systems
 * are generally forbidden for standard travel.
 */
public enum Zone {
    GREEN("Green", "#00FF00"),
    AMBER("Amber", "#FF8000"),
    RED("Red", "#FF0000");

    private String name;
    private String colour;

    private Zone(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    /**
     * Gets the colour to display for this type of zone.
     *
     * @return  RGB colour code.
     */
    public String getColour() {
        return colour;
    }

    /**
     * Gets the name of this zone type, either "Green", "Amber" or "Red".
     *
     * @return  Zone type as a string.
     */
    public String getName() {
        return name;
    }
}

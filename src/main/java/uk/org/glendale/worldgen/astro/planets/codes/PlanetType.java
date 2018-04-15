/**
 * PlanetType.java
 *
 * Copyright (C) 2011 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.codes;

/**
 * Defines the available planetary types.
 *
 * @author Samuel Penn
 */
public enum PlanetType {
    // Belt Group
    AsteroidBelt(PlanetClass.Circumstellar, "#777777"),
    VulcanianBelt(PlanetClass.Circumstellar, "#775555"),
    MetallicBelt(PlanetClass.Circumstellar, "#555555"),
    IceBelt(PlanetClass.Circumstellar, "#aaaaaa"),
    OortCloud(PlanetClass.Circumstellar, "#aaaaaa"),
    DustDisc(PlanetClass.Circumstellar, "#ddbbbb"),
    PlanetesimalDisc(PlanetClass.Circumstellar, "#997777"),
    IceRing(PlanetClass.PlanetaryRing, "#aaaaaa"),

    // Small Body Group
    Vulcanian(PlanetClass.Vulcanoidal, 7.5, 2, "#333333"),
    Metallic(PlanetClass.Asteroidal, 8.5, 2, "#333333"),
    Silicaceous(PlanetClass.Asteroidal, 4.0, 2, "#333333"),
    Carbonaceous(PlanetClass.Asteroidal, 4.0, 2, "#333333"),
    Gelidaceous(PlanetClass.Asteroidal, 2.0, 2, "#555555"),
    Aggregate(PlanetClass.Asteroidal, 2.0, 2, "#333333"),

    // Dwarf Terrestrial Group
    // Ferrinian Type
    Ferrinian(PlanetClass.GeoPassive, 7.0, 3, "#333333"),
    // Lithic Type
    Janian(PlanetClass.GeoPassive, 6.0, 3, "#444444"),
    Hermian(PlanetClass.GeoPassive, 6.0, 3, "#444444"),
    Vestian(PlanetClass.GeoPassive, 4.0, 3, "#444444"),
    Selenian(PlanetClass.GeoPassive, 3.0, 3, "#444444"),
    Cerean(PlanetClass.GeoPassive, 2.0, 2, "#666666"),
    // GeoCyclic
    EoArean(PlanetClass.GeoCyclic, 5.0, 3, "#554444"),
    MesoArean(PlanetClass.GeoCyclic, 5.0, 3, "#554444"),
    EuArean(PlanetClass.GeoCyclic, 5.0, 3, "#554444"),
    AreanLacustric(PlanetClass.GeoCyclic, 5.0, 3, "#554444"),

    // Jovian Group

    // SubJovian's have 0.04 to 0.48 mass of Jupiter.
    Sokarian(PlanetClass.SubJovian, 1.0, 6, "#777755"),
    Poseidonic(PlanetClass.SubJovian, 1.0, 6, "#777755"),
    Neptunian(PlanetClass.SubJovian, 1.5, 6, "#555577"),

    // DwarfJovian's have 0.06 to 0.8 mass of Jupiter.
    Osirian(PlanetClass.DwarfJovian, 2.0, 6, "#777755"),
    Brammian(PlanetClass.DwarfJovian, 1.5, 6, "#777755"),
    Saturnian(PlanetClass.DwarfJovian, 1.0, 6, "#777755"),

    // Mass of Jupiter to 2.5 mass.
    Junic(PlanetClass.MesoJovian, 0.5, 10, "#777755"),
    Jovic(PlanetClass.MesoJovian, 2.0, 10, "#777755"),

    SuperJunic(PlanetClass.SuperJovian, 0.5, 15, "#997777"),
    SuperJovic(PlanetClass.SuperJovian, 2.0, 15, "#997777"),

    Chthonian(PlanetClass.Chthonian, 4.0, 6, "#666666"),

    // Terrestrial Group
    EoGaian(PlanetClass.Tectonic, 5.5, 6500, "gaian"),
    MesoGaian(PlanetClass.Tectonic, 5.5, 6500, "gaian"),

    Undefined(null, 0.0, 0);

/*
    AsteroidBelt(Category.Belt, 0.0, 0),
    VulcanianBelt(Category.Belt, 0.0, 0),
    MetallicBelt(Category.Belt, 0.0, 0),
    IceBelt(Category.Belt, 0.0, 0),
    OortCloud(Category.Belt, 0.0, 0),

    MatrioshkaBrain(Category.Construct, 0.0, 0),
    RingWorld(Category.Construct, 0.0, 0),
    Orbital(Category.Construct, 0.0, 0),
    DysonSphere(Category.Construct, 0.0, 0),
    GlobusCassus(Category.Construct, 0.0, 0),
    OrbitalRing(Category.Construct, 0.0, 0),

    Vulcanian(Category.Asteroid, 7.5, 100, "hotrock"),		// Asteroid close to parent sun, heavy metals.
    Silicaceous(Category.Asteroid, 4.0, 150),				// Asteroid with nickle-iron core.
    Sideritic(Category.Asteroid, 6.0, 120),					// Pure nickel-iron, very dense.
    Basaltic(Category.Asteroid, 4.0, 80),					// Cooled larva on surface, smooth. Rare.
    Carbonaceous(Category.Asteroid, 3.0, 150),  			// Very dark, rich in carbon. Outer middle solar systems. (C-type)
    Enceladean(Category.Asteroid, 1.6, 220, "ice"),    		// Enceladus (ice, active)
    Mimean(Category.Asteroid, 1.1, 180, "ice"),        		// Mimas (ice, inactive)
    Oortean(Category.Asteroid, 1.5, 100, "ice"),			// World out in the Oort cloud.

    Hadean(Category.Dwarf, 7.0, 500, "hotrock"),           	// Planetoid very iron rich, just a core.
    Cerean(Category.Dwarf, 2.0, 500, "ice"), 				// Ceres (rocky core, ice layer, dusty crust)
    Vesperian(Category.Dwarf, 3.4, 300),					// Vespa (iron-nickel core, rocky mantle and crust)
    Vestian(Category.Dwarf, 2.0, 2500),         			// Silicate rich moons.
    Kuiperian(Category.Dwarf, 2.0, 1100, "ice"),       		// Pluto
    Hephaestian(Category.Dwarf, 3.0, 1800), 				// Io
    Iapetean(Category.Dwarf, 1.5, 1500, "ice"),				// Iapetus, stretched and cracked ice world.
    Tritonic(Category.Dwarf, 2.0, 1000, "ice"),				// Triton, icy volcanism.

    MesoTitanian(Category.Dwarf, 3.0, 2300),	// Dead Titan
    EuTitanian(Category.Dwarf, 3.0, 2500),		// Titan (methane, with solid water ice)
    TitaniLacustric(Category.Dwarf, 3.0, 2700),	// Warm Titan, seas.

    MesoUtgardian(Category.Dwarf, 2.0, 1200),	// Ammonia
    EuUtgardian(Category.Dwarf, 2.0, 1500),		// Ammonia
    UtgardiLacustric(Category.Dwarf, 2.0, 1800),// Ammonia

    Ferrinian(Category.Dwarf, 6.0, 1800), 		// Iron rich
    Selenian(Category.Dwarf, 3.3, 1700), 		// Moon
    Europan(Category.Dwarf, 2.5, 1500), 		// Europa
    Stygian(Category.Dwarf, 2.5, 2000), 		// Now frozen after death of star.
    LithicGelidian(Category.Dwarf, 2.0, 2000),  // Rock/ice worlds, often moons. Ganymede/Callisto

    // Gaian type worlds
    EoGaian(Category.Terrestrial, 5.5, 6500, "gaian"),
    MesoGaian(Category.Terrestrial, 5.5, 6500, "gaian"),
    ArchaeoGaian(Category.Terrestrial, 5.5, 6500, "gaian"),
    Gaian(Category.Terrestrial, 5.5, 6500, "gaian"),
    GaianTundral(Category.Terrestrial, 5.5, 6200, "gaian"),
    GaianXenic(Category.Terrestrial, 5.5, 6200, "gaian"),
    PostGaian(Category.Terrestrial, 5.5, 6500, "gaian"),

    // Chlorine worlds
    EoChloritic(Category.Terrestrial, 5.5, 6500),
    MesoChloritic(Category.Terrestrial, 5.5, 6500),
    ArchaeoChloritic(Category.Terrestrial, 5.5, 6500),
    Chloritic(Category.Terrestrial, 5.5, 6500),
    ChloriticTundral(Category.Terrestrial, 5.5, 6500),
    PostChloritic(Category.Terrestrial, 5.5, 6500),

    // Sulphur worlds
    EoThio(Category.Terrestrial, 5.5, 6500),
    MesoThio(Category.Terrestrial, 5.5, 6500),
    ArchaeoThio(Category.Terrestrial, 5.5, 6500),
    Thio(Category.Terrestrial, 5.5, 6500),
    ThioTundral(Category.Terrestrial, 5.5, 6500),
    PostThio(Category.Terrestrial, 5.5, 6500),

    Hermian(Category.Terrestrial, 5.0, 2500, "hotrock"), 		// Mercury
    EoArean(Category.Terrestrial, 4.5, 3500),
    MesoArean(Category.Terrestrial, 4.5, 3500),
    AreanLacustric(Category.Terrestrial, 4.5, 3500), // Watery Arean
    Arean(Category.Terrestrial, 4.5, 3500),
    AreanXenic(Category.Terrestrial, 4.5, 3500),   // Hot Arean
    Cytherean(Category.Terrestrial, 5.5, 6200),    // Venus
    PelaCytherean(Category.Terrestrial, 5.5, 6200), // Venus with ocean
    Phosphorian(Category.Terrestrial, 5.5, 6200),  // Cloudless Venus
    JaniLithic(Category.Terrestrial, 5.5, 5500),   // Dry, hot, atmosphere.
    Pelagic(Category.Terrestrial, 6.0, 7000),
    Panthalassic(Category.Terrestrial, 5.5, 10000), // Huge world ocean

    CryoJovian(Category.Jovian, 1.1, 50000, "cryojovian"),
    SubJovian(Category.Jovian, 0.8, 70000, "jovian"),
    EuJovian(Category.Jovian, 1.0, 90000, "jovian"),
    SuperJovian(Category.Jovian, 1.5, 120000, "jovian"),
    MacroJovian(Category.Jovian, 2.0, 160000, "jovian"),
    EpiStellarJovian(Category.Jovian, 1.2, 100000, "jovian");
*/


    private final PlanetClass	planetClass;
    private final double		density;
    private final int		    radius;
    private final String        colour;

    private PlanetType(final PlanetClass planetClass) {
        this.planetClass = planetClass;
        this.density = 0.0;
        this.radius = 0;
        this.colour = "#000000";
    }

    private PlanetType(final PlanetClass planetClass, String colour) {
        this.planetClass = planetClass;
        this.density = 0.0;
        this.radius = 0;
        this.colour = colour;
    }

    private PlanetType(final PlanetClass planetClass, double density, int radius) {
        this.planetClass = planetClass;
        this.density = density;
        this.radius = radius;
        this.colour = "#000000";
    }

    private PlanetType(final PlanetClass planetClass, double density, int radius, String colour) {
        this.planetClass = planetClass;
        this.density = density;
        this.radius = radius;
        this.colour = colour;
    }

    /**
     * Gets the mid level classification for this planet type.
     *
     * @return  Mid level class.
     */
    public final PlanetClass getClassification() {
        return planetClass;
    }

    /**
     * Gets the top level group classification for this planet type.
     *
     * @return  Top level group.
     */
    public final PlanetGroup getGroup() {
        return planetClass.getGroup();
    }


    public int getRadius() {
        return radius;
    }

    public double getDensity() {
        return density;
    }

    public String getColour() {
        return colour;
    }

}

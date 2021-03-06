package uk.org.glendale.worldgen.astro.systems;

import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.StarPort;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.Star;

import javax.persistence.*;
import java.util.*;

/**
 * Defines a StarSystem. A star system contains zero or more stars and zero or more planets. Star systems without
 * stars are considered to be rogue planets and aren't normally displayed on the map. Each star system takes up
 * a parsec wide hex on a sector map, so multiple star systems can't exist in the same location.
 */
@Entity
@Table(name = "systems")
public class StarSystem {
    @Id @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "sector_id")
    private int sectorId;

    @Column
    private String name;
    @Column
    private int x;
    @Column
    private int y;

    @Column (name="type")
    @Enumerated(EnumType.STRING)
    private StarSystemType  type;

    @Column(name="zone")
    @Enumerated(EnumType.STRING)
    private Zone zone;

    @Column(name="planets")
    private int planetCount;

    @Column(name="port")
    @Enumerated(EnumType.STRING)
    private StarPort port;

    @Column
    private int tech;

    @Column
    private long population;

    @Column
    private String codes;

    @Column
    private String description = "";

    @OneToMany(mappedBy = "system", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Star> stars = new ArrayList<>();

    // List of planets in this system. This is temporary and not stored.
    @Transient
    private List<Planet> planets = new ArrayList<>();

    public static final int PRIMARY_COG = -1;
    public static final int SECONDARY_COG = -2;

    /**
     * Private constructor for the persistence layer.
     */
    public StarSystem() {

    }

    StarSystem(Sector sector, String name, int x, int y, StarSystemType type, Zone zone) {
        this.sectorId = sector.getId();

        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("StarSystem must have a non-empty name.");
        }
        this.name = name.trim();

        if (x < 1 || y < 1 || x > Sector.WIDTH || y > Sector.HEIGHT) {
            throw new IllegalArgumentException(
                    String.format("StarSystem coordinates [%d,%d] are out of range.", x, y));
        }

        this.x = x;
        this.y = y;
        this.type = type;
        this.zone = zone;

        this.planetCount = 0;
        this.port = StarPort.X;
        this.codes = "";
    }

    public int getId() {
        return id;
    }

    public int getSectorId() {
        return sectorId;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public StarSystemType getType() {
        return type;
    }

    public void setType(StarSystemType type) {
        if (type == null) {
            throw new IllegalArgumentException("System type cannot be null.");
        }
        this.type = type;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("System Zone cannot be null.");
        }
        this.zone = zone;
    }

    public StarPort getStarPort() {
        return port;
    }

    public int getTechLevel() {
        return tech;
    }

    public long getPopulation() {
        return population;
    }

    public int getPlanetCount() {
        return planetCount;
    }

    public Set<StarSystemCode> getTradeCodes() {
        Set<StarSystemCode> tradeCodes = EnumSet.noneOf(StarSystemCode.class);

        if (codes.length() > 0) {
            for (String code : codes.split(" ")) {
                tradeCodes.add(StarSystemCode.valueOf(code));
            }
        }

        return tradeCodes;
    }

    public void setTradeCodes(String codes) {
        this.codes = codes;
    }

    public void addTradeCode(StarSystemCode code) {
        if (!getTradeCodes().contains(code)) {
            codes = (codes + " " + code.name()).trim();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    void setSystemData(List<Planet> planets) {
        this.planetCount = planets.size();

        int count = 0;
        long population = 0;
        int tech = 0;
        StarPort port = StarPort.X;
        for (Planet planet : planets) {
            count++;
            population += planet.getPopulation();
            if (planet.getStarPort().isBetterThan(port)) {
                port = planet.getStarPort();
                tech = planet.getTechLevel();
            }
        }

        this.planetCount = count;
        this.population = population;
        this.tech = tech;
        this.port = port;
    }

    public List<Star> getStars() {
        return stars;
    }

    public void setStars(List<Star> stars) {
        this.stars = stars;
    }

    /**
     * Add a new star to the system. There can be up to three stars in a star system.
     *
     * @param star  Star to add.
     */
    public void addStar(Star star) {
        if (stars.size() > 2) {
            throw new IllegalStateException("A system can't have more than 3 stars.");
        }
        this.stars.add(star);
    }

    public void setPlanets(List<Planet> planets) {
        if (planets != null) {
            this.planets = planets;
        } else {
            this.planets = new ArrayList<Planet>();
        }
    }

    public void addPlanets(List<Planet> planets) {
        if (planets != null) {
            this.planets.addAll(planets);
        }
    }

    public void addPlanet(Planet planet) {
        if (planet != null) {
            this.planets.add(planet);
        }
    }

    public List<Planet> getPlanets() {
        return this.planets;
    }

    public String toString() {
        return String.format("%s [%02d%02d]", name, x, y);
    }
}

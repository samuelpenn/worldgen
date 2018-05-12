package uk.org.glendale.worldgen.civ;

import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.exceptions.NoSuchObjectException;

public class NoSuchFacilityException extends NoSuchObjectException {
    public NoSuchFacilityException(int id) {
        super(String.format("Cannot find facility [%d]", id));
    }

    public NoSuchFacilityException(Planet planet, String name) {
        super(String.format("Cannot find facility [%s] on planet [%d]", name, planet.getId()));
    }
}

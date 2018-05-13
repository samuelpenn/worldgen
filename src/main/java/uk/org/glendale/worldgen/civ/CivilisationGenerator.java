/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ;

import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates a new civilisation on a planet. A civilisation isn't recorded, but specifies
 * a list of facilities that are recorded against the planet.
 */
public abstract class CivilisationGenerator {
    protected WorldGen worldGen;
    protected StarSystem system;
    protected Planet planet;
    protected Set<CivilisationFeature> features;

    public CivilisationGenerator(WorldGen worldGen, StarSystem system, Planet planet) {
        this.worldGen = worldGen;
        this.system = system;
        this.planet = planet;

        this.features = new HashSet<CivilisationFeature>();
    }

    /**
     * Abstract generation class that must be extended.
     *
     * @param features  Optional list of features to apply to this civilisation.
     */
    public abstract void generate(CivilisationFeature... features);

    public void setFeatures(CivilisationFeature[] features) {
        for (CivilisationFeature f : features) {
            this.features.add(f);
        }
    }

    public void setFeatures(Set<CivilisationFeature> features) {
        this.features = features;
    }

    public boolean hasFeature(CivilisationFeature feature) {
        return this.features.contains(feature);
    }

    /**
     * Append a description of the civilisation to the end of the planet's physical description.
     * Each facility gets a chance to add a description, which has its own heading. If a facility
     * does not have descriptions defined, then no heading is added.
     *
     * @param facilities    List of facilities to describe.
     */
    protected void generateDescription(List<Facility> facilities) {
        String text = planet.getDescription();
        for (Facility f : facilities) {
            TextGenerator t = new TextGenerator(planet, f);
            String          description = t.getFacilityDescription();
            if (description.length() > 0) {
                String type = f.getType().getTitle();
                if (type.length() > 0) {
                    type = " (" + type + ")";
                }
                text += "<h5>" + f.getTitle() + type + "</h5>";
                text += "<p>" + description + "</p>";
            }
        }
        planet.setDescription(text);
    }
}

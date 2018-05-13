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
 * Generates a new civilisation.
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

    public abstract void generate(CivilisationFeature... features);

    protected void generateDescription(List<Facility> facilities) {
        String text = planet.getDescription();
        for (Facility f : facilities) {
            TextGenerator t = new TextGenerator(planet, f);
            String          description = t.getFacilityDescription();
            if (description.length() > 0) {
                text += "<h5>" + f.getTitle() + "</h5>";
                text += "<p>" + description + "</p>";
            }
        }
        planet.setDescription(text);
    }
}

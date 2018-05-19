/**
 * PlanetMapper.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets;

import javax.persistence.*;

/**
 * Persisted object for storing and accessing bitmap planetary maps. A planet can have multiple maps
 * associated with it, which are identified by a name. Most planets will have a 'MAIN' map.
 */
@Entity
@Table(name="planet_maps")
public class PlanetMap {
    @Id
    @GeneratedValue
    private int id;

    @Column (name = "planet_id")
    private int planetId;

    @Column
    private String name;

    @Column
    private byte[] data;

    // The name for the main map for this planet.
    public final static String MAIN = "main";
    // Hieght map for this planet.
    public final static String HEIGHT = "height";
    // Night time map, showing civilisation lights.
    public final static String NIGHT = "night";
    // Cloud map.
    public final static String CLOUD = "cloud";
    // Deform map.
    public final static String DEFORM = "deform";

    // Belts and similar planets don't have a main map.
    public final static String BELT = "belt";

    protected PlanetMap() {

    }

    public PlanetMap(int planetId, String name, byte[] data) {
        this.planetId = planetId;
        this.name = name;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public void setPlanetId(final int planetId) {
        this.planetId = planetId;
    }

    public int getPlanetId() {
        return planetId;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

}

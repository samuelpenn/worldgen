/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ;

import javax.persistence.*;

@Entity
@Table(name = "facilities")
public class Facility {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "planet_id")
    private int planetId;

    @Column
    private String name;

    @Column
    private String title;

    @Column (name="type")
    @Enumerated(EnumType.STRING)
    private FacilityType type;

    @Column (name="rating")
    private int rating;

    @Column (name="tech")
    private int tech;

    public int getId() {
        return id;
    }

    public int getPlanetId() {
        return planetId;
    }

    public void setPlanetId(int planetId) {
        this.planetId = planetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public FacilityType getType() {
        return type;
    }

    public void setType(FacilityType type) {
        this.type = type;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getTechLevel() {
        return tech;
    }

    public void setTechLevel(int tech) {
        this.tech = tech;
    }
}

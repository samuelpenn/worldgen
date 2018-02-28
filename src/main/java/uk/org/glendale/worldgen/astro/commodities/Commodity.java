/**
 * Commodity.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.commodities;

import javax.persistence.*;

/**
 * A commodity is a type of resource or good that be mined, produced or traded. Planets have commodities
 * in terms of resources, facilities consume and produce commodities, and traders deal with them as
 * of trade goods.
 */
@Entity
@Table(name = "commodities")
public class Commodity {
    @Id @GeneratedValue @Column
    private int id;

    @Column
    private String name;

    @Column @Enumerated (EnumType.STRING)
    private Frequency frequency;

    @Column
    private String image;

    protected Commodity() {

    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name.trim();
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(final Frequency frequency) {
        this.frequency = frequency;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image.trim();
    }

    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }
        return ((Commodity)o).id == id;
    }

}

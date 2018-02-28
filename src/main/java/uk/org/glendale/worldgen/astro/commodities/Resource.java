package uk.org.glendale.worldgen.astro.commodities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class Resource {
    @ManyToOne
    @JoinColumn(name = "commodity_id")
    private Commodity commodity;
    @Column(name = "density")
    private int density;

    @SuppressWarnings("unused")
    public Resource() {
        // Exists only for persistence reasons.
    }

    public Resource(Commodity c, int density) {
        if (c == null) {
            throw new IllegalArgumentException(
                    "Resource must have a valid commodity");
        }
        if (density < 1) {
            throw new IllegalArgumentException("Resource density for ["
                    + c.getName() + "] cannot be zero or less");
        }
        this.commodity = c;
        this.density = density;
    }

    /**
     * Gets the commodity type that this resource provides.
     *
     * @return Type of commodity.
     */
    public Commodity getCommodity() {
        return commodity;
    }

    /**
     * Gets the density of this resource. Densities will typically range from 1
     * to 1000, though may in rare instances be higher. The density will never be
     * lower than 1. If density is 0 or less, the resource will not be listed.
     *
     * @return Density, from 1 - 1000.
     */
    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = Math.max(density, 1);
    }
}

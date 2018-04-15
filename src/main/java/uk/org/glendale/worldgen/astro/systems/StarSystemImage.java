/**
 * Luminosity.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetGroup;
import uk.org.glendale.worldgen.astro.planets.maps.belt.DustDiscMapper;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.sectors.SubSector;
import uk.org.glendale.worldgen.astro.sectors.SubSectorImage;
import uk.org.glendale.worldgen.astro.stars.Star;

import java.awt.*;
import java.util.List;
import java.util.Random;

import static uk.org.glendale.worldgen.astro.systems.StarSystemImage.ScaleType.AU;
import static uk.org.glendale.worldgen.astro.systems.StarSystemImage.ScaleType.MKM;

/**
 * Draws a map of the given star system.
 */
public class StarSystemImage {
    private static final Logger logger = LoggerFactory.getLogger(StarSystemImage.class);

    private final StarSystem system;
    private List<Planet> planets;

    private SimpleImage image		= null;
    private int			scale		= -1;
    private int         objectScale = -1;
    private int         width       = 1000;
    private long        currentTime = 0;

    private boolean     drawZones   = false;
    private boolean     drawTitle   = false;
    private boolean     drawLabels  = true;
    private ScaleType   scaleType   = MKM;
    private long        maxDistance = 150;

    public enum ScaleType {
        NONE,
        AU,
        MKM
    }

    /**
     * Create a new StarSystemImage for the given system.
     *
     * @param worldgen
     *            WorldGen object.
     * @param system
     *            StarSystem to draw map for.
     */
    public StarSystemImage(WorldGen worldgen, StarSystem system) {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        this.system = system;
        this.planets = planetFactory.getPlanets(system);

        this.currentTime = worldgen.getUniverse().getSimTime();
    }

    /**
     * Set the scale of the map to be generated. Scale gives size of each pixel,
     * in kilometres. So scale = 1000, 1 pixel = 1000km.
     *
     * @param scale
     *            Size of the map.
     */
    public void setScale(int scale) {
        this.scale = scale;
        this.objectScale = (int) Math.sqrt(scale);
    }

    /**
     * Gets the scale of the map. This is the number of km per pixel.
     *
     * @return      Scale in kilometres per pixel.
     */
    public int getScale() {
        return scale;
    }

    public void setWidth(int width) {
        this.width = Math.max(width, 500);
    }

    public int getWidth() {
        return width;
    }

    public boolean hasZones() {
        return drawZones;
    }

    /**
     * Sets whether to draw the habitable zones on the map or not. Defaults to false.
     *
     * @param drawZones     True if the habitable zones should be drawn.
     */
    public void setZones(boolean drawZones) {
        this.drawZones = drawZones;
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType != null) {
            this.scaleType = scaleType;
        } else {
            this.scaleType = ScaleType.NONE;
        }
    }

    /**
     * Gets the current time in seconds since the start of the epoc. This is normally set
     * to the current time of the simulation when the class is instantiated, unless it has
     * been manually overridden.
     *
     * @return  Time in seconds since the start of the simulation epoch.
     */
    public long getTime() {
        return currentTime;
    }

    /**
     * Sets the current time in seconds since the start of the epoc for this map. This
     * is used to determine where objects are in their orbits.
     *
     * @param time  Time in seconds since the start of the simulation epoch.
     */
    public void setTime(long time) {
        if (time < 0L) {
            throw new IllegalArgumentException("Time cannot be set to be negative.");
        }
        this.currentTime = time;
    }

    /**
     * Get the angular position of an object in orbit around its primary. This is based on the
     * current simulation time (or map time if that has been explicitly set). Assumes that planets
     * orbit counter-clockwise.
     *
     * @param primary       Primary star.
     * @param distance      Distance in Mkm.
     * @return              Angle, between 0 and 360 degrees.
     */
    private double getAngleOffset(Star primary, double distance) {
        double angle = (distance + primary.getId()) % 360;

        long period = Math.max(1, primary.getPeriod((int)distance));

        angle += (360.0 * (getTime() % period)) / period;
        angle %= 360;

        // Switch to counter clockwise.
        angle = (360 - angle) % 360;

        return angle;
    }

    /**
     * Set a default scale based on the size of the solar system.
     */
    private void setScale() {
        maxDistance = Physics.MKM;
        switch (system.getType()) {
            case EMPTY:
                maxDistance = Physics.AU;
                break;
            case SINGLE:
            case CONJOINED_BINARY:
                for (Planet p: planets) {
                    long d = p.getDistance();
                    if (p.getType().getGroup().equals(PlanetGroup.Belt)) {
                        d += p.getRadius();
                    }
                    if (d > maxDistance) {
                        maxDistance = d;
                    }
                }
                break;
            case CLOSE_BINARY:
            case MEDIUM_BINARY:
            case FAR_BINARY:
                // Smaller star orbits a larger star. Secondary star will be further away than
                // all the planets of the primary, so we just need to know how big the system
                // of the secondary star is, and add that to the distance between the two
                // stars.
                List<Star> stars = system.getStars();
                for (Planet p: planets) {
                    if (p.getParentId() == stars.get(1).getId()) {
                        long d = p.getDistance();
                        if (p.getType().getGroup().equals(PlanetGroup.Belt)) {
                            d += p.getRadius();
                        }
                        if (d > maxDistance) {
                            maxDistance = d;
                        }
                    }
                }
                // Add distance between stars.
                maxDistance += stars.get(1).getDistance();
                break;
            default:
                // More complex systems not yet fully supported, so just guess.
                maxDistance = 1500 * Physics.MKM;
                break;
        }
        maxDistance *= 1.1;
        scale = (int) (maxDistance * 2) / width;
        objectScale = (int) Math.sqrt(scale);
    }

    public SimpleImage draw() {
        image = new SimpleImage(width, width, "#ffffff");

        if (scale == -1) {
            setScale();
        }
        logger.info(String.format("Drawing system map for [%s] at scale of [%d]", system.getName(), getScale()));

        Star primary, secondary;
        switch(system.getType()) {
            case EMPTY:
                // Nothing to do.
                break;
            case SINGLE:
                primary = system.getStars().get(0);
                drawStar(primary, width/2, width/2);
                break;
            case CONJOINED_BINARY:
            case CLOSE_BINARY:
                primary = system.getStars().get(0);
                secondary = system.getStars().get(1);
                if (primary.getParentId() == StarSystem.PRIMARY_COG && secondary.getParentId() == StarSystem.PRIMARY_COG) {
                    if (drawZones) {
                        double constant = Physics.getSolarConstant(primary) + Physics.getSolarConstant(secondary);
                        drawZones(image, width/2, width/2, constant);
                    }
                    drawStar(primary, width/2 - (int)getScaledPixels(primary.getDistance()), width/2);
                    drawStar(secondary, width/2 + (int)getScaledPixels(secondary.getDistance()), width/2);
                } else {
                    logger.error(String.format("Conjoined stars in [%s] do not share each other as a parent", system.getName()));
                }
                break;
            case FAR_BINARY:
                primary = system.getStars().get(0);
                drawStar(primary, width/2, width/2);

                secondary = system.getStars().get(1);
                drawStar(secondary, width/2 + (int)getScaledPixels(secondary.getDistance()), width/2);
                break;
        }
        drawScale();

        return image;
    }

    private void drawZones(SimpleImage image, int cx, int cy, double constant) {
        image.circle(cx, cy, (int) getScaledPixels(constant * Physics.SNOW_DISTANCE), "#FFFFDD");
        image.circle(cx, cy, (int) getScaledPixels(constant * Physics.OUTER_DISTANCE), "#DDFFDD");
        image.circle(cx, cy, (int) getScaledPixels(constant * Physics.INNER_DISTANCE), "#FFDDDD");
        image.circle(cx, cy, (int) getScaledPixels(constant * Physics.MINIMUM_DISTANCE), "#FFFFFF");
    }

    /**
     * Gets a distance scaled to fit on the map. Distance is specified in KM,
     * and value is returned in pixels.
     *
     * @param km     Actual star system distance in kilometres.
     * @return       Scaled distance in pixels.
     */
    private double getScaledPixels(double km) {
        return (km / scale);
    }

    /**
     * Draws a scale line along the bottom of the map.
     */
    private void drawScale() {
        int scaleWidth = 150;
        String label = "1 AU";

        if (scaleType == ScaleType.NONE) {
            return;
        } else if (scaleType == MKM) {
            scaleWidth = 100;

            if (maxDistance < 10 * Physics.MKM) {
                scaleWidth = 1;
            } else if (maxDistance < 30 * Physics.MKM) {
                scaleWidth = 10;
            } else if (maxDistance < 100 * Physics.MKM) {
                scaleWidth = 25;
            } else if (maxDistance < 300 * Physics.MKM) {
                scaleWidth = 50;
            } else if (maxDistance < 1000 * Physics.MKM) {
                scaleWidth = 100;
            } else {
                scaleWidth = 500;
            }

            label = scaleWidth + " Mkm";
        }
        System.out.println(scale);
        System.out.println(scaleWidth);

        int left = width / 2;
        int right = width / 2 + (int) getScaledPixels(scaleWidth * Physics.MKM);
        image.line(left, width - 10, right, width - 10, "#000000", 2);
        image.line(left, width - 10, left, width - 20, "#000000", 2);
        image.line(right, width - 10, right, width - 20, "#000000", 2);

        int pos = (left + right) / 2 - image.getTextWidth(label, 0, 12) / 2;
        image.text(pos, width - 20, label, 0, 12, "#000000");
    }

    private void drawBelt(Star star, int cx, int cy, Planet planet) {
        switch (planet.getType()) {
            case AsteroidBelt:
            case MetallicBelt:
            case VulcanianBelt:
            case IceBelt:
                drawAsteroidBelt(star, cx, cy, planet);
                break;
            case DustDisc:
                drawDustDisc(star, cx, cy, planet);
                break;
            case PlanetesimalDisc:
                drawPlanetesimalDisc(star, cx, cy, planet);
                break;
            default:
                // Invalid or unsupported type.
                break;
        }
    }

    /**
     * Draws a belt of planetesimals, displaying them as multiple dots on the map in a
     * ring around their origin. Location of each dot is random, but seeded so the belt
     * should be drawn identically each time.
     *
     * @param star      Parent star.
     * @param cx        X Origin of star, in pixels.
     * @param cy        Y origin of star, in pixels.
     * @param planet    Planet to draw.
     */
    private void drawAsteroidBelt(Star star, int cx, int cy, Planet planet) {
        long    distance = planet.getDistance();
        Random  random = new Random(star.getId() * distance);

        int     maxNumber = (int) ((distance * 10) / Physics.MKM);
        int     number = 150 + random.nextInt(maxNumber) + random.nextInt(maxNumber);
        int     sizeModifier = 0;
        if (distance < 10 * Physics.MKM) {
            sizeModifier = 99;
        } else if (distance < 30 * Physics.MKM) {
            sizeModifier = 50;
        } else if (distance < 100 * Physics.MKM) {
            sizeModifier = 9;
        }
        logger.info("drawAsteroidBelt " + number);

        for (; number > 0; number --) {
            double  d = planet.getDistance() + random.nextFloat() * planet.getRadius() - random.nextFloat() * planet.getRadius();
            double  angle = random.nextDouble() * 360.0 + getAngleOffset(star, d);
//            int     x = cx + getScaledPixels((int) (Math.cos(Math.toRadians(angle)) * d));
  //          int     y = cy + getScaledPixels((int) (Math.sin(Math.toRadians(angle)) * d));
            int     x = cx + (int) (Math.cos(Math.toRadians(angle)) * getScaledPixels(d));
            int     y = cy + (int) (Math.sin(Math.toRadians(angle)) * getScaledPixels(d));

            // Determine size class of this asteroid, based on random distribution.
            int     size = random.nextInt(1000) + sizeModifier;
            if (size == 0) {
                // Giant asteroid, > 100km radius.
                image.rectangleFill(x-1, y-1, 3, 3, "#000000");
            } else if (size < 10) {
                // Large asteroid, > 30km radius.
                image.rectangleFill(x, y, 2, 2, "#000000");
            } else if (size < 100) {
                // Medium asteroid, > 10km radius.
                image.rectangleFill(x, y, 2, 2, SimpleImage.getDarker(planet.getType().getColour()));
            } else {
                // Small asteroid, > 3km radius.
                image.rectangleFill(x, y, 2, 2, planet.getType().getColour());
            }
        }
    }

    private void drawPlanetesimalDisc(Star star, int cx, int cy, Planet planet) {
        long    distance = planet.getDistance();
        Random  random = new Random(star.getId() * distance);

        int     maxNumber = (int) ((distance * 5) / Physics.MKM);
        int     number = (int)((distance * 10) / Physics.MKM) + random.nextInt(maxNumber) + random.nextInt(maxNumber);
        logger.info("drawPlanetesimalDisc " + number);

        String darker = image.getDarker(planet.getType().getColour());
        for (; number > 0; number --) {
            long    d = planet.getDistance() + random.nextInt(planet.getRadius()) - random.nextInt(planet.getRadius());
            double  angle = random.nextDouble() * 360.0 + getAngleOffset(star, d);
            int     x = cx + (int) getScaledPixels((int) (Math.cos(Math.toRadians(angle)) * d));
            int     y = cy + (int) getScaledPixels((int) (Math.sin(Math.toRadians(angle)) * d));

            image.rectangleFill(x, y, 1, 1, darker);
        }
    }

    private void drawDustDisc(Star star, int cx, int cy, Planet planet) {
        DustDiscMapper mapper = new DustDiscMapper(planet);

        mapper.drawOrbit(image, cx, cy, getScale());
    }

    /**
     * Draw the planet, with its orbit and any labels.
     *
     * @param star          Star to centre it around.
     * @param cx            X coordinate of centre of orbit (pixels).
     * @param cy            Y coordinate of centre of orbit (pixels).
     * @param planet        Planet to draw.
     * @param distance      Distance from star (pixels).
     */
    private void drawPlanet(Star star, int cx, int cy, Planet planet, int distance) {
        logger.info("drawPlanet");

        double angle = getAngleOffset(star, planet.getDistance());

        int     x = cx + (int) (Math.cos(Math.toRadians(angle)) * distance);
        int     y = cy + (int) (Math.sin(Math.toRadians(angle)) * distance);

        image.circleOutline(cx, cy, distance, "#777777");
        int radius = (int)(Math.sqrt(planet.getRadius()) * objectScale * 0.001);
        if (radius < 4) {
            radius = 4;
        }
        image.circle(x, y, radius, planet.getType().getColour());

        if (drawLabels && distance > 40) {
            drawPlanetLabel(x, y, angle, planet);
        }
    }

    private void drawPlanetLabel(int x, int y, double angle, Planet planet) {
        String text = planet.getName().replaceAll(".* ", "");
        text += " / " + planet.getType();
        String dtext = planet.getDistance() + "Mkm";
        int fontSize = 12;
        int textWidth = image.getTextWidth(text, 0, fontSize);
        int padding = 10;

        if (angle < 90) {
            // Bottom right.
            image.text(x + padding, y + padding, text, 0, fontSize, "#000000");
            image.text(x + padding, y + (int)(padding * 2.5), dtext, 0, fontSize, "#000000");
        } else if (angle < 180) {
            // Bottom left.
            image.text(x - padding - textWidth, y + padding, text, 0, fontSize, "#000000");
            image.text(x - padding - textWidth, y + (int)(padding * 2.5), dtext, 0, fontSize, "#000000");
        } else if (angle < 270) {
            // Top left.
            image.text(x - padding - textWidth, y - padding, text, 0, fontSize, "#000000");
            image.text(x - padding - textWidth, y + (int)(padding * 0.5), dtext, 0, fontSize, "#000000");
        } else {
            // Top right.
            image.text(x + padding, y - padding, text, 0, fontSize, "#000000");
            image.text(x + padding, y + (int)(padding * 0.5), dtext, 0, fontSize, "#000000");
        }

    }

    private void drawStar(Star star, int cx, int cy) {
        // Draw regions.
        /*
        if (drawZones) {
            image.circle(cx, cy, (int) getScaledPixels(star.getSnowLineDistance()), "#FFFFDD");
            image.circle(cx, cy, (int) getScaledPixels(star.getOuterWarmDistance()), "#DDFFDD");
            image.circle(cx, cy, (int) getScaledPixels(star.getInnerWarmDistance()), "#FFDDDD");
            image.circle(cx, cy, (int) getScaledPixels(star.getMinimumDistance()), "#FFFFFF");
        }
        */

        // Draw the star itself.
        image.circle(cx, cy, (int)(star.getRadius() / scale),
                star.getSpectralType().getRGBColour());
        image.circleOutline(cx, cy, (int)(star.getRadius() / scale), "#000000");

        for (Planet planet : planets) {
            if (planet.getParentId() != star.getId()) {
                continue;
            }

            int distance = (int) getScaledPixels(planet.getDistance());
            logger.info(String.format("Planet [%s] at [%d] KM", planet.getName(), distance));

            switch (planet.getType().getGroup()) {
                case Belt:
                    drawBelt(star, cx, cy, planet);
                    break;
                default:
                    drawPlanet(star, cx, cy, planet, distance);
                    break;
            }
        }
    }
}

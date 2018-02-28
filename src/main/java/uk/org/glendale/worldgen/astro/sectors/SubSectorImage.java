/*
 * SectorImage.java
 *
 * Copyright (C) 2007 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.sectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.NoSuchStarSystemException;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.Zone;
import uk.org.glendale.worldgen.exceptions.NoSuchObjectException;
import uk.org.glendale.worldgen.web.Server;

import java.awt.Font;
import java.io.File;
import java.util.EnumSet;
import java.util.List;


/**
 * Create a bitmap image of a subsector map. Designed for display on a webpage,
 * to allow a scrollable view. Also designed to have varying levels of detail.
 * The maps are supposed to stitch together, so edges can appear truncated since
 * these hexes will be half drawn on the neighbouring map.
 *
 * @author Samuel Penn
 *
 */
public class SubSectorImage {
    private static final Logger logger = LoggerFactory.getLogger(SubSectorImage.class);

    private final WorldGen worldgen;

    private Sector		sector;
    private int			ssx			= 0;
    private int			ssy			= 0;

    private SimpleImage image		= null;
    private int			scale		= 32;
    private int			verbosity	= 0;
    private boolean		standalone	= false;

    // Some simple constants.
    static final double	COS30		= Math.sqrt(3.0) / 2.0;
    static final double	COS60		= 0.5;
    static final double	SIN60		= Math.sqrt(3.0) / 2.0;
    static final double	SIN30		= 0.5;
    static final double	ROOT_TWO	= Math.sqrt(2.0);

    enum HexSides {
        Top, TopRight, BottomRight, Bottom, BottomLeft, TopLeft
    };

    static String	SYMBOL_BASE	= "images/symbols/";

    private int		leftMargin	= 0;
    private int		topMargin	= 0;

    public static void setSymbolBase(String base) {
        System.out.println("setSymbolBase: [" + base + "]");
        SYMBOL_BASE = base;
    }

    /**
     * Create a new SubSectorImage for the given sector and sub sector
     * coordinates. Coordinates are from top-left corner of the sector.
     *
     * @param sector
     *            The sector to draw subsector map for.
     * @param subSector
     *            SubSector within the Sector.
     */
    public SubSectorImage(WorldGen worldgen, Sector sector, SubSector subSector) {
        this.worldgen = worldgen;
        this.sector = sector;
        this.ssx = subSector.getX();
        this.ssy = subSector.getY();
    }

    /**
     * Set the scale of the maps to be generated. This is the width of each hex.
     * 64 gives a good sized hex, 48 is medium and 32 is considered small.
     *
     * @param scale
     *            Size of the map.
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }

    /**
     * Set whether this subsector map should be drawn as a standalone map. Maps
     * can be either tiled or standalone. A tiled map is designed to fit exactly
     * next to its neighbours, and will have half hexes drawn at the edges. A
     * standlone map only shows full hexes, and doesn't display systems which
     * belong to its neighbours.
     */
    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    public boolean isStandalone() {
        return standalone;
    }

    /**
     * Get actual coordinate of the hexagon specified by x and y index.
     *
     * @param x
     *            X index of hexagon.
     * @param y
     *            Y index of hexagon.
     * @return X coordinate of top left of hexagon.
     */
    public double getX(int x, int y) {
        return leftMargin + ((x - 1) * (scale * 1.5));
    }

    /**
     * Get actual coordinate of the hexagon specified by x and y index.
     *
     * @param x
     *            X index of hexagon.
     * @param y
     *            Y index of hexagon.
     * @return Y coordinate of top left of hexagon.
     */
    public double getY(final int x, final int y) {
        return (topMargin + (SIN60 * 2 * scale) + (Math.abs(x - 1) % 2)
                * (scale * SIN60) + (y - 1) * (SIN60 * 2 * scale));
    }

    /**
     * Angles: x = x * cos(a) - y * sin(a) y = x * sin(a) + y * cos(a)
     */
    private void plotHexagon(final double x, final double y, EnumSet<HexSides> flags) {
        double topLeft_x, top_y, topRight_x, right_x, middle_y, bottom_y, left_x;
        double size = scale;

        // Work out basic positions.
        topLeft_x = x;
        top_y = y;
        topRight_x = x + size;
        right_x = topRight_x + (size * COS60 - 0 * SIN60);
        middle_y = y - (size * SIN60 + 0 * COS60);
        bottom_y = y - 2 * (size * SIN60 + 0 * COS60);
        left_x = x - (size * COS60 - 0 * SIN60);

        String normal = "#000000";
        String emphasis = "#FF0000";
        float width = (float) (scale / 64.0);
        float emWidth = (float) (scale / 16.0);

        // Now draw the hexagon. The hexagon is actually upside down, so the
        // Top/Bottom flags aren't quite what you'd expect them to be.
        image.line(topLeft_x, top_y, topRight_x, top_y,
                (flags.contains(HexSides.Bottom) ? emphasis : normal),
                (flags.contains(HexSides.Bottom) ? emWidth : width));
        image.line(topRight_x, top_y, right_x, middle_y,
                (flags.contains(HexSides.BottomRight) ? emphasis : normal),
                flags.contains(HexSides.BottomRight) ? emWidth : width);
        image.line(right_x, middle_y, topRight_x, bottom_y,
                (flags.contains(HexSides.TopRight) ? emphasis : normal),
                flags.contains(HexSides.TopRight) ? emWidth : width);
        image.line(topRight_x, bottom_y, topLeft_x, bottom_y,
                (flags.contains(HexSides.Top) ? emphasis : normal),
                flags.contains(HexSides.Top) ? emWidth : width);
        image.line(topLeft_x, bottom_y, left_x, middle_y,
                (flags.contains(HexSides.TopLeft) ? emphasis : normal),
                flags.contains(HexSides.TopLeft) ? emWidth : width);
        image.line(left_x, middle_y, topLeft_x, top_y,
                (flags.contains(HexSides.BottomLeft) ? emphasis : normal),
                flags.contains(HexSides.BottomLeft) ? emWidth : width);
    }

    private void plotText(double x, double y, String text, int style, int size,
                          String colour) {
        image.text((int) x, (int) y, text, style, size, colour);
    }

    private void plotText(double x, double y, int hx, int hy) {
        String text = "";

        // if (hx == 8) return;

        if (hx < 1) {
            text += 32 + hx;
            return;
        } else if (hx < 10) {
            text += "0" + hx;
        } else {
            text += hx;
        }

        if (hy < 1) {
            text += 40 + hy;
            return;
        } else if (hy < 10) {
            text += "0" + hy;
        } else {
            text += hy;
        }

        plotText(x + scale * 0.1, y - scale * 1.4, text, 0,
                (int) (scale * 0.3), "#000000");
    }

    /**
     * Draw a subsector map onto an image buffer. The basic hexagons are drawn,
     * together with all the star systems and related data.
     *
     * @throws NoSuchObjectException    If something cannot be found.
     */
    private void drawBaseMap() throws NoSuchObjectException {
        int hexWidth = (int) (scale + 1.0 * scale * COS60);
        int hexHeight = (int) (2.0 * scale * SIN60);
        int mapWidth = hexWidth * 8;
        int mapHeight = hexHeight * 10 + (int) (scale * SIN30 * 0.25);

        if (standalone) {
            mapHeight += hexHeight * 0.55;
            mapWidth += hexWidth * 0.4;
        }

        // image = new SimpleImage(hexWidth * 8 + (int)(scale * COS60),
        // hexHeight * 10, "FFFFFF");
        image = new SimpleImage(mapWidth, mapHeight, "FFFFFF");

        logger.info("Drawing map for [" + sector.getName() + "]");

        leftMargin = (int) (scale * 0.5);
        topMargin = 0;

        int baseX = ssx * 8;
        int baseY = ssy * 10;

        int startX = -1, startY = -1;
        int endX = 9, endY = 12;

        if (standalone) {
            startX = startY = 1;
            endX = 9;
            endY = 11;
        }

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int sx = baseX + x;
                int sy = baseY + y;
                EnumSet<HexSides> flags = EnumSet.noneOf(HexSides.class);

                if (sx == 1) {
                    flags.add(HexSides.TopLeft);
                    flags.add(HexSides.BottomLeft);
                }
                if (sy == 1) {
                    flags.add(HexSides.Top);
                    if (sx % 2 != 0) {
                        flags.add(HexSides.TopLeft);
                        flags.add(HexSides.TopRight);
                    }
                }
                if (sx == 32) {
                    flags.add(HexSides.TopRight);
                    flags.add(HexSides.BottomRight);
                }
                if (sy > 39) {
                    flags.add(HexSides.Bottom);
                    if (sx % 2 == 0) {
                        flags.add(HexSides.BottomLeft);
                        flags.add(HexSides.BottomRight);
                    }
                }

                plotHexagon(getX(x, y), getY(x, y), flags);
                plotText(getX(x, y), getY(x, y), baseX + x, baseY + y);

                Sector hexSector = sector;
                try {
                    if (sx < 1 && sy < 1) {
                        // Top left
                        hexSector = worldgen.getSectorFactory().getSector(sector.getX() - 1, sector.getY() - 1);
                        sx += 32;
                        sy += 40;
                    } else if (sx < 1) {
                        // Left
                        hexSector = worldgen.getSectorFactory().getSector(sector.getX() - 1, sector.getY());
                        sx += 32;
                    } else if (sy < 1) {
                        // Top
                        hexSector = worldgen.getSectorFactory().getSector(sector.getX(), sector.getY() - 1);
                        sy += 40;
                    }
                    StarSystem system = worldgen.getStarSystemFactory().getStarSystem(hexSector, sx, sy);
                    drawStarSystem(system, x, y);
                } catch (NoSuchSectorException e) {
                    // There isn't a neighbouring sector, so skip.
                    continue;
                } catch (NoSuchStarSystemException e) {
                    // There isn't a star system here.
                    continue;
                }
            }
        }
    }

    private void drawStar(SimpleImage image, int x, int y, StarSystem system, Star star) {
        int radius = (int)(star.getLuminosity().getSize() * scale * 0.125);
        String colour = star.getSpectralType().getRGBColour();

        image.circle(x, y, radius + 2, "#000000");
        image.circle(x, y, radius, colour);

        drawPlanets(image, system, star, (int)(x + scale * 0.25), y);
    }

    private void drawPlanets(SimpleImage image, StarSystem system, Star star, int cx, int cy) {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        List<Planet> planets = planetFactory.getPlanets(system);

        if (planets.size() > 0) {
            logger.info(String.format("System [%s] has [%d] planets", system.getName(), planets.size()));

            int x = (int) cx;
            int y = (int) cy;

            for (Planet planet : planets) {
                if (planet.getParentId() != star.getId()) {
                    continue;
                }
                logger.debug(String.format("Drawing planet [%s]", planet.getName()));
                int radius = (int)(planet.getType().getRadius() * scale * 0.02);
                if (radius < 2) {
                    radius = 2;
                }
                switch (planet.getType().getGroup()) {
                    case Belt:
                        radius /= 2;
                        image.line(x, y + radius, x, y - radius, "#777777", 2);
                        break;
                    default:
                        image.circle(x, y, radius, "#000000");
                        break;
                }
                x += (int) (scale * 0.10);
            }
        }

    }

    private void drawStarSystem(StarSystem system, int x, int y) {

        if (system == null) {
            return;
        }

        int cx = (int) (getX(x, y) + scale * 0.5); // X coordinate of centre.
        int cy = (int) (getY(x, y) - (scale * SIN60)); // Y coordinate of centre.

        String colour = "#000000";
        if (system.getZone() == Zone.RED) {
            colour = "#FF0000";
        } else if (system.getZone() == Zone.AMBER) {
            colour = "#FF8000";
        }

        List<Star> stars = system.getStars();
        Star star = null;
        switch (stars.size()) {
            case 1:
                star = stars.get(0);
                drawStar(image, cx, cy - (int) (scale * 0.15), system, star);
                break;
            case 2:
                star = stars.get(0);
                drawStar( image, cx, cy - (int) (scale * 0.15), system, star);
                star = (Star) (stars.get(1));
                drawStar( image, cx, cy + (int) (scale * 0.05), system, star);
                break;
            case 3:
                star = stars.get(0);
                drawStar( image, cx, cy - (int) (scale * 0.15), system, star);
                star = (Star) (stars.get(1));
                drawStar(image, cx, cy + (int) (scale * 0.05), system, star);
                star = (Star) (stars.get(2));
                drawStar( image, cx, cy + (int) (scale * 0.25), system, star);
                break;
        }

        double fontSize = scale * 0.2;
        int len = 0;

        if (scale < 40) {
            fontSize += 3;
        }

        try {
            len = image.getTextWidth(system.getName(), Font.BOLD,
                    (int) fontSize);
        } catch (Throwable e) {
            logger.error("Cannot work out text width", e);
        }

        double tx = getX(x, y) + scale * 0.5 - (len / 2);
        double ty = getY(x, y) - scale * 0.2;
        int weight = Font.BOLD;

        if (scale < 40) {
            weight = Font.PLAIN;
            tx = getX(x, y) + scale * 0.5 - (len / 2);
            ty = getY(x, y) - scale * 0.4;
        }

        plotText(tx, ty, system.getName(), weight, (int) fontSize,
                colour);

    }

    public SimpleImage getImage() throws NoSuchObjectException {
        drawBaseMap();
        return image;
    }

    public static void main(String[] args) throws Exception {

        try (WorldGen worldGen = Server.getWorldGen()) {
            Sector sector = worldGen.getSectorFactory().getSector(0, 0);

            SubSectorImage image = new SubSectorImage(worldGen, sector, SubSector.A);
            image.setStandalone(true);
            image.setScale(64);

            image.getImage().save(new File("/home/sam/image.jpg"));
        } catch (Exception e) {
            logger.error("Something has gone wrong", e);
        }

        /*
        Sector sector = new Sector(id);
        for (SubSector ss : SubSector.values()) {
            SubSectorImage sub = new SubSectorImage(id, ss);
            sub.setScale(64);
            sub.setStandalone(true);
            sub.getImage().save(
                    new File("/home/sam/ss_" + ss.name()
                            + ".jpg"));

            FileWriter writer = new FileWriter(
                    "/home/sam/tmp/maps/subsectors/ss_" + ss.name() + ".html");

            String title = sector.getName() + " / " + ss.name();
            writer.write("<html><head><title>" + title
                    + "</title></head><body>");
            writer.write("<p style=\"margin:0pt\">" + title + "</p>");
            writer.write("<img src=\"ss_" + ss.name() + ".jpg\"/>");

            writer.write("</body></html>");
            writer.close();
        }
        */
        System.exit(0);

    }

}

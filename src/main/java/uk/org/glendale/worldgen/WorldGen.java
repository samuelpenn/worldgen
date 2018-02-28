/**
 * WorldGen.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.worldgen.astro.Universe;
import uk.org.glendale.worldgen.astro.commodities.CommodityFactory;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.sectors.NoSuchSectorException;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.sectors.SectorFactory;
import uk.org.glendale.worldgen.astro.stars.StarFactory;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemFactory;
import uk.org.glendale.worldgen.text.NameGenerator;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * High level application class that does session and transaction management, providing
 * access to Factory classes for various object types.
 */
public class WorldGen implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(WorldGen.class);

    private EntityManager       session;
    private EntityTransaction   transaction;
    private Config              configuration;

    private static int          count = 0;

    /**
     * Session constructor. Automatically starts a transaction on the session.
     *
     * @param session   Session.
     */
    public WorldGen(EntityManager session, Config configuration) {
        logger.info(String.format("WorldGen: Creating new database session [%d]", ++count));
        if (session == null || !session.isOpen()) {
            throw new IllegalArgumentException("Cannot create WorldGen object with a non-open session.");
        }
        this.configuration = configuration;
        this.session = session;
        session.setFlushMode(FlushModeType.COMMIT);
        transaction = session.getTransaction();
        transaction.begin();
    }

    /**
     * Tries to commit the session transaction. If commit is not possible (because there
     * has been an error earlier in the transaction for example), then the transaction is
     * rolled back instead.
     *
     * This is called automatically when the object goes out of scope.
     */
    public void close() {
        if (session == null || transaction == null) {
            // Cannot close twice.
            return;
        }
        try {
            count--;
            if (transaction.getRollbackOnly()) {
                System.out.println("Transaction marked for rollback.");
                transaction.rollback();
            } else {
                transaction.commit();
            }
        } catch (PersistenceException e) {
            System.out.println(String.format("Cannot commit transaction (%s), rolling back.", e.getMessage()));
            transaction.rollback();
        }
        transaction = null;
        session = null;
    }

    public Config getConfig() {
        return configuration;
    }

    private void validate() {
        if (transaction == null || session == null || !session.isOpen()) {
            throw new IllegalStateException("Session or transaction is closed.");
        }
    }

    public SectorFactory getSectorFactory() {
        validate();
        return new SectorFactory(session);
    }

    public StarSystemFactory getStarSystemFactory() {
        validate();
        return new StarSystemFactory(session);
    }

    public StarFactory getStarFactory() {
        validate();
        return new StarFactory(session);
    }

    public PlanetFactory getPlanetFactory() {
        validate();
        return new PlanetFactory(this, session);
    }

    public CommodityFactory getCommodityFactory() {
        validate();
        return new CommodityFactory(session);
    }

    /**
     * Gets a text generator for random star system names.
     *
     * @return  Random star system name generator.
     */
    public NameGenerator getStarSystemNameGenerator() {
        return new NameGenerator("systems");
    }

    /**
     * Gets the current value of a fundamental constant. These are considered
     * immutable, and are always longs.
     *
     * @param name  Name of the constant.
     * @return      Value of the constant.
     */
    public Constant getConstant(String name) {
        validate();
        Constant constant = session.find(Constant.class, name);

        return constant;
    }

    public Constant getConstant(Constant.Name name) {
        return getConstant(name.getName());
    }

    /**
     * Gets metadata about the universe. There is only ever one universe.
     *
     * @return  Gets the current universe.
     */
    public Universe
    getUniverse() {
        validate();
        // The key value of the universe is always 1, since it is a singleton.
        Universe universe = session.find(Universe.class, 1);

        return universe;
    }

    public void setUniverse(final Universe universe) {
        validate();
        session.persist(universe);
    }

    public Date getCreatedDate() {
        validate();
        Query query = session.createQuery("SELECT U.createdDate FROM Universe U");
        Date creation = (Date) query.getSingleResult();
        return creation;
    }

    public Date getLastEvent() {
        validate();
        Query query = session.createQuery("SELECT U.lastDate FROM Universe U");
        Date last = (Date) query.getSingleResult();
        return last;
    }

    /**
     * Gets the current time of the universe. This is the number of seconds that have
     * passed in the simulation since it was created.
     *
     * @return  Seconds since the universe was created.
     */
    public long getCurrentTime() {
        validate();
        Query query = session.createQuery("SELECT U.simTime FROM Universe U");
        Long simTime = (Long) query.getSingleResult();
        return simTime.longValue();
    }

    /**
     * Sets the current time of the universe. This is the number of seconds that have
     * passed in the simulation since it was created. Time cannot be set backwards, so
     * the new time must be at least as big as the old time.
     *
     * Setting the current time also sets the real world last access time.
     *
     * @param simTime   Seconds since the universe was created.
     */
    public synchronized void setCurrentTime(long simTime) {
        validate();
        Universe universe = session.find(Universe.class, 1);
        logger.info("Universe " + universe.getName() + " - " + universe.getCreatedDate() + " to " + simTime);
        universe.setCurrentTime(simTime);
        session.persist(universe);
    }

    private SimpleImage galaxyMap = null;

    public List<String> getImages() {
        Query query = session.createQuery("SELECT I.name FROM ImageBlob I");

        try {
            List<String> results = (List<String>) query.getResultList();

            return results;

        } catch (NoResultException e) {
            logger.error(String.format("No images found."), e);
            return null;
        }
    }

    public SimpleImage getImage(String name) {
        Query query = session.createQuery("FROM ImageBlob I WHERE name=:name");
        query.setParameter("name", name);

        try {
            ImageBlob blob = (ImageBlob) query.getSingleResult();

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(blob.getData());
                SimpleImage image = new SimpleImage(ImageIO.read(bais));
                return image;
            } catch (IOException e) {
                logger.error(String.format("Unable to create image from data (%s)", e.getMessage()));
            }
        } catch (NoResultException e) {
            logger.error(String.format("No image [%s] found.", name), e);
            return null;
        }

        return null;

    }

    /**
     * Gets the density map for the galaxy with no modifications. This is a basic image
     * showing the density distribution as a greyscale map. 1px = 1 parsec.
     *
     * @return  Image of the galaxy.
     */
    public SimpleImage getGalaxyMap() {
        if (galaxyMap != null) {
            return galaxyMap;
        }
        Query query = session.createQuery("FROM ImageBlob I WHERE name='galaxy'");

        ImageBlob blob = (ImageBlob) query.getSingleResult();
        if (blob == null) {
            logger.warn(String.format("No image 'galaxy' found."));
            return null;
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(blob.getData());
            galaxyMap = new SimpleImage(ImageIO.read(bais));
            return galaxyMap;
        } catch (IOException e) {
            logger.warn(String.format("Unable to create image from data (%s)", e.getMessage()));
        }

        return null;
    }

    public SimpleImage getGalaxyMapGrid() {
        SimpleImage image = getGalaxyMap();

        try {
            logger.debug(String.format("Galaxy Map image width [%d] height [%d]",
                    image.getWidth(), image.getHeight()));

            Universe u = getUniverse();
            int w = image.getWidth();
            int h = image.getHeight();
            int sw = w / Sector.WIDTH;
            int sh = h / Sector.HEIGHT;

            int ox = sw / 2;
            int oy = sh / 2;

            image = image.resize(image.getWidth()*4, image.getHeight()*4);

            SectorFactory sectorFactory = getSectorFactory();
            StarSystemFactory systemFactory = getStarSystemFactory();

            for (int y = u.getMinY(); y <= u.getMaxY(); y++) {
                for (int x = u.getMinX(); x <= u.getMaxX(); x++) {
                    try {
                        Sector sector = sectorFactory.getSector(x, y);

                        // We have sector data, so blank out the area ready for showing the systems.
                        int sox = (ox + x) * Sector.WIDTH * 4;
                        int soy = (oy + y) * Sector.HEIGHT * 4;
                        image.rectangleFill(sox, soy,Sector.WIDTH * 4, Sector.HEIGHT * 4, "#000000");
                        image.rectangle(sox, soy,Sector.WIDTH * 4, Sector.HEIGHT * 4, "#ffffff");

                        //image.text(sox + 12, soy + 120, ""+SectorFactory.getSectorNumber(x, y), 0, 48,"#777777");

                        List<StarSystem> systems = systemFactory.getStarSystems(sector);
                        for (StarSystem system : systems) {
                            int sysX = system.getX() - 1;
                            int sysY = system.getY() - 1;

                            image.rectangleFill(sox + sysX * 4 + 1, soy + sysY * 4 + (sysX%2 * 2) + 1,
                                    2, 2, "#ffffff");

                        }

                    } catch (NoSuchSectorException e) {
                        // No sector is defined, so just draw the grid outline.
                        image.rectangle((ox + x) * Sector.WIDTH * 4, (oy + y) * Sector.HEIGHT * 4,
                                Sector.WIDTH * 4, Sector.HEIGHT * 4, "#ffffff");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Failed to create image", e);
            return null;
        }

        return image;
    }

    /**
     * Gets the background image for a given sector. This may be from the background image, or an actual map
     * if the sector has been defined. Each thumbnail is 128px x 160px in size. The background image is
     * centred on sector 0,0.
     *
     * @param x     X coordinate of sector to get image for.
     * @param y     Y coordinate of sector to get image for.
     * @return      Image 128px x 160px in size.
     */
    public SimpleImage getSectorBackground(final int x, final int y) {
        SimpleImage background = getGalaxyMap();

        // Get width and height of background image in pixels.
        int         wpx = background.getWidth();
        int         hpx = background.getHeight();

        // Get width and height of background image in sectors.
        int         ws = wpx / Sector.WIDTH;
        int         hs = hpx / Sector.HEIGHT;

        int         ox = ws / 2;
        int         oy = hs / 2;

        int         sx = (ox + x) * Sector.WIDTH;
        int         sy = (oy + y) * Sector.HEIGHT;
        SimpleImage thumbnail = background.crop(sx, sy, Sector.WIDTH, Sector.HEIGHT);

        return thumbnail.resize(Sector.WIDTH * 4, Sector.HEIGHT * 4);
    }

    public SimpleImage getSectorThumbnail(Sector sector) {
        StarSystemFactory systemFactory = getStarSystemFactory();
        List<StarSystem> systems = systemFactory.getStarSystems(sector);

        SimpleImage image = getSectorBackground(sector.getX(), sector.getY());

        for (StarSystem system : systems) {
            int sysX = system.getX() - 1;
            int sysY = system.getY() - 1;

            image.rectangleFill(sysX * 4 + 1, sysY * 4 + (sysX%2 * 2) + 1,
                    2, 2, "#ffffff");
        }

        return image;
    }

    public void setGalaxyMap(SimpleImage image) throws IOException {
        ByteArrayOutputStream stream = image.save();

        Query query = session.createQuery("FROM ImageBlob I WHERE name='galaxy'");
        ImageBlob blob;

        try {
            blob = (ImageBlob) query.getSingleResult();
            blob.setData(stream.toByteArray());
        } catch (NoResultException e) {
            blob = new ImageBlob("galaxy", stream.toByteArray());
        }

        session.persist(blob);
        galaxyMap = null;
    }
}

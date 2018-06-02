/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.Universe;
import uk.org.glendale.worldgen.astro.sectors.NoSuchSectorException;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.sectors.SectorFactory;
import uk.org.glendale.worldgen.astro.sectors.SectorGenerator;
import uk.org.glendale.worldgen.astro.systems.NoSuchStarSystemException;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemFactory;
import uk.org.glendale.worldgen.astro.systems.StarSystemSelector;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;
import uk.org.glendale.worldgen.web.Server;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Command line based interface to WorldGen. Provides a number of simple commands that allow
 * the universe to be manipulated directly from the command line. Mostly designed for testing
 * purposes, but can be used to manipulate real data.
 */
public class CommandLine extends Main {
    private static final Logger logger = LoggerFactory.getLogger(CommandLine.class);

    private CommandLine() {
    }

    private void usage() {
        System.out.println("<command> <options>");
        System.out.println("  status   - Get status on configured universe.");
        System.out.println("  server   - Start a web application server running.");
        System.out.println("  sectors  - List all known sectors.");
        System.out.println("  sector   - Create one or more new sectors.");
        System.out.println("             <x,y> <name> ...");
        System.out.println("  system   - Create one or more new systems.");
        System.out.println("             <sector> <xxyy> [<name>]");
        System.out.println("  populate - Populate a sector.");
        System.out.println("             <sector>");
    }

    private void execute(String[] args) {

        if (args.length == 0) {
            usage();
            return;
        }

        String cmd = args[0];
        String[] options;

        options = Arrays.copyOfRange(args, 1, args.length);
        if (args.length > 1) {
            options = Arrays.copyOfRange(args, 1, args.length);
        } else {
            options = new String[]{};
        }

        if (cmd.equals("status")) {
            commandStatus(options);
        } else if (cmd.equals("server")) {
            commandServer(options);
        } else if (cmd.equals("sectors")) {
            commandListSectors(options);
        } else if (cmd.equals("sector")) {
            commandCreateSector(options);
        } else if (cmd.equals("system")) {
            commandCreateSystem(options);
        } else if (cmd.equals("populate")) {
            commandPopulateSector(options);
        }

    }

    private void print(String key, String value) {
        System.out.println(String.format("%s: %s", key, value));
    }

    private void print(String key, int value) {
        System.out.println(String.format("%s: %d", key, value));
    }

    private void print(String key, long value) {
        System.out.println(String.format("%s: %d", key, value));
    }

    private void print(String key, Date value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        print(key, format.format(value));
    }

    /**
     * Prints out the current status of the universe. This includes current time and some configuration
     * values, as well as some useful statistics.
     *
     * @param options  Options, ignored.
     */
    private void commandStatus(String[] options) {
        try (WorldGen wg = getWorldGen()) {
            Universe u = wg.getUniverse();

            print("Universe", u.getName());
            print("Created Date", u.getCreatedDate());
            print("Last Date", u.getLastDate());
            print("Current Time", u.getCurrentDateTime());

            print("Number of Sectors", wg.getSectorFactory().getSectors().size());
            print("Number of Systems", wg.getStarSystemFactory().getStarSystemCount());
            print("Number of Planets", wg.getPlanetFactory().getPlanetCount());
        }
    }

    /**
     * Starts the web server running.
     *
     * @param options   Options, ignored.
     */
    private void commandServer(String[] options) {
        Server server = new Server();

        server.startServer();
    }

    private void commandListSectors(String[] options) {
        try (WorldGen wg = getWorldGen()) {
            List<Sector> sectors = wg.getSectorFactory().getSectors();

            for (Sector sector : sectors) {
                System.out.println(String.format("%d %d,%d %s",
                        sector.getId(), sector.getX(), sector.getY(), sector.getName()));
            }
        }
    }

    /**
     * Creates an empty sector. Options contains the list of arguments, being the X and Y coordinate of the
     * sector, followed by an optional name. If no name is given, a name is determined based on the location
     * of the sector.
     *
     * @param options   X, Y, [Name]
     */
    private void commandCreateSector(String[] options) {
        if (options.length < 1) {
            System.out.println("Usage: sector <x,y> [<name>] ...");
            return;
        }
        try (WorldGen wg = Main.getWorldGen()) {
            SectorFactory factory = wg.getSectorFactory();
            while (options.length > 0) {
                String coords = options[0];
                if (!SectorFactory.isCoord(coords)) {
                    System.out.println(String.format("Expected coordinate pair, instead got [%s]", coords));
                    return;
                }

                int x = SectorFactory.getXCoord(coords);
                int y = SectorFactory.getYCoord(coords);

                String name;

                if (options.length > 1 && !SectorFactory.isCoord(options[1])) {
                    name = options[1];
                    options = Arrays.copyOfRange(options, 2, options.length);
                } else {
                    name = "Sector " + SectorFactory.getSectorNumber(x, y);
                    options = Arrays.copyOfRange(options, 1, options.length);
                }
                if (factory.hasSector(x, y)) {
                    continue;
                }

                Sector sector = factory.createSector(name, x, y);

                System.out.println(String.format("Created sector [%d] [%s]", sector.getId(), sector.getName()));
            }
        } catch (DuplicateObjectException e) {
            e.printStackTrace();
        } finally {
            logger.debug("Finished.");
        }
    }

    /**
     * Searches for an empty parsec within the sector. If one is found, returns x and y coordinates
     * in an array. If not found, returns null. Looks for a random location first, and if that fails
     * starts searching from top left to bottom right.
     *
     * @param sector    Sector to search in.
     * @param factory   Factory for finding systems.
     * @return          Array of { x, y } if empty parsec found, otherwise null.
     */
    private int[] findEmptyParsec(Sector sector, StarSystemFactory factory) {
        int y = Die.die(40);
        int x = Die.die(32);
        try {
            factory.getStarSystem(sector, x, y);
        } catch (NoSuchStarSystemException e) {
            // This is an empty parsec, so return x and y values in array.
            return new int[] { x, y };
        }

        for (y = 1; y <= 40; y++) {
            for (x = 1; x < 32; x++) {
                try {
                    factory.getStarSystem(sector, x, y);
                } catch (NoSuchStarSystemException e) {
                    // This is an empty parsec, so return x and y values in array.
                    return new int[] { x, y };
                }
            }
        }
        return null;
    }

    private void commandCreateSystem(String[] options) {
        try (WorldGen wg = Main.getWorldGen()) {
            SectorFactory sectorFactory = wg.getSectorFactory();
            StarSystemFactory factory = wg.getStarSystemFactory();

            String sectorId = options[0];
            Sector sector = sectorFactory.getSectorByIdentifier(sectorId);

            String  coord = options[1];
            int     x, y;
            if (coord.startsWith("x")) {
                int[] coords = findEmptyParsec(sector, factory);
                if (coords == null || coords.length != 2) {
                    throw new IllegalStateException("Unable to locate an empty hex within this sector.");
                }
                x = coords[0];
                y = coords[1];
            } else {
                x = StarSystemFactory.getXCoord(coord);
                y = StarSystemFactory.getYCoord(coord);
            }

            String name;

            if (options.length < 4) {
                if (options.length == 3) {
                    name = options[2];
                } else {
                    name = wg.getStarSystemNameGenerator().generateName();
                }
                StarSystemSelector selector = new StarSystemSelector(wg);
                selector.createRandomSystem(sector, name, x, y);
            } else {
                String generator = options[2];
                String type = options[3];

                if (options.length > 4) {
                    name = options[4];
                } else {
                    name = wg.getStarSystemNameGenerator().generateName();
                }
                StarSystemSelector selector = new StarSystemSelector(wg);
                StarSystem system = selector.createByGeneratorType(sector, name, x, y, generator, type);

                System.out.println(system);
            }

        } catch (NoSuchSectorException e) {
            System.out.println(e.getMessage());
            return;
        } catch (DuplicateObjectException e) {
            e.printStackTrace();
        }
    }

    private void commandPopulateSector(String[] options) {
        try (WorldGen wg = Main.getWorldGen()) {
            SectorFactory sectorFactory = wg.getSectorFactory();

            String sectorId = options[0];
            Sector sector = sectorFactory.getSectorByIdentifier(sectorId);

            SectorGenerator generator = new SectorGenerator(wg);
            generator.createSectorByDensity(sector);

        } catch (NoSuchSectorException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    public static void main(String[] args) {
        logger.info("== WorldGen CommandLine ==");

        try {
            CommandLine cmd = new CommandLine();

            cmd.execute(args);
        } catch (Exception e) {
            System.out.println(String.format("Execution exception (%s)", e.getMessage()));
            e.printStackTrace();
        } finally {
            //System.exit(0);
        }

    }
}

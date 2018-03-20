/**
 * SectorAPI.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.sectors.NoSuchSectorException;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.sectors.SectorFactory;
import uk.org.glendale.worldgen.web.Controller;
import uk.org.glendale.worldgen.web.Server;

import static spark.Spark.get;

/**
 * Defines REST APIs for accessing Sector level information. This also provides image
 * maps and thumbnails for sectors.
 */
public class SectorAPI extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(SectorAPI.class);

    public void setupEndpoints() {
        logger.info("Setting up endpoints for SectorAPI");
        get("/api/sectors/", (request, response) -> getSectors(request, response), json());
        get("/api/sectors/:id", (request, response) -> getSector(request, response), json());

        get("/api/sector/:id/background", (request, response) -> getSectorBackground(request, response));
        get("/api/sector/:id/image", (request, response) -> getSectorImage(request, response));
    }

    /**
     * Gets a list of all the known sectors. Returned as a list.
     *
     * @param request       Request object.
     * @param response      Response object.
     * @return              Array of all the currently known sectors.
     */
    public Object getSectors(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {
            response.type("application/json");

            return worldGen.getSectorFactory().getSectors();
        } catch (Exception e) {
            logger.error(String.format("getSectors throws exception (%s)", e.getMessage()), e);
        }
        return null;
    }

    /**
     * Gets data on a single sector, either by its name or unique id.
     *
     * @param request       Request object, with param "id".
     * @param response      Response object.
     * @return              Data response.
     */
    public Object getSector(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {
            response.type("application/json");

            String id = request.params(":id");

            return worldGen.getSectorFactory().getSectorByIdentifier(id);
        } catch (NoSuchSectorException e) {
            response.status(404);
            return "No such sector";
        }
    }

    /**
     * Gets the background image for this sector.
     *
     * @param request   Request object.
     * @param response  Response object.
     * @return          JPEG image of the sector.
     */
    public Object getSectorBackground(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {
            response.type("image/png");

            String id = request.params(":id");
            int x;
            int y;

            if (SectorFactory.isCoord(id)) {
                x = SectorFactory.getXCoord(id);
                y = SectorFactory.getYCoord(id);
            } else {
                SectorFactory factory = worldGen.getSectorFactory();
                try {
                    Sector sector = factory.getSectorByIdentifier(id);
                    x = sector.getX();
                    y = sector.getY();
                } catch (NoSuchSectorException e) {
                    response.status(404);
                    return "No such sector";
                }
            }

            return worldGen.getSectorBackground(x, y).save().toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public Object getSectorImage(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {
            response.type("image/png");

            SimpleImage     image;
            SectorFactory   factory = worldGen.getSectorFactory();

            String id = request.params(":id");
            try {
                Sector sector = factory.getSectorByIdentifier(id);
                image = worldGen.getSectorThumbnail(sector);
            } catch (NoSuchSectorException e) {
                if (SectorFactory.isCoord(id)) {
                    int x = SectorFactory.getXCoord(id);
                    int y = SectorFactory.getYCoord(id);
                    image = worldGen.getSectorBackground(x, y);
                } else {
                    response.status(404);
                    return "No such sector";
                }
            }

            return image.save().toByteArray();
        } catch (Exception e) {
            logger.error("Unable to get image for sector", e);
        }
        return null;
    }
}

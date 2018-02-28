/**
 * MapAPI.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.sectors.SectorFactory;
import uk.org.glendale.worldgen.astro.sectors.SubSector;
import uk.org.glendale.worldgen.astro.sectors.SubSectorImage;
import uk.org.glendale.worldgen.web.Controller;
import uk.org.glendale.worldgen.web.Server;

import static spark.Spark.get;

/**
 * Controller API for handling sector maps.
 */
public class MapAPI extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(MapAPI.class);

    public void setupEndpoints() {
        logger.info("Setting up endpoints for MapAPI");
        get("/api/map/sector/:id/:sub", (request, response) -> getSectorMap(request, response));
        get("/api/map/system/:id", (request, response) -> getSystemMap(request, response));
    }

    /**
     * Gets a map for the named subsector.
     *
     * @param request       Request object.
     * @param response      Response object.
     * @return              Image for the sub-sector map.
     */
    public Object getSectorMap(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {
            SectorFactory factory = worldGen.getSectorFactory();
            String sectorId = request.params("id");

            Sector sector = factory.getSectorByIdentifier(sectorId);
            SubSector subSector = SubSector.valueOf(request.params("sub").toUpperCase());

            logger.info(String.format("getSectorMap: [%s / %s]", sectorId, subSector.toString()));

            SubSectorImage image = new SubSectorImage(worldGen, sector, subSector);

            String scaleParam = request.queryParamOrDefault("scale", "48");
            int scale = Integer.parseInt(scaleParam);
            image.setScale(scale);
            image.setStandalone(true);

            response.type("image/jpg");
            return image.getImage().save().toByteArray();
        } catch (NumberFormatException e) {
            response.status(400);
            response.body("Number format exception, Sector Id is not valid.");
            return null;
        } catch (Exception e) {
            response.status(404);
            logger.warn("No such image found", e);

        }
        response.status(500);
        response.body("Unknown error");
        return null;
    }

    public Object getSystemMap(Request request, Response response) {
        return null;
    }
}

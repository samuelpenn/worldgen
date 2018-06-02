/**
 * ImageAPI.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.PlanetMap;
import uk.org.glendale.worldgen.astro.stars.NoSuchStarException;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.NoSuchStarSystemException;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemFactory;
import uk.org.glendale.worldgen.astro.systems.StarSystemImage;
import uk.org.glendale.worldgen.exceptions.ApiException;
import uk.org.glendale.worldgen.web.Controller;
import uk.org.glendale.worldgen.web.Server;

import java.io.IOException;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.put;

/**
 * Defines the REST API for accessing information about a star system.
 */
public class SystemAPI extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(SystemAPI.class);

    public void setupEndpoints() {
        logger.info("Setting up endpoints for ImageAPI");
        get("/api/system/:id/map", (request, response) -> getSystemMap(request, response));
        get("/api/system/:id/planets", (request, response) -> getPlanets(request, response), json());
        get("/api/star/:id/planets", (request, response) -> getPlanetsAroundStar(request, response), json());
        get("/api/planet/:id/map", (request, response) -> getPlanetMap(request, response));
        get("/api/planet/:id/maps", (request, response) -> getPlanetMaps(request, response), json());
    }


    /**
     * Gets a map of the star system as a JPEG image.
     *
     * @param request       Request object.
     * @param response      Response object.
     * @return              Image data of type image/jpg.
     */
    public Object getSystemMap(Request request, Response response) {
        try {
            int  id = getIdParam(request, "id");
            int  width = getIntParamWithDefault(request,"width", 2048);
            int  scale = getIntParamWithDefault(request, "scale", -1);
            long time = getLongParamWithDefault(request,"time", -1);
            boolean zones = getBooleanParamWithDefault(request, "zones", false);


            // Width should be between 64px and 4096px.
            width = Math.min(Math.max(64, width), 4096);

            try (WorldGen worldGen = Server.getWorldGen()) {

                StarSystemFactory factory = worldGen.getStarSystemFactory();

                StarSystem system = factory.getStarSystem(id);
                StarSystemImage image = new StarSystemImage(worldGen, system);


                response.type("image/jpg");

                image.setWidth(width);
                image.setScale(scale);
                image.setZones(zones);
                if (time > -1) {
                    image.setTime(time);
                }

                return image.draw().save().toByteArray();
            } catch (NoSuchStarSystemException e) {
                throw new ApiException(404, String.format("There is no star system with id [%d]", id));
            } catch (IOException e) {
                throw new ApiException(500, String.format("Inteneral error drawing map (%s)", e.getMessage()));
            }
        } catch (ApiException e) {
            logger.error(String.format("getSystemMap: %s", e.getMessage()));

            response.status(e.getStatusCode());
            response.body(e.getMessage());
            return e.getMessage();
        }
    }

    /**
     * Gets a list of all the planets that are part of the specified star system.
     *
     * @param request       HTTP Request object.
     * @param response      HTTP Response object.
     * @return              List of planets.
     */
    public List<Planet> getPlanets(Request request, Response response) {
        try {
            int  id = getIdParam(request, "id");

            logger.info(String.format("getPlanets: [%d]", id));

            try (WorldGen worldGen = Server.getWorldGen()) {
                StarSystem    system = worldGen.getStarSystemFactory().getStarSystem(id);
                PlanetFactory factory = worldGen.getPlanetFactory();

                response.type("application/json");

                return factory.getPlanets(system);
            } catch (NoSuchStarSystemException e) {
                e.printStackTrace();
            }
        } catch (ApiException e) {
            logger.error(String.format("getPlanets: %s", e.getMessage()));

            response.status(e.getStatusCode());
            response.body(e.getMessage());

            return null;
        }
        return null;
    }

    /**
     * Gets a list of all the planets that orbit the specified star.
     *
     * @param request       HTTP Request object.
     * @param response      HTTP Response object.
     * @return              List of planets.
     */
    public List<Planet> getPlanetsAroundStar(Request request, Response response) {
        try {
            int  id = getIdParam(request, "id");

            logger.info(String.format("getPlanetsAroundStar: [%d]", id));

            try (WorldGen worldGen = Server.getWorldGen()) {
                Star star = worldGen.getStarFactory().getStar(id);
                PlanetFactory factory = worldGen.getPlanetFactory();

                response.type("application/json");
                return factory.getPlanets(star);
            } catch (NoSuchStarException e) {
                e.printStackTrace();
            }
        } catch (ApiException e) {
            logger.error(String.format("getPlanetsAroundStar: %s", e.getMessage()));

            response.status(e.getStatusCode());
            response.body(e.getMessage());

            return null;
        }
        return null;
    }

    public Object getPlanetMap(Request request, Response response) {

        try {
            int id = getIdParam(request, "id");
            String name = getStringParamWithDefault(request, "name", PlanetMap.MAIN);
            boolean stretch = getBooleanParamWithDefault(request, "stretch", false);
            int width = getIntParamWithDefault(request, "width", 1024);

            logger.info(String.format("getPlanetMap: [%d] [%s]", id, name));

            try (WorldGen worldGen = Server.getWorldGen()) {
                PlanetFactory factory = worldGen.getPlanetFactory();

                SimpleImage image = factory.getPlanetMap(id, name);

                if (stretch) {
                    logger.info("Stretching the image");
                    image = Icosahedron.stretchImage(image, width);
                }

                response.type("image/png");
                return image.save(!stretch).toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ApiException e) {
            logger.error(String.format("getPlanetMap: %s", e.getMessage()));

            response.status(e.getStatusCode());
            response.body(e.getMessage());
        }
        return null;
    }

    public List<String> getPlanetMaps(Request request, Response response) {

        try {
            int id = getIdParam(request, "id");

            logger.info(String.format("getPlanetMaps: [%d]", id));

            try (WorldGen worldGen = Server.getWorldGen()) {
                PlanetFactory factory = worldGen.getPlanetFactory();

                return factory.getPlanetMaps(id);
            }
        } catch (ApiException e) {
            logger.error(String.format("getPlanetMap: %s", e.getMessage()));

            response.status(e.getStatusCode());
            response.body(e.getMessage());
        }
        return null;
    }
}

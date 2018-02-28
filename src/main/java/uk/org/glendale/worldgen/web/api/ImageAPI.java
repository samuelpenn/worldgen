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
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.web.Controller;
import uk.org.glendale.worldgen.web.Server;

import static spark.Spark.get;
import static spark.Spark.put;

/**
 * Provides a web API to the general images stored in the database.
 * An 'image' in this case is identified by name, and not associated
 * with any single astronomical body, such as a planet or star system.
 * Images of planets and star systems are obtained by using the API
 * for those object types.
 */
public class ImageAPI extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(ImageAPI.class);

    public void setupEndpoints() {
        logger.info("Setting up endpoints for ImageAPI");
        get("/api/images/", (request, response) -> getImages(request, response), json());
        get("/api/images/:name", (request, response) -> getImage(request, response));
        put( "/api/images/:name", (request, response) -> putImage(request, response));
    }

    /**
     * Gets a list of the names of all the images that are currently stored.
     *
     * @param request       Request object.
     * @param response      Response object.
     * @return              JSON array of image names, or an empty list.
     */
    public Object getImages(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {
            response.type("application/json");
            return worldGen.getImages();
        } catch (Exception e) {
            response.status(500);
            logger.error("No list of images found", e);
        }
        return null;
    }

    /**
     * Gets the image identified by name.
     *
     * @param request       Request object.
     * @param response      Response object.
     * @return              Image data of type image/jpg.
     */
    public Object getImage(Request request, Response response) {
        String name = request.params(":name");

        try (WorldGen worldGen = Server.getWorldGen()) {
            response.type("image/jpg");

            return worldGen.getImage(name).save().toByteArray();
        } catch (Exception e) {
            response.status(404);
            logger.warn("No such image found", e);
        }
        return null;
    }

    /**
     * Image data is the body. The name in the URL defines the image name. If it
     * already exists then it will be overwritten.
     *
     * @param request       Request object.
     * @param response      Response object.
     * @return
     */
    public Object putImage(Request request, Response response) {
        String name = request.params(":name");

        return null;
    }

}

/**
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Universe;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * Controller for configuring the server.
 */
public class ConfigController extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    public void setupEndpoints() {
        logger.info("Setting up endpoints for ConfigController");
        get("/config", (request, response) -> showConfigurationPage(request, response));
        post( "/config", (request, response) -> postConfigurationForm(request, response));
     }

    public Object showConfigurationPage(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {
            Universe u = worldGen.getUniverse();
            if (u.isLocked()) {
                return "Configuration is not permitted.";
            } else {
                Map<String,Object> model = new HashMap<>();
                model.put("universe", u);

                return new VelocityTemplateEngine().render(
                        new ModelAndView(model, "templates/config.vm")
                );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Object postConfigurationForm(Request request, Response response) {
        logger.debug(String.format("Posted form [%s] [%d]", request.contentType(), request.contentLength()));

        try (WorldGen wg = Server.getWorldGen()) {
            Universe u = wg.getUniverse();

            if (u.isLocked()) {
                return "Configuration is not permitted";
            }

            String location = "/tmp/worldgen";   // the directory location where files will be stored
            long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
            long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                    location, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    multipartConfigElement);

            File filePath = new File(location);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            logger.debug("Looking for name");
            String name = readFormString(request, "name");
            int    minX = readFormInt(request, "minX");
            int    maxX = readFormInt(request, "maxX");
            logger.debug("Has read [" + name + "] [" + minX + "] [" + maxX + "]");
            int    minY = readFormInt(request, "minY");
            int    maxY = readFormInt(request, "maxY");
            logger.debug("Has read [" + name + "] [" + minY + "] [" + maxY + "]");

            if (maxX < minX) {
                return "MaxX cannot be less than MinX";
            }
            if (maxY < minY) {
                return "MaxY cannot be less than MinY";
            }
            if (maxX < 0 || minX > 0 || maxY < 0 || minY > 0) {
                return "Universe limits must contain 0,0";
            }

            logger.debug("Try to read image");
            SimpleImage image = readFormImage(request, "uploadFile");
            logger.debug("Image width: " + image.getBufferedImage().getWidth());
            logger.debug("Image height: " + image.getBufferedImage().getHeight());

            try {
                wg.setGalaxyMap(image);

                u.setName(name);
                u.setMaxX(maxX);
                u.setMinX(minX);
                u.setMaxY(maxY);
                u.setMinY(minY);

                u.setConfigured(true);

                wg.setUniverse(u);
            } catch (Exception e) {
                logger.error("Exception", e);
            }


            /*
            Part namePart = request.raw().getPart("name");
            String name = new BufferedReader(new InputStreamReader(namePart.getInputStream())).lines().collect(Collectors.joining("\n"));

            Collection<Part> parts = request.raw().getParts();
            for (Part part : parts) {
                logger.debug("Name: " + part.getName());
                logger.debug("Size: " + part.getSize());
                logger.debug("Filename: " + part.getSubmittedFileName());
            }
            /*
            Part name = request.raw().getPart("name");
            System.out.println("Part 'name':" + name.getSize());

            String formName = request.queryParams("name");
            String formMinX = request.queryParams("minX");
            String formMaxX = request.queryParams("maxX");
            String formMinY = request.queryParams("minY");
            String formMaxY = request.queryParams("maxY");
            String formFile = request.queryParams("uploadFile");

            System.out.printf("Name [%s] X [%s] x [%s] Y [%s] y [%s]\n",
                    formName, formMinX, formMaxX, formMinY, formMaxY);

            System.out.printf("[%s]\n", formFile);

            for (String qp : request.queryParams()) {
                System.out.printf("Param [%s]\n", qp);
            }
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));

            for (Part p : request.raw().getParts()) {
                System.out.printf("Part [%s] Size [%l]\n", p.getName(), p.getSize());

            }
            */
        } catch (Exception e) {
            return e.getMessage();
        }

        return "Okay";
    }
}

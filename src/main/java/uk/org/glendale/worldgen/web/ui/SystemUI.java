/**
 * SystemUI.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.web.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.sectors.NoSuchSectorException;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.systems.NoSuchStarSystemException;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.exceptions.ApiException;
import uk.org.glendale.worldgen.web.Controller;
import uk.org.glendale.worldgen.web.IndexController;
import uk.org.glendale.worldgen.web.Server;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static uk.org.glendale.worldgen.Main.getWorldGen;

public class SystemUI extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(SystemUI.class);

    @Override
    public void setupEndpoints() {
        logger.info("Setting up endpoints for ConfigController");
        get("/system/:id", (request, response) -> showStarSystem(request, response));

    }

    private String toTitleCase(String text) {
        text = text.toLowerCase().replaceAll("_", " ").trim();

        String title = "";
        for ( String word : text.split(" ")) {
            title += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
        }

        return title.trim();
    }

    private Object showStarSystem(Request request, Response response) {
        try (WorldGen worldGen = getWorldGen()) {

            int id = getIdParam(request, "id");
            StarSystem system = worldGen.getStarSystemFactory().getStarSystem(id);


            Map<String,Object> model = new HashMap<>();
            model.put("id", id);
            model.put("system", system);
            model.put("x", String.format("%02d", system.getX()));
            model.put("y", String.format("%02d", system.getY()));

            model.put("systemType", toTitleCase("" + system.getType()));
            model.put("systemZone", toTitleCase("" + system.getZone()));

            Sector sector = worldGen.getSectorFactory().getSector(system.getSectorId());
            model.put("sector", sector);
            model.put("subsector", sector.getSubSectorName(system.getX(), system.getY()));

            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "templates/system.vm")
            );

        } catch (ApiException e) {
            logger.error("Failed to display star system", e);
            response.status(500);
            return "Internal server error (" + e.getMessage() + ")";
        } catch (NoSuchStarSystemException e) {
            logger.error("No such star system", e);
            response.status(404);
            return "System not found (" + e.getMessage() + ")";
        } catch (NoSuchSectorException e) {
            logger.error("No such sector", e);
            response.status(500);
            return "Sector not found (" + e.getMessage() + ")";
        }

    }
}

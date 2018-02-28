/**
 * SystemUI.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.web.ui;

import org.apache.velocity.tools.generic.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.sectors.NoSuchSectorException;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.sectors.SectorFactory;
import uk.org.glendale.worldgen.astro.systems.NoSuchStarSystemException;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.exceptions.ApiException;
import uk.org.glendale.worldgen.web.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static uk.org.glendale.worldgen.Main.getWorldGen;

/**
 * GUI controller for the Sector information page. Shows basic information on the
 * sector, simple navigation controls plus list of star systems.
 */
public class SectorUI extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(SectorUI.class);

    @Override
    public void setupEndpoints() {
        logger.info("Setting up endpoints for SectorUI");
        get("/sector/:id", (request, response) -> sector(request, response));

    }

    private Object sector(Request request, Response response) {
        try (WorldGen worldGen = getWorldGen()) {
            String id = getStringParam(request, "id");

            // Get information on this sector.
            SectorFactory factory = worldGen.getSectorFactory();
            Sector sector = null;
            List<StarSystem> list = null;
            int x, y;
            try {
                sector = factory.getSectorByIdentifier(id);
                list = worldGen.getStarSystemFactory().getStarSystems(sector);
                x = sector.getX();
                y = sector.getY();
            } catch (NoSuchSectorException e) {
                // No sector found. How we respond depends on how sector was requested.
                if (SectorFactory.isCoord(id)) {
                    x = SectorFactory.getXCoord(id);
                    y = SectorFactory.getYCoord(id);
                    sector = new Sector("Uncharted Sector " + SectorFactory.getSectorNumber(x, y), x, y);
                } else {
                    throw e;
                }
            }

            // Get information on neighbouring sectors.
            Sector coreward = null, rimward = null, trailing = null, spinward = null;

            if (factory.hasSector(x - 1, y)) {
                spinward = factory.getSector(x - 1, y);
            }
            if (factory.hasSector(x + 1, y)) {
                trailing = factory.getSector(x + 1, y);
            }
            if (factory.hasSector(x, y - 1)) {
                coreward = factory.getSector(x, y - 1);
            }
            if (factory.hasSector(x, y + 1)) {
                rimward = factory.getSector(x, y + 1);
            }

            // Populate model with information.
            Map<String,Object> model = new HashMap<>();
            model.put("sector", sector);
            model.put("systems", list);
            model.put("numberTool", new NumberTool());
            model.put("spinward", spinward);
            model.put("trailing", trailing);
            model.put("coreward", coreward);
            model.put("rimward", rimward);

            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "templates/sector.vm")
            );

        } catch (ApiException e) {
            logger.error("Failed to display star system", e);
            response.status(500);
            return "Internal server error (" + e.getMessage() + ")";
        } catch (NoSuchSectorException e) {
            logger.error("No such sector", e);
            response.status(404);
            return "Sector not found (" + e.getMessage() + ")";
        }

    }
}

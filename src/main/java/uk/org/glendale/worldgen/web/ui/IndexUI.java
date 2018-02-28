/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
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
import uk.org.glendale.worldgen.astro.Universe;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.sectors.SectorFactory;
import uk.org.glendale.worldgen.web.Controller;
import uk.org.glendale.worldgen.web.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static uk.org.glendale.worldgen.Main.getWorldGen;

/**
 * Controller for the GUI interface for the index page.
 */
public class IndexUI extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(IndexUI.class);

    @Override
    public void setupEndpoints() {
        logger.info("Setting up endpoints for IndexUI");
        get("/", (request, response) -> index(request, response));
        get("/index", (request, response) -> index(request, response));
        get("/index.html", (request, response) -> index(request, response));

    }

    private Object index(Request requests, Response response) {
        try (WorldGen worldGen = getWorldGen()) {
            Universe u = worldGen.getUniverse();

            Map<String,Object> model = new HashMap<>();
            model.put("name", u.getName());
            model.put("maxX", u.getMaxX());
            model.put("minX", u.getMinX());
            model.put("maxY", u.getMaxY());
            model.put("minY", u.getMinY());

            Map<String,String>  map = new HashMap<String,String>();
            for (int y = u.getMinY(); y <= u.getMaxY(); y++) {
                for (int x = u.getMinX(); x <= u.getMaxX(); x++) {
                    map.put(x+"_"+y, "Sector " + SectorFactory.getSectorNumber(x, y));
                }
            }
            List<Sector> sectors = worldGen.getSectorFactory().getSectors();
            for (Sector sector : sectors) {
                map.put(sector.getX() + "_" + sector.getY(), sector.getName());
            }
            model.put("sectors", map);

            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "templates/index.vm")
            );

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

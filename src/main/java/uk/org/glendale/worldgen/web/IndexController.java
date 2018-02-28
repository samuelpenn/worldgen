package uk.org.glendale.worldgen.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.worldgen.WorldGen;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

public class IndexController extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Override
    public void setupEndpoints() {
        logger.info("Setting up endpoints for ConfigController");
        get("/image", (request, response) -> getGalaxyMap(request, response));
    }

    public Object getGalaxyMap(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {
            response.type("image/png");

            return worldGen.getGalaxyMapGrid().save().toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

/**
 * TextAPI.java
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
import uk.org.glendale.worldgen.text.NameGenerator;
import uk.org.glendale.worldgen.web.Controller;
import uk.org.glendale.worldgen.web.Server;

import java.util.ArrayList;

import static spark.Spark.get;

/**
 * REST API onto the random text generation services.
 */
public class TextAPI extends Controller{
    private static final Logger logger = LoggerFactory.getLogger(TextAPI.class);


    public void setupEndpoints() {
        logger.info("Setting up endpoints for TextAPI");
        get("/api/text/names/system/", (request, response) -> getRandomSystemNameRoots(request, response), json());
        get("/api/text/names/system/:root", (request, response) -> getRandomSystemNames(request, response), json());
        get("/api/text/names/system/:root/:modifier", (request, response) -> getRandomSystemNames(request, response), json());
    }

    /**
     * Gets a list of all the available roots for the star system name generator.
     *
     * @param request       Request object.
     * @param response      Response object.
     * @return              JSON array of root names.
     */
    public Object getRandomSystemNameRoots(Request request, Response response) {
        logger.info("getRandomSystemNameRoots:");

        try (WorldGen worldGen = Server.getWorldGen()) {
            NameGenerator generator = worldGen.getStarSystemNameGenerator();

            String [] roots = generator.getRoots();
            return roots;

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Gets an array of one or more randomly generated star system names.
     * The path parameters 'root' and (optionally) 'modifier' specify the root and modifier
     * for the names to be generated. If 'number' is passed as a query param, then that gives
     * the number of names to generate. If 'unique'=true then the list is guaranteed to contain
     * only unique names, but it may not return the full number requested.
     *
     * @param request       Request object.
     * @param response      Response object.
     * @return              JSON array of root names.
     */
    public Object getRandomSystemNames(Request request, Response response) {

        try (WorldGen worldGen = Server.getWorldGen()) {
            NameGenerator generator = worldGen.getStarSystemNameGenerator();

            String  root = request.params("root");
            String  modifier = request.params("modifier");
            int     number = 1;
            boolean unique = false;

            try {
                number = Integer.parseInt(request.queryParams("number"));
                if (number < 1) {
                    number = 1;
                } else if (number > 10000) {
                    number = 10000;
                }
            } catch (Exception e) {
                // Undefined or not a number, so ignore.
            }
            if (request.queryParams("unique") != null) {
                if (request.queryParams("unique").equals("true")) {
                    unique = true;
                }
            }

            logger.info(String.format("getRandomSystemNames: [%s] [%s] [%d]", root, modifier, number));

            ArrayList<String> names = new ArrayList<>();
            int nonUnique = 0;
            for (int i=0; i < number; i++) {
                String name = generator.generateName(root, modifier);
                if (unique && names.contains(name)) {
                    logger.trace(String.format("Rejecting non-unique name [%s]", name));
                    if (nonUnique++ < Math.max(number, 100)) {
                        i--;
                    }
                    continue;
                }
                names.add(name);
            }
            logger.debug(String.format("Returning [%d/%d] names", names.size(), number));

            return names;

        } catch (Exception e) {

        }
        return null;
    }
}

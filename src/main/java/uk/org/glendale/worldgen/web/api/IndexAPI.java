package uk.org.glendale.worldgen.web.api;

import spark.Request;
import spark.Response;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.web.Controller;
import uk.org.glendale.worldgen.web.Server;

public class IndexAPI extends Controller {
    public void setupEndpoints() {

    }

    public Object getConfig(Request request, Response response) {
        try (WorldGen worldGen = Server.getWorldGen()) {

        }
        return null;
    }
}

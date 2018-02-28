/**
 * Server.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.web;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;
import uk.org.glendale.worldgen.Main;
import uk.org.glendale.worldgen.Ticker;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Universe;

import java.util.*;

import static spark.Spark.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.DateBuilder.*;

/**
 * Web front end.
 *
 */
public class Server extends Main {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    // Quartz schedular factory.
    private static final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    private static boolean haveTicker = false;

    /**
     * Creates a scheduler to run every minute.
     *
     * @throws SchedulerException
     */
    private static synchronized void setupTimer() throws SchedulerException {
        if (haveTicker) {
            // Should only do this once.
            return;
        }
        Scheduler  scheduler = schedulerFactory.getScheduler();

        scheduler.start();

        JobDetail job = newJob(Ticker.class).withIdentity("universeJob", "universe").build();

        int frequency = getWorldGen().getConfig().getSimFrequency();
        Trigger trigger = newTrigger()
                .withIdentity("trigger", "universe")
                .startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(frequency).repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);

        haveTicker = true;
    }

    /**
     * Starts the web application server running. Configures the ticker event, sets up controllers
     * on the endpoints and then waits for connections.
     */
    public void startServer() {
        logger.info("== WorldGen AppServer ==");

        Spark.staticFileLocation("/public");
        Spark.port(getConfiguration().getHttpPort());

        try {
            Server.setupTimer();
        } catch (SchedulerException e) {
            logger.error("Failed to start scheduler", e);
            System.exit(1);
        }

        try (WorldGen wg = Server.getWorldGen()) {
            logger.debug("Current Time is: " + wg.getCurrentTime());

            // Check that we are configured.

            Reflections reflections = new Reflections("uk.org.glendale.worldgen");
            Set<Class<? extends Controller>> controllers = reflections.getSubTypesOf(Controller.class);

            logger.debug("Started controller loading");
            for (Class controller : controllers) {
                logger.info("Adding controller [" + controller.getSimpleName() + "]");
                try {
                    Controller c = (Controller) controller.newInstance();
                    c.setupEndpoints();
                } catch (InstantiationException e) {
                    System.out.println("Failed to instantiate new controller (" + e.getMessage() + ")");
                } catch (IllegalAccessException e) {
                    System.out.println("Failed to access new controller (" + e.getMessage() + ")");
                }
            }
            logger.debug("Finished controller loading");

        } catch (Exception e) {

        }

    }

    /**
     * Defaults to http://localhost:4567/index
     */
    public static void main(String[] args) {
        Server server = new Server();

        server.startServer();
    }
}

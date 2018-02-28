/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.worldgen.astro.Universe;
import uk.org.glendale.worldgen.web.Server;

/**
 * A scheduled job which runs on a regular basis to perform house keeping operations.
 * It is expected that it will be called every 60 seconds within the web application.
 */
public class Ticker implements Job {
    private static final Logger logger = LoggerFactory.getLogger(Ticker.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Tick tock");
        WorldGen    worldGen = Server.getWorldGen();
        Universe    universe = worldGen.getUniverse();

        if (universe.isRunning() && universe.isConfigured()) {
            // First, work out how long has passed in the universe since the last time we ran.
            long realTimeNow = System.currentTimeMillis();
            long realTimeLast = universe.getLastDate().getTime();
            Constant speed = worldGen.getConstant(Constant.Name.SPEED);

            long timePassed = ((realTimeNow - realTimeLast) * speed.getValue()) / 1000;

            boolean skipDowntime = worldGen.getConfig().getSkipDowntime();
            int     frequency = worldGen.getConfig().getSimFrequency();
            if (skipDowntime && timePassed > frequency * 10) {
                // If sufficient time has passed since our last update, then assume that the server
                // has been down. Limit the amount we update by so we don't skip a huge period of
                // simulation time.
                logger.info(String.format("Server appears to have been down for [%d] seconds, capping updates", timePassed));
                timePassed = frequency * 10;
            }
            logger.debug(String.format("Updating universe by [%d] seconds", timePassed));

            worldGen.setCurrentTime(universe.getSimTime() + timePassed);
            logger.debug(String.format("Time is now [%s]", universe.getCurrentDateTime()));
        } else {
            worldGen.setCurrentTime(worldGen.getCurrentTime());
        }

        worldGen.close();
    }
}

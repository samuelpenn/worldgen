/**
 * Sector.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Defines a universe which consists of global information about the simulation.
 * Includes metadata such as the universe name, permissions and owner, as well
 * as current settings such as the current time.
 */
@Entity
@Table(name = "universe")
public final class Universe {

    @Id
    @Column(name = "id")
    private int id;

    // The name of this universe.
    @Column(name = "name")
    private String  name;

    // Real date universe was created.
    @Column(name = "created_date")
    private Date    createdDate;

    // Real date universe was last updated.
    @Column(name = "last_date")
    private Date    lastDate;

    // In-universe time, in seconds since creation.
    @Column(name = "sim_time")
    private long    simTime;

    // Is the simulation currently running?
    @Column(name = "running")
    private boolean running;

    // Is the universe currently configured?
    @Column(name = "configured")
    private boolean configured;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "min_x")
    private int minX;

    @Column(name = "max_x")
    private int maxX;

    @Column(name = "min_y")
    private int minY;

    @Column(name = "max_y")
    private int maxY;

    // Length of a standard day, in seconds.
    public static final long DAY_LENGTH = 86400;

    // Length of a standard week, in standard days.
    public static final long WEEK_LENGTH = 7;

    // Length of a standard year, in standard days.
    public static final long YEAR_LENGTH = 365;

    /**
     * Basic constructor, used only by the persistence layer. A universe object should not
     * be created outside of the persistence framework.
     */
    protected Universe() {
        this.id = 1;
        this.createdDate = new Date();
        this.lastDate = new Date();
        this.simTime = 0L;
        this.name = "Universe";
    }

    /**
     * Gets the name of this universe. There is only one universe, so this is just a
     * descriptive label for it.
     *
     * @return  Name of this universe.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this universe.
     *
     * @param name  Name of this universe.
     */
    public void setName(final String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Universe name must be non-empty.");
        }

        this.name = name.trim();
    }

    /**
     * Sets the real world date that this universe was created. Immutable.
     *
     * @return  Real world creation date.
     */
    public Date getCreatedDate() {
        return createdDate;
    }


    private void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    private void setLastDate(final Date lastDate) {
        this.lastDate = lastDate;
    }

    public long getSimTime() {
        return simTime;
    }

    private void setSimTime(final long simTime) {
        this.simTime = simTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(final boolean configured) {
        this.configured = configured;
    }

    /**
     * If locked, the ability to configure or write to the application is blocked from the
     * web interface.
     *
     * @return  True iff if the application is in locked state.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets whether to lock or unlock the application. When locked, the application is in read only
     * mode and cannot be modified from the web interface.
     *
     * @param locked    Set to true to lock the application, false to unlock.
     */
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    /**
     * Sets the current time of the simulation. This will also update the last real world
     * access date as well, so that we can calculate how much time has passed since the
     * simulation time was updated.
     *
     * @param simTime   Time in seconds since creation within the simulation.
     */
    public void setCurrentTime(long simTime) {
        if (simTime < this.simTime) {
            throw new IllegalArgumentException("Universe date cannot go backwards.");
        }
        this.lastDate = new Date();
        this.simTime = simTime;
    }

    /**
     * Gets the current time of the simulation as a string.
     *
     * @return  Time in years, days, hours, minutes, seconds.
     */
    public String getCurrentDateTime() {
        long     year = simTime / (YEAR_LENGTH * DAY_LENGTH);
        long     day = simTime % (YEAR_LENGTH * DAY_LENGTH);
        long     hour = day % DAY_LENGTH;
        day = day / DAY_LENGTH;
        long     minute = hour % 3600;
        hour = hour / 3600;
        minute = minute / 60;

        return String.format("%d.%03d %02d:%02d", year, day, hour, minute);
    }

    /**
     * Maximum X coordinate of a sector in the galaxy. This will be zero or greater.
     * There may not be mapped sectors with this coordinate, but this defines a
     * boundary for drawing the known galaxy.
     *
     * @return  Maximum X coordinate.
     */
    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        if (maxX < 0) {
            throw new IllegalArgumentException("Maximum X must be zero or positive.");
        }
        this.maxX = maxX;
    }

    /**
     * Minimum X coordinate of a sector in the galaxy. This will be zero or lower.
     * There may not be mapped sectors with this coordinate, but this defines a
     * boundary for drawing the known galaxy.
     *
     * @return  Minimum Y coordinate.
     */
    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        if (minX > 0) {
            throw new IllegalArgumentException("Minimum X must be zero or negative.");
        }
        this.minX = minX;
    }

    /**
     * Maximum Y coordinate of a sector in the galaxy. This will be zero or greater.
     * There may not be mapped sectors with this coordinate, but this defines a
     * boundary for drawing the known galaxy.
     *
     * @return  Maximum Y coordinate.
     */
    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        if (maxY < 0) {
            throw new IllegalArgumentException("Maximum Y must be zero or positive.");
        }
        this.maxY = maxY;
    }

    /**
     * Minimum Y coordinate of a sector in the galaxy. This will be zero or lower.
     * There may not be mapped sectors with this coordinate, but this defines a
     * boundary for drawing the known galaxy.
     *
     * @return  Minimum Y coordinate.
     */
    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        if (minY > 0) {
            throw new IllegalArgumentException("Minimum Y must be zero or negative.");
        }
        this.minY = minY;
    }
}

/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.systems.generators.Barren;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;
import uk.org.glendale.worldgen.web.Server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This is a special type of star system generator that allows the caller to specify which
 * generator to use.
 */
class NamedGenerator extends StarSystemGenerator {
    private static final Logger logger = LoggerFactory.getLogger(NamedGenerator.class);

    public NamedGenerator(WorldGen worldgen) {
        super(worldgen);
    }

    public StarSystem generate(Sector sector, String name, int x, int y) throws DuplicateObjectException {
        return null;
    }

    public void colonise(StarSystem system) {
        return;
    }

    protected StarSystem generate(Sector sector, String name, int x, int y, String generator, String type) throws UnsupportedException, DuplicateObjectException {
        StarSystem system = createEmptySystem(sector, name, x, y);

        logger.info(String.format("Generating system [%s] using [%s.%s]", name, generator, type));

        String generatorName = "uk.org.glendale.worldgen.astro.systems.generators." + generator;

        StarSystemGenerator g = null;
        Class c = null;
        try {
            c = Class.forName(generatorName);
            Constructor constructor = c.getConstructor(WorldGen.class);
            g = (StarSystemGenerator) constructor.newInstance(worldgen);
        } catch (Exception e) {
            logger.error(String.format("Unable to find system generator [%s]", generator));
            throw new UnsupportedException(String.format("Star system generator [%s] does not exist", generator), e);
        }

        String methodName = "create" + type;

        try {
            Method method = c.getMethod(methodName, StarSystem.class);
            method.invoke(g, system);
            g.colonise(system);
        } catch (NoSuchMethodException e) {
            logger.error(String.format("Unable to find method type [%s.%s]", generator, methodName));
            throw new UnsupportedException(String.format("Star system generator [%s] does not have method [%s]",
                    generator, methodName), e);
        } catch (IllegalAccessException e) {
            throw new UnsupportedException(String.format("Star system generator [%s] cannot access method [%s]",
                    generator, methodName), e);
        } catch (InvocationTargetException e) {
            throw new UnsupportedException(String.format("Star system generator [%s] cannot invoke method [%s]",
                    generator, methodName), e);
        }

        updateStarSystem(system);

        return system;
    }



    public static void main(String[] args) throws DuplicateObjectException {
        NamedGenerator g = new NamedGenerator(Server.getWorldGen());


        g.generate(null, "Test", 1, 1, "Barren", "SmallDwarf");
    }

}

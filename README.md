WorldGen
========

***WorldGen*** is a Java based application for generating worlds and civilisations
within a galaxy, using a style loosely inspired by *Traveller*. It provides a way
to create star systems and worlds, and a web application for displayign them.

Future plans include being able to simulate trade between the various worlds.

The web application is built around SparkJava. Data is stored in a database. It has
been developed and tested against MySQL, other databases might work with a bit of
effort. 

Building
--------

Use *gradle* to build the application. Depends on Java 8, SparkJava and MySQL.
On Linux, you can build it with:

```
./gradlew shadow
```

Creates jar build/libs/worldgen-1.0-SNAPSHOT-all.jar

It expects a properties file to configure the database and web server.

```properties
database.url=jdbc:mysql://localhost:3306/worldgen?useSSL=false
database.username=world
database.password=world

server.port=4567

map.density.min=1
map.density.max=90

style.useRealStarColours=false

sim.frequency=60
sim.skipDowntime=true

```

Running
-------

There is a commandline application which can be used to run, control and report
on the application.

```
java -Dworldgen.config=wg.config -jar build/libs/worldgen-1.0-SNAPSHOT-all.jar status
```

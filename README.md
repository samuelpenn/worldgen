WorldGen
========

***WorldGen*** is a Java based application for generating worlds and civilisations
within a galaxy, using a style loosely inspired by *Traveller*. It provides a way
to create star systems and worlds, and a web application for displaying them.

**Goals**

* To build something which could be used in a *Traveller*-like RPG, where
  players can easily travel between many worlds in an interstellar 
  civilisation;
* That makes use of the large amounts of processing power and storage now 
  readily available to most people;
* That makes use of the powerful internet enabled electronic devices that
  most gamers have available when at the table top;
* That can generate information for millions of worlds at a level of detail
  useful to a star-spanning RPG;
* That simulates trade between those worlds, to give the illusion of a
  living environment.

**Features (WIP)**

* Web based access to the data.  
* Multiple stars and worlds per system.
* Detailed world types, with each world having its own unique map and description.
* Detailed cultures and civilisations for each world.
* Trade simulated at the level of individual star ships.

The web application is built around [SparkJava](http://sparkjava.com/) . Data is stored 
in a database. It has been developed and tested against MySQL, other databases might 
work with a bit of effort. 

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
$ java -Dworldgen.config=wg.config -jar build/libs/worldgen-1.0-SNAPSHOT-all.jar status
Universe: A Few Worlds
Created Date: 2018-03-04 22:21:32
Last Date: 2018-05-23 23:11:56
Current Time: 3.015 06:20
Number of Sectors: 3
Number of Systems: 175
Number of Planets: 392
```

Other commands are as follows:

**server**

Run the web server. This will run a webserver on the port specified in the configuration
file, or 4567 by default.

**sectors**

Lists all the sectors in the current universe.

**sector**

Arguments: sector <*x,y*> <*name*>

Creates a new empty named sector at the given coordinates. 0,0 is the centre sector.
Positive x is to the right, and positive y is down.

**system**

Arguments: system <*sector*> <*xxyy*> <*generator*> <*type*>

Generates a new star system.


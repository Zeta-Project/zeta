# Zeta
> Model-Driven Generation of Graphical Editors

## Development

Check out this repository. It needs to be located in an folder called **Zeta**

To start the Zeta in development mode. You need: 

*  [IntelliJ Ultimate Edition.](https://www.jetbrains.com/idea/download/) This is [free for students.](https://www.jetbrains.com/student/)
*  [MongoDB.](https://www.mongodb.com/download-center)
*  [NodeJs.](https://nodejs.org/en/download/) *This is legacy and will be removed in the future*
*  yarn: `npm install -g yarn` *This is legacy and will be removed in the future*

You need to start 3 things:

*  MongoDB: 
   * in commandline: `mongod(.exe) --dbpath "*full path to where i want my dockerfiles to be stored*".`
*  Webapp:
   * If you are working under windows. Execute all webapp commands in MINGW (comes with git).
   * make sure yarn is installed. 
   * cd into the webapp directory
   * call: `yarn`
   * call: `./node_modules/.bin/bower install --allow-root`
   * to start, call: `yarn run dev`
* IntelliJ
   * File | Open -> folder Zeta/api
   * VCS | Enable Version Control Integration -> git
   * navigate to build.sbt and enable sbt integration if asked.
   * View | Tool Windows | SBT -> press the blue refresh button
   * Run | Edit Configurations... -> `+` -> Play 2 App 
       * Play2 Module = `api`
       * Environment variables -> `...`
           * name = `ZETA_DEPLOYMENT`  value = `development`
   * Run the new Configuration
   
   
If you change something in the code. You just need to press refresh and IntelliJ will recompile the project.

**If you intend to change anything in the webapp. Remove it from the webapp and add it into the play app**

## Overview Production

List of services in docker-compose:

- **api** Webpage, Auth, REST API,..
- **couchbase-server** NoSQL database
- **mongodb** NoSQL database
- **sync-gateway** Couchbase loadbalancer, Couchbase cluser synchronisation
- **webapp** Setup database and Webapplication for generators
- **proxy** Nginx web server

## Prerequisites
The production stack is completely based on Docker. Therefore you first need to install Docker and make sure that your system fulfill the below dependencies.

**install dependencies:**

1) Install [docker](https://docs.docker.com/engine/installation/linux/) (tested with Docker version 1.12.3)

2) [Manage Docker as a non-root user](https://docs.docker.com/engine/installation/linux/linux-postinstall/)

3) Install docker-compose

4) Install [sbt](http://www.scala-sbt.org/)

5) Install Java JDK 8


## Getting Started
First all docker images need to be build.
```sh
./setup.sh
```

To start all services simply run the below command.

```sh
docker-compose up -d
```

To stop and remove **all** docker containers.
```sh
docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)
```

## Access the database
- [couchbase-server](http://localhost:8091)
- [sync-gateway-admin](http://localhost:4985/_admin/)

## Create a Generator
Two steps are required to create a generator.

### 1. Create a Docker Image
First you need to create a docker image for the execution of a generator. A few examples can be found in the [images](./api/images/generator) folder.

### 2. Make the Docker Image available
After an image was created we need to create a document in the database which link to the docker image. The created document will make the previous created docker image available for users.

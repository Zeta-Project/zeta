# Zeta
 Model-Driven Generation of Graphical Editors

* [ Manual](#manual)
    * [Setup](#setup)
    * [Start](#start)
* [Docker](#docker)
    * [Services](#services)
    * [Getting Started](#getting-started)
* [Create a Generator](#create-a-generator)

## Manual

Setup and run Zeta in an development environment. This is the prefered way to install and run Zeta on Windows.

### Setup

Check out this repository. It needs to be located in an folder called **Zeta**

To setup the Zeta for development mode. You need: 

*  [IntelliJ Ultimate Edition.](https://www.jetbrains.com/idea/download/) This is [free for students.](https://www.jetbrains.com/student/)
*  [MongoDB.](https://www.mongodb.com/download-center)
*  [NodeJs.](https://nodejs.org/en/download/) *This is legacy and will be removed in the future*
*  yarn: `npm install -g yarn` *This is legacy and will be removed in the future*

You need to setup 3 things:

1.  **MongoDB:**
    * to start, call in commandline: `mongod(.exe) --dbpath "*full path to where i want my dockerfiles to be stored*"`. 
    * you can also create a batch/shell script containing this command. In windows you should replace `mongod` with the full path to mongod in double quotes. It also helps to add a `pause` / `read` at the end to catch possible errors if MongoDB shuts down unexpectedly.
2. **Webapp:**
    * If you are working under windows. Execute all webapp commands in MINGW (comes with git).
    * make sure yarn is installed. 
    * cd into the webapp directory
    * call: `yarn`
    * call: `./node_modules/.bin/bower install --allow-root`
    * to start, call: `yarn run dev`
3. **IntelliJ IDEA:**
    * File | Open -> folder Zeta/api
    * VCS | Enable Version Control Integration -> git
    * navigate to build.sbt and enable sbt integration if asked.
    * View | Tool Windows | SBT -> press the blue refresh button
    * Run | Edit Configurations... -> `+` -> Play 2 App 
        * Play2 Module = `api`
        * Environment variables -> `...`
            * name = `ZETA_DEPLOYMENT`  value = `development`
    * Run the new Configuration
   
### Start

To start Zeta in development mode, you need to start 3 things.

1. **MongoDB:**
    Execute your script **or** call in commandline: `mongod(.exe) --dbpath "*full path to where i want my dockerfiles to be stored*"`.
2. **Webapp:**
    In commandline navigate to Zeta/webapp/. Now call: `yarn run dev`
3. **IntelliJ IDEA:**
    Start the Play2 configuration you have created.
   
If you change something in the code. You just need to press refresh and IntelliJ will recompile the project.

**If you intend to change anything in the webapp. Remove it from the webapp and add it into the play app**

## Docker

You can install and run Zeta fully automised via  docker-compose and also necessary for generator. This is the prefered way on Linux. First you have to install docker with docker-compose:

1. Install [docker](https://docs.docker.com/engine/installation/linux/) (tested with Docker version 1.12.3]
2. [Manage Docker as a non-root user](https://docs.docker.com/engine/installation/linux/linux-postinstall/)
3. Install [docker-compose](https://docs.docker.com/compose/install/)

After installation you have to checkout this repository on your local system.

### Services

List of docker-compose services:

- **images** Build generator images
- **api** Webpage, Auth, REST API,..
- **mongodb** NoSQL database
- **webapp** Webapplication for generators

### Getting Started

First all docker images need to be build.
```sh
docker-compose up images
```

To start all services simply run the below command.

```sh
docker-compose up -d api
```

## Create a Generator
Two steps are required to create a generator.

1. **Create a Docker Image**
First you need to create a docker image for the execution of a generator. A few examples can be found in the [images](./api/images/generator) directory. An example how to build an image can be found in [createDockerImages.sh](/api/createDockerImages.sh), which is primarly used by the images service (docker-compose).

2. **Make the Docker Image available**
After an image was created we need to create a document in the database which link to the docker image. The created document will make the previous created docker image available for users.

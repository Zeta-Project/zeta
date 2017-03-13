# Zeta
> Model-Driven Generation of Graphical Editors

## Overview
- **api** Webpage, Auth, REST API,..
- **couchbase-server** Database configuration
- **sync-gateway** Manage connection between Database and Clients
- **webapp** Webapplication for generators
- **proxy** Nginx settings
- **data** Provide mock data

## Prerequisites
The development stack is completely based on Docker.
Therefore you first need to install Docker and make sure
that your system fulfill the below dependencies.

**install dependencies:**

1) Install [docker](https://docs.docker.com/engine/installation/linux/) (tested with Docker version 1.12.3)

2) [Manage Docker as a non-root user](https://docs.docker.com/engine/installation/linux/linux-postinstall/)

3) Install docker-compose

4) Install [sbt](http://www.scala-sbt.org/)

5) Install Java JDK 8 


## Getting Started
First all docker images need to be build.
```sh
chmod +x setup.sh
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

## Initialize Data
To setup initial data run
```sh
docker run -i -t --network zeta_default modigen:data
```

## Create a Generator
Two steps are required to create a generator.

### 1. Create a Docker Image
First you need to create a docker image for the execution of a generator.
A few examples can be found in the [images](./api/images/generator) folder.

### 2. Make the Docker Image available
After an image was created we need to create a document in the database which link to the docker image.
The created document will make the previous created docker image available for users.
Images can be added in the [data](./data) folder
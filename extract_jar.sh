#!/bin/bash
set -e
docker build -t gephi-toolkit-forceatlas2-standalone .
docker create --name gtkfas gephi-toolkit-forceatlas2-standalone
docker cp gtkfas:/project/target/gephi-toolkit-forceatlas-0.0.1-jar-with-dependencies.jar .
docker rm gtkfas

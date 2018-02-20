gephi-toolkit-forceatlas2
========================

Docker image for ForceAtlas2 visualisation of graphs.

Usage:

```
docker run --rm -v $(pwd)/out:/output -v $(pwd)/input:/input:ro lukauskas/gephi-toolkit-forceatlas2 /input/LesMiserables.gexf --scale 100
```

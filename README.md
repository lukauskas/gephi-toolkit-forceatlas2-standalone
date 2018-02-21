gephi-toolkit-forceatlas2
========================

Docker image for ForceAtlas2 visualisation of graphs.

Usage:

```
docker run --rm -v $(pwd)/out:/output -v $(pwd)/input:/input:ro lukauskas/gephi-toolkit-forceatlas2 /input/LesMiserables.gexf --scale 10 -O /output
```

Also see `--help` for more usage options:

```
usage: Main [-h] [-O OUTDIR] [--gravity GRAVITY] [--scale SCALE] [--theta THETA] [--tolerance TOLERANCE] [--linlog {true,false}]
            [--weightinfluence WEIGHTINFLUENCE] [--stronggravity {true,false}] [--threads THREADS] [--duration DURATION] [--proportion PROPORTION]
            [--straight {true,false}] [--edgecolor {SOURCE,TARGET,MIXED,ORIGINAL}] input_file

Create ForceAtlas2 visualisation for graph.

positional arguments:
  input_file             File to create visualisation for

named arguments:
  -h, --help             show this help message and exit
  -O OUTDIR, --outdir OUTDIR
                         Output directory (default: .)

Layout options:
  --gravity GRAVITY      Gravity parameter of ForceAtlas2 (default: 1.0)
  --scale SCALE          Scale parameter of ForceAtlas2 (default: 10.0)
  --theta THETA          (default: 1.2)
  --tolerance TOLERANCE  (default: 1.0)
  --linlog {true,false}  (default: false)
  --weightinfluence WEIGHTINFLUENCE
                         (default: 1.0)
  --stronggravity {true,false}
                         (default: false)
  --threads THREADS      (default: 7)

Autolayout options:
  --duration DURATION    Duration in seconds to run the algorithm for (default: 60)
  --proportion PROPORTION
                         Proportion of time to allocate for fast ForceAtlas2.The rest is allocated for slow, no-overlap fa2 (default: 0.8)

Visualisation options:
  --straight {true,false}
                         Draw straight edges (default: false)
  --edgecolor {SOURCE,TARGET,MIXED,ORIGINAL}
                         Edge color mode (default: SOURCE)
```

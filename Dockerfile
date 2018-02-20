FROM maven:3.5.2-jdk-8

# Build dependencies
COPY project/pom.xml /project/pom.xml
WORKDIR /project

RUN ["mvn", "verify", "clean", "--fail-never"]

COPY project/src /project/src
RUN mvn -o package

VOLUME /output
VOLUME /input

ENTRYPOINT ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", \
            "-jar", "target/gephi-toolkit-forceatlas-0.0.1-jar-with-dependencies.jar", "-O", "/output"]

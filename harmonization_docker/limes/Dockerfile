FROM maven:3.3.9-jdk-8 as builder

WORKDIR /
RUN wget https://github.com/dice-group/LIMES/archive/1.5.0.zip
RUN unzip 1.5.0.zip && rm 1.5.0.zip

WORKDIR /LIMES-1.5.0/limes-core
RUN mvn clean package shade:shade -Dmaven.test.skip=true

FROM openjdk:8-jdk

# set workdir
WORKDIR /LIMES-1.5.0
COPY --from=builder /LIMES-1.5.0/limes-core/target/limes-core-1.5.0.jar limes-core-1.5.0.jar

# assign start command
CMD ["java", "-jar", "limes-core-1.5.0.jar", "-s"]
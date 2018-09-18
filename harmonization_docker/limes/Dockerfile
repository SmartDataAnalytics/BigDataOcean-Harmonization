FROM java:8

RUN apt-get update \
  && apt-get install -y maven unzip build-essential

WORKDIR /
RUN wget https://github.com/dice-group/LIMES/archive/1.5.0.zip
RUN unzip 1.5.0.zip && rm 1.5.0.zip

WORKDIR /LIMES-1.5.0
RUN mvn clean package shade:shade -Dmaven.test.skip=true

# set workdir
WORKDIR /LIMES-1.5.0/limes-core/target
# assign start command
CMD ["java", "-jar", "limes-core-1.5.0.jar", "-s"]
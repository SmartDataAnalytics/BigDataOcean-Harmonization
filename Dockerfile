FROM java:8

RUN apt-get update \
  && apt-get install -y maven unzip build-essential

RUN apt-get update \
  && apt-get install -y python3-pip python3-dev \
  && cd /usr/local/bin \
  && ln -s /usr/bin/python3 python \
  && pip3 install --upgrade pip 

# Install vim
RUN ["apt-get", "update"]
RUN ["apt-get", "install", "-y", "vim"]

# Install flask, flask bootstrap, requests, numpy, flask-testing
RUN pip install flask flask-bootstrap requests numpy Flask-Testing Flask-JWT apscheduler==2.1.2

# Copy files to BDOHarmonization
WORKDIR /BDOHarmonization/BigDataOcean-Harmonization
ADD . /BDOHarmonization
RUN mkdir /src
RUN mkdir /logs
RUN cd BigDataOcean-Harmonization/Backend/bdodatasets/ && mvn clean install -Dmaven.test.skip=true

# Install fuseki
RUN apt-get install -y ruby-full
WORKDIR /
RUN wget http://archive.apache.org/dist/jena/binaries/apache-jena-fuseki-3.4.0.zip
RUN unzip apache-jena-fuseki-3.4.0.zip && rm apache-jena-fuseki-3.4.0.zip
RUN chmod +x apache-jena-fuseki-3.4.0/bin/s-*

# Create app-volume
WORKDIR /
RUN mkdir -p /dataHarmonization/ontologiesN3

# Set timezone 
ENV TZ=Europe/Athens
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /BDOHarmonization/BigDataOcean-Harmonization/Frontend/Flask/
ENTRYPOINT ["python"]
CMD ["app.py"]


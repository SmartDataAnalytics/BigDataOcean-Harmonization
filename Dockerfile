FROM java:8

RUN apt-get update \
  && apt-get install -y maven unzip build-essential
RUN apt-get update \
  && apt-get install -y python3-pip python3-dev \
  && cd /usr/local/bin \
  && ln -s /usr/bin/python3 python \
  && pip3 install --upgrade pip 


# Install flask, flask bootstrap, requests, numpy, flask-testing
RUN pip install flask flask-bootstrap requests numpy Flask-Testing Flask-JWT apscheduler==2.1.2

# Copy files to BDOHarmonization
WORKDIR /BDOHarmonization
ADD . /BDOHarmonization
RUN mkdir /src
RUN mkdir /logs
RUN cd BigDataOcean-Harmonization/Backend/bdodatasets/ && mvn clean install -Dmaven.test.skip=true

WORKDIR /BDOHarmonization/BigDataOcean-Harmonization/Frontend/Flask/
ENTRYPOINT ["python"]
CMD ["app.py"]


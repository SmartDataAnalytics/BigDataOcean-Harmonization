FROM java:8

RUN apt-get update \
  && apt-get install -y maven unzip build-essential
RUN apt-get update \
  && apt-get install -y python3-pip python3-dev \
  && cd /usr/local/bin \
  && ln -s /usr/bin/python3 python \
  && pip3 install --upgrade pip 


# Install flask, flask bootstrap, requests, numpy, flask-testing
RUN pip install flask
RUN pip install flask-bootstrap
RUN pip install requests 
RUN pip install numpy
RUN pip install Flask-Testing


# Install bdoHarmonization
WORKDIR /BDOHarmonization
RUN git clone https://github.com/SmartDataAnalytics/BigDataOcean-Harmonization.git
RUN mkdir /src
RUN mkdir /logs
RUN cd BigDataOcean-Harmonization/Backend/bdodatasets/ && mvn clean install

# Install fuseki
WORKDIR /
RUN apt-get install -y ruby
RUN wget http://archive.apache.org/dist/jena/binaries/apache-jena-fuseki-3.4.0.zip
RUN unzip apache-jena-fuseki-3.4.0.zip && rm apache-jena-fuseki-3.4.0.zip
RUN chmod +x apache-jena-fuseki-3.4.0/bin/s-*

WORKDIR /
ADD bdoHarmonization.ttl /fuseki/configuration/bdoHarmonization.ttl
ADD TripleStore/ /fuseki/TripleStore/

WORKDIR /BDOHarmonization/BigDataOcean-Harmonization/Frontend/Flask/
ENTRYPOINT ["python"]
CMD ["app.py"]


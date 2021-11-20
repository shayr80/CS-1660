FROM openkbs/jdk-mvn-py3-x11
WORKDIR /Mini Search Engine
COPY ./Data Data
COPY ./Jar/searchEngineGUI.jar searchEngineGUI.jar
COPY ./projectJSON.json projectJSON.json
ENV GOOGLE_APPLICATION_CREDENTIALS projectJSON.json
CMD java -jar searchEngineGUI.jar

FROM adoptopenjdk/openjdk11
COPY target/helmholtz-cerebrum-*.jar app.jar
EXPOSE 8090
CMD java -jar app.jar

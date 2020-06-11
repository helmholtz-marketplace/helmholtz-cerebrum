FROM adoptopenjdk/openjdk11
COPY target/helmholtz-cerebrum-*.jar app.jar
CMD java -jar app.jar

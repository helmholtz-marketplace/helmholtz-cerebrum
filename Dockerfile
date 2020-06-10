FROM adoptopenjdk/openjdk11
COPY target/helmholtz-cerebrum-0.0.1-SNAPSHOT.jar
RUN java -jar helmholtz-cerebrum-0.0.1-SNAPSHOT.jar
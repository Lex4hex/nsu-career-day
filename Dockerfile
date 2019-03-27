FROM java:openjdk-8-jre
VOLUME /tmp
ADD build/libs/nsu-career-day-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]

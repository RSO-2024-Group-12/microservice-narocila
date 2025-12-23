FROM eclipse-temurin:21-jre
WORKDIR /work/
COPY target/quarkus-app/ /work/
EXPOSE 8083
CMD ["java", "-jar", "quarkus-run.jar"]
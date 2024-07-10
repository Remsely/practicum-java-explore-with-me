FROM amazoncorretto:11
COPY target/*.jar main-svc.jar
ENTRYPOINT ["java", "-jar", "/main-svc.jar"]
EXPOSE 8080
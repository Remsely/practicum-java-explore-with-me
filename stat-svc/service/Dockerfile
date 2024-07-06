FROM amazoncorretto:11
COPY target/*.jar stat-svc.jar
ENTRYPOINT ["java", "-jar", "/stat-svc.jar"]
EXPOSE 9090
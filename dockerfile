FROM amazoncorretto:17

ADD target/PostCalendarBackend-0.0.1-SNAPSHOT.jar PostCalendarBackend.jar

ENTRYPOINT ["java", "-jar", "PostCalendarBackend.jar"]

EXPOSE 10088

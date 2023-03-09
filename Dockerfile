FROM openjdk:11
COPY target/language-measures-latest.jar language-measures-latest.jar
ENTRYPOINT ["java", "-jar", "/language-measures-latest.jar"]
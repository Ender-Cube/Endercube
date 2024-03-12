FROM eclipse-temurin:21-jdk
COPY /build/libs/endercube-?.?.?.jar app.jar
EXPOSE 25565/tcp
CMD ["java","-jar","/app.jar"]
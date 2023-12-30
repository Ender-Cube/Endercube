FROM eclipse-temurin:21-jdk
COPY /build/libs/Endercube-?.?.?.jar app.jar
EXPOSE 25565/tcp
CMD ["java","-jar","/app.jar"]
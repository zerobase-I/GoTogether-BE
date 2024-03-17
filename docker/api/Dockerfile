FROM khipu/openjdk17-alpine
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul", "-jar","/app.jar"]
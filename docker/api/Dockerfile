FROM khipu/openjdk17-alpine
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-Duser.timezone=Asia/Seoul", "-jar","/app.jar"]
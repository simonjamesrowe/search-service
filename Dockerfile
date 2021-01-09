FROM openjdk:11-jre-slim
ENV PORT 8080
ENV CLASSPATH /opt/lib
EXPOSE 8080
COPY build/libs/*.jar /opt/app.jar
WORKDIR /opt
CMD ["java", "-jar", "app.jar"]

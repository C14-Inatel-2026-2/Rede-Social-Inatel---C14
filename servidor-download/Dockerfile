FROM eclipse-temurin:25-jre
WORKDIR /app
COPY app.jar app.jar
COPY lib ./lib
EXPOSE 8080 3000-3005
CMD ["java", "-cp", "app.jar:lib/*", "Main"]

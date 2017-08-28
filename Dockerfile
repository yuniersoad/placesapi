FROM maven:latest
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package -DskipTests

FROM java:8-jdk-alpine
WORKDIR /app
COPY --from=0 /usr/src/app/target/api-0.0.1-SNAPSHOT.jar .
EXPOSE 5005 8080
ENTRYPOINT ["java","-jar", "/app/api-0.0.1-SNAPSHOT.jar"]


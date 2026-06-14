FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /build

COPY java-server/pom.xml .
COPY java-server/mvnw .
COPY java-server/.mvn .mvn
COPY java-server/src/ ./src

RUN chmod +x mvnw
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /build/target/*.jar application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]

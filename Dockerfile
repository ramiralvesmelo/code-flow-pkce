# ====== Etapa 1: Build ======
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

ARG APP_MODULE=app-client
ARG MAVEN_ARGS=-DskipTests=true
ARG ARTIFACT_FILE=app-client/target/app-client-0.0.1-SNAPSHOT.war

COPY . .
RUN mvn -B -f ${APP_MODULE}/pom.xml -am clean package ${MAVEN_ARGS}
RUN test -f "${ARTIFACT_FILE}" && cp "${ARTIFACT_FILE}" /build/app.war

# ====== Etapa 2: Runtime ======
FROM eclipse-temurin:21-jre
WORKDIR /app

# (opcional) se for usar healthcheck HTTP
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

ENV JAVA_OPTS="" \
    SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=default

COPY --from=builder /build/app.war /app/app.war

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${SERVER_PORT} -jar /app/app.war"]
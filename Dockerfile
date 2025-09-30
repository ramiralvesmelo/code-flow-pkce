# ====== Etapa 1: Build ======
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

ARG APP_MODULE=.
ARG MAVEN_ARGS=-DskipTests=true
ARG ARTIFACT_FILE=

COPY . .

RUN if [ "$APP_MODULE" = "." ]; then \
      mvn -B -f pom.xml clean package ${MAVEN_ARGS}; \
    else \
      test -f "$APP_MODULE/pom.xml"; \
      mvn -B -f "$APP_MODULE/pom.xml" -am clean package ${MAVEN_ARGS}; \
    fi

RUN set -eux; \
    if [ -n "$ARTIFACT_FILE" ]; then \
      test -f "$ARTIFACT_FILE"; \
      cp "$ARTIFACT_FILE" /build/app.bin; \
    else \
      CAND="$(find /build -type f -path '*/target/*.*ar' \
               ! -name '*sources.jar' ! -name '*javadoc.jar' \
               -printf '%s %p\n' | sort -nr | head -n1 | cut -d' ' -f2-)"; \
      echo "Detected artifact: ${CAND:-<none>}"; \
      test -n "$CAND"; \
      cp "$CAND" /build/app.bin; \
    fi

# ====== Etapa 2: Runtime ======
FROM eclipse-temurin:21-jre
WORKDIR /app

# instala curl (base Debian/Ubuntu)
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

ENV JAVA_OPTS="" \
    SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=default \
    # URL do health do Keycloak via rede Docker (ajuste se o serviço tiver outro nome)
    KEYCLOAK_HEALTH_URL=http://keycloak:9000/health/ready

COPY --from=builder /build/app.bin /app/app.bin

EXPOSE 8080

# healthcheck desta app DEPENDENDO do Keycloak estar pronto
HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=30 \
  CMD curl -fsS "$KEYCLOAK_HEALTH_URL" || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${SERVER_PORT} -jar /app/app.bin"]
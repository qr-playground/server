
######## BUILD ########
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew bootJar -x test

######## RUNTIME ######
FROM eclipse-temurin:21-jre-alpine

# pinpoint agent
ARG PINPOINT_VERSION=3.0.2
ENV PINPOINT_HOME=/opt/pinpoint-agent-${PINPOINT_VERSION}

RUN apk add --no-cache curl tar \
 && curl -fL "https://repo1.maven.org/maven2/com/navercorp/pinpoint/pinpoint-agent/${PINPOINT_VERSION}/pinpoint-agent-${PINPOINT_VERSION}.tar.gz" \
      | tar -xz -C /opt \
 && rm -rf /opt/pinpoint-agent-${PINPOINT_VERSION}/quickstart

WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
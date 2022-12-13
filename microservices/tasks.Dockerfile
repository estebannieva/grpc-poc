ARG VERSION=11.0.15

FROM openjdk:${VERSION}-jdk-slim as BUILD
ENV SRC=tasks
ENV DESTN=/app
WORKDIR ${DESTN}
COPY gradle/ ${DESTN}/gradle/
COPY gradlew ${DESTN}/gradlew
COPY ${SRC}/src ${DESTN}/src/
COPY ${SRC}/build.gradle ${DESTN}/build.gradle
RUN /bin/bash -c './gradlew build -Dorg.gradle.java.home=$JAVA_HOME -x test'

FROM openjdk:${VERSION}-jre-slim
ENV SRC=/app
ENV API_PORT=8081
ENV GRPC_PORT=50051
COPY --from=BUILD ${SRC}/build/libs/*-SNAPSHOT.jar /app.jar
EXPOSE ${API_PORT}
EXPOSE ${GRPC_PORT}
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM openjdk:17-slim as Builder
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar --stacktrace

FROM openjdk:17-slim
WORKDIR /app
COPY --from=Builder /app/build/libs/api-0.0.1.jar .

RUN apt-get update && apt-get install -y net-tools && apt-get install -y iproute2

EXPOSE 8080

ARG ACTIVE_PROFILE
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
CMD ["/entrypoint.sh"]

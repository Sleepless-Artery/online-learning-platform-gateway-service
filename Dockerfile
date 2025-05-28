# Этап сборки
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Этап рантайма
FROM eclipse-temurin:17-jre-jammy as runtime
WORKDIR /app

# Копируем только JAR из этапа сборки
COPY --from=builder /app/target/gateway-service-*.jar app.jar

# Оптимизации для JVM в контейнере
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
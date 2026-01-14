# Build - Folosim Maven pentru a compila codul
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiem fișierul pom.xml si sursele
COPY pom.xml .
COPY src ./src
# Construim arhiva .jar fara a rula testele (pentru viteza)
RUN mvn clean package -DskipTests

# Run - Folosim un JRE ușor pentru execuție
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiem doar jar-ul din etapa de build
COPY --from=build /app/target/*.jar app.jar
# Expunem portul pe care ruleaza Spring Boot
EXPOSE 8080
# Comanda de pornire
ENTRYPOINT ["java", "-jar", "app.jar"]
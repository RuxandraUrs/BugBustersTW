# Folosim o imagine de bază cu Java
FROM eclipse-temurin:21-jdk-alpine
# Setăm directorul de lucru
WORKDIR /app

# Copiem fișierul jar generat (asigură-te că numele este generic sau îl redenumești)
# Sfat: În pom.xml poți seta <finalName>app</finalName> ca să fie mereu app.jar
COPY target/*.jar app.jar

# Comanda de start
ENTRYPOINT ["java", "-jar", "app.jar"]
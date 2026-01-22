# Paso 1: Usar una imagen base de Java (JDK 17 o la que uses)
FROM eclipse-temurin:17-jdk-alpine

# Paso 2: Crear un directorio para la app
WORKDIR /app

# Paso 3: Copiar el archivo JAR generado a la imagen
# Nota: Asegúrate de que el nombre coincida con el que genera tu build
COPY target/*.jar app.jar

# Paso 4: Exponer el puerto (Render/Railway suelen usar el 8080 por defecto)
EXPOSE 8080

# Paso 5: Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
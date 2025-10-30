# ParcialOneIntegracionSistemas
Proyecto Apache Camel File transfer, transformation y APi
# EcoLogistics — Integración (prototipo)

- Proyecto Java 17 + Maven con Spring Boot y Apache Camel.
- Lee envíos desde `input/envio.csv`, los transforma a JSON, guarda en memoria y escribe un archivo JSON en `output/`.
- Expone API REST para consultar y registrar envíos.
- Genera documentación OpenAPI (Swagger UI) si está disponible `springdoc`.

Requisitos
- JDK 17 (recomendado). Si usas JDK >17, el proyecto se compila con `release 17`.
- Maven o usar el wrapper proporcionado (`mvnw` / `mvnw.cmd`).
- PowerShell / Terminal (instrucciones incluidas).
- (Opcional) Node.js + npm o Docker para lint de OpenAPI (Spectral).

Clonar el repositorio
1. En tu máquina:
   git clone <URL_DEL_REPOSITORIO>
2. Entrar en la carpeta:
   cd <repo>/eco-logistics

Estructura importante
- input/envio.csv — archivo de entrada (ejemplo incluido).
- output/ — carpeta donde se escriben los JSON transformados.
- src/main/java/.../routes/EnvioRoute.java — flujo Camel (lectura, transformación, escritura).
- src/main/java/.../services/EnvioService.java — almacenamiento en memoria (ConcurrentHashMap).
- src/main/java/.../controllers/EnvioController.java — API REST.
- src/main/resources/application.properties — configuración (puerto, etc.).
- pom.xml — dependencias.

Compilar y ejecutar (Windows PowerShell)
- Compilar:
  .\mvnw.cmd clean package
  (o `mvn clean package` si no tienes wrapper)
- Ejecutar con Maven (ver logs):
  .\mvnw.cmd spring-boot:run
- Ejecutar en puerto específico (ej. 8081):
  .\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
- Ejecutar JAR:
  java -jar .\target\eco-logistics-0.0.1-SNAPSHOT.jar --server.port=8081

Compilar y ejecutar (Linux / macOS / WSL)
- ./mvnw clean package
- ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
- java -jar target/eco-logistics-0.0.1-SNAPSHOT.jar --server.port=8081

Verificar que se procesó el CSV
- Al arrancar, revisar logs en la consola: buscar líneas como:
  - [INFO] Archivo cargado: envio.csv
  - [INFO] JSON generado en: output/...
- Confirmar existencia del archivo:
  - Windows: Get-ChildItem .\output\*.json
  - Bash: ls -l output/*.json

Swagger / OpenAPI
- Swagger UI (visual): http://localhost:8081/swagger-ui/index.html
- Spec JSON: http://localhost:8081/v3/api-docs
- Spec YAML: http://localhost:8081/v3/api-docs.yaml (si está disponible)
- Guardar spec localmente:
  curl.exe http://localhost:8081/v3/api-docs.yaml -o src\main\resources\openapi.yaml

Lint / Validación del spec (Spectral)
- Con npm:
  npm install -g @stoplight/spectral
  spectral lint src/main/resources/openapi.yaml
- Con Docker:
  docker run --rm -v ${PWD}:/tmp stoplight/spectral lint /tmp/src/main/resources/openapi.yaml

Scripts incluidos
- scripts/build.ps1 / build.sh — compilar
- scripts/run.ps1 / run.sh — ejecutar con mvn
- scripts/run-jar.ps1 / run-jar.sh — ejecutar JAR
- scripts/get-openapi.ps1 — descargar openapi.yaml desde la app

Configuración rápida (puerto)
- Cambiar puerto temporalmente al arrancar:
  -Dspring-boot.run.arguments="--server.port=8081"
- O editar `src/main/resources/application.properties`:
  server.port=8081

Notas y solución rápida de problemas
- Puerto en uso: elegir otro puerto o eliminar la app que usa el puerto:
  netstat -ano | findstr :8081
  taskkill /PID <pid> /F
- Si no ves logs de Camel: confirmar que `input/envio.csv` existe:
  Get-ChildItem .\input -Filter envio.csv
- Persistencia: datos en memoria (se pierden al reiniciar). Cambiar a DB si es necesario.
- Logs de error: pegar la traza completa en caso de excepciones.






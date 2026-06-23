# Navium | Microservicio de Andenes (ms-andenes)

En las operaciones del Puerto el Andén representa el espacio físico donde los contenedores son descargados o cargados. Este microservicio de Andenes es el responsable de orquestar el espacio fisico del puerto en tiempo real.

Su objetivo es asegurar que el flujo de carga y descarga de contenedores (aquellos que ingresan y egresan del puerto) sea rápido y eficaz.

### Responsabilidades del Microservicio

1. **Administración de Andenes (CRUD):** crea, consulta y elimina andenes, manteniendo unicidad por `zona + numero` y generando el `codigo` compuesto.
2. **Disponibilidad y estados operativos:** permite consultar andenes por estado (disponible/ocupado/mantenimiento) y aplicar reglas de negocio para cambios de estado.
3. **Asignación y liberación de andenes:** registra asignaciones activas (contenedor + transporte) y su historial (inicio/fin), y libera el anden cuando finaliza la asignación.
4. **Manejo consistente de errores:** estandariza respuestas de error (400/404/500) para facilitar el consumo desde frontends.

### Dependencias

- **Java:** 21
- **Framework:** Spring Boot 4.0.5
- **API:** Spring WebMVC
- **Persistencia:** Spring Data JPA + Hibernate
- **Base de datos:** PostgreSQL
- **Build:** Maven (incluye Maven Wrapper `mvnw` / `mvnw.cmd`)
- **Documentación API:** OpenAPI 3 + Swagger UI (springdoc)
- **Utilidades:** Lombok

### Configuración de entorno de desarrollo

#### Entorno local sin Docker

- Por defecto el proyecto utiliza el puerto `8083`
- **JDK 21** instalado y configurado en `JAVA_HOME`.
- **PostgreSQL** corriendo en local (ayuda con pgAdmin4).
- Credenciales y URL configuradas en `src/main/resources/application.properties` (perfil por defecto).

Para compilación ejecuta:
```bash
./mvnw clean package
```

Ejecuta la aplicación localmente
```bash
./mvnw.cmd spring-boot:run
```

#### Entorno con Docker

Dirígete al directorio del proyecto, y crea un archivo `.env` como `.env.example` especificado en el proyecto.

```bash
cd navium-ms-andenes/
```
Levanta el contenedor. Docker se encarga de ejecutar la creación de la imagen (con `Dockerfile`) automáticamente con `--build`.
```bash
docker compose up --build -d
```

**Nota:** En caso de modificar tras el levantamiento del contenedor, **debes** eliminar el contenedor y luego volver a levantarlo.

### Documentación de API (Swagger UI)

La documentación de endpoints utiliza **OpenAPI/Swagger UI**.

1. Primero levanta el servicio.
2. Abre en el navegador:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

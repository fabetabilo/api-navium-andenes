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
- **Seguridad:** JWT (`jjwt`) + `navium-security-lib`
- **Documentación API:** OpenAPI 3 + Swagger UI (springdoc)
- **Utilidades:** Lombok

### Configuración de entorno de desarrollo

1. **JDK 21** instalado y configurado en `JAVA_HOME`.
2. **PostgreSQL** corriendo en local.
3. Credenciales y URL configuradas en `src/main/resources/application-dev.properties` (perfil por defecto).

Por defecto el proyecto usa:

- Perfil: `dev` (`spring.profiles.active=dev`)
- Puerto: `8080` (`server.port=8080`)

### Ejecución local

Desde la raíz del repositorio:

```bash
# Windows
./mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Si necesitas cambiar la base de datos, ajusta `application-dev.properties` (URL/usuario/contraseña). En `dev` el esquema se actualiza automáticamente (`spring.jpa.hibernate.ddl-auto=update`).

### Documentación de API (Swagger UI)

La documentación de endpoints utiliza **OpenAPI/Swagger UI**.

1. Primero levanta el servicio.
2. Abre en el navegador:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Probar endpoints protegidos (JWT)

Si los endpoints requieren autenticación, en Swagger UI usa el botón **Authorize** y pega el token en formato:

`Bearer <tu_jwt>`

Luego ejecuta las operaciones desde la UI con el header `Authorization` aplicado.
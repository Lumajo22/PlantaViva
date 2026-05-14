# 🌿 PlantaViva — Backend

Backend Spring Boot del sistema **PlantaViva — Control Inteligente de Cultivos**.

## 📋 Stack tecnológico

- **Spring Boot 4.0** — Framework principal
- **Java 25** — Lenguaje (LTS)
- **PostgreSQL 15+** — Base de datos
- **Spring Data JPA** — Persistencia ORM
- **Spring Validation** — Validación de DTOs
- **SpringDoc OpenAPI** — Documentación automática (Swagger)
- **Lombok** — Reducción de código repetitivo
- **JUnit 5** — Pruebas unitarias

## 📁 Estructura del proyecto

```
plantaviva-backend/
├── pom.xml
└── src/main/
    ├── java/com/plantaviva/
    │   ├── PlantaVivaApplication.java   ← Punto de entrada
    │   ├── config/                       ← Configuraciones (Security, CORS)
    │   ├── controller/                   ← Endpoints REST
    │   ├── dto/                          ← Objetos de transferencia
    │   ├── entity/                       ← Entidades JPA
    │   ├── enums/                        ← Tipos enumerados
    │   ├── exception/                    ← Manejo global de errores
    │   ├── repository/                   ← Spring Data JPA
    │   └── service/                      ← Lógica de negocio
    └── resources/
        └── application.properties        ← Configuración
```

## ⚙️ Configuración previa

### 1. Verificar Java 25

```bash
java --version
# Debe mostrar: openjdk 25.x.x
```

### 2. Verificar PostgreSQL

Asegúrate de que tu base de datos `plantaviva` esté creada y que el script `PlantaViva_schema.sql` ya esté ejecutado.

Para encontrar el puerto de tu PostgreSQL, en pgAdmin:
- Click derecho sobre el servidor → **Properties** → pestaña **Connection** → campo **Port** (default: `5432`).

### 3. Editar `application.properties`

Si tu PostgreSQL usa **otro puerto, usuario o contraseña distinto**, modifica:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/plantaviva
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASEÑA
```

## 🚀 Ejecutar el proyecto

### Opción 1 — Maven Wrapper (recomendado en VS Code)

```bash
./mvnw spring-boot:run
```

### Opción 2 — Maven instalado globalmente

```bash
mvn spring-boot:run
```

### Opción 3 — Desde VS Code

Instala la extensión **Spring Boot Extension Pack** y haz click en **Run** sobre `PlantaVivaApplication.java`.

## 🔍 Verificar que funciona

Una vez iniciado, abre estas URLs en tu navegador:

| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/api/plantas` | Lista de plantas (JSON) |
| `http://localhost:8080/swagger-ui.html` | Documentación interactiva |
| `http://localhost:8080/v3/api-docs` | OpenAPI spec en JSON |

## 🧪 Probar el CRUD desde Swagger

1. Abre `http://localhost:8080/swagger-ui.html`
2. Expande la sección **Plantas**
3. Prueba **POST /api/plantas** con este JSON:

```json
{
  "nombre": "Tomate cherry",
  "especie": "Solanum lycopersicum",
  "ubicacion": "Invernadero A",
  "descripcion": "Tomate de variedad pequeña",
  "fechaSiembra": "2024-04-15",
  "activa": true
}
```

4. Luego prueba **GET /api/plantas** para ver tu planta recién creada.

## 📚 Endpoints implementados

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/plantas` | Lista paginada |
| `GET` | `/api/plantas/{id}` | Obtener por ID |
| `POST` | `/api/plantas` | Crear nueva |
| `PUT` | `/api/plantas/{id}` | Actualizar |
| `DELETE` | `/api/plantas/{id}` | Eliminar (cascada) |

## 🔮 Próximos pasos

Este backend tiene actualmente el **CRUD completo de Plantas**. En los siguientes pasos del proyecto añadiremos:

- ✅ Paso 3 (actual): Plantas CRUD funcional
- ⏳ Paso 3.2: Sensores y Lecturas con detección de anomalías (IA)
- ⏳ Paso 3.3: Alertas automáticas con correo Gmail
- ⏳ Paso 4: Frontend React + Vite
- ⏳ Paso 5: Pruebas JUnit + Selenium
- ⏳ Paso 6: Docker + GitHub Actions
- ⏳ Paso 7: OAuth Google + i18n

## 🐛 Solución de problemas comunes

### Error: `Cannot connect to PostgreSQL`
- Verifica que PostgreSQL esté corriendo
- Confirma el puerto y la contraseña en `application.properties`

### Error: `Schema validation: missing table`
- Ejecuta el script `PlantaViva_schema.sql` en tu BD
- O cambia temporalmente `spring.jpa.hibernate.ddl-auto=update` para que JPA cree las tablas

### Error de Lombok: `cannot find symbol`
- En VS Code instala la extensión **Lombok Annotations Support for VS Code**
- En IntelliJ ve a Settings → Plugins → busca "Lombok"

---

**Equipo PlantaViva** • Proyecto académico • Versión 1.0.0

<div align="center">

# 🏆 DOSW — Microservicio de Gamificación

### *"Gana monas, acumula XP y compite en el ranking semanal"*

---

### 🛠️ Stack Tecnológico

![Java](https://img.shields.io/Mona/Java-21-007396?style=for-the-Mona&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/Mona/Spring%20Boot-4.0.6-6DB33F?style=for-the-Mona&logo=spring-boot&logoColor=white)
![MongoDB](https://img.shields.io/Mona/MongoDB-Latest-47A248?style=for-the-Mona&logo=mongodb&logoColor=white)

### ☁️ Infraestructura & Calidad

![Railway](https://img.shields.io/Mona/Railway-Deploy-0B0D0E?style=for-the-Mona&logo=railway&logoColor=white)
![Docker](https://img.shields.io/Mona/Docker-Container-2496ED?style=for-the-Mona&logo=docker&logoColor=white)
![Maven](https://img.shields.io/Mona/Maven-Build-C71A36?style=for-the-Mona&logo=apache-maven&logoColor=white)

### 🏗️ Arquitectura

![Hexagonal](https://img.shields.io/Mona/Architecture-Hexagonal-blueviolet?style=for-the-Mona)
![Clean Architecture](https://img.shields.io/Mona/Clean-Architecture-blue?style=for-the-Mona)
![REST API](https://img.shields.io/Mona/REST-API-009688?style=for-the-Mona)

</div>

---

## 📑 Tabla de Contenidos

1. [👤 Integrantes](#1--integrantes)
2. [🎯 Objetivo del Microservicio](#2--objetivo-del-microservicio)
3. [⚡ Funcionalidades Principales](#3--funcionalidades-principales)
4. [📋 Estrategia de Versionamiento y Branches](#4--manejo-de-estrategia-de-versionamiento-y-branches)
   - [4.1 Convenciones para crear ramas](#41-convenciones-para-crear-ramas)
   - [4.2 Convenciones para crear commits](#42-convenciones-para-crear-commits)
5. [⚙️ Tecnologías Utilizadas](#5--tecnologias-utilizadas)
6. [🧩 Funcionalidad](#6--funcionalidad)
7. [📊 Diagramas](#7--diagramas)
8. [⚠️ Manejo de Errores](#8--manejo-de-errores)
9. [🧪 Evidencia de Pruebas y Ejecución](#9--evidencia-de-las-pruebas-y-como-ejecutarlas)
10. [🗂️ Organización del Código](#10--codigo-de-la-implementacion-organizado-en-las-respectivas-carpetas)
11. [🚀 Ejecución del Proyecto](#11--ejecucion-del-proyecto)
12. [☁️ CI/CD y Despliegue en Railway](#12--evidencia-de-cicd-y-despliegue-en-railway)
13. [🤝 Contribuciones](#13--contribuciones)

---

## 1. 👤 Integrantes:

- David Shadday Correa Gonzalez
- Juan Camilo Melo Cupitra
- Juan Esteban Tellez Valencia
- Stiven Esneider Pardo Gutierrez

## 2. 🎯 Objetivo del microservicio

El microservicio de Gamificación tiene como objetivo gestionar el sistema de recompensas y motivación dentro de la plataforma DOSW. Este servicio administra el catálogo de monas (*monas*), otorga XP a los estudiantes al completar logros, rastrea el progreso hacia cada mona, y mantiene un ranking semanal de los participantes más activos. Además, implementa reglas de negocio como la unicidad de monas por usuario, el reinicio automático del XP semanal cada lunes y la participación opcional en el ranking, garantizando una experiencia de gamificación justa y motivadora para toda la comunidad estudiantil DOSW.

---

## 3. ⚡ Funcionalidades principales

<div align="center">

<table>
  <thead>
    <tr>
      <th>💡 Funcionalidad</th>
      <th>Descripción</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Gestión de Monas</strong></td>
      <td>Crea y administra el catálogo de monas con nombre, descripción, categoría (COMMON → LEGENDARY), recompensa de XP e ícono.</td>
    </tr>
    <tr>
      <td><strong>Otorgamiento de XP</strong></td>
      <td>Asigna monas a usuarios acumulando XP total y semanal automáticamente. Si el usuario no tiene perfil, se crea uno nuevo al momento del otorgamiento.</td>
    </tr>
    <tr>
      <td><strong>Progreso hacia Monas</strong></td>
      <td>Expone el avance del usuario hacia cada mona aún no desbloqueada, mostrando el valor actual, el requerido y el porcentaje completado.</td>
    </tr>
    <tr>
      <td><strong>Ranking Semanal</strong></td>
      <td>Mantiene un ranking de los usuarios más activos ordenados por XP semanal. La participación es opcional y puede activarse o desactivarse en cualquier momento.</td>
    </tr>
    <tr>
      <td><strong>Reset Semanal Automático</strong></td>
      <td>Un scheduler reinicia el XP semanal de todos los participantes del ranking cada lunes a medianoche, manteniendo la competencia fresca y justa.</td>
    </tr>
    <tr>
      <td><strong>Notificaciones de Eventos</strong></td>
      <td>Publica eventos de forma asíncrona al servicio de notificaciones (M05) cuando un usuario desbloquea una nueva mona.</td>
    </tr>
  </tbody>
</table>

</div>


## 4. 📋 Manejo de Estrategia de versionamiento y branches

### Estrategia de Ramas (Git Flow)

### Ramas y propósito
- Manejaremos GitFlow, el modelo de ramificación para el control de versiones de Git

#### `main`
- **Propósito:** rama **estable** con la versión final (lista para demo/producción).
- **Reglas:**
    - Solo recibe merges desde `release/*` y `hotfix/*`.
    - Cada merge a `main` debe crear un **tag** SemVer (`vX.Y.Z`).
    - Rama **protegida**: PR obligatorio, 1–2 aprobaciones, checks de CI en verde.

#### `develop`
- **Propósito:** integración continua de trabajo; base de nuevas funcionalidades.
- **Reglas:**
    - Recibe merges desde `feature/*` y también desde `release/*` al finalizar un release.
    - Rama **protegida** similar a `main`.

#### `feature/*`
- **Propósito:** desarrollo de una funcionalidad, refactor o spike.
- **Base:** `develop`.
- **Cierre:** se fusiona a `develop` mediante **PR**


#### `release/*`
- **Propósito:** congelar cambios para estabilizar pruebas, textos y versiones previas al deploy.
- **Base:** `develop`.
- **Cierre:** merge a `main` (crear **tag** `vX.Y.Z`) **y** merge de vuelta a `develop`.
- **Ejemplo de nombre:**  
  `release/1.3.0`

#### `hotfix/*`
- **Propósito:** corregir un bug **crítico** detectado en `main`.
- **Base:** `main`.
- **Cierre:** merge a `main` (crear **tag** de **PATCH**) **y** merge a `develop` para mantener paridad.
- **Ejemplos de nombre:**  
  `hotfix/fix-ranking-reset`, `hotfix/fix-Mona-duplicate`


---

### 4.1 Convenciones para **crear ramas**

#### `feature/*`
**Formato:**
```
feature/[nombre-funcionalidad]
```

**Ejemplos:**
- `feature/gestionMonas`
- `feature/rankingSemanal`

**Reglas de nomenclatura:**
- Usar **PascalCase** (palabras separadas por mayúscula)
- Máximo 50 caracteres en total
- Descripción clara y específica de la funcionalidad

#### `release/*`
**Formato:**
```
release/[version]
```
**Ejemplo:** `release/1.0.0`

#### `hotfix/*`
**Formato:**
```
hotfix/[descripcion-breve-del-fix]
```
**Ejemplos:**
- `hotfix/corregirResetSemanal`
- `hotfix/fixDuplicadoMona`

---

### 4.2 Convenciones para **crear commits**

#### **Formato:**
```
[tipo]: [descripción específica de la acción]
```

#### **Tipos de commit:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de errores
- `docs`: Cambios en documentación

## 5. ⚙️ Tecnologías Utilizadas


| **Tecnología / Herramienta** | **Uso principal en el proyecto** |
|------------------------------|----------------------------------|
| **Java 21 (OpenJDK)** | Lenguaje de programación base del microservicio backend, con soporte a records, switch expressions y mejoras modernas. |
| **Spring Boot 4.0.6** | Framework principal para construir el microservicio, exponiendo APIs REST y gestionando configuración e inyección de dependencias. |
| **Spring Web** | Exposición de endpoints REST (controladores HTTP) dentro de la arquitectura hexagonal. |
| **Spring Security** | Configuración de seguridad del microservicio; protege endpoints mediante validación de tokens JWT. |
| **Spring Data MongoDB** | Integración del microservicio con MongoDB usando el patrón Repository y puertos/adaptadores. |
| **MongoDB** | Base de datos NoSQL principal, con colecciones para `Monas` y `user_gamification`. Desplegada en Railway. |
| **JJWT 0.12.6** | Validación y parseo de tokens JWT para autenticar las peticiones entrantes al microservicio. |
| **Apache Maven** | Gestión de dependencias, empaquetado del microservicio y automatización de builds en los pipelines CI/CD. |
| **Lombok** | Reducción de código repetitivo con anotaciones como `@Getter`, `@Builder`, `@Data` y `@RequiredArgsConstructor`. |
| **JUnit 5** | Framework de pruebas unitarias para validar la lógica de dominio y casos de uso en el microservicio. |
| **Mockito** | Simulación de dependencias (puertos, repositorios) en pruebas unitarias sin acceder a infraestructura real. |
| **Flapdoodle Embed MongoDB** | MongoDB embebido para pruebas de integración sin necesidad de una instancia externa de MongoDB. |
| **Swagger (OpenAPI 3 / springdoc)** | Generación automática de documentación y prueba interactiva de los endpoints REST. |
| **Postman** | Validación manual de peticiones y respuestas JSON de los endpoints (`POST`, `GET`, `PATCH`). |
| **Docker** | Contenerización del microservicio con build multi-stage para despliegues aislados y consistentes. |
| **Docker Compose** | Orquestación local de la aplicación y MongoDB para desarrollo y pruebas. |
| **Railway** | Plataforma cloud donde se despliega el contenedor Docker del microservicio junto a su base de datos MongoDB. |


> 🧠 **Stack tecnológico seleccionado** para asegurar **escalabilidad**, **modularidad**, **seguridad**, **trazabilidad** y **mantenibilidad**, aplicando buenas prácticas de ingeniería de software.

## 6. 🧩 Funcionalidades

---

### 🔑 Funcionalidades principales

### 1️⃣ Crear Mona

Permite registrar una nueva mona en el catálogo del sistema. Solo accesible por usuarios con rol ADMIN mediante token JWT válido.

**Endpoint principal:**  
`POST /api/v1/Monas`

---

### 📦 Estructura de la Solicitud (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| name | String | Obligatorio, no vacío | Nombre único de la mona. |
| description | String | Obligatorio, no vacío | Descripción del logro representado. |
| category | Enum | Obligatorio | Rareza de la mona (COMMON, UNCOMMON, RARE, EPIC, LEGENDARY). |
| xpReward | Integer | Mínimo 1 | Puntos de XP que otorga la mona al ser ganada. |
| iconUrl | String | Opcional | URL del ícono visual de la mona. |

</div>

---

### 📦 Estructura de la Respuesta (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| id | String | Identificador único de la mona (ObjectId de MongoDB). |
| name | String | Nombre de la mona. |
| description | String | Descripción del logro. |
| category | Enum | Categoría de rareza. |
| xpReward | Integer | XP otorgado al ganar la mona. |
| iconUrl | String | URL del ícono. |
| createdAt | LocalDateTime | Fecha y hora de creación. |

</div>


---

### ✅ Happy Path (Ejemplo de Uso Exitoso)

1. El administrador envía un POST con los datos de la mona e incluye su JWT Bearer Token.
2. El sistema valida el token y verifica que el usuario tiene rol ADMIN.
3. Se crea la mona como activa en la colección `Monas` de MongoDB.
4. Se retorna `201 CREATED` con los datos de la mona.

**Request (Solicitud):**
```json
POST /api/v1/Monas
Headers: Authorization: Bearer <token>

{
  "name": "Primer Parche",
  "description": "Asististe a tu primer parche en DOSW",
  "category": "COMMON",
  "xpReward": 100,
  "iconUrl": "https://cdn.dosw.app/Monas/primer-parche.png"
}
```

**Response (Respuesta):**
```json
{
  "id": "6641b3c8e4f2a30012345678",
  "name": "Primer Parche",
  "description": "Asististe a tu primer parche en DOSW",
  "category": "COMMON",
  "xpReward": 100,
  "iconUrl": "https://cdn.dosw.app/Monas/primer-parche.png",
  "createdAt": "2026-05-08T10:00:00"
}
```

---

### 🖼️ Diagrama de Secuencia

![Diagrama de Secuencia Crear Mona](docs/images/crearMona.png)

<details>
<summary><strong>🟢 Explicación del Flujo</strong></summary>

El proceso inicia cuando el administrador envía un POST al `MonaController` con el JWT en el header. El `JwtAuthFilter` valida el token y extrae el rol del usuario. El `CreateMonaUseCase` recibe el request, construye la entidad de dominio `Mona` y la persiste en MongoDB a través del `MonaRepositoryAdapter`. Se retorna la respuesta con la mona creada.

</details>

---

### 📊 Tipos de errores manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/Mona/400-Bad_Request-red?style=flat) | Datos inválidos | `"El nombre de la mona es obligatorio"` |
| ![400](https://img.shields.io/Mona/400-Bad_Request-red?style=flat) | XP inválido | `"La recompensa de XP debe ser mayor que 0"` |
| ![401](https://img.shields.io/Mona/401-Unauthorized-red?style=flat) | Token ausente o inválido | `"Token JWT ausente o inválido"` |
| ![403](https://img.shields.io/Mona/403-Forbidden-red?style=flat) | No es ADMIN | `"Acceso denegado — se requiere rol ADMIN"` |

</div>

---

### 2️⃣ Otorgar Mona a un Usuario

Permite asignar una mona existente del catálogo a un usuario específico. Si el usuario no tiene perfil de gamificación, se crea automáticamente. Si ya posee la mona, se retorna la existente sin generar error.

**Endpoint principal:**  
`POST /api/v1/Monas/award`

---

### 📦 Estructura de la Solicitud (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| userId | String | Obligatorio, no vacío | ID del usuario que recibirá la mona. |
| MonaId | String | Obligatorio, no vacío | ID de la mona a otorgar. |

</div>

---

### 📦 Estructura de la Respuesta (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| MonaId | String | ID de la mona ganada. |
| MonaName | String | Nombre de la mona. |
| earnedAt | LocalDateTime | Fecha y hora en que fue otorgada. |
| xpAwarded | Integer | XP acreditado al usuario por esta mona. |

</div>

---

### ✅ Happy Path (Ejemplo de Uso Exitoso)

1. El administrador envía un POST con el `userId` y el `MonaId`.
2. El sistema verifica que la mona existe en el catálogo.
3. Si el usuario no tiene perfil, se crea un `UserGamification` nuevo.
4. Se otorga la mona, se suma el XP (total y semanal) y se persiste.
5. Se dispara una notificación asíncrona al servicio M05.
6. Se retorna `200 OK` con los datos de la mona ganada.

**Request (Solicitud):**
```json
POST /api/v1/Monas/award
Headers: Authorization: Bearer <token>

{
  "userId": "user-abc123",
  "MonaId": "6641b3c8e4f2a30012345678"
}
```

**Response (Respuesta):**
```json
{
  "MonaId": "6641b3c8e4f2a30012345678",
  "MonaName": "Primer Parche",
  "earnedAt": "2026-05-08T10:15:00",
  "xpAwarded": 100
}
```

---

### 🖼️ Diagrama de Secuencia

![Diagrama de Secuencia Otorgar Mona](docs/images/otorgarMona.png)

<details>
<summary><strong>🟢 Explicación del Flujo</strong></summary>

El `AwardMonaservice` verifica que la mona exista vía `MonaRepositoryPort`. Luego busca el perfil del usuario en `UserGamificationRepositoryPort`; si no existe, crea uno nuevo con `UserGamification.newUser()`. Invoca `awardMona()` en el dominio (que suma XP y registra la mona) y persiste el resultado. Finalmente, dispara `notifyMonaEarned()` de forma asíncrona hacia el microservicio de notificaciones.

</details>

---

### 📊 Tipos de errores manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/Mona/400-Bad_Request-red?style=flat) | Campos vacíos | `"El userId es obligatorio"` |
| ![401](https://img.shields.io/Mona/401-Unauthorized-red?style=flat) | Token inválido | `"Token JWT ausente o inválido"` |
| ![403](https://img.shields.io/Mona/403-Forbidden-red?style=flat) | No es ADMIN | `"Acceso denegado — se requiere rol ADMIN"` |
| ![404](https://img.shields.io/Mona/404-Not_Found-orange?style=flat) | Mona no existe | `"Mona not found: <MonaId>"` |

</div>

---

### 3️⃣ Consultar Mis Monas

Retorna todas las monas desbloqueadas por el usuario autenticado.

**Endpoint principal:**  
`GET /api/v1/gamification/me/Monas`

---

### 📦 Estructura de la Respuesta (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| (lista) | List\<EarnedMonaResponse\> | Lista de monas ganadas por el usuario. |

</div>

---

### ✅ Happy Path (Ejemplo de Uso Exitoso)

1. El usuario envía un GET con su JWT Bearer Token.
2. El sistema extrae el `userId` del token JWT.
3. Se retorna la lista de monas ganadas.

**Request (Solicitud):**
```
GET /api/v1/gamification/me/Monas
Headers: Authorization: Bearer <token>
```

**Response (Respuesta):**
```json
[
  {
    "MonaId": "6641b3c8e4f2a30012345678",
    "MonaName": "Primer Parche",
    "earnedAt": "2026-05-08T10:15:00",
    "xpAwarded": 100
  }
]
```

---

### 🖼️ Diagrama de Secuencia

![Diagrama de Secuencia Mis Monas](docs/images/misMonas.png)

<details>
<summary><strong>🟢 Explicación del Flujo</strong></summary>

El `UserGamificationController` extrae el `userId` del principal autenticado. El `GetUserMonasUseCase` busca el perfil del usuario y retorna la lista de `EarnedMona` mapeada a `EarnedMonaResponse`.

</details>

---

### 📊 Tipos de errores manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/Mona/401-Unauthorized-red?style=flat) | Token inválido | `"Token JWT ausente o inválido"` |
| ![404](https://img.shields.io/Mona/404-Not_Found-orange?style=flat) | Sin perfil de gamificación | `"UserGamification not found for userId: <id>"` |

</div>

---

### 4️⃣ Consultar Mi Progreso hacia Monas

Retorna el progreso actual del usuario autenticado hacia cada mona aún no desbloqueada.

**Endpoint principal:**  
`GET /api/v1/gamification/me/progress`

---

### 📦 Estructura de la Respuesta (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| MonaId | String | ID de la mona objetivo. |
| currentValue | Integer | Valor actual de progreso. |
| requiredValue | Integer | Valor requerido para desbloquear. |
| completed | Boolean | Indica si el criterio ya fue completado. |
| percentageComplete | Integer | Porcentaje de completitud (0–100). |

</div>

---

### ✅ Happy Path (Ejemplo de Uso Exitoso)

1. El usuario envía un GET con su JWT.
2. El sistema retorna el progreso por mona no desbloqueada.

**Request (Solicitud):**
```
GET /api/v1/gamification/me/progress
Headers: Authorization: Bearer <token>
```

**Response (Respuesta):**
```json
[
  {
    "MonaId": "6641b3c8e4f2a30098765432",
    "currentValue": 3,
    "requiredValue": 5,
    "completed": false,
    "percentageComplete": 60
  }
]
```

---

### 🖼️ Diagrama de Secuencia

![Diagrama de Secuencia Mi Progreso](docs/images/miProgreso.png)

<details>
<summary><strong>🟢 Explicación del Flujo</strong></summary>

El `GetUserProgressUseCase` recupera el perfil de gamificación del usuario y retorna su lista de `MonaProgress` mapeada a `MonaProgressResponse`.

</details>

---

### 📊 Tipos de errores manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/Mona/401-Unauthorized-red?style=flat) | Token inválido | `"Token JWT ausente o inválido"` |
| ![404](https://img.shields.io/Mona/404-Not_Found-orange?style=flat) | Sin perfil | `"UserGamification not found for userId: <id>"` |

</div>

---

### 5️⃣ Consultar Mis Estadísticas

Retorna XP total, XP semanal, cantidad de monas ganadas y estado de participación en el ranking.

**Endpoint principal:**  
`GET /api/v1/gamification/me/stats`

---

### 📦 Estructura de la Respuesta (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| userId | String | ID del usuario. |
| totalXp | Integer | XP acumulado total del usuario. |
| weeklyXp | Integer | XP acumulado en la semana actual. |
| rankingOptIn | Boolean | Si el usuario participa en el ranking semanal. |
| totalMonasEarned | Integer | Número total de monas desbloqueadas. |

</div>

---

### ✅ Happy Path (Ejemplo de Uso Exitoso)

1. El usuario envía un GET con su JWT.
2. Se retorna un resumen completo de su estado de gamificación.

**Request (Solicitud):**
```
GET /api/v1/gamification/me/stats
Headers: Authorization: Bearer <token>
```

**Response (Respuesta):**
```json
{
  "userId": "user-abc123",
  "totalXp": 350,
  "weeklyXp": 100,
  "rankingOptIn": true,
  "totalMonasEarned": 3
}
```

---

### 🖼️ Diagrama de Secuencia

![Diagrama de Secuencia Mis Estadísticas](docs/images/misEstadisticas.png)

<details>
<summary><strong>🟢 Explicación del Flujo</strong></summary>

El `GetUserStatsUseCase` recupera el `UserGamification` del usuario y construye el `UserStatsResponse` con el conteo de monas, XP total, XP semanal y estado del ranking.

</details>

---

### 📊 Tipos de errores manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/Mona/401-Unauthorized-red?style=flat) | Token inválido | `"Token JWT ausente o inválido"` |
| ![404](https://img.shields.io/Mona/404-Not_Found-orange?style=flat) | Sin perfil | `"UserGamification not found for userId: <id>"` |

</div>

---

### 6️⃣ Activar / Desactivar Participación en el Ranking

Alterna la participación del usuario en el ranking semanal. Si estaba participando, sale; si no estaba, entra.

**Endpoint principal:**  
`PATCH /api/v1/gamification/me/ranking/toggle`

---

### 📦 Estructura de la Respuesta (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| userId | String | ID del usuario. |
| rankingOptIn | Boolean | Nuevo estado de participación en el ranking. |
| message | String | Mensaje descriptivo del cambio realizado. |

</div>

---

### ✅ Happy Path (Ejemplo de Uso Exitoso)

1. El usuario envía un PATCH con su JWT.
2. El sistema alterna el campo `rankingOptIn` del perfil del usuario.
3. Se retorna `200 OK` con el nuevo estado.

**Request (Solicitud):**
```
PATCH /api/v1/gamification/me/ranking/toggle
Headers: Authorization: Bearer <token>
```

**Response (Respuesta):**
```json
{
  "userId": "user-abc123",
  "rankingOptIn": true,
  "message": "Ahora participas en el ranking semanal."
}
```

---

### 🖼️ Diagrama de Secuencia

![Diagrama de Secuencia Toggle Ranking](docs/images/toggleRanking.png)

<details>
<summary><strong>🟢 Explicación del Flujo</strong></summary>

El `ToggleRankingOptInUseCase` recupera el perfil del usuario, invoca `toggleRankingOptIn()` en el dominio (que invierte el valor booleano) y persiste el cambio. Si el usuario no tiene perfil, se crea uno nuevo con `rankingOptIn = true`.

</details>

---

### 📊 Tipos de errores manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/Mona/401-Unauthorized-red?style=flat) | Token inválido | `"Token JWT ausente o inválido"` |

</div>

---

### 7️⃣ Consultar Ranking Semanal

Retorna el top N de usuarios que participan en el ranking, ordenados por XP semanal de mayor a menor.

**Endpoint principal:**  
`GET /api/v1/gamification/ranking`

---

### 📦 Estructura de la Solicitud (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| limit | Integer | Opcional (query param, default 10) | Número máximo de posiciones a retornar. |

</div>

---

### 📦 Estructura de la Respuesta (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| position | Integer | Posición en el ranking (1 = primero). |
| userId | String | ID del usuario. |
| weeklyXp | Integer | XP acumulado en la semana actual. |
| totalMonasEarned | Integer | Número total de monas del usuario. |

</div>

---

### ✅ Happy Path (Ejemplo de Uso Exitoso)

1. El cliente envía un GET con el parámetro `limit` opcional.
2. El sistema filtra los usuarios con `rankingOptIn = true`, los ordena por `weeklyXp` descendente y asigna posiciones.
3. Se retorna la lista del top N.

**Request (Solicitud):**
```
GET /api/v1/gamification/ranking?limit=5
Headers: Authorization: Bearer <token>
```

**Response (Respuesta):**
```json
[
  {
    "position": 1,
    "userId": "user-abc123",
    "weeklyXp": 350,
    "totalMonasEarned": 5
  },
  {
    "position": 2,
    "userId": "user-def456",
    "weeklyXp": 200,
    "totalMonasEarned": 3
  }
]
```

---

### 🖼️ Diagrama de Secuencia

![Diagrama de Secuencia Ranking Semanal](docs/images/rankingSemanal.png)

<details>
<summary><strong>🟢 Explicación del Flujo</strong></summary>

El `GetRankingUseCase` consulta el repositorio filtrando únicamente usuarios con `rankingOptIn = true`, los ordena por `weeklyXp` de forma descendente, aplica el límite solicitado y construye la lista de `RankingEntryResponse` asignando posiciones correlativas.

</details>

---

### 📊 Tipos de errores manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![200](https://img.shields.io/Mona/200-OK-success?style=flat) | Sin participantes | Lista vacía `[]` |
| ![401](https://img.shields.io/Mona/401-Unauthorized-red?style=flat) | Token inválido | `"Token JWT ausente o inválido"` |

</div>

---

## 7. 📊 Diagramas

Esta sección muestra los diagramas clave del microservicio de gamificación, ilustrando su arquitectura, componentes principales y despliegue.

---

### 🏗️ Diagrama de Componentes — Vista General
<div align="center">
<img src="docs/uml/DiagramaComponentesGeneral.png" alt="Diagrama de Componentes General" width="600"/>
</div>


---

### 🔍 Diagrama de Componentes — Vista Específica

<div align="center">
<img src="docs/uml/DiagramaComponentesEspecifico.png" alt="Diagrama de Componentes Específico" width="600"/>
</div>

**Arquitectura Hexagonal:**  
El microservicio de Gamificación separa controladores, casos de uso, lógica de negocio y adaptadores externos para mantener modularidad y escalabilidad.

**Flujo principal:**

- **MonaController / UserGamificationController**
  - Reciben solicitudes HTTP y las delegan a los puertos de entrada correspondientes.

**Lógica de Negocio (Dominio):**

- **Casos de Uso (Application Layer)**
  - `CreateMonaUseCase`, `AwardMonaUseCase`
  - `GetUserMonasUseCase`, `GetUserProgressUseCase`, `GetUserStatsUseCase`
  - `ToggleRankingOptInUseCase`, `GetRankingUseCase`
  - Cada caso de uso implementa un puerto de entrada y orquesta la lógica mediante puertos de salida.

- **WeeklyXpResetScheduler**
  - Tarea programada que se ejecuta cada lunes a medianoche para reiniciar el XP semanal de todos los participantes del ranking.

**Integración y Adaptadores:**

- **Persistencia:**
  - Adaptadores `MonaRepositoryAdapter` y `UserGamificationRepositoryAdapter` implementan los puertos de salida.
  - Persiste en MongoDB en las colecciones `Monas` y `user_gamification`.

- **Notificaciones:**
  - `NotificacionAdapter` publica eventos de forma asíncrona al microservicio M05 (`notifyMonaEarned`) cuando un usuario desbloquea una mona.

- **Seguridad:**
  - `JwtAuthFilter` intercepta todas las peticiones, valida el JWT y extrae el `userId` y `role` para poblar el `SecurityContext`.

- **Manejo de Errores:**
  - `GlobalExceptionHandler` centraliza el manejo de excepciones de dominio.

> El microservicio de Gamificación gestiona todo el ciclo de vida de las recompensas estudiantiles, integrándose con otros servicios del ecosistema DOSW a través de eventos asincrónicos.


### 🔌 Servicios Externos Integrados

El microservicio se integra con otros sistemas del ecosistema DOSW.

<div align="center">

| 🌍 **Microservicio** | ⚙️ **Operación** | 📋 **Propósito** |
|:---------------|:----------------|:-----------------------|
| **Hangout Service** | Unirse a parche / Invitación aceptada | Dispara el otorgamiento de monas y XP al usuario |
| **Notification Service (M05)** | Mona desbloqueada | Enviar notificación push al estudiante cuando gana una mona |

</div>

**Dominio y Mapeo:**

- Las entidades `Mona` y `UserGamification` encapsulan la lógica central.
- Los value objects `EarnedMona` y `MonaProgress` representan subdocumentos embebidos en MongoDB.

> El diagrama ilustra cómo el dominio de gamificación se mantiene aislado de la infraestructura, permitiendo cambiar la base de datos o los adaptadores externos sin afectar las reglas de negocio.


---
### 📊 Diagrama de base de datos

<div align="center">
<img src="docs/images/gamification_db.png" alt="Diagrama de base de datos" width="600"/>
</div>

El microservicio de Gamificación utiliza **MongoDB** como motor de base de datos NoSQL. Contiene dos colecciones principales: `Monas` y `user_gamification`.

#### 📋 Colección: `Monas`

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción | ⚠️ Restricciones |
|:---|:---|:---|:---|
| **_id** | `ObjectId` | Identificador único de la mona | Primary Key (auto) |
| **name** | `String` | Nombre de la mona | NOT NULL, Unique |
| **description** | `String` | Descripción del logro | NOT NULL |
| **category** | `String` | Rareza: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY | NOT NULL |
| **xpReward** | `Integer` | XP otorgado al ganar la mona | NOT NULL, ≥ 1 |
| **iconUrl** | `String` | URL del ícono visual | Opcional |
| **createdAt** | `LocalDateTime` | Fecha de creación | NOT NULL |
| **active** | `Boolean` | Si la mona está activa en el catálogo | NOT NULL |

</div>

#### 📋 Colección: `user_gamification`

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción | ⚠️ Restricciones |
|:---|:---|:---|:---|
| **_id** | `ObjectId` | Identificador único del perfil | Primary Key (auto) |
| **userId** | `String` | ID externo del usuario | NOT NULL, Unique |
| **totalXp** | `Integer` | XP total acumulado | NOT NULL, DEFAULT 0 |
| **weeklyXp** | `Integer` | XP acumulado en la semana | NOT NULL, DEFAULT 0 |
| **rankingOptIn** | `Boolean` | Participación en el ranking | NOT NULL, DEFAULT false |
| **earnedMonas** | `Array<EarnedMona>` | Subdocumentos de monas ganadas | Embebido |
| **progress** | `Array<MonaProgress>` | Subdocumentos de progreso por mona | Embebido |

</div>

##### Subdocumento: `EarnedMona`

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|:---|:---|:---|
| **MonaId** | `String` | ID de la mona ganada |
| **MonaName** | `String` | Nombre de la mona |
| **earnedAt** | `LocalDateTime` | Fecha y hora de desbloqueo |
| **xpAwarded** | `Integer` | XP acreditado en ese momento |

</div>

##### Subdocumento: `MonaProgress`

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|:---|:---|:---|
| **MonaId** | `String` | ID de la mona objetivo |
| **currentValue** | `Integer` | Valor actual de progreso |
| **requiredValue** | `Integer` | Valor requerido para desbloquear |
| **completed** | `Boolean` | Si el criterio fue completado |

</div>

---

### 📦 Diagrama de Clases del Dominio

<div align="center">
<img src="docs/uml/DiagramaDeClases.png" alt="Diagrama de Clases" width="600"/>
</div>

**Resumen del diseño de dominio:**

La arquitectura de dominio se centra en las entidades **Mona** y **UserGamification**.

- **Entidad de Dominio:** `Mona` contiene identificadores, datos del logro y metadatos del catálogo. El campo `active` controla su visibilidad.
- **Perfil de Usuario:** `UserGamification` vincula un estudiante con su historial de monas, XP total, XP semanal y estado en el ranking.
- **Value Objects:** `EarnedMona` y `MonaProgress` son subdocumentos inmutables embebidos en `UserGamification`.
- **Enumeraciones:** `MonaCategory` garantiza categorías controladas (COMMON, UNCOMMON, RARE, EPIC, LEGENDARY).

> Este diseño asegura la integridad de los datos de gamificación y permite extender funcionalidades (nuevas categorías, nuevos criterios de desbloqueo) sin afectar las reglas de negocio centrales.


---

### 📦 DTOs Principales

<div align="center">
<div style="background:#111; color:#fff; border-radius:12px; padding:24px 12px; box-shadow:0 2px 12px #0002;">

<table style="border:2px solid #4A90E2; border-radius:8px;">
  <caption style="font-size:1.15em; font-weight:bold; color:#4A90E2; padding:8px;">📨 <u>Request DTOs</u></caption>
  <thead style="background:#222; color:#fff;">
    <tr>
      <th style="padding:8px;">DTO</th>
      <th style="padding:8px;">Atributos Principales</th>
      <th style="padding:8px;">Descripción</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><b>CreateMonaRequest</b></td>
      <td>name, description, category, xpReward, iconUrl</td>
      <td>Solicitud para registrar una nueva mona en el catálogo. Valida nombre, descripción y XP mínimo de 1.</td>
    </tr>
    <tr>
      <td><b>AwardMonaRequest</b></td>
      <td>userId, MonaId</td>
      <td>Solicitud para otorgar una mona existente a un usuario específico.</td>
    </tr>
    <tr>
      <td><b>ToggleRankingOptInRequest</b></td>
      <td>optIn</td>
      <td>Alterna la participación del usuario en el ranking semanal.</td>
    </tr>
  </tbody>
</table>

<br>

<table style="border:2px solid #43A047; border-radius:8px;">
  <caption style="font-size:1.15em; font-weight:bold; color:#43A047; padding:8px;">📤 <u>Response DTOs</u></caption>
  <thead style="background:#222; color:#fff;">
    <tr>
      <th style="padding:8px;">DTO</th>
      <th style="padding:8px;">Atributos Principales</th>
      <th style="padding:8px;">Descripción</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><b>MonaResponse</b></td>
      <td>id, name, description, category, xpReward, iconUrl, createdAt</td>
      <td>Respuesta completa con los datos de una mona del catálogo.</td>
    </tr>
    <tr>
      <td><b>EarnedMonaResponse</b></td>
      <td>MonaId, MonaName, earnedAt, xpAwarded</td>
      <td>Confirmación de mona ganada con timestamp y XP acreditado.</td>
    </tr>
    <tr>
      <td><b>MonaProgressResponse</b></td>
      <td>MonaId, currentValue, requiredValue, completed, percentageComplete</td>
      <td>Progreso del usuario hacia una mona aún no desbloqueada.</td>
    </tr>
    <tr>
      <td><b>UserStatsResponse</b></td>
      <td>userId, totalXp, weeklyXp, rankingOptIn, totalMonasEarned</td>
      <td>Resumen del estado de gamificación del usuario autenticado.</td>
    </tr>
    <tr>
      <td><b>RankingEntryResponse</b></td>
      <td>position, userId, weeklyXp, totalMonasEarned</td>
      <td>Entrada del ranking semanal con posición y métricas del usuario.</td>
    </tr>
  </tbody>
</table>

<br>

<table style="border:2px solid #F0AD4E; border-radius:8px;">
  <caption style="font-size:1.15em; font-weight:bold; color:#F0AD4E; padding:8px;">⚙️ <u>Enums del Dominio</u></caption>
  <thead style="background:#222; color:#fff;">
    <tr>
      <th style="padding:8px;">Enum</th>
      <th style="padding:8px;">Valores</th>
      <th style="padding:8px;">Descripción</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><b>MonaCategory</b></td>
      <td>COMMON, UNCOMMON, RARE, EPIC, LEGENDARY</td>
      <td>Nivel de rareza de la mona, de menor a mayor dificultad de obtención.</td>
    </tr>
  </tbody>
</table>

</div>
</div>

---

### 🗄️ Diagrama de Despliegue

<div align="center">
<img src="docs/images/railwayGamification.png" alt="Diagrama de Despliegue" width="600"/>
</div>

---

#### 🚀 Despliegue e Infraestructura

El microservicio de **Gamificación** se ejecuta como un contenedor Docker en **Railway**, respaldado por una arquitectura robusta de CI/CD.

- **Ejecución:** Contenedor Docker en Railway (imagen construida con Dockerfile multi-stage).
- **Base de datos:** **MongoDB** provisionada en Railway con variable de entorno `MONGODB_URI` inyectada automáticamente.
- **CI/CD (GitHub Actions):**
  - Pruebas unitarias (JUnit 5) con MongoDB embebido (Flapdoodle).
  - Despliegue automático a Railway vía deploy hook en merges a `main`.
- **Construcción:** Dockerfile multi-stage (Maven Build → JRE 21 Alpine Runtime).
- **Configuración:** Variables de entorno gestionadas desde Railway dashboard.

<div align="center">

| 🌐 **Componente** | 📝 **Descripción** |
|------------------|-------------------|
| Railway App | Hosting del contenedor Docker del microservicio |
| Railway MongoDB | Base de datos NoSQL gestionada con backups |
| GitHub Actions | Automatización de CI/CD y calidad de código |
| Swagger UI | Documentación interactiva en `/swagger-ui.html` |

</div>



---

## 8. ⚠️ Manejo de Errores

El microservicio de **Gamificación** implementa un **mecanismo centralizado de manejo de errores** que garantiza uniformidad, claridad y seguridad en todas las respuestas enviadas al cliente cuando ocurre un fallo.

---

### 🧠 Estrategia general de manejo de errores

El sistema utiliza una **clase global** `GlobalExceptionHandler` con la anotación `@RestControllerAdvice` que intercepta todas las excepciones lanzadas desde los controladores REST. Cada excepción de dominio se transforma en una respuesta **JSON estandarizada** con el código HTTP apropiado.


---

### ⚙️ Global Exception Handler

El **Global Exception Handler** captura y maneja todas las excepciones del sistema de forma centralizada. Utiliza métodos con `@ExceptionHandler` para procesar cada tipo de error.

**✨ Características principales:**

- ✅ **Centraliza** la captura de excepciones desde todos los controladores
- ✅ **Retorna mensajes JSON consistentes** con el mismo formato estructurado (timestamp, status, error, message)
- ✅ **Asigna códigos HTTP** según la naturaleza del error (400, 403, 404, 409, 500)
- ✅ **Define mensajes descriptivos** que ayudan tanto al desarrollador como al usuario
- ✅ **Mantiene la aplicación limpia**, eliminando bloques try-catch redundantes
- ✅ **Mejora la trazabilidad** y facilita la depuración en entornos de prueba y producción


---

### 🧩 Excepciones de dominio manejadas

<div align="center">

| ⚠️ **Excepción** | 🔢 **HTTP** | 💬 **Escenario** |
|:----------------|:----------:|:----------------|
| `MonaNotFoundException` | 404 | La mona solicitada no existe en el catálogo |
| `UserGamificationNotFoundException` | 404 | El perfil de gamificación del usuario no existe |
| `MonaAlreadyEarnedException` | 409 | El usuario ya posee la mona que se intenta otorgar |
| `AccessDeniedException` | 403 | El usuario no tiene permisos para la operación |
| `MethodArgumentNotValidException` | 400 | Validación de campos del DTO fallida (`@NotBlank`, `@Min`, etc.) |
| `Exception` (genérica) | 500 | Error inesperado del servidor |

</div>

---

### ✅ Beneficios del manejo centralizado

<div align="center">

| 🎯 **Beneficio** | 📋 **Descripción** |
|:-----------------|:-------------------|
| **🎯 Uniformidad** | Todas las respuestas de error tienen el mismo formato JSON estandarizado |
| **🔧 Mantenibilidad** | Agregar nuevas excepciones no requiere modificar cada controlador |
| **🔒 Seguridad** | Oculta los detalles internos del servidor y evita exponer trazas sensibles |
| **📍 Trazabilidad** | Cada error incluye timestamp, código HTTP y descripción del fallo |
| **🤝 Integración fluida** | Facilita la comunicación con frontend y herramientas como Postman/Swagger |

</div>

---

> Gracias a este enfoque, el microservicio de Gamificación logra un manejo de errores **robusto**, **escalable** y **seguro**, garantizando una experiencia de usuario más confiable y profesional.

---


---

## 9. 🧪 Evidencia de las pruebas y cómo ejecutarlas

El microservicio de **Gamificación** implementa una **estrategia integral de pruebas** que garantiza la calidad, funcionalidad y confiabilidad del código mediante pruebas unitarias y de capa de controlador.

---

### 🎯 Tipos de pruebas implementadas

<div align="center">

| 🧪 **Tipo de Prueba** | 📋 **Descripción** | 🛠️ **Herramientas** |
|:---------------------|:-------------------|:--------------------|
| **Pruebas Unitarias de Casos de Uso** | Validan el funcionamiento aislado de cada caso de uso con mocks de puertos | ![JUnit](https://img.shields.io/Mona/JUnit_5-25A162?style=flat&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/Mona/Mockito-C5D928?style=flat) |
| **Pruebas de Dominio** | Verifican la lógica de negocio pura en las entidades (`UserGamification`) | ![JUnit](https://img.shields.io/Mona/JUnit_5-25A162?style=flat&logo=junit5&logoColor=white) |
| **Pruebas de Controlador** | Validan los endpoints REST con MockMvc y MongoDB embebido | ![Spring Test](https://img.shields.io/Mona/Spring_Test-6DB33F?style=flat&logo=spring&logoColor=white) ![Flapdoodle](https://img.shields.io/Mona/Flapdoodle_MongoDB-47A248?style=flat) |

</div>

---

### 🚀 Cómo ejecutar las pruebas

#### **1️⃣ Ejecutar todas las pruebas unitarias**

```bash
mvn test
```

#### **2️⃣ Ejecutar una prueba específica**

```bash
mvn test -Dtest=AwardMonaserviceTest
```

#### **3️⃣ Ejecutar pruebas desde IntelliJ IDEA**

1. Click derecho sobre la carpeta `src/test/java`
2. Selecciona **"Run 'Tests in...'**
3. Ver resultados en el panel inferior

---

### 🧪 Clases de prueba implementadas

<div align="center">

| 🧪 **Clase de Prueba** | 📋 **Qué valida** |
|:-----------------------|:------------------|
| `AwardMonaserviceTest` | Otorgamiento de monas, creación de perfil nuevo, manejo de mona ya poseída, suma correcta de XP |
| `CreateMonaserviceTest` | Creación de monas en el catálogo con validaciones de datos |
| `GetRankingServiceTest` | Construcción del ranking semanal ordenado por XP con asignación de posiciones |
| `GetUserMonasServiceTest` | Recuperación de monas ganadas por usuario |
| `GetUserStatsServiceTest` | Cálculo de estadísticas de gamificación del usuario |
| `ToggleRankingOptInServiceTest` | Alternancia de participación en el ranking y creación de perfil si no existe |
| `UserGamificationTest` | Lógica de dominio: `awardMona()`, `resetWeeklyXp()`, `toggleRankingOptIn()`, `hasMona()` |
| `MonaControllerTest` | Endpoints de `MonaController` con MockMvc |
| `UserGamificationControllerTest` | Endpoints de `UserGamificationController` con MockMvc |

</div>

---

### 🧪 Ejemplo de prueba unitaria

```java
@Test
@DisplayName("Otorgar mona a usuario nuevo crea perfil y suma XP")
void execute_shouldCreateProfileAndAwardMona_whenUserDoesNotExist() {
    when(MonaRepository.findById("Mona-001")).thenReturn(Optional.of(Mona));
    when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.empty());
    when(userGamificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(userGamificationMapper.toEarnedMonaResponse(any())).thenReturn(
            EarnedMonaResponse.builder().MonaId("Mona-001").xpAwarded(100).build());

    EarnedMonaResponse response = service.execute(request);

    assertThat(response.getMonaId()).isEqualTo("Mona-001");
    assertThat(response.getXpAwarded()).isEqualTo(100);
    verify(userGamificationRepository).save(any());
    verify(notificationEventPort).notifyMonaEarned("user-001", Mona);
}
```

---

### 🖼️ Evidencias de ejecución

1. **Consola mostrando pruebas ejecutándose exitosamente**

    ![Evidencia consola pruebas](./docs/images/consoleTest.png)

---

### ✅ Criterios de aceptación de pruebas

Para considerar el sistema correctamente probado, se debe cumplir:

- ✅ **Todas las pruebas en estado PASSED** (sin fallos)
- ✅ **Cero errores de compilación** en el código de pruebas
- ✅ **Pruebas de casos felices y casos de error** implementadas
- ✅ **Lógica de dominio** cubierta con pruebas de la entidad `UserGamification`

---

## 10. 🗂️ Código de la implementación organizado en las respectivas carpetas

El microservicio de **Gamificación** sigue una **arquitectura hexagonal (puertos y adaptadores)** que separa las responsabilidades en capas bien definidas, promoviendo la escalabilidad, testabilidad y mantenibilidad del código.

---

### 📂 Estructura general del proyecto (Scaffolding)

```
charizard-compiled-gamification-service/
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/charizad/compiled/gamification_service/
│   │   │   │
│   │   │   ├── 📁 application/                              # 🔵 CAPA DE APLICACIÓN
│   │   │   │   ├── 📁 dto/
│   │   │   │   │   ├── 📁 request/   (CreateMonaRequest, AwardMonaRequest, ToggleRankingOptInRequest)
│   │   │   │   │   └── 📁 response/  (MonaResponse, EarnedMonaResponse, MonaProgressResponse,
│   │   │   │   │                      UserStatsResponse, RankingEntryResponse)
│   │   │   │   ├── 📁 mapper/        (MonaMapper, UserGamificationMapper)
│   │   │   │   └── 📁 usecase/       (CreateMonaservice, AwardMonaservice, GetUserMonasService,
│   │   │   │                          GetUserProgressService, GetUserStatsService,
│   │   │   │                          ToggleRankingOptInService, GetRankingService)
│   │   │   │
│   │   │   ├── 📁 domain/                                   # 🟢 CAPA DE DOMINIO
│   │   │   │   ├── 📁 exceptions/    (MonaNotFoundException, UserGamificationNotFoundException,
│   │   │   │   │                      MonaAlreadyEarnedException, AccessDeniedException)
│   │   │   │   ├── 📁 model/         (Mona, UserGamification)
│   │   │   │   │   └── 📁 enums/     (MonaCategory)
│   │   │   │   ├── 📁 ports/
│   │   │   │   │   ├── 📁 in/        (CreateMonaUseCase, AwardMonaUseCase, GetUserMonasUseCase,
│   │   │   │   │   │                  GetUserProgressUseCase, GetUserStatsUseCase,
│   │   │   │   │   │                  ToggleRankingOptInUseCase, GetRankingUseCase)
│   │   │   │   │   └── 📁 out/       (MonaRepositoryPort, UserGamificationRepositoryPort,
│   │   │   │   │                      NotificationEventPort)
│   │   │   │   └── 📁 valueobjects/  (EarnedMona, MonaProgress)
│   │   │   │
│   │   │   ├── 📁 entrypoints/                              # 🟠 ENTRADA (DRIVING ADAPTERS)
│   │   │   │   ├── 📁 advice/        (GlobalExceptionHandler)
│   │   │   │   └── 📁 rest/
│   │   │   │       ├── 📁 controller/ (MonaController, UserGamificationController)
│   │   │   │       └── 📁 mapper/    (MonaRestMapper, UserGamificationRestMapper)
│   │   │   │
│   │   │   └── 📁 infrastructure/                           # 🟠 INFRAESTRUCTURA (DRIVEN ADAPTERS)
│   │   │       ├── 📁 adapters/
│   │   │       │   ├── 📁 adapter/   (MonaRepositoryAdapter, UserGamificationRepositoryAdapter)
│   │   │       │   └── 📁 persistence/
│   │   │       │       ├── 📁 entity/   (MonaDocument, UserGamificationDocument,
│   │   │       │       │                 EarnedMonasubdocument, MonaProgressSubdocument)
│   │   │       │       ├── 📁 mapper/   (MonaDocumentMapper, UserGamificationDocumentMapper)
│   │   │       │       └── 📁 repository/ (MonaMongoRepository, UserGamificationMongoRepository)
│   │   │       ├── 📁 config/        (MongoConfig, SecurityConfig, OpenApiConfig, DataSeeder)
│   │   │       ├── 📁 external/      (NotificacionAdapter)
│   │   │       └── 📁 scheduler/     (WeeklyXpResetScheduler)
│   │   │
│   │   └── 📁 resources/
│   │       └── 📄 application.yml
│   │
│   └── 📁 test/                                             # 🧪 PRUEBAS
│       └── 📁 java/.../
│           ├── 📁 application/usecase/   (AwardMonaserviceTest, CreateMonaserviceTest,
│           │                              GetRankingServiceTest, GetUserMonasServiceTest,
│           │                              GetUserStatsServiceTest, ToggleRankingOptInServiceTest)
│           ├── 📁 domain/model/          (UserGamificationTest)
│           └── 📁 entrypoints/rest/controller/ (MonaControllerTest, UserGamificationControllerTest)
│
├── 📄 Dockerfile
├── 📄 docker-compose.yml
├── 📄 pom.xml
└── 📄 README.md
```

---

> ℹ️ El código fuente está organizado siguiendo estrictamente la arquitectura hexagonal para garantizar la separación de responsabilidades y facilitar el mantenimiento y la extensión del sistema.

### 🏛️ Arquitectura Hexagonal Implementada

<div align="center">

| 🎨 **Capa** | 📋 **Responsabilidad** | 🔗 **Dependencias** |
|:-----------|:----------------------|:-------------------|
| **🟢 Domain** | Lógica de negocio pura, entidades (`Mona`, `UserGamification`), value objects, enums y puertos (interfaces) | ❌ Ninguna (independiente) |
| **🔵 Application** | Casos de uso, DTOs y mappers | ✅ Solo `Domain` |
| **🟠 Entrypoints** | Controladores REST y manejador global de excepciones | ✅ `Domain` + `Application` |
| **🟠 Infrastructure** | Adaptadores MongoDB, scheduler, notificaciones asíncronas y configuración | ✅ `Domain` + `Application` |

</div>

**Flujo de dependencias:** `Entrypoints / Infrastructure → Application → Domain`

---

### 🎯 Principios de diseño aplicados

<div align="center">

| ✅ **Principio** | 📋 **Implementación** |
|:----------------|:---------------------|
| **Separación de responsabilidades** | Cada capa tiene un propósito único y bien definido |
| **Inversión de dependencias** | Las capas externas dependen de interfaces (puertos) definidas en el dominio |
| **Independencia del framework** | La lógica de negocio no depende de Spring ni de MongoDB |
| **Patrón Ports & Adapters** | Los casos de uso consumen puertos; la infraestructura los implementa |
| **Testabilidad** | Fácil crear pruebas unitarias mockeando puertos; controladores con MockMvc y MongoDB embebido |
| **Mantenibilidad** | Cambios en una capa no afectan a las demás |

</div>  

---

## 11. 🚀 Ejecución del Proyecto

### 📋 Prerrequisitos
- **Java 21**
- **Maven 3.9+**
- **Docker & Docker Compose** (para ejecución containerizada)
- **MongoDB** (si ejecutas localmente sin Docker)

### 🛠️ Opción 1: Ejecución Local (Maven)

```bash
# 1. Clonar repositorio
git clone https://github.com/<org>/charizard-compiled-gamification-service.git

# 2. Levantar base de datos local
docker compose up -d mongodb

# 3. Ejecutar aplicación
mvn spring-boot:run
```
📍 **URL Local:** `http://localhost:8086`  
📚 **Documentación API:** `http://localhost:8086/swagger-ui.html`

### 🐳 Opción 2: Ejecución con Docker Compose

```bash
# Levantar toda la stack (app + mongodb)
docker compose up --build
```

Esto levanta:
- `gamification-mongodb`: MongoDB en el puerto 27017
- `gamification-service`: La aplicación en el puerto 8086

### ⚙️ Variables de Entorno

| Variable | Valor por defecto | Descripción |
|:---------|:-----------------|:------------|
| `MONGODB_URI` | `mongodb://localhost:27017/gamification_db` | URI de conexión a MongoDB |
| `JWT_SECRET` | `charizard-compiled-secret-key-2025-very-long-key-for-hs256` | Clave secreta para validar tokens JWT |
| `NOTIFICATION_SERVICE_URL` | `http://localhost:8085` | URL del microservicio de notificaciones (M05) |
| `PORT` | `8086` | Puerto del servidor |

## 12. ☁️ CI/CD y Despliegue en Railway

El proyecto implementa un **pipeline automatizado** con **GitHub Actions** para garantizar la calidad del código y el despliegue continuo en **Railway**.

---

### 🔗 Enlaces de Despliegue

<div align="center">

| 🌍 Ambiente | 📝 Estado |
|:-----------|:---------|
| **🟢 Producción (Railway)** | ![Active](https://img.shields.io/Mona/Status-Active-success?style=flat) |

</div>

---

### 🔄 Pipeline de Automatización

El flujo de trabajo ejecuta los siguientes pasos en cada push o PR:

1. **Build** — Compila el proyecto con Maven (`mvn package -DskipTests`).
2. **Tests Unitarios** — Ejecuta `mvn test` con MongoDB embebido (Flapdoodle) para no requerir infraestructura externa.
3. **Deploy** — En merges a `main`, dispara el deploy hook de Railway vía `curl -X POST ${{ secrets.DEPLOY_HOOK }}`.

---

### ☁️ Infraestructura

<div align="center">

| Componente | Servicio | Propósito |
|:-----------|:---------|:----------|
| **Compute** | ![Railway](https://img.shields.io/Mona/Railway-0B0D0E?logo=railway&logoColor=white) | Ejecución del contenedor Docker del microservicio |
| **Database** | ![MongoDB](https://img.shields.io/Mona/MongoDB-47A248?logo=mongodb&logoColor=white) | Persistencia de monas y perfiles de gamificación |
| **CI/CD** | ![GitHub Actions](https://img.shields.io/Mona/GitHub_Actions-2088FF?logo=github-actions&logoColor=white) | Automatización de pruebas y despliegue continuo |
| **API Docs** | ![Swagger](https://img.shields.io/Mona/Swagger-85EA2D?logo=swagger&logoColor=black) | Documentación interactiva de endpoints REST |

</div>

---

### 📊 Evidencias de Despliegue

**Railway — Aplicación en ejecución**

<div align="center">
  <img src="docs/uml/DiagramaDespliegue.png" alt="Evidencia Railway Deploy" width="80%" />
</div>

---

## 13. 🤝 Contribuciones y Metodología

El equipo **Charizard Compiled** aplicó la metodología **Scrum** con sprints semanales para garantizar una entrega incremental de valor y mejora continua.

### 👥 Equipo Scrum

| Rol | Responsabilidad |
|:---|:---|
| **Product Owner** | Priorización del Backlog y maximización de valor. |
| **Scrum Master** | Facilitador del proceso y eliminación de impedimentos. |
| **Developers** | Diseño, implementación y pruebas de funcionalidades. |

### 🔄 Eventos y Artefactos

- **Sprints Semanales**: Ciclos cortos de desarrollo.
- **Daily Scrum**: Sincronización diaria (15 min).
- **Sprint Review & Retrospective**: Demostración de incrementos y mejora de procesos.
- **Backlogs**: Gestión de tareas en Jira/GitHub Projects.

### 🎯 Valores del Equipo
Compromiso, Coraje, Enfoque, Apertura y Respeto fueron los pilares para afrontar desafíos técnicos como la arquitectura hexagonal con Spring Boot 4, la integración con MongoDB y el diseño de un sistema de gamificación escalable.

---

<div align="center">

### 🏆 Equipo **Charizard Compiled**

![Team](https://img.shields.io/Mona/Team-Charizard_Compiled-blueviolet?style=for-the-Mona&logo=github&logoColor=white)
![Course](https://img.shields.io/Mona/Course-DOSW-orange?style=for-the-Mona)
![Year](https://img.shields.io/Mona/Year-2026--1-blue?style=for-the-Mona)

> 💡 **DOSW Gamification Service** es un proyecto académico, pero su arquitectura y calidad están pensadas para ser escalables y adaptables a escenarios reales en instituciones educativas.

**🎓 Escuela Colombiana de Ingeniería Julio Garavito**

</div>

---

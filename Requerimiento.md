PTR13.1 — Desbloquear Monas Coleccionables



Campo

Detalle

Código

PTR13.1

Nombre

Desbloquear Monas Coleccionables

Módulo

M13 — Gamificación

Descripción

El sistema detecta automáticamente cuando un estudiante cumple la condición de desbloqueo de una mona y la asigna a su perfil sin intervención manual. Existen dos flujos de desbloqueo: por uso de la plataforma (conexiones, parches, mensajes, visita a lugares del campus) y por asistencia a eventos universitarios mediante código alfanumérico.

Cómo se ejecutará

Cada vez que el estudiante realiza una acción relevante en la plataforma, el sistema evalúa si se cumple alguna condición de desbloqueo. Para monas de eventos, el estudiante ingresa un código alfanumérico único. Si la condición se cumple, la mona se asigna automáticamente y se notifica al estudiante.

Actor principal

Estudiante autenticado

Precondiciones

El usuario debe tener sesión activa con JWT válido. Deben existir estadísticas de actividad registradas en el sistema. Autenticación requerida — Header: Authorization: Bearer <JWT>. El backend extrae el userId del payload del token; el cliente nunca envía el userId directamente.



Catálogo de Monas Coleccionables

Nombre de la Mona

Rareza

Condición de Desbloqueo

Flujo de Obtención

Primera Conexión

Común

Realizar tu primera conexión con otro usuario

Uso de plataforma

Conector

Poco común

Acumular 5 conexiones activas

Uso de plataforma

Embajador Social

Raro

Acumular 10 conexiones activas

Uso de plataforma

Primer Parche

Común

Unirte o crear tu primer parche

Uso de plataforma

Anfitrión

Poco común

Crear 2 parches como capitán

Uso de plataforma

Planificador

Común

Crear un parche con más de 3 días de anticipación

Uso de plataforma

Explorador I

Común

Visitar 3 lugares distintos del campus

Uso de plataforma

Explorador II

Poco común

Visitar 5 lugares distintos del campus

Uso de plataforma

Asistente

Raro

Ingresar código alfanumérico válido de un evento universitario institucional

Código alfanumérico de evento

Primer Mensaje

Común

Enviar el primer mensaje en un parche recién creado

Uso de plataforma

Imán Social

Común

Tener un nuevo usuario uniéndose a un parche que creaste

Uso de plataforma

Meteoro Social

Épico

Pasar de 0 a 10 conexiones en menos de 30 días desde el registro

Uso de plataforma

Coleccionista

Legendario

Desbloquear las 12 monas anteriores

Uso de plataforma



Datos de Entrada



Para monas de tipo 'Uso de plataforma': No requieren entrada explícita del usuario; el sistema evalúa las condiciones automáticamente.



Para monas de tipo 'Código alfanumérico de evento' (Endpoint: POST /api/v1/gamificacion/monas/evento):



Campo

Descripción

Tipo

Reglas / Aplicación

Obligatorio

eventCode

Código alfanumérico único del evento universitario

String

Formato alfanumérico. Distingue mayúsculas. Válido por el período del evento.

Sí



Datos de Salida

GET /api/v1/gamificacion/monas — Listado de monas

Campo

Descripción

Tipo

Reglas / Aplicación

monaId

UUID de la mona

UUID

Generado automáticamente.

name

Nombre de la mona

String

—

description

Descripción de la condición de desbloqueo

String

—

rarity

Rareza de la mona

Enum: MonaRarity

COMMON | UNCOMMON | RARE | EPIC | LEGENDARY

unlocked

Si el estudiante ya la obtuvo

Boolean

true | false

earnedAt

Fecha en que fue obtenida

LocalDate

Nulo si no ha sido desbloqueada. Solo se muestra la fecha (sin hora). Formato ISO 8601.

currentCount

Progreso actual del estudiante

Integer

Para monas con conteo acumulable.

targetCount

Meta requerida para desbloquear

Integer

—

progressPercentage

Porcentaje de avance

Float

0.0 a 100.0. Para monas ya obtenidas, siempre 100.0.



GET /api/v1/gamificacion/monas/{monaId} — Detalle de mona

Retorna el detalle de una mona específica y el progreso del estudiante hacia ella. Mismos campos que el listado.



Flujo Básico

Flujo A — Desbloqueo por Uso de Plataforma

Paso

Actor

Descripción

1

Estudiante

Realiza una acción en la plataforma (crear parche, conectarse, enviar mensaje, visitar lugar del campus, etc.).



Flujo B — Desbloqueo por Código Alfanumérico de Evento

Paso

Actor

Descripción

1

Estudiante

Accede a la sección de gamificación e ingresa el código alfanumérico del evento universitario.

2

Sistema

Valida el JWT y verifica que el código sea válido, activo y no haya sido usado por este estudiante.

3

Sistema

Asigna la mona 'Asistente' al perfil del estudiante.

4

Sistema

Notifica al estudiante sobre el desbloqueo y los XP ganados.

5

Sistema

Recalcula el XP total y el nivel del estudiante.



Flujo C — Consulta de Monas

Paso

Actor

Descripción

1

Estudiante

Accede a la sección de gamificación o a su perfil.

2

Sistema

Valida el JWT del estudiante.

3

Sistema

Retorna el listado de todas las monas con estado, fecha de obtención (solo fecha) y progreso.



Flujo Alterno / Excepciones

Código

Condición que lo dispara

Respuesta del sistema

E1

El estudiante ya obtuvo esa mona anteriormente.

El sistema no vuelve a asignarla. No genera error visible.

E2

Fallo en el servicio de notificaciones.

El desbloqueo queda registrado igualmente. El fallo se registra en el log.

E3

Token JWT inválido o ausente.

HTTP 401 Unauthorized. Mensaje: 'Autenticación requerida.'

E4

monaId no corresponde a ningún registro.

HTTP 404 Not Found. Mensaje: 'Mona no encontrada.'

E5

Código alfanumérico inválido, expirado o ya usado.

HTTP 400 Bad Request. Mensaje: 'Código no válido o ya utilizado.'

E6

Error de conexión o fallo en base de datos.

HTTP 500 Internal Server Error. Se registra el error en el log.



Notas y Comentarios

El sistema NO utiliza códigos QR. El único mecanismo de validación de eventos es el código alfanumérico ingresado manualmente por el estudiante.

No existen sobres ni medallas. Las monas son el único coleccionable del sistema de gamificación.

En el listado de monas, solo se muestra la fecha de obtención (sin hora), en formato ISO 8601 (YYYY-MM-DD).

Las monas 'Explorador I' y 'Explorador II' requieren que el estudiante tenga geoLocationEnabled = true para que las visitas a lugares del campus sean contabilizadas.

La mona 'Coleccionista' (Legendaria) se desbloquea automáticamente al obtener las 12 monas anteriores.

El catálogo de monas puede incluir monas adicionales relacionadas a lugares específicos del campus; el diseño de los íconos de cada mona es responsabilidad del equipo de diseño (Stiven).



Anexos: Prototipos y Diagramas de Caso de Uso



Pendiente de adjuntar por el equipo de diseño:

Prototipo de pantalla — Listado de monas coleccionables.

Prototipo de pantalla — Detalle de mona con progreso.

Prototipo de pantalla — Ingreso de código alfanumérico de evento.

Diagrama de caso de uso — PTR13.1.

Diseño de íconos de monas (todas las monas, incluyendo monas de lugares) — Equipo de diseño / Stiven.



Reglas de Negocio



No.

Descripción

RN-13.1.1

Las monas se asignan automáticamente; ningún administrador ni el propio estudiante puede asignarlas manualmente.

RN-13.1.2

Cada mona solo puede obtenerse una vez por estudiante.

RN-13.1.3

El progreso de monas con conteo acumulable (ej: conexiones) se actualiza en tiempo real tras cada acción relevante.

RN-13.1.4

Las monas de rareza EPIC y LEGENDARY requieren que se cumplan todas sus condiciones compuestas antes de asignarse.

RN-13.1.5

Las monas 'Explorador I' y 'Explorador II' requieren geoLocationEnabled = true para contabilizar visitas a lugares del campus.

RN-13.1.6

El sistema NO implementa códigos QR. El mecanismo de validación de eventos es exclusivamente mediante código alfanumérico.

RN-13.1.7

Las Monas son el único coleccionable del sistema; no existen otros tipos de recompensas físicas o digitales similares.

RN-13.1.8

En el listado de monas, solo se expone la fecha de obtención (campo earnedAt), sin la hora.

RN-13.1.9

Cada mona desbloqueada otorga una cantidad de XP al estudiante según lo definido en PTR13.2.

RN-13.1.10

Los logros y monas no generan ninguna ventaja funcional directa; su valor es social, decorativo y de progresión de nivel/XP.



Abreviaturas



Abreviatura

Significado

JWT

JSON Web Token — mecanismo de autenticación utilizado por el sistema.

UUID

Universally Unique Identifier — identificador único universal.

PTR

Punto de Requerimiento — nomenclatura de requerimientos del proyecto.

RF

Requisito Funcional (nomenclatura anterior).

RN

Regla de Negocio.

HTTP

HyperText Transfer Protocol.

API

Application Programming Interface.

REST

Representational State Transfer.

DTO

Data Transfer Object.

XP

Puntos de Experiencia — métrica de progresión del sistema de niveles.

ISO 8601

Estándar internacional para representación de fechas y horas.

M13

Módulo 13 — Gamificación.



Historial de Revisión



Versión

Fecha

Autor

Descripción del cambio

1.0

15/05/2026

Equipo Charizart

Creación inicial del requisito RF13.1 — Desbloquear Monas Coleccionables.

2.0

18/05/2026

Equipo Charizart

Reestructuración a PTR13.1. Eliminación de QR, sobres y medallas. Adición de código alfanumérico para eventos. Monas de lugares del campus. Cambio de earnedAt a solo fecha. Integración con XP (PTR13.2).













PTR13.2 — Sistema de Niveles y XP

Información General



Campo

Detalle

Código

PTR13.2

Nombre

Sistema de Niveles y XP

Módulo

M13 — Gamificación

Descripción

Cada mona desbloqueada otorga puntos de experiencia (XP) al estudiante. Al acumular suficiente XP, el estudiante sube de nivel automáticamente. Por cada nuevo nivel alcanzado, el sistema desbloquea una recompensa asociada. El nivel y las recompensas son visibles en el perfil público del estudiante.

Cómo se ejecutará

Cuando el estudiante desbloquea una mona, el sistema incrementa su XP total según el valor de XP de esa mona. Si el nuevo total de XP supera el umbral del siguiente nivel, el sistema actualiza el nivel, desbloquea la recompensa correspondiente y notifica al estudiante.

Actor principal

Estudiante autenticado

Precondiciones

El usuario debe tener sesión activa con JWT válido. Debe existir al menos una mona registrada en el sistema. Autenticación requerida — Header: Authorization: Bearer <JWT>.



XP por Mona

Nota: Los valores exactos de XP por mona son definidos por el equipo de diseño de gamificación. La siguiente tabla es referencial.



Rareza

XP sugerido por mona

Monas de este tipo

Común (COMMON)

100 XP

Primera Conexión, Primer Parche, Planificador, Explorador I, Primer Mensaje, Imán Social

Poco común (UNCOMMON)

250 XP

Conector, Anfitrión, Explorador II

Raro (RARE)

500 XP

Embajador Social, Asistente

Épico (EPIC)

1000 XP

Meteoro Social

Legendario (LEGENDARY)

2000 XP

Coleccionista





















Datos de Entrada



No requiere entrada explícita del usuario. El sistema calcula el XP y el nivel automáticamente al desbloquear una mona.



Datos de Salida - GET /api/v1/gamificacion/nivel



Campo

Descripción

Tipo

Reglas / Aplicación

currentLevel

Número del nivel actual

Integer

Entre 1 y 5.

levelName

Nombre del nivel actual

String

Ej: 'Conector'.

totalXP

Total de XP acumulado por el estudiante

Integer

Suma de XP de todas las monas desbloqueadas.

xpForNextLevel

XP necesario para el siguiente nivel

Integer

Nulo si ya está en el nivel máximo.

xpRemaining

XP que faltan para subir de nivel

Integer

Nulo si ya está en el nivel máximo.

totalMonasEarned

Total de monas obtenidas

Integer

—

progressPercentage

Porcentaje de avance hacia el siguiente nivel (por XP)

Float

0.0 a 100.0. Si está en nivel máximo, retorna 100.0.

isMaxLevel

Indica si el estudiante alcanzó el nivel máximo

Boolean

true | false

currentReward

Recompensa desbloqueada en el nivel actual

String

Descripción de la recompensa. Nulo en nivel 1.



Flujo Básico

Flujo A — Ascenso de Nivel

Paso

Actor

Descripción

1

Sistema

El estudiante desbloquea una mona (ver PTR13.1).

2

Sistema

Incrementa el XP total del estudiante según el valor de XP de la mona obtenida.

3

Sistema

Compara el XP total contra el umbral del siguiente nivel.

4

Sistema

Si el XP supera el umbral: actualiza el nivel, notifica al estudiante.

5

Sistema

Si el XP no supera el umbral: actualiza únicamente el XP total y el progreso porcentual.









Flujo B — Consulta de Nivel

Paso

Actor

Descripción

1

Estudiante

Accede a su perfil o a la sección de gamificación.

2

Sistema

Valida el JWT del estudiante.

3

Sistema

Retorna el nivel actual, XP total, progreso hacia el siguiente nivel y recompensa desbloqueada.



Flujo Alterno / Excepciones



Código

Condición que lo dispara

Respuesta del sistema

E1

Token JWT inválido o ausente.

HTTP 401 Unauthorized. Mensaje: 'Autenticación requerida.'

E2

Fallo en el servicio de notificaciones al subir de nivel.

El ascenso queda registrado igualmente. El fallo se registra en el log.

E3

Error de conexión o fallo en base de datos.

HTTP 500 Internal Server Error. Se registra el error en el log.



Notas y Comentarios



El nivel se calcula con base en el XP total acumulado, no en el número de monas directamente.

El nivel nunca puede disminuir; el XP y las monas obtenidas son permanentes.

Por cada nuevo nivel alcanzado (excepto el nivel 1), se desbloquea una recompensa específica. Las recompensas deben ser definidas por el equipo de producto.

El nivel actual y la recompensa desbloqueada son visibles en el perfil público del estudiante.

El nivel no genera ventajas funcionales adicionales fuera de las recompensas explícitas definidas por el equipo de producto.



Anexos: Prototipos y Diagramas de Caso de Uso



Prototipo de pantalla — Sección de nivel y XP en perfil del estudiante.

Prototipo de pantalla — Notificación de ascenso de nivel y recompensa.

Diagrama de caso de uso — PTR13.2.



Reglas de Negocio



No.

Descripción

RN-13.2.1

El nivel se calcula exclusivamente con base en el XP total acumulado por monas desbloqueadas.

RN-13.2.2

Cada mona desbloqueada otorga XP al estudiante según su rareza.

RN-13.2.3

El ascenso de nivel es automático e inmediato cuando el XP supera el umbral definido.

RN-13.2.4

El nivel nunca puede disminuir; el XP acumulado es permanente.

RN-13.2.5

Por cada nuevo nivel alcanzado (nivel 2 en adelante) se desbloquea una recompensa específica.

RN-13.2.6

Las recompensas desbloqueadas son acumulativas; el estudiante retiene las recompensas de todos los niveles alcanzados.

RN-13.2.7

El nivel actual y las recompensas son visibles en el perfil público del estudiante.



Abreviaturas



Abreviatura

Significado

XP

Puntos de Experiencia — métrica de progresión del sistema de niveles.

JWT

JSON Web Token — mecanismo de autenticación.

UUID

Universally Unique Identifier.

PTR

Punto de Requerimiento.

M13

Módulo 13 — Gamificación.



Historial de Revisión



Versión

Fecha

Autor

Descripción del cambio

1.0

15/05/2026

Equipo Charizart

Creación inicial del requisito RF13.2 — Sistema de Niveles.

2.0

18/05/2026

Equipo Charizart

Reestructuración a PTR13.2. Incorporación de XP por mona, recompensas por nivel, tabla de niveles actualizada con XP como criterio de ascenso.





PTR13.3 — Ranking Social

Información General



Campo

Detalle

Código

PTR13.3

Nombre

Ranking Social

Módulo

M13 — Gamificación

Descripción

El sistema ofrece un ranking opcional (opt-in) en el que los estudiantes compiten por posición según monas desbloqueadas. Existen tres tipos de ranking: Semanal, Mensual y Semestral. El ranking es visible solo para quienes decidieron participar. El estudiante puede activar o desactivar su participación en cualquier momento.

Cómo se ejecutará

El estudiante activa su participación desde la configuración de su perfil. El sistema calcula la posición de cada participante por período (semana, mes, semestre) y expone los rankings a través de los endpoints. El ranking semanal se reinicia cada lunes a las 00:00; el mensual, el primer día de cada mes; el semestral, al inicio de cada semestre académico.

Actor principal

Estudiante autenticado

Precondiciones

El usuario debe tener sesión activa con JWT válido. El estudiante debe haber activado el opt-in del ranking para aparecer en él. Autenticación requerida — Header: Authorization: Bearer <JWT>.



Datos de Entrada - PATCH /api/v1/gamificacion/ranking/optin



Campo

Descripción

Tipo

Reglas / Aplicación

Obligatorio

participar

Activa o desactiva la participación en el ranking

Boolean

true = participar | false = salir

Sí



Datos de Salida - PATCH /api/v1/gamificacion/ranking/optin — Respuesta



Campo

Descripción

Tipo

studentId

UUID del estudiante

UUID

rankingOptIn

Estado actualizado de participación

Boolean

updatedAt

Fecha y hora del cambio

LocalDateTime





GET /api/v1/gamificacion/ranking?tipo={semanal|mensual|semestral} — Ranking



Campo

Descripción

Tipo

Reglas / Aplicación

position

Posición en el ranking

Integer

Ordenado de mayor a menor monas del período.

studentId

UUID del estudiante

UUID

—

displayName

Nombre público del estudiante

String

Según configuración de privacidad del perfil.

levelName

Nombre del nivel actual del estudiante

String

—

monasThisPeriod

Monas desbloqueadas en el período en curso

Integer

Se reinicia según el tipo de ranking.

totalMonas

Total histórico de monas del estudiante

Integer

Usado como criterio de desempate.



GET /api/v1/gamificacion/ranking/mi-posicion?tipo={semanal|mensual|semestral}



Campo

Descripción

Tipo

Reglas / Aplicación

position

Posición actual en el ranking

Integer

Nulo si no participa.

monasThisPeriod

Monas obtenidas en el período actual

Integer

—

rankingOptIn

Si el estudiante participa activamente

Boolean

—

periodStart

Inicio del período en curso

LocalDate

Lunes (semanal) / Primer día del mes (mensual) / Inicio del semestre (semestral).

periodEnd

Fin del período en curso

LocalDate

Domingo (semanal) / Último día del mes (mensual) / Fin del semestre (semestral).

rankingType

Tipo de ranking consultado

Enum

WEEKLY | MONTHLY | SEMESTER



Flujo Básico

Flujo A — Activar Participación en Ranking

Paso

Actor

Descripción

Excepciones

1

Estudiante

Accede a la configuración de su perfil y activa la opción 'Participar en los rankings'.

—

2

Sistema

Actualiza el campo rankingOptIn a true para el estudiante.

E1: Token inválido.

3

Sistema

El estudiante comienza a aparecer en los tres tipos de ranking (semanal, mensual, semestral) a partir de ese momento.

—



Flujo B — Consultar Ranking

Paso

Actor

Descripción

Excepciones

1

Estudiante

Accede a la sección de ranking y selecciona el tipo (semanal, mensual, semestral).

—

2

Sistema

Valida el JWT y consulta el listado de estudiantes con rankingOptIn = true.

E1: Token inválido.

3

Sistema

Ordena los participantes por monas del período seleccionado (descendente). En caso de empate, usa el total histórico.

—

4

Sistema

Retorna el ranking paginado con posición, nombre, nivel y monas del período.

E2: Ningún estudiante ha activado opt-in.



Flujo C — Salir del Ranking

Paso

Actor

Descripción

1

Estudiante

Desactiva la participación desde su perfil o configuración.

2

Sistema

Actualiza el campo rankingOptIn a false.

3

Sistema

El estudiante deja de aparecer en todos los rankings de forma inmediata.



Flujo Alterno / Excepciones



Código

Condición que lo dispara

Respuesta del sistema

E1

Token JWT inválido o ausente.

HTTP 401 Unauthorized. Mensaje: 'Autenticación requerida.'

E2

Ningún estudiante tiene opt-in activo.

HTTP 200 OK con lista vacía []. No se considera un error.

E3

El estudiante consulta su posición sin tener opt-in activo.

HTTP 200 OK. El campo position retorna null y rankingOptIn retorna false.

E4

Tipo de ranking no válido en el parámetro.

HTTP 400 Bad Request. Mensaje: 'Tipo de ranking no válido. Use: semanal, mensual o semestral.'

E5

Error de conexión o fallo en base de datos.

HTTP 500 Internal Server Error. Se registra el error en el log.

Notas y Comentarios



La participación en el ranking es opt-in: por defecto, ningún estudiante aparece. El campo rankingOptIn se inicializa en false al crear la cuenta.

Existen tres tipos de ranking: Semanal (reinicio cada lunes a las 00:00), Mensual (reinicio el primer día de cada mes) y Semestral (reinicio al inicio de cada semestre académico).

Al desactivar el opt-in, el estudiante desaparece de todos los rankings de forma inmediata, sin esperar el reinicio del período.

En caso de empate en monas del período, el criterio de desempate es el total histórico de monas del estudiante.

El displayName respeta la configuración de privacidad del perfil: si el estudiante optó por no mostrar su nombre real, se usa un alias.

El ranking no tiene límite de participantes; retorna todos los estudiantes con opt-in activo ordenados por posición.

Si el estudiante consulta su posición sin tener opt-in activo, el sistema retorna HTTP 200 con position: null y rankingOptIn: false, sin error.



Anexos: Prototipos y Diagramas de Caso de Uso



Prototipo de pantalla — Vista de ranking semanal.

Prototipo de pantalla — Vista de ranking mensual.

Prototipo de pantalla — Vista de ranking semestral.

Prototipo de pantalla — Mi posición en el ranking.

Diagrama de caso de uso — PTR13.3.



Reglas de Negocio



No.

Descripción

RN-13.3.1

La participación en el ranking es completamente voluntaria (opt-in). Por defecto, el estudiante no participa.

RN-13.3.2

Existen tres tipos de ranking: Semanal (lunes a domingo), Mensual (primer al último día del mes) y Semestral (duración del semestre académico).

RN-13.3.3

El ranking semanal se reinicia cada lunes a las 00:00 hora del servidor. El mensual, el primer día de cada mes. El semestral, al inicio del semestre.

RN-13.3.4

El estudiante puede activar o desactivar su participación en cualquier momento. Al desactivarla, desaparece inmediatamente de todos los rankings.

RN-13.3.5

En caso de empate en monas del período, se usa el total histórico de monas como criterio de desempate.

RN-13.3.6

El displayName respeta la configuración de privacidad del perfil del estudiante.

RN-13.3.7

El ranking no genera ventajas funcionales dentro de la plataforma; es estrictamente social.







Abreviaturas



Abreviatura

Significado

JWT

JSON Web Token — mecanismo de autenticación utilizado por el sistema.

UUID

Universally Unique Identifier.

PTR

Punto de Requerimiento.

RN

Regla de Negocio.

HTTP

HyperText Transfer Protocol.

API

Application Programming Interface.

REST

Representational State Transfer.

DTO

Data Transfer Object.

RSVP

Répondez s'il vous plaît — confirmación de asistencia a un evento.

M13

Módulo 13 — Gamificación.

ISO 8601

Estándar internacional para representación de fechas y horas.



Historial de Revisión



Versión

Fecha

Autor

Descripción del cambio

1.0

15/05/2026

Equipo Charizart

Creación inicial del requisito RF13.3 — Ranking Social.

2.0

18/05/2026

Equipo Charizart

Reestructuración a PTR13.3. Adición de tres tipos de ranking (semanal, mensual, semestral). Parámetro tipo en endpoints. Actualización de reglas de negocio y flujos. 

ión de reglas de negocio y flujos. 


package com.charizad.compiled.gamification_service.domain.model;

/**
 * Tipos de eventos que pueden disparar el desbloqueo de una mona (RF13.1.1).
 * Cada servicio externo usará uno de estos valores al notificar a gamificación.
 */
public enum BadgeUnlockEventType {
    /** social-matching: usuario realizó o recibió una conexión */
    CONNECTION_CREATED,

    /** hangout-service: usuario se unió o creó un parche */
    PARCHE_JOINED_OR_CREATED,

    /** hangout-service: un nuevo miembro se unió al parche del capitán */
    MEMBER_JOINED_PARCHE,

    /** geolocalización: usuario visitó una nueva zona del campus */
    ZONE_VISITED,

    /** eventos institucionales: usuario asistió a un evento universitario */
    INSTITUTIONAL_EVENT_ATTENDED,

    /** chat/parche: usuario envió su primer mensaje en un parche */
    FIRST_MESSAGE_SENT
}

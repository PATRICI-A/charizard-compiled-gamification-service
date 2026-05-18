package com.charizad.compiled.gamification_service.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Outbound exchange (gamification publishes badge.earned) ──────────────
    @Value("${rabbitmq.exchange.gamification:gamification.events}")
    private String gamificationExchange;

    // ── Inbound exchanges (owned by other microservices) ─────────────────────
    @Value("${rabbitmq.exchange.social:social.events}")
    private String socialExchange;

    @Value("${rabbitmq.exchange.hangout:hangout.events}")
    private String hangoutExchange;

    @Value("${rabbitmq.exchange.geolocation:geolocation.events}")
    private String geolocationExchange;

    @Value("${rabbitmq.exchange.institutional:institutional.events}")
    private String institutionalExchange;

    // ── Queues ────────────────────────────────────────────────────────────────
    @Value("${rabbitmq.queue.connection-created:gamification.connection.queue}")
    private String connectionCreatedQueue;

    @Value("${rabbitmq.queue.parche-created:gamification.parche.queue}")
    private String parcheCreatedQueue;

    @Value("${rabbitmq.queue.member-joined:gamification.member.queue}")
    private String memberJoinedQueue;

    @Value("${rabbitmq.queue.message-sent:gamification.message.queue}")
    private String messageSentQueue;

    @Value("${rabbitmq.queue.zone-visited:gamification.zone.queue}")
    private String zoneVisitedQueue;

    @Value("${rabbitmq.queue.event-attended:gamification.institutional.queue}")
    private String eventAttendedQueue;

    // ── Outbound exchange ─────────────────────────────────────────────────────
    @Bean
    public TopicExchange gamificationExchange() {
        return new TopicExchange(gamificationExchange, true, false);
    }

    // ── Inbound exchanges (declared here so startup doesn't fail if the
    //    producer hasn't created them yet) ─────────────────────────────────────
    @Bean public TopicExchange socialExchange()        { return new TopicExchange(socialExchange,        true, false); }
    @Bean public TopicExchange hangoutExchange()       { return new TopicExchange(hangoutExchange,       true, false); }
    @Bean public TopicExchange geolocationExchange()   { return new TopicExchange(geolocationExchange,   true, false); }
    @Bean public TopicExchange institutionalExchange() { return new TopicExchange(institutionalExchange, true, false); }

    // ── Queues ────────────────────────────────────────────────────────────────
    @Bean public Queue connectionCreatedQueue() { return new Queue(connectionCreatedQueue, true); }
    @Bean public Queue parcheCreatedQueue()     { return new Queue(parcheCreatedQueue,     true); }
    @Bean public Queue memberJoinedQueue()      { return new Queue(memberJoinedQueue,      true); }
    @Bean public Queue messageSentQueue()       { return new Queue(messageSentQueue,       true); }
    @Bean public Queue zoneVisitedQueue()       { return new Queue(zoneVisitedQueue,       true); }
    @Bean public Queue eventAttendedQueue()     { return new Queue(eventAttendedQueue,     true); }

    // ── Bindings ──────────────────────────────────────────────────────────────
    @Bean public Binding bindConnectionCreated() {
        return BindingBuilder.bind(connectionCreatedQueue()).to(socialExchange()).with("connection.created");
    }
    @Bean public Binding bindParcheCreated() {
        return BindingBuilder.bind(parcheCreatedQueue()).to(hangoutExchange()).with("parche.created");
    }
    @Bean public Binding bindMemberJoined() {
        return BindingBuilder.bind(memberJoinedQueue()).to(hangoutExchange()).with("member.joined");
    }
    @Bean public Binding bindMessageSent() {
        return BindingBuilder.bind(messageSentQueue()).to(hangoutExchange()).with("message.sent");
    }
    @Bean public Binding bindZoneVisited() {
        return BindingBuilder.bind(zoneVisitedQueue()).to(geolocationExchange()).with("zone.visited");
    }
    @Bean public Binding bindEventAttended() {
        return BindingBuilder.bind(eventAttendedQueue()).to(institutionalExchange()).with("event.attended");
    }
}

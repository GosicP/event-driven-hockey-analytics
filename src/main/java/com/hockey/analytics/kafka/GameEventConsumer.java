package com.hockey.analytics.kafka;

import com.hockey.analytics.model.GameEvent;
import com.hockey.analytics.service.GameEventProcessingService;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

@Component
public class GameEventConsumer {
    private static final Logger log = (Logger) LoggerFactory.getLogger(GameEventConsumer.class);


    private final GameEventProcessingService gameEventProcessingService;

    public GameEventConsumer(GameEventProcessingService gameEventProcessingService) {
        this.gameEventProcessingService = gameEventProcessingService;
    }

    @KafkaListener(
            topics = "${hockey.kafka.topic.game-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(GameEvent event) {
        log.info(
                "Consumed game event from Kafka: type={}, gameId={}, teamId={}, playerId={}, sequence={}",
                event.getEventType(),
                event.getGameId(),
                event.getTeamId(),
                event.getPlayerId(),
                event.getSequenceNumber()
        );
        gameEventProcessingService.processNewEvent(event);
    }
}
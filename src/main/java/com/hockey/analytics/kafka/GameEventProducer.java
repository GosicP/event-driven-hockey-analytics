package com.hockey.analytics.kafka;

import com.hockey.analytics.model.GameEvent;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

@Service
public class GameEventProducer {
    private static final Logger log = LoggerFactory.getLogger(GameEventProducer.class);

    private final KafkaTemplate<String, GameEvent> kafkaTemplate;
    private final String topicName;

    public GameEventProducer(
            KafkaTemplate<String, GameEvent> kafkaTemplate,
            @Value("${hockey.kafka.topic.game-events}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void send(GameEvent event) {
        log.info(
                "Sending game event to Kafka: type={}, gameId={}, teamId={}, playerId={}, sequence={}",
                event.getEventType(),
                event.getGameId(),
                event.getTeamId(),
                event.getPlayerId(),
                event.getSequenceNumber()
        );
        kafkaTemplate.send(topicName, event.getGameId().toString(), event);
    }
}
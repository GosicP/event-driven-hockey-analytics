package com.hockey.analytics.repository;

import com.hockey.analytics.model.GameEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameEventRepository extends JpaRepository<GameEvent, UUID> {

    List<GameEvent> findByGameIdOrderBySequenceNumber(UUID gameId);

}
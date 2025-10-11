package ru.practicum.repository.types;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.ScenarioAction;
import ru.practicum.entity.ScenarioActionId;

public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {
    @Modifying
    @Transactional
    @Query("delete from ScenarioCondition sc where sc.id.scenarioId = ?1")
    void deleteByIdScenarioId(Long scenarioId);
}
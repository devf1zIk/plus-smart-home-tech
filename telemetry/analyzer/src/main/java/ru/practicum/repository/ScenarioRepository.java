package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.Scenario;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    List<Scenario> findByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);
}
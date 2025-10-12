package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.Sensor;
import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, String> {

    boolean existsByIdInAndHubId(List<String> ids, String hubId);

    void deleteByIdAndHubId(String id, String hubId);

    @Query("SELECT s FROM Sensor s WHERE s.id IN :sensorIds AND s.hubId = :hubId")
    List<Sensor> findByIdInAndHubId(@Param("sensorIds") List<String> sensorIds, @Param("hubId") String hubId);

}
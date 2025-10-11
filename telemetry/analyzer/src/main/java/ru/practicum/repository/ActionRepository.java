package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.Action;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
}

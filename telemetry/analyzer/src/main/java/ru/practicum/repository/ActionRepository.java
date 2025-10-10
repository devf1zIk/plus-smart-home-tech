package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {

}
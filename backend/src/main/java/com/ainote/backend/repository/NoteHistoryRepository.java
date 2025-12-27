package com.ainote.backend.repository;

import com.ainote.backend.model.NoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for NoteHistory entity.
 * Spring Data JPA generates implementation automatically.
 */
@Repository
public interface NoteHistoryRepository extends JpaRepository<NoteHistory, Long> {

    /**
     * Find the 50 most recent note transformations.
     * Method name follows Spring Data JPA naming convention.
     */
    List<NoteHistory> findTop50ByOrderByCreatedAtDesc();
}
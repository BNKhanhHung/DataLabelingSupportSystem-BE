package com.anotation.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    boolean existsByName(String name);

    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameAndIdNot(String name, UUID id);
}

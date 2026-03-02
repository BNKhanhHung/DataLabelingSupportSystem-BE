package com.anotation.label;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {

    Page<Label> findByProjectId(UUID projectId, Pageable pageable);

    boolean existsByNameAndProjectId(String name, UUID projectId);

    boolean existsByNameAndProjectIdAndIdNot(String name, UUID projectId, UUID id);
}

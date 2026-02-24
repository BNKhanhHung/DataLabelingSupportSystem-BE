package com.anotation.dataset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, UUID> {

    // Check duplicate name within same project
    boolean existsByNameAndProjectId(String name, UUID projectId);

    // Check duplicate name within same project excluding self (for update)
    boolean existsByNameAndProjectIdAndIdNot(String name, UUID projectId, UUID id);

    // Get all datasets in a project
    List<Dataset> findByProjectId(UUID projectId);
}

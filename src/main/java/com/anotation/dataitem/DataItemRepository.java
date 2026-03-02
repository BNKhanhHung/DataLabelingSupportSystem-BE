package com.anotation.dataitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DataItemRepository extends JpaRepository<DataItem, UUID> {

    // Check duplicate contentUrl within same dataset
    boolean existsByContentUrlAndDatasetId(String contentUrl, UUID datasetId);

    // Get all items in a dataset
    Page<DataItem> findByDatasetId(UUID datasetId, Pageable pageable);

    // Get items by status
    Page<DataItem> findByStatus(DataItemStatus status, Pageable pageable);

    // Get items by dataset and status
    Page<DataItem> findByDatasetIdAndStatus(UUID datasetId, DataItemStatus status, Pageable pageable);
}

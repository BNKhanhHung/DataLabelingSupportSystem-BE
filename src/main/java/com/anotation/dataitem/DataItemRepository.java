package com.anotation.dataitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataItemRepository extends JpaRepository<DataItem, UUID> {

    // Check duplicate contentUrl within same dataset
    boolean existsByContentUrlAndDatasetId(String contentUrl, UUID datasetId);

    // Get all items in a dataset
    List<DataItem> findByDatasetId(UUID datasetId);

    // Get items by status
    List<DataItem> findByStatus(DataItemStatus status);

    // Get items by dataset and status
    List<DataItem> findByDatasetIdAndStatus(UUID datasetId, DataItemStatus status);
}

package com.anotation.dataset;

import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.project.Project;
import com.anotation.project.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DatasetServiceImpl implements DatasetService {

    private final DatasetRepository datasetRepository;
    private final ProjectRepository projectRepository;
    private final DatasetMapper datasetMapper;

    public DatasetServiceImpl(DatasetRepository datasetRepository,
            ProjectRepository projectRepository,
            DatasetMapper datasetMapper) {
        this.datasetRepository = datasetRepository;
        this.projectRepository = projectRepository;
        this.datasetMapper = datasetMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatasetResponse> getAll() {
        return datasetRepository.findAll()
                .stream()
                .map(datasetMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DatasetResponse getById(UUID id) {
        return datasetMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatasetResponse> getByProject(UUID projectId) {
        return datasetRepository.findByProjectId(projectId)
                .stream()
                .map(datasetMapper::toResponse)
                .toList();
    }

    @Override
    public DatasetResponse create(DatasetRequest request) {
        // 1. Validate project exists
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NotFoundException(
                        "Project not found: " + request.getProjectId()));

        // 2. Prevent duplicate name within same project
        if (datasetRepository.existsByNameAndProjectId(request.getName(), request.getProjectId())) {
            throw new DuplicateException(
                    "Dataset name '" + request.getName() + "' already exists in this project.");
        }

        Dataset dataset = new Dataset();
        dataset.setName(request.getName());
        dataset.setDescription(request.getDescription());
        dataset.setProject(project);

        return datasetMapper.toResponse(datasetRepository.save(dataset));
    }

    @Override
    public DatasetResponse update(UUID id, DatasetRequest request) {
        Dataset dataset = findOrThrow(id);

        // Prevent duplicate name within same project (excluding self)
        if (datasetRepository.existsByNameAndProjectIdAndIdNot(
                request.getName(), dataset.getProject().getId(), id)) {
            throw new DuplicateException(
                    "Dataset name '" + request.getName() + "' already exists in this project.");
        }

        datasetMapper.updateEntity(request, dataset);
        return datasetMapper.toResponse(datasetRepository.save(dataset));
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        datasetRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private Dataset findOrThrow(UUID id) {
        return datasetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dataset not found with id: " + id));
    }
}

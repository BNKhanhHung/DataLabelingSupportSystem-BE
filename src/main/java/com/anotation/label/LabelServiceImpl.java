package com.anotation.label;

import com.anotation.common.PageResponse;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.project.Project;
import com.anotation.project.ProjectRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final ProjectRepository projectRepository;
    private final LabelMapper labelMapper;

    public LabelServiceImpl(LabelRepository labelRepository,
            ProjectRepository projectRepository,
            LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.projectRepository = projectRepository;
        this.labelMapper = labelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LabelResponse> getAll(Pageable pageable) {
        return PageResponse.from(labelRepository.findAll(pageable), labelMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public LabelResponse getById(UUID id) {
        return labelMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LabelResponse> getByProject(UUID projectId, Pageable pageable) {
        return PageResponse.from(labelRepository.findByProjectId(projectId, pageable),
                labelMapper::toResponse);
    }

    @Override
    public LabelResponse create(LabelRequest request) {
        // 1. Validate project exists
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NotFoundException(
                        "Project not found: " + request.getProjectId()));

        // 2. Prevent duplicate name within same project
        if (labelRepository.existsByNameAndProjectId(request.getName(), request.getProjectId())) {
            throw new DuplicateException(
                    "Label name '" + request.getName() + "' already exists in this project.");
        }

        Label label = new Label();
        label.setName(request.getName());
        label.setDescription(request.getDescription());
        label.setColor(request.getColor());
        label.setProject(project);

        return labelMapper.toResponse(labelRepository.save(label));
    }

    @Override
    public LabelResponse update(UUID id, LabelRequest request) {
        Label label = findOrThrow(id);

        // Prevent duplicate name within same project (excluding self)
        if (labelRepository.existsByNameAndProjectIdAndIdNot(
                request.getName(), label.getProject().getId(), id)) {
            throw new DuplicateException(
                    "Label name '" + request.getName() + "' already exists in this project.");
        }

        labelMapper.updateEntity(request, label);
        return labelMapper.toResponse(labelRepository.save(label));
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        labelRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private Label findOrThrow(UUID id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Label not found with id: " + id));
    }
}

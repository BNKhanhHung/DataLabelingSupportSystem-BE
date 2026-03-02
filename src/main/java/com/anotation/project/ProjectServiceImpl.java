package com.anotation.project;

import com.anotation.common.PageResponse;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> getAll(Pageable pageable) {
        return PageResponse.from(projectRepository.findAll(pageable), projectMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> searchByName(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return getAll(pageable);
        }
        return PageResponse.from(projectRepository.findByNameContainingIgnoreCase(name, pageable),
                projectMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID id) {
        Project project = findOrThrow(id);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        if (projectRepository.existsByName(request.getName())) {
            throw new DuplicateException("Project name already exists: " + request.getName());
        }

        Project project = projectMapper.toEntity(request);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = findOrThrow(id);

        if (projectRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateException("Project name already exists: " + request.getName());
        }

        projectMapper.updateEntity(request, project);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        projectRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private Project findOrThrow(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found with id: " + id));
    }
}

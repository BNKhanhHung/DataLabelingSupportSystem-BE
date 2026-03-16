package com.anotation.project;

import com.anotation.common.PageResponse;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.task.Task;
import com.anotation.task.TaskRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
            ProjectMapper projectMapper,
            TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> getAll(Pageable pageable) {
        try {
            return PageResponse.from(projectRepository.findAll(pageable), this::toResponseWithStatus);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(projectRepository.findAll(safe), this::toResponseWithStatus);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> searchByName(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return getAll(pageable);
        }
        try {
            return PageResponse.from(projectRepository.findByNameContainingIgnoreCase(name, pageable),
                    this::toResponseWithStatus);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(projectRepository.findByNameContainingIgnoreCase(name, safe),
                    this::toResponseWithStatus);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID id) {
        Project project = findOrThrow(id);
        return toResponseWithStatus(project);
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        if (projectRepository.existsByName(request.getName())) {
            throw new DuplicateException("Project name already exists: " + request.getName());
        }

        Project project = projectMapper.toEntity(request);
        // Nếu không đặt deadline → mặc định 7 ngày sau khi tạo
        if (project.getDeadline() == null) {
            project.setDeadline(LocalDateTime.now().plusDays(7));
        }
        return toResponseWithStatus(projectRepository.save(project));
    }

    @Override
    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = findOrThrow(id);

        if (projectRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateException("Project name already exists: " + request.getName());
        }

        projectMapper.updateEntity(request, project);
        return toResponseWithStatus(projectRepository.save(project));
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

    /**
     * Convert project to response with computed status from its tasks.
     */
    private ProjectResponse toResponseWithStatus(Project project) {
        List<Task> tasks = taskRepository.findByProjectId(project.getId(),
                Pageable.unpaged()).getContent();
        return projectMapper.toResponse(project, tasks);
    }
}

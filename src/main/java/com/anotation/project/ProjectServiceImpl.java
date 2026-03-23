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

/**
 * Triển khai {@link ProjectService}: CRUD dự án, tìm theo tên, và map sang
 * {@link ProjectResponse} kèm {@link ProjectStatus} suy ra từ danh sách {@link Task}
 * của dự án.
 * <p>
 * Mọi phương thức ghi đều chạy trong transaction; các thao tác đọc dùng
 * {@code @Transactional(readOnly = true)} khi có thể.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;

    /**
     * @param projectRepository lưu trữ {@link Project}
     * @param projectMapper     chuyển entity ↔ DTO
     * @param taskRepository    dùng để lấy task theo project khi tính trạng thái tổng hợp
     */
    public ProjectServiceImpl(ProjectRepository projectRepository,
            ProjectMapper projectMapper,
            TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.taskRepository = taskRepository;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID id) {
        Project project = findOrThrow(id);
        return toResponseWithStatus(project);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Nếu entity sau khi map không có {@code deadline}, gán mặc định 7 ngày kể từ thời điểm tạo.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = findOrThrow(id);

        if (projectRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateException("Project name already exists: " + request.getName());
        }

        projectMapper.updateEntity(request, project);
        return toResponseWithStatus(projectRepository.save(project));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        projectRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    /**
     * Tìm dự án theo id hoặc ném {@link NotFoundException}.
     *
     * @param id UUID dự án
     * @return entity {@link Project}
     */
    private Project findOrThrow(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found with id: " + id));
    }

    /**
     * Chuyển {@link Project} sang {@link ProjectResponse}, đồng thời tính
     * {@link ProjectStatus} dựa trên toàn bộ {@link Task} thuộc dự án (không phân trang).
     *
     * @param project entity dự án
     * @return DTO phản hồi kèm trạng thái tổng hợp
     */
    private ProjectResponse toResponseWithStatus(Project project) {
        List<Task> tasks = taskRepository.findByProjectId(project.getId(),
                Pageable.unpaged()).getContent();
        return projectMapper.toResponse(project, tasks);
    }
}

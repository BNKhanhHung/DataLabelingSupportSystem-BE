package com.anotation.task;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Hợp đồng dịch vụ nghiệp vụ cho {@link Task}: truy vấn phân trang, tạo/sửa/xóa task,
 * luồng nộp review, hoàn tất review, quá hạn, KPI annotator, từ chối nhận việc và phân công lại.
 */
public interface TaskService {

    /**
     * Lấy danh sách task phân trang theo tham số {@link Pageable} (sắp xếp, kích thước trang).
     *
     * @param pageable thông tin trang và sort; nếu sort không hợp lệ, implementation có thể fallback sort theo {@code id}
     * @return {@link PageResponse} chứa các {@link TaskResponse}
     */
    PageResponse<TaskResponse> getAll(Pageable pageable);

    /**
     * Lấy chi tiết một task theo định danh.
     *
     * @param id UUID của task
     * @return DTO {@link TaskResponse} tương ứng
     * @throws com.anotation.exception.NotFoundException nếu không tồn tại task
     */
    TaskResponse getById(UUID id);

    /**
     * Lấy danh sách mục (task items) thuộc một task: phục vụ màn hình gán nhãn của annotator.
     * Mỗi phần tử gồm taskItemId, dataItemId, URL nội dung và cờ đã có annotation hay chưa.
     *
     * @param taskId UUID của task
     * @return danh sách {@link TaskItemResponse}; chỉ annotator được giao hoặc Manager/Admin mới được xem
     */
    List<TaskItemResponse> getTaskItems(UUID taskId);

    /**
     * Lấy các task thuộc một dự án, phân trang.
     *
     * @param projectId UUID dự án
     * @param pageable tham số phân trang
     * @return trang kết quả {@link TaskResponse}
     */
    PageResponse<TaskResponse> getByProject(UUID projectId, Pageable pageable);

    /**
     * Lấy task do một annotator đảm nhiệm, loại trừ trạng thái đã xong (ví dụ COMPLETED/REVIEWED) theo quy tắc danh sách “được giao”.
     *
     * @param annotatorId UUID user annotator
     * @param pageable tham số phân trang
     * @return trang kết quả
     */
    PageResponse<TaskResponse> getByAnnotator(UUID annotatorId, Pageable pageable);

    /**
     * Lịch sử task của annotator: các task đã nộp/được duyệt/đã hoàn tất (không còn trong danh sách “cần gán nhãn”).
     *
     * @param annotatorId UUID user annotator
     * @param pageable tham số phân trang
     * @return trang kết quả lịch sử
     */
    PageResponse<TaskResponse> getAnnotatorHistory(UUID annotatorId, Pageable pageable);

    /**
     * Lấy hàng đợi review của reviewer: task ở trạng thái chờ duyệt (ví dụ SUBMITTED, OVERDUE) mà reviewer đó được giao.
     *
     * @param reviewerId UUID user reviewer
     * @param pageable tham số phân trang
     * @return trang kết quả
     */
    PageResponse<TaskResponse> getByReviewer(UUID reviewerId, Pageable pageable);

    /**
     * Lịch sử task của reviewer: các task reviewer đã review xong hoặc đã hoàn tất.
     *
     * @param reviewerId UUID user reviewer
     * @param pageable tham số phân trang
     * @return trang kết quả lịch sử
     */
    PageResponse<TaskResponse> getReviewerHistory(UUID reviewerId, Pageable pageable);

    /**
     * Tìm kiếm task theo tên (hoặc tiêu chí tìm kiếm do repository định nghĩa) và lọc theo {@link TaskStatus}.
     *
     * @param name chuỗi tìm kiếm (có thể null tùy implementation/repository)
     * @param status trạng thái lọc (có thể null)
     * @param pageable tham số phân trang
     * @return trang kết quả
     */
    PageResponse<TaskResponse> search(String name, TaskStatus status, Pageable pageable);

    /**
     * Tạo task mới: gán project, annotator, reviewer, danh sách data item, deadline; kiểm tra quyền Manager và giới hạn WIP.
     *
     * @param request payload {@link TaskRequest}
     * @return {@link TaskResponse} sau khi lưu
     */
    TaskResponse create(TaskRequest request);

    /**
     * Cập nhật trạng thái task (thường dùng cho thao tác quản trị); có thể kèm gửi thông báo.
     *
     * @param id UUID task
     * @param status trạng thái mới
     * @return DTO sau cập nhật
     */
    TaskResponse updateStatus(UUID id, TaskStatus status);

    /**
     * Cập nhật hạn hoàn thành (due date). Truyền {@code null} để xóa deadline trên task.
     *
     * @param id UUID task
     * @param dueDate thời điểm hạn mới hoặc {@code null}
     * @return DTO sau cập nhật
     */
    TaskResponse updateDueDate(UUID id, LocalDateTime dueDate);

    /**
     * Annotator nộp task sau khi đã gán nhãn đủ: chuyển sang {@link TaskStatus#SUBMITTED} để reviewer kiểm duyệt.
     *
     * @param taskId UUID task
     * @return DTO sau khi nộp
     */
    TaskResponse submitForReview(UUID taskId);

    /**
     * Reviewer hoàn tất review: nếu không còn nhãn bị từ chối → {@link TaskStatus#REVIEWED};
     * nếu còn nhãn REJECTED → {@link TaskStatus#DENIED} để annotator sửa lại.
     *
     * @param taskId UUID task
     * @return DTO trạng thái sau xử lý
     */
    TaskResponse completeReview(UUID taskId);

    /**
     * Lấy danh sách task quá hạn (đã qua due date và chưa hoàn thành theo định nghĩa repository).
     *
     * @param pageable tham số phân trang
     * @return trang {@link TaskResponse}
     */
    PageResponse<TaskResponse> getOverdueTasks(Pageable pageable);

    /**
     * Job/batch đánh dấu các task đủ điều kiện sang {@link TaskStatus#OVERDUE}, tăng cảnh báo user chịu trách nhiệm và gửi thông báo.
     */
    void markOverdueTasks();

    /**
     * Tính chỉ số KPI hiệu suất cho một annotator (số task, hoàn thành, quá hạn, annotation duyệt/từ chối, tỷ lệ duyệt).
     *
     * @param userId UUID user (annotator)
     * @return {@link KpiResponse} đã điền số liệu
     */
    KpiResponse getAnnotatorKpi(UUID userId);

    /**
     * Annotator hoặc reviewer được giao từ chối nhận task: task về {@link TaskStatus#OPEN}, gỡ gán (theo nghiệp vụ) và thông báo Manager.
     *
     * @param taskId UUID task
     * @param reason lý do từ chối (hiển thị/ghi log thông báo)
     * @return DTO sau cập nhật
     */
    TaskResponse refuseTask(UUID taskId, String reason);

    /**
     * Manager/Admin đổi phân công annotator và reviewer cho task (không áp dụng khi task đã ở trạng thái kết thúc luồng như SUBMITTED/REVIEWED/COMPLETED).
     *
     * @param taskId UUID task
     * @param annotatorId UUID annotator mới
     * @param reviewerId UUID reviewer mới
     * @return DTO sau phân công
     */
    TaskResponse assign(UUID taskId, UUID annotatorId, UUID reviewerId);

    /**
     * Danh sách task {@link TaskStatus#IN_PROGRESS} mà annotator được giao.
     *
     * @param annotatorId UUID annotator
     * @param pageable phân trang
     * @return trang kết quả
     */
    PageResponse<TaskResponse> getAssignedInProgressByAnnotator(UUID annotatorId, Pageable pageable);

    /**
     * Danh sách task {@link TaskStatus#IN_PROGRESS} mà reviewer được giao (task đang gắn reviewer đó).
     *
     * @param reviewerId UUID reviewer
     * @param pageable phân trang
     * @return trang kết quả
     */
    PageResponse<TaskResponse> getAssignedInProgressByReviewer(UUID reviewerId, Pageable pageable);

    /**
     * Xóa task và dữ liệu phụ thuộc (feedback, annotation, task items) theo thứ tự an toàn.
     *
     * @param id UUID task cần xóa
     */
    void delete(UUID id);
}

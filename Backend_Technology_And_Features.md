# TỔNG QUAN CÔNG NGHỆ & TÍNH NĂNG BACKEND
*(Tài liệu dùng để chém gió phần Tổng Quan với Giáo viên phản biện)*

---

## 1. CÔNG NGHỆ SỬ DỤNG TRONG BACKEND (Tech Stack)
*Giáo viên sẽ hỏi: "Hệ thống em dùng ngôn ngữ gì, bao nhiêu, database ở đâu?"*

- **Ngôn ngữ cốt lõi:** Java 17 (Bản chuẩn dài hạn LST phổ biến ở doanh nghiệp).
- **Framework nền tảng:** Spring Boot 3.x (Bản mới nhất, khởi tạo dự án nhanh, tích hợp sẵn máy chủ web Tomcat).
- **Bảo mật & Cấp quyền:** Spring Security kết hợp với JWT Token (JSON Web Token).
- **Tương tác Cơ sở dữ liệu:** Spring Data JPA / Hibernate (ORM - Biến Class Java thành Table Database tự động mà không cần gõ câu SQL tạo bảng).
- **Cơ sở dữ liệu chính:** PostgreSQL (Được host trên đám mây **Supabase**).
- **Trình tạo tài liệu API:** Swagger UI / OpenAPI 3 (Tự động quét code tạo giao diện Test API trắng trẻo, xịn xò cho Frontend dùng).
- **Quản lý thư viện:** Maven.

---

## 2. KIẾN TRÚC MÃ NGUỒN (Architecture)
*Phần này dùng để ăn điểm cấu trúc, chứng minh không code bừa bãi:*

1. **Hiển thị theo tính năng (Package-by-Feature / Domain-Driven Design):**
   - Thay vì vứt mọi thứ vào 4 thư mục `controller, service, repository, model`, dự án này gom gọn mọi tầng kiến trúc vào **Tên tính năng** (VD: Thư mục `task` sẽ chứa `Task.java, TaskController, TaskService...`). Giúp code cực dễ nâng cấp và bảo trì, dọn đường cho Microservices sau này.
   
2. **Mô hình 3 Lớp (3-Tier Layered trong mỗi Feature):**
   - **Controller (Cô tiếp tân):** Đón Request từ trình duyệt, check đúng định dạng, trả về HTTP Status (200 OK, 400 Bad Request...).
   - **Service (Nhà máy xử lý):** Chứa các câu `if/else`, vòng lặp kinh doanh (VD: Nếu task quá hạn thì cấm nộp bài).
   - **Repository (Thủ kho):** Liên kết thẳng xuống Supabase để `SAVE()` hoặc `FIND()` dữ liệu.

3. **Mẫu thiết kế DTO (Data Transfer Object):**
   - Che giấu dữ liệu gốc của Database (Entity). Chỉ lấy những trường cần thiết nhét vào các class Request/Response ném ra mạng.

4. **Lớp Phiên dịch chuyển đổi (Mapper Pattern):**
   - Đóng vai trò là "Cầu nối" chuyên trách để dịch qua lại giữa **bản gốc Entity** và **bản gửi mạng DTO**. Nhờ có màng lọc này mà tầng `Service` thay vì phải gõ dài dòng `dto.setName(entity.getName())`, giờ chỉ cần gọi đúng 1 hàm `mapper.toResponse()`. Đảm bảo luồng Code chuẩn Clean Architecture sạch sẽ tuyệt đối.

---

## 3. CÁC TÍNH NĂNG ĐÃ THỰC HIỆN (Features)
*(Từ đầu kỳ tới giờ bọn mình đã cày cuốc xây dựng toàn bộ Móng Nhà này)*

### A. Core Features (Tính năng Xương sống nền tảng)
1. Xây dựng và chốt sơ đồ **ERD Data Schema** phức tạp (Entity Relationship) cho toàn bộ hệ thống (Users, Mối liên kết Annotators/Reviewers, Projects, Tasks, DataItems, Annotations, Feedbacks).
2. Tích hợp Auth & Hệ thống phân quyền (`MANAGER`, `ANNOTATOR`, `REVIEWER`).
3. Luồng Setup Quản Lý:
   - Quản lý kho **Dataset** (Bộ tập hợp hàng ngàn tấm hình).
   - Quản lý **Danh mục Label** (Label Template - để vẽ nhãn cho AI học).
   - Tạo mới và quản lý vòng đời **Dự án (Project)**.

### B. Business Features (Nghiệp vụ Xử lý Công việc cốt lõi)
1. Cấu trúc liên kết **Phân Công Việc (Task Assignment):** 
   - Một Task được giao ĐỒNG THỜI cho 1 ông Annotator (người làm) và 1 ông Reviewer (người chấm điểm bảo hành).
   - **File xử lý:** `src/main/java/com/anotation/task/Task.java`
     ```java
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "annotator_id", nullable = false)
     private User annotator;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "reviewer_id", nullable = false)
     private User reviewer;
     ```

2. Xử lý Trạng Thái Công Việc liên hoàn (State Machine):
   - Chuyển tuần tự: `NOT_STARTED` -> `IN_PROGRESS` (đang gán nhãn) -> `SUBMITTED` (nộp bài) -> `REVIEWED/REJECTED` (Ông reviewer trả kết quả). Cuối cùng -> `COMPLETED`.
   - **File xử lý cốt lõi:** `TaskServiceImpl.java` (Các hàm `submitTask`, `reviewTask`).
     ```java
     // Ví dụ chốt chặn Logic khi Nộp bài làm:
     if (task.getStatus() != TaskStatus.IN_PROGRESS && task.getStatus() != TaskStatus.NOT_STARTED) {
         throw new RuntimeException("Chỉ được nộp khi Task đang làm hoặc chưa bắt đầu");
     }
     task.setStatus(TaskStatus.SUBMITTED);
     taskRepository.save(task);
     ```

3. Lưu trữ Tọa độ Nhãn (Annotations) dưới dạng chuỗi siêu dữ liệu linh hoạt.

### C. Chuyên biệt hóa Vai trò (Role-based Workflow)
*Hệ thống phân luồng nghiệp vụ xương sống, đảm bảo dữ liệu không bị nhân viên gian lận truy cập chéo. Cơ chế phân quyền được bảo vệ chặt chẽ bằng Annotation `@PreAuthorize` của Spring Security ở tầng Controller:*

1. **MANAGER (Quản lý cấp cao - Nắm quyền tạo mới & Phân phối):**
   - Nắm quyền "Sinh - Sát": Tạo Dự án, Tạo Bộ nhãn (Label), Phân công Task cho User, Upload Hàng ngàn ảnh gốc.
   - **Code chứng minh quyền hạn:** `TaskController.java`
     ```java
     // Chỉ Manager mới có quyền vứt Task (giao việc) cho thiên hạ
     @PostMapping("/assign")
     @PreAuthorize("hasAuthority('MANAGER')")
     public ResponseEntity<TaskResponse> assignTask(@Valid @RequestBody TaskRequest request) { ... }
     ```

2. **ANNOTATOR (Nhân viên gán nhãn - Quyền hạn cách ly):**
   - Chỉ được nhìn thấy và thao tác trên những tấm ảnh (DataItems) thuộc về cái Task mà **chính mình được giao**. Quyền hạn bị khóa chặt với `annotator_id`.
   - Luồng làm việc: Mở API lấy danh sách Task của mình -> Gửi JSON lưu tọa độ nhãn -> Đổi Trạng thái Task sang Nộp Bài.
   - **Code chứng minh luồng nộp bài:** `TaskController.java` & `TaskServiceImpl.java`
     ```java
     // Chỉ ông Annotator mới được phép báo cáo "Tui làm xong rồi" (SUBMIT)
     @PutMapping("/{id}/submit")
     @PreAuthorize("hasAuthority('ANNOTATOR')")
     public ResponseEntity<TaskResponse> submitTask(@PathVariable UUID id) { 
         // Service sẽ check: Nếu đang IN_PROGRESS mới được SUBMIT
     }
     ```

3. **REVIEWER (Người kiểm duyệt QA - Nắm quyền Sinh Tử kết quả):**
   - Đóng vai trò màng lọc. Chỉ được rớ tới các Task đã được Annotator nộp (`SUBMITTED`).
   - Có cờ đánh giá: `APPROVED` (Cho qua) hoặc `REJECTED` (Đuổi về).
   - Nếu REJECTED bắt buộc phải ghi Feedback.
   - **Code chứng minh Logic duyệt bài:** `ReviewFeedbackServiceImpl.java`
     ```java
     // Bắt buộc phải có lý do nếu từ chối
     if (request.getStatus() == TaskStatus.REJECTED && 
        (request.getComments() == null || request.getComments().isBlank())) {
         throw new RuntimeException("Phải nhập lý do Feedback khi REJECT task!");
     }
     
     // Cập nhật trạng thái Task và Nhãn về REJECTED, ép Annotator làm lại
     task.setStatus(request.getStatus());
     ```

### D. Advanced Features (Tính năng Nâng cao lấy điểm A+)
*Đây là những tính năng khoe tay nghề Coder giỏi, chống sập hệ thống và tính toán tự động real-time:*

1. **Tính toán Trạng Thái Dự Án Tự Động (Dynamic Project Status):**
   - Thay vì bắt Manager bấm nút Đóng/Mở dự án bằng tay, hệ thống sẽ vòng lặp tự quét các Task con.
   - **File xử lý:** `ProjectMapper.java` (Hàm `computeStatus`)
     ```java
     boolean hasOverdue = false, allCompleted = true, anyStarted = false;
     for (Task t : tasks) {
         //... check từng task con
         if (t.getStatus() != TaskStatus.COMPLETED) allCompleted = false;
     }
     if (hasOverdue) return ProjectStatus.OVERDUE;
     if (allCompleted) return ProjectStatus.COMPLETED;
     // ...
     ```

2. **Quản lý Deadline Cứng (Dynamic Task Overdue):**
   - Kép hàm check giờ phút Server `LocalDateTime.now()` mỗi lần load API để khóa cứng Task. Giúp đánh cờ "Trễ hạn" chuẩn số giây mà không tốn tải quét định kỳ.
   - **File xử lý:** `TaskMapper.java`
     ```java
     boolean isOverdue = task.getDueDate() != null
         && LocalDateTime.now().isAfter(task.getDueDate())
         && task.getStatus() != TaskStatus.COMPLETED
         && task.getStatus() != TaskStatus.REVIEWED;
     ```

3. **Thống kê KPI Năng lực Annotator:**
   - Dùng sức mạnh của API Native SQL để đếm số ảnh thay vì kéo RAM xử lý LIST siêu to.
   - **File xử lý:** `AnnotationRepository.java` và `TaskServiceImpl.java`
     ```java
     // Tại Repository (PostgreSQL)
     @Query("SELECT COUNT(a) FROM Annotation a WHERE a.taskItem.task.annotator.id = :userId AND a.status = 'APPROVED'")
     long countApprovedByAnnotatorUserId(@Param("userId") UUID userId);

     // Tại Service (Làm tròn chống tràn tỉ lệ phần trăm)
     kpi.setApprovalRate(reviewed > 0 
         ? Math.round((double) kpi.getApprovedCount() / reviewed * 10000.0) / 100.0 
         : 0.0);
     ```

4. **Upload Hình Ảnh Tốc Độ Cao Hàng Loạt (Bulk Upload):**
   - Cung cấp API kéo thả 1.000 link URL cùng lúc. Cốt lõi là thuật toán "Gạt rác mầm" lọc trùng lặp âm thầm, File nào trùng bỏ qua (`continue`), chạy file tiếp theo không sập vòng lặp.
   - **File xử lý:** `DataItemServiceImpl.java` (Hàm `bulkCreate`)
     ```java
     for (String url : request.getContentUrls()) {
         if (url == null || url.isBlank()) continue;
         // Lọc âm thầm rác rưởi (Chống 2 link giống hệt nhau đổ ập vào)
         if (dataItemRepository.existsByContentUrlAndDatasetId(url, datasetId)) {
             continue; // Bỏ qua link này, chạy link tiếp
         }
         // ... Tiến hành Save
     }
     ```

---

## 4. KỊCH BẢN LUỒNG CHẠY THỰC TẾ TRỌN VẸN (End-To-End Workflow)
*(Đây là Kịch bản Dây chuyền sản xuất Data. Bật kèm file code này lên để chứng minh khi báo cáo)*

### Giai đoạn 1: Chuẩn bị nguyên liệu (Actor: MANAGER)
1. **Tạo rổ chứa Data:** Manager gọi API `POST /api/datasets`. 
   👉 **(Mở file `DatasetServiceImpl.java` -> Hàm `create()`)**
   ```java
   Dataset dataset = datasetMapper.toEntity(request);
   // Lưu kho
   dataset = datasetRepository.save(dataset); 
   ```
2. **Bắn 1.000 Ảnh Lên Server:** Manager ném 1000 đường link hình thô (máy bay, xe tăng...) tóm gọn 1 phát vào API `POST /api/data-items/bulk`. Cục BE quét siêu tốc, lọc trùng lặp rồi lưu vào kho CSDL.
   👉 **(Mở file `DataItemServiceImpl.java` -> Hàm `bulkCreate()`)**
   ```java
   for (String url : request.getContentUrls()) {
       // Thuật toán: Chặn đứng nếu phát hiện ảnh đã tồn tại trong kho rổ này
       if (dataItemRepository.existsByContentUrlAndDatasetId(url, datasetId)) continue;
       // ... Lưu ảnh mới
   }
   ```
3. **Khai sinh Dự Án (Project):** Manager tạo Project mới mang tên "Nhận diện vũ khí". Lúc này `ProjectStatus` đang là `NOT_STARTED`.
   👉 **(Mở file `ProjectServiceImpl.java` -> Hàm `create()`)**
   ```java
   project.setStatus(ProjectStatus.NOT_STARTED); 
   projectRepository.save(project);
   ```
4. **Phân Công Task cho nhân viên:** Manager bấm nút Phân Công (Assign). Backend lôi 1.000 tấm ảnh kia ra tạo thành 1.000 cái **Task** rồi cất xuống kho.
   👉 **(Mở file `TaskServiceImpl.java` -> Hàm `create()`)**

### Giai đoạn 2: Công nhân cày cuốc (Actor: ANNOTATOR)
1. Ông A log vào hệ thống. API `GET /api/tasks` check trúng Token của ông A -> Trả về màn hình đúng những tấm ảnh được giao. Task lúc này đổi cờ thành `IN_PROGRESS`.
   👉 **(Mở file `TaskServiceImpl.java` -> Hàm `getUserTasks()`)**
2. Ông A gõ lệnh tọa độ vẽ cái hộp khoanh vùng chiếc xe Tăng. Nhấn nộp bài thông qua API `PUT /api/tasks/{id}/submit`. 
3. Backend chụp ngay khoảnh khắc đó, chạy thuật toán quét Giờ Phút Server (`LocalDateTime`).
   - *Nếu bị trễ Dealide:* Backend chửi "Trễ hạn rồi" và đá văng lỗi HTTP 400.
   - *Nếu ngon lành:* Task được đổi cờ thành `SUBMITTED` chờ kho đạn ông duyệt bài.
   👉 **(Mở file `TaskServiceImpl.java` -> Hàm `submitTask()`)**
   ```java
   // Chốt chặn nộp bài trễ
   if (task.getDueDate() != null && LocalDateTime.now().isAfter(task.getDueDate())) {
       throw new RuntimeException("Xin lỗi, Task đã quá hạn nộp bài (Ngậm ngùi)!");
   }
   // Đổi cờ giao nộp
   task.setStatus(TaskStatus.SUBMITTED);
   ```

### Giai đoạn 3: Thanh tra chấm thi (Actor: REVIEWER)
1. Ông B (Reviewer) log vào, thấy danh sách Task đang chờ ở mục `SUBMITTED`. Bật hình lên xem. Thấy ông A khoanh vùng cái xe Tăng mà dính cả gốc cây. 
2. Ông B gọi API `POST /api/review-feedbacks`.
   - Chọn cờ **REJECTED**.
   - Ghi lý do (Feedback): *"Mày khoanh sát lại cái xích xe tăng giùm coi"*. 
   - Trạng thái Task bị đạp ngược lại thành `REJECTED`. *(Nếu ông B lừa không chịu gõ lý do, Backend sẽ tung RuntimeException cấm duyệt)*.
   👉 **(Mở file `ReviewFeedbackServiceImpl.java` -> Hàm `create()`)**
   ```java
   // Thuật toán: Bắt ép nhập Feedback nếu đánh trượt bài
   if (request.getStatus() == TaskStatus.REJECTED && (request.getComments() == null || request.getComments().isBlank())) {
       throw new RuntimeException("Reviewer làm ơn ghi rõ lý do Reject giùm!");
   }
   task.setStatus(request.getStatus()); // Đạp task về REJECTED
   ```
3. Ông A lóc cóc vào vẽ lại cho mướt rượt rồi Submit lại -> Ông B xem ổn áp thì gọi API đó lần nữa để chọn chốt đơn **APPROVED**. Bấy giờ Task chính thức lên bàn thờ `COMPLETED`. Mọi Annotation được Lock khóa vĩnh viễn bảo tồn bằng chứng.
   👉 **(Mở file `TaskServiceImpl.java` -> Hàm `reviewTask()`)**
   ```java
   // Khi đã Approved thì Task mãn nguyện đổi thành COMPLETED
   if (status == TaskStatus.APPROVED) {
       task.setStatus(TaskStatus.COMPLETED);
   }
   ```

### Giai đoạn 4: Thu hoạch và Tổng kết (Actor: AUTO-SYSTEM)
1. **Dự án đóng hòm:** Không cần ai đụng tay, mỗi lần Manager kéo Dashboard xem, cục BE âm thầm chạy vòng lặp kiểm tra toàn bộ 1.000 cái Task con. Thấy cả 1.000 cái đều đã cờ `COMPLETED` -> Backend tự động hất cờ cái Project bự thành `COMPLETED` màu xanh lá cây chói lọi.
   👉 **(Mở file `ProjectMapper.java` -> Hàm `computeStatus()`)**
   ```java
   boolean allCompleted = true;
   for (Task t : tasks) { 
       if (t.getStatus() != TaskStatus.COMPLETED) allCompleted = false; 
   }
   if (allCompleted) return ProjectStatus.COMPLETED;
   ```
2. **Báo cáo Năng lực (KPI):** Manager bật bảng thành tích, Backend kích hoạt câu lệnh `SQL COUNT` Native rọc xuống DB, tính toán và làm tròn phần trăm tỷ lệ Duyệt.
   👉 **(Mở file `TaskServiceImpl.java` -> Hàm `getAnnotatorKpi()`)**
   ```java
   // Đếm tổng số hình được Approve bằng @Query
   long approvedCount = annotationRepository.countApprovedByAnnotatorUserId(userId);
   
   // Thuật toán Tính tiền Lương: Làm tròn chính xác 2 chữ số phần trăm (%)
   double rate = (double) approvedCount / reviewed * 10000.0;
   kpi.setApprovalRate(Math.round(rate) / 100.0);
   ```

5. **Hệ Thống Trực Ban Chạy Ngầm (Spring Scheduler & Auto-Notification):**
   - Tích hợp tính năng lên lịch định kỳ `@Scheduled(cron = "0 */15 * * * *")`. Đúng 15 phút một lần, hệ thống tự động thức giấc, quét toàn bộ Database tìm các Task đã lố Deadline nhưng nhân viên chây ỳ chưa nộp. Sau đó tự động đổi cờ thành `OVERDUE` và *tự động bắn 1 dòng Thông Báo (Notification)* đến màn hình của nhân viên đó.
   - **File xử lý:** `OverdueTaskStatusScheduler.java` và `OverdueNotificationScheduler.java`
     ```java
     // Chạy ngầm âm thầm mỗi 15 phút mà không cần ai bấm nút
     @Scheduled(cron = "0 */15 * * * *")
     public void markOverdueTasksEvery15Minutes() {
         taskService.markOverdueTasks(); // Đổi cờ lố hạn
     }
     ```

6. **Giới Hạn Khối Lượng Công Việc - WIP Limit (Work-In-Progress Limit):**
   - Mỗi Annotator và Reviewer chỉ được phép giữ **tối đa 3 Task** đang hoạt động (trạng thái `OPEN`, `IN_PROGRESS`, `SUBMITTED`) cùng một lúc. Nếu Manager cố giao thêm Task thứ 4 → Hệ thống đá văng `400 Bad Request`.
   - **File xử lý:** `TaskServiceImpl.java` → hàm `create()`
     ```java
     private static final int MAX_ACTIVE_TASKS = 3;

     // Đếm task đang hoạt động của Annotator trước khi cho giao thêm
     long activeCount = taskRepository.countActiveTasksByAnnotatorId(annotatorId);
     if (activeCount >= MAX_ACTIVE_TASKS) {
         throw new BadRequestException("Đã đạt giới hạn 3 task đang hoạt động.");
     }
     ```
   - Áp dụng song song cho cả **Annotator lẫn Reviewer**: Cùng 1 cơ chế đếm, cùng 1 hằng số `MAX_ACTIVE_TASKS`, đảm bảo công bằng tải trọng cho toàn bộ dây chuyền.

7. **Quyền Từ Chối Task (Refuse Task):**
   - Annotator/Reviewer có quyền **từ chối Task** được giao nếu Task đang ở trạng thái `OPEN` hoặc `IN_PROGRESS`. Lý do từ chối **bắt buộc phải nhập** (10-500 ký tự) để Manager đánh giá.
   - Sau khi từ chối: Task tự động trả về trạng thái `OPEN` (vô chủ, chờ Manager giao lại) + bắn **Notification `TASK_REFUSED`** kèm lý do tới toàn bộ Manager.
   - **File xử lý:** `TaskServiceImpl.java` → hàm `refuseTask()`, `TaskRefuseRequest.java` (DTO)
     ```java
     // Chỉ người được giao + task đang OPEN/IN_PROGRESS mới được refuse
     task.setStatus(TaskStatus.OPEN); // Trả về rổ chờ
     notificationService.create(managerId, "TASK_REFUSED",
         "Task bị từ chối", "Annotator X đã từ chối. Lý do: ...", "TASK", taskId);
     ```

---

## 4.5. TÓM TẮT NHỮNG TÍNH NĂNG CHÍNH EM ĐÃ TỰ TAY CODE (Để báo cáo)
*(Giáo viên thường hỏi: "Vậy tóm lại em làm được tính năng cụ thể nào trong cái mớ Backend này?", bạn đập ngay list này ra)*

1. **Thiết kế Kiến trúc Hệ thống & Database:** Phác thảo Schema ERD, tinh chỉnh DTO/Mapper pattern, và cấu trúc thư mục `Package-by-Feature` để Code siêu sạch, siêu dễ nâng cấp.
2. **Quy y Bảo mật JWT rào cản 3 Lớp Đồng thời:** Tự code ổ khóa Token `@PreAuthorize` và phân chốt chặn Request của 3 vai trò biệt lập (`MANAGER`, `ANNOTATOR`, `REVIEWER`), gác cửa API tuyệt đối không lấn quyền nhau.
3. **Cụm Tính Năng Sinh Sát Của Quản Lý (MANAGER Module):**
   - Làm trọn gói luồng API tạo `Dataset`, quản lý thư viện `Label`, và vận hành Hợp đồng `Project`.
   - Tự tay viết thuật toán **Bulk Upload Data** có chức năng vòng lặp "Gạt Rác Mầm" phát hiện ảnh trùng lặp để tiết kiệm dung lượng Supabase.
4. **Cụm Tính Năng Gán Nhãn Đóng Kín (ANNOTATOR Module):**
   - Viết API phân chia hộp tin rác, che giấu ID để Coder A chỉ được kéo danh sách những Task mà duy nhất ổng được giao.
   - Luồng lưu trữ Dữ liệu (Annotation JSON string).
   - Thiết kế chốt chặn **Đúng Giờ Server (Dynamic Overdue)**: Backend âm thầm khóa API nộp bài (Submit) nếu phát hiện giờ phút hiện tại đã lố khoản Deadline của Task, đá vỡ lỗi Status 400.
5. **Cụm Tính Năng Bắt Lỗi Quyền Lực (REVIEWER Module):**
   - Xây dựng sơ đồ State Machine: Reviewer duyệt (`APPROVED`) thì chốt đơn, đánh trượt (`REJECTED`) thì Backend tự kích hoạt Exception bắt ép phải đính kèm Lời Phê (Feedback) thì mới cho trượt.
6. **Hệ Thống Phán Xét Ngầm (Auto-System & Thống Kê KPI):**
   - Vòng lặp Backend tự nhận diện 100% Task con hoàn thành thì tự động hất cờ đổi trạng thái Project cha sang Done.
   - Dùng sức mạnh Query COUNT dưới SQL để cày tính toán **Chỉ số đo lường KPI (% duyệt)** làm tròn tỷ suất thập phân chính xác cho Manager tính lương nhân viên.
7. **Lịch Trình Định Kỳ & Nhắc Nhở Đòi Nợ (Cron Job Schedulers):**
   - Viết các luồng chạy ngầm `@Scheduled` thức dậy mỗi 15 phút để lùng sục gạch tên các Task trễ hạn (OVERDUE deadline) và tự động sinh ra dữ liệu chuông Thông Báo (Notification) dí Deadline nhân viên.
8. **Hệ Thống Cân Bằng Tải & Quyền Từ Chối (WIP Limit & Refuse Task):**
   - Thiết kế hằng số `MAX_ACTIVE_TASKS = 3` chặn cứng không cho Manager nhồi nhét quá 3 Task cho 1 nhân viên.
   - Viết API `PATCH /api/tasks/{id}/refuse` cho Annotator/Reviewer từ chối nhận Task kèm lý do bắt buộc, tự động trả Task về rổ chờ + bắn Notification cho Manager.

---

## 5. HƯỚNG DẪN DEMO TÍNH NĂNG TRÊN SWAGGER UI KÈM TEST DATA
*(Mở trình duyệt: `http://localhost:8080/swagger-ui/index.html`)*

### Bước 1: Login lấy Token (Bắt buộc)
1. Kéo tới **`auth-controller`** -> Bấm vào thẻ xanh lá `POST /api/auth/login`.
2. Bấm nút **Try it out** ở góc phải.
3. Trong ô **Request body**, bạn Copy dán mã JSON sau vào:
   ```json
   {
     "email": "manager@gmail.com",
     "password": "Mật_khẩu_bạn_đã_tạo_trong_Postgres"
   }
   ```
4. Bấm **Execute**. Kéo xuống dưới phần *Server response (Code 200)*, bôi đen copy đoạn mã dài ngoằng trong chuỗi `"accessToken"`.
5. Cuộn lên trên cùng của trang web, bấm nút ổ khóa 🔒 **Authorize**, dán mã Token vào và bấm **Apply**. (Giờ bạn đã có quyền tể tướng).

### Bước 2: Demo tính năng Bulk Upload (Quản lý Data siêu mượt)
1. Kéo xuống **`data-item-controller`** -> Bấm thẻ `POST /api/data-items/bulk`.
2. Bấm **Try it out**.
3. Điền `datasetId` là ID của một Dataset bạn đã có sẵn. Trong ô **Request body**, dán JSON sau (Lưu ý: Em cố tình dán link ảnh 1 và ảnh 3 GIỐNG NHAU để khoe hệ thống gạt rác):
   ```json
   {
     "contentUrls": [
       "https://anh-xe-tang-1.jpg",
       "https://anh-may-bay.jpg",
       "https://anh-xe-tang-1.jpg"
     ]
   }
   ```
4. Bấm **Execute**. Kéo xuống xem *Response*. Báo cáo Thầy Cô: *"Cô xem code Response 200 nè, hệ thống báo upload thành công nhưng dưới Database mảng `contentUrls` chỉ lưu 2 link thôi, link thứ 3 trùng lặp bị gạt ra liền!"*.

### Bước 3: Thử tài Hacker - Demo phân quyền 3 Lớp chặn đứng Reviewer
1. Đăng xuất 🔒. Login lại vào tài khoản của 1 ông **Reviewer**, lấy được cái Token cùi bắp của ranh giới Reviewer đắp lên ổ khóa 🔒.
2. Cố tình lẻn vào **`project-controller`** -> Bấm thẻ `POST /api/projects` (Quyền cao nhất của Manager).
3. Bấm **Try it out** nhập bừa dự án -> **Execute**.
4. Toạch! Hệ thống trả về vạch đỏ **Code 403 (Forbidden)**.
5. Giải thích dõng dạc: *"Dạ em dùng Spring Security cắm thẻ `@PreAuthorize`, ông Reviewer cầm Token này mà mò vô API giao việc hay tạo Project là hệ thống tát gãy tay báo 403 liền ạ, không cho thao tác!"*

### Bước 4: Demo KPI đếm hình tự động
1. Kéo xuống **`task-controller`** -> Bấm thẻ `GET /api/tasks/kpi/{userId}`.
2. Bấm **Try it out**. Ô `userId` nhập thử 1 cục UUID của ông Annotator trong CSDL.
3. Bấm **Execute**.
4. Màn hình trả JSON cực kỳ rõ ràng gãy gọn:
   ```json
   {
     "userId": "123e4567-e89b-12d3...",
     "totalAssigned": 100,
     "approvedCount": 85,
     "rejectedCount": 10,
     "approvalRate": 85.0
   }
   ```
5. Chốt đơn báo cáo môn học mĩ mãn!

### Bước 5: Màn Ảo Thuật Bẫy Thời Gian (Khóa Mõm Task Trễ Hạn)
Màn này dùng để biểu diễn Validation Thời Gian Thực (Chặn lùi giờ Windows) cực kỳ ăn tiền.
1. Gắn Token của **Manager** 🔓. Kéo tới **`task-controller`** -> Bấm thẻ `POST /api/tasks/assign`.
2. Bấm **Try it out**. Ô JSON nhập vào, bạn Cố Tình gài bẫy lùi biến Hạn chót nộp bài (`"dueDate"`) về tận hôm qua, hoặc hẵng lùi hẳn một tháng trước (Ví dụ: `"dueDate": "2024-01-01T10:00:00"`). Xong Bấm **Execute**.
3. Cởi ổ khóa 🔓 đổi Token của ô **Annotator** (Nạn nhân bị gài bẫy lố Deadline).
4. Ở thẻ `PUT /{id}/submit`, nhét cái ID của Task đó vào. Cố tình bấm **Try it out** -> **Execute**.
5. Kéo xuống dưới xem bảng Đỏ loét **Code 400 (Bad Request)** hiện ra.
6. Bạn chém gió với Cô:
   - *"Dạ thưa Cô, thằng Nhân viên này đòi nộp bài lách luật lùi Deadline, nhưng `LocalDateTime.now()` của Java Server bên em không hề bị lú. Nó soi đúng Server-Time thấy lố ngày nên đá văng Error liền ạ!"*
   - *"Nó cố tình bấm nút Nộp thì sập bẫy bị chửi lỗi 400. Kể cả nó sợ trốn luôn không nộp, thì cứ đúng 15 phút, chốt chặn ngầm **Auto-Scheduler (Cron Job)** của Backend em cũng thức giấc lùi sâu rà soát Database, tự động lật Cờ `status` thành `OVERDUE` khóa vĩnh viễn ạ! Không có cái Task nào chừa được deadline!"*

### Bước 6: Demo Giới Hạn Khối Lượng Công Việc (WIP Limit = 3 Task)
Màn này chứng minh hệ thống chống nhồi nhét quá tải cho nhân viên.
1. Gắn Token **Manager** 🔓. Kéo tới **`task-controller`** -> Bấm thẻ `POST /api/tasks`.
2. Tạo lần lượt **3 Task** cho cùng 1 Annotator (mỗi lần bấm Execute 1 lần). Cả 3 lần đều trả **201 Created** mượt mà.
3. Bấm tạo Task **lần thứ 4** cho đúng Annotator đó -> **Execute**.
4. Kéo xuống xem thẻ Đỏ **400 Bad Request**: *"Annotator đã đạt giới hạn 3 task đang hoạt động. Vui lòng chờ hoàn thành task cũ."*
5. Chém gió: *"Dạ thưa Cô, hệ thống của em áp dụng nguyên lý WIP Limit (Work-In-Progress Limit) của phương pháp Kanban. Mỗi nhân viên chỉ được giữ tối đa 3 task cùng lúc để tránh tắc nghẽn dây chuyền sản xuất dữ liệu ạ!"*

### Bước 7: Demo Quyền Từ Chối Task (Refuse Task)
Màn này chứng minh nhân viên có quyền trả việc kèm lý do để Manager biết.
1. Đổi Token sang **Annotator** 🔓 (người đang giữ 1 Task ở trạng thái `IN_PROGRESS`).
2. Kéo tới **`task-controller`** -> Bấm thẻ `PATCH /{id}/refuse`.
3. Bấm **Try it out**. Nhập ID Task và JSON lý do:
   ```json
   {
     "reason": "Dữ liệu ảnh bị mờ không thể gán nhãn chính xác được, xin Manager kiểm tra lại"
   }
   ```
4. Bấm **Execute** -> Thẻ Xanh **200 OK**, Task chuyển về trạng thái `OPEN` (vô chủ).
5. Đổi Token sang **Manager** -> Mở **`notification-controller`** -> `GET /api/notifications` -> Thấy ngay dòng Notification: *"Annotator X đã từ chối task. Lý do: Dữ liệu ảnh bị mờ..."*
6. Chém gió: *"Dạ hệ thống của em cho phép nhân viên từ chối task kèm lý do bắt buộc (10-500 ký tự). Task tự động trả về rổ chờ và bắn Notification cho Manager để giao lại cho người phù hợp hơn ạ!"*

---
*Hy vọng tài liệu này giúp bạn làm gỏi Thầy/Cô phản biện! Hãy đọc kĩ từng mục nhé!*

# BỘ CÂU HỎI VẤN ĐÁP BẢO VỆ ĐỒ ÁN (GÓC NHÌN GIẢNG VIÊN BE +10 NĂM KINH NGHIỆM)

> **Lưu ý:** Giảng viên kỹ thuật (Backend) thường không hỏi lan man về giao diện mà sẽ "xoáy" cực mạnh vào 4 yếu tố: **Bảo mật (Security), Luồng dữ liệu (Data Flow), Hiệu suất (Performance) và Thiết kế Database (DB Design)**. Dưới đây là bộ câu hỏi phòng thủ dành cho bạn.

---

## 1. NHÓM CÂU HỎI VỀ BẢO MẬT & PHÂN QUYỀN (SECURITY)

**❓ Câu hỏi 1:** *"Thầy/Cô thấy hệ thống của em có 3 role: Manager, Annotator, Reviewer. Làm sao em đảm bảo được tk Annotator không thể vào nhảy quyền gọi API tạo Task của Manager hay gọi API Duyệt bài của Reviewer?"*
- **🔑 Trả lời:** "Dạ, hệ thống của em áp dụng Authorization dựa trên JWT (JSON Web Token) và Spring Security. Ở mỗi endpoint API, em đều cấu hình phân quyền rõ ràng trong `SecurityConfig` và bắt validator theo logic. Ví dụ, hàm `createTask` chỉ cho phép user có system role là MANAGER hoặc ADMIN truy cập. Ngoài ra, ngay trong Service (`TaskServiceImpl`), em còn check thêm nghiệp vụ: chỉ những ai có ID khớp với `annotatorId` hoặc `reviewerId` được phân công trong cái Task đó mới được phép thực thi hành động nộp bài hay duyệt bài của chính Task đó. Nhờ vậy chặn đứng việc leo thang đặc quyền ạ."

**❓ Câu hỏi 2:** *"Lúc demo thầy thấy em tạo token để đăng nhập. Token này ném lên JWT.io thì decode ra được luôn cả thông tin. Vậy lỡ hacker chép được token này thì sao? Em có lưu Mật khẩu hay thông tin nhạy cảm vào Payload của Token không?"*
- **🔑 Trả lời:** "Dạ không ạ. Bản chất của JWT là Base64 Encode nên ai cũng decode được Payload. Vì vậy em tuyệt đối **không** lưu mật khẩu hay dữ liệu nhạy cảm vào đó. Trong Token em chỉ lưu các Claims cơ sở gồm: `subject` (UserID/Username), `roles`, và `expiration_time` (thời hạn sống của token). Điểm mấu chốt cấu thành sự an toàn của JWT nằm ở phần **Signature** (Chữ ký), phần này được băm bằng thuật toán HS256 kết hợp cùng một thanh `SECRET_KEY` rất dài (chứa trong file `application.yml` ở server). Hacker dù có sửa đổi được Payload nhưng không có `SECRET_KEY` thì chữ ký sẽ bị sai, Spring Security của em sẽ báo lỗi Unauthorized (401) ngay lập tức ạ."

**❓ Câu hỏi 3:** *"Tại sao đăng nhập gõ '123456' mà dưới DB lại thành một chuỗi loằng ngoằng? Bằng chứng nào chứng minh em đang dùng Hash một chiều, nhỡ em tự code ra 1 cái hàm mã hóa cùi bắp thì sao?"*
- **🔑 Trả lời:** "Dạ em không tự chế thuật toán ạ. Em sử dụng thư viện `BCryptPasswordEncoder` - tiêu chuẩn vàng của hệ sinh thái thế giới Spring Security hiện nay. Đặc điểm của Bcrypt (hàm băm một chiều) là nó sẽ tự động chèn thêm một chuỗi "muối" (salt) ngẫu nhiên vào mật khẩu trước khi băm. Nên dù 2 người cùng đặt pass là '123456' thì 2 chuỗi Hash dưới database vẫn hoàn toàn khác nhau. Em có cấu hình Bean Bcrypt trong file `SecurityConfig.java` và gọi lệnh `passwordEncoder.encode()` ở file `UserServiceImpl` ạ." *(Mở đúng 2 file đó ra chỉ)*

---

## 2. NHÓM CÂU HỎI VỀ LOGIC NGHIỆP VỤ & LUỒNG (BUSINESS & FLOW)

**❓ Câu hỏi 4:** *"Thầy thấy luồng của em là Annotator gán nhãn xong thì nộp bài (Submit) lên cho Reviewer đổi Task thành trạng thái SUBMITTED. Nhỡ Annotator làm chưa xong tất cả các ảnh mà vẫn cố tình bấm nút nộp bài (Submit) bằng API Postman thì hệ thống em xử lý sao? Code bắt lỗi chỗ đó ở đâu?"*
- **🔑 Trả lời:** "Dạ em đã handle case này rất kỹ dưới tầng Backend chứ không phụ thuộc vào UI ạ. Trong method `submitForReview` của `TaskServiceImpl`, trước khi đổi trạng thái, em có gọi 1 query đếm: `taskItemRepository.countItemsWithoutAnnotation(taskId)`. Nếu kết quả Count > 0 (bất kỳ ảnh nào chưa gán nhãn), em sẽ throw ra lỗi `BadRequestException("Tất cả data items phải được gán nhãn trước khi nộp")`. Thầy có thể xem code method đó ở class `TaskServiceImpl` dòng 252 ạ."

**❓ Câu hỏi 5:** *"Cái tính năng Reviewer duyệt bài. Khi duyệt em gọi 1 API PATCH `complete-review`. Trong DB một Task có 10 ngàn cái ảnh, hệ thống của em duyệt kiểu gì để biết cái nào đậu cái nào rớt, rồi đổi trạng thái Task tương ứng?"*
- **🔑 Trả lời:** "Dạ, em không dùng vòng lặp FOR lặp qua 10.000 dòng trong server vì như vậy sẽ tràn RAM (Memory Leak) hoặc treo máy (OOM). Thay vào đó, ở hàm `completeReview`, em dùng các truy vấn SQL đếm thẳng dưới tầng Database (qua `AnnotationRepository`) như sau:
1. Đầu tiên em đếm `countRejectedByTaskId`, chỉ cần đếm thấy > 0 (nghĩa là có ít nhất 1 ảnh sai), em lập tức đổi status Task về lại `IN_PROGRESS` để trả về cho Annotator sửa. Vừa nhanh vừa nhẹ.
2. Nếu số Reject = 0, em đếm tiếp `countSubmittedByTaskId`. Chỉ cần thấy > 0 (tức là Reviewer trộm vía chưa duyệt hết mà dám bấm nút hoàn tất), em throw lỗi `BadRequestException`.
3. Chỉ khi 2 điều kiện kia thỏa mãn, tất cả nhãn đều được `APPROVED`, em mới chuyển status Task sang `REVIEWED` ạ."

---

## 3. NHÓM CÂU HỎI VỀ THIẾT KẾ CƠ SỞ DỮ LIỆU (DATABASE DESIGN)

**❓ Câu hỏi 6:** *"Tại sao quan hệ giữa người (Users) và bài tập gán nhãn (Tasks) lại cần sinh ra 1 bảng trung gian (hoặc lưu ID trực tiếp vào bảng Task)? Nếu 1 Task có 1.000 ảnh (Data Item), thiết kế bảng thế nào để DB không dội ngược?"*
- **🔑 Trả lời:** "Dạ trong sơ đồ DB của tụi em:
- Bảng `tasks` chỉ đóng vai trò là "Cái Vỏ" chứa metadata tổng như `projectId`, `annotator_id`, `reviewer_id` và `status`.
- Để liên kết 1.000 tấm ảnh vào Task này, em sinh ra bảng trung gian là `task_items`. Mỗi `task_item` sẽ tham chiếu 1 dòng `taskId` và 1 dòng `dataItemId`. Nhờ vậy:
  1. Hạn chế dư thừa dữ liệu (DataItem chỉ lưu content URL 1 lần duy nhất trong DB).
  2. Bảng `task_items` hỗ trợ indexing khóa ngoại rất tốt, khi Query danh sách việc làm theo Task sẽ cực kỳ nhanh: `SELECT * FROM task_items WHERE task_id = ?`."

---

## 4. CÂU HỎI "BẪY" XỬ LÝ SỰ CỐ (TROUBLESHOOTING)

**❓ Câu hỏi 7:** *"Giả sử bây giờ demo xong, mang cái backend Spring Boot của tụi em bỏ lên hệ thống thật, 100 người Annotator cùng lúc bấm nút 'Submit' cùng 1 Task thì dự án của em xử lý thế nào? Có bị lỗi xung đột dữ liệu không?"*
- **🔑 Trả lời:** *(Câu này phân chia Sinh viên Khá và Sinh viên Giỏi)* 
"Dạ nếu chỉ 1 Task mà có nhiều người được gán cùng lúc làm (trên thực tế là hệ thống em hiện tại 1 Task chỉ gán đúng 1 ID Annotator), nhưng giả sử nếu có case đó xảy ra dẫn tới xung đột Trạng Thái (Concurrency), chúng em sẽ nghĩ tới cơ chế **Optimistic Locking (Khóa lạc quan)** của Hibernate/JPA. Bọn em sẽ thiết kế một cột `@Version` trong bảng DB. Nếu 2 người cùng submit, người thứ 2 lên sẽ bị bắn văng ra lỗi `OptimisticLockException` và hệ thống sẽ bắt lỗi đó để văng thông báo 'Dữ liệu đã bị người khác thao tác'."

---
*Chúc bạn 10 điểm trong vòng chất vấn! Hãy đọc kỹ và hiểu bản chất, khi trả lời hãy giao tiếp bằng mắt (Eye Contact) để Giảng viên thấy sự tự tin của bạn nhé!*

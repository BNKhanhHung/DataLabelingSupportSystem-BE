# Project Status: Data Labeling Support System - Backend

Tài liệu này theo dõi tiến độ thực hiện các tính năng ở Backend.

## 1. Đã hoàn thành (Implemented) ✅
Đây là các tính năng cốt lõi đã được xây dựng và có API cơ bản:

- **Xác thực & Phân quyền (Auth & Security):**
  - [x] Đăng ký / Đăng nhập.
  - [x] Phân quyền dựa trên Roles (Bảng `user_roles`).
- **Quản lý tài nguyên (Resources):**
  - [x] Quản lý thông tin Người dùng (User).
  - [x] Quản lý Dự án (Project).
  - [x] Định nghĩa Nhãn (Label) cho dự án.
  - [x] Quản lý Tập dữ liệu (Dataset) và Dữ liệu chi tiết (DataItem).
- **Luồng phân công công việc:**
  - [x] Tạo Task cho dự án.
  - [x] Phân công 1 Annotator và 1 Reviewer vào cùng một Task.
- **Thực thi Gán nhãn & Đánh giá:**
  - [x] Annotator lưu kết quả gán nhãn (Annotation).
  - [x] Reviewer đánh giá nhãn (ReviewFeedback: Approve/Reject).

## 2. Việc cần làm (To-Do) 🚧
Các tính năng chưa được phát triển hoặc cần bổ sung theo thiết kế nghiệp vụ hiện tại:

- **Hạ tầng cơ sở:**
  - [ ] **Upload File**: Xây dựng `FileStorageService` dùng (Amazon S3, Supabase Storage, hoặc Local) để nhận và lưu file ảnh (cho đối tượng DataItem).
  - [ ] **Phân trang (Pagination)**: Thêm cơ chế `Pageable` vào toàn bộ các API lấy danh sách (`GET /...`) để không bị quá tải khi dữ liệu lớn.
- **Nghiệp vụ nâng cao:**
  - [ ] **Quản lý Deadline**: Thêm thuộc tính `deadline`, `status` vào `Task` Entity. Cập nhật API để tính toán và cảnh báo trễ hạn.
  - [ ] **Đánh giá Năng lực**: Thêm bảng/logic theo dõi hiệu suất của Annotator (tốc độ, tỷ lệ lỗi) làm căn cứ gợi ý nhân sự.
  - [ ] **Tích hợp AI**: Bổ sung các cấu trúc dữ liệu lưu (`ai_predicted_label`, `confidence_score`) vào `DataItem`/`Annotation` để hỗ trợ hiển thị gợi ý cho Annotator và Reviewer.
- **Tối ưu hóa (Optimization):**
  - [ ] Audit Logging (Lưu lịch sử thay đổi quan trọng: ai sửa, sửa lúc nào).
  - [ ] Swagger API Documentation (Có thể đã có thư viện nhưng rà soát lại để mô tả chi tiết từng Endpoint).

---
*Cập nhật lần cuối: Xem theo lịch sử Git hoặc thời điểm file này được sửa đổi.*

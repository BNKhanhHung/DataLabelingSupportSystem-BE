# Data Labeling Support System - Business Context & Requirements

Dưới đây là tài liệu tổng hợp ngữ cảnh nghiệp vụ (Business Context) và thiết kế cơ sở dữ liệu (ERD/Schema) dựa trên các cuộc trao đổi trước đó của chúng ta. Mục đích của tài liệu này là để rà soát lại xem có thiếu sót thông tin nào trước khi tiếp tục phát triển không.

## 1. Tổng quan hệ thống (System Overview)
Hệ thống là một nền tảng hỗ trợ gán nhãn dữ liệu (Data Labeling Support System), trong đó quản lý người dùng, dự án gán nhãn, và các tác vụ gán nhãn. Hệ thống có sự phân loại rõ ràng về vai trò của người dùng trong việc thực hiện và đánh giá chất lượng dữ liệu.

## 2. Quản lý Người dùng & Phân quyền (Users & Roles)
- **Users**: Quản lý thông tin tài khoản người dùng đăng nhập hệ thống.
- **Roles**: Các vai trò trong hệ thống (như Admin, Manager, Annotator, Reviewer, v.v.).
- **User_Roles**: 
  - Thay vì quản lý quyền truy cập qua nhiều bảng phức tạp, hệ thống đã chuẩn hóa và gộp quản lý phân quyền vào một bảng `user_roles` duy nhất.
  - Bảng dư thừa trước đây như `project_members` đã được **loại bỏ** để tối ưu hóa thiết kế lược đồ quản lý thành viên và quyền trong dự án, giúp giản lược ERD và dễ bảo trì.

## 3. Dự án & Luồng Công việc (Projects & Tasks)
- **Projects**: Quản lý các dự án gán nhãn. Mỗi dự án bao gồm nhiều tác vụ (Tasks) cần hoàn thành.
- **Tasks**: Đơn vị công việc nhỏ nhất trong dự án.
  - **Phân công công việc**: Bảng `tasks` đã được thiết kế lại để hỗ trợ trực tiếp việc phân công tác vụ cho cả 2 đối tượng:
    1. **Annotator** (Người gán nhãn): Người trực tiếp thực hiện công việc gán nhãn dữ liệu.
    2. **Reviewer** (Người đánh giá): Người kiểm tra và duyệt lại kết quả gán nhãn của Annotator.
  - Sự thay đổi này giúp quy trình làm việc trở nên rõ ràng hơn: từ bước gán nhãn đến bước đánh giá đều được tracking rõ ràng trên cùng một dòng dữ liệu tác vụ (task).

## 4. Các luồng quy trình chính (Business Flows)
- **Luồng phân công**: Admin/Manager tạo Project -> Tạo Task -> Gán Assignee (Annotator) và Reviewer cho Task đó.
- **Luồng gán nhãn**: Annotator nhận Task -> Thực hiện gán nhãn (Annotations) -> Submit kết quả.
- **Luồng đánh giá**: Reviewer nhận Task đã hoàn thành gán nhãn -> Xem xét kết quả -> Approve (Chấp thuận) hoặc Reject/Feedback (Từ chối/Yêu cầu sửa lại).

## 5. Đánh giá chất lượng và Quản lý tiến độ (Performance & Progress Tracking)
- **Quản lý Deadline**: Các tác vụ (Tasks) hoặc dự án (Projects) cần có thời gian bắt đầu và kết thúc. Hệ thống cần theo dõi trạng thái tiến độ thời gian (Ví dụ: đúng hạn, trễ deadline).
- **Đánh giá năng lực Annotator/Reviewer**: Dựa trên kết quả thực hiện công việc (tỷ lệ lỗi bị Reviewer từ chối, số task trễ deadline, tốc độ hoàn thành...), Manager có thể đánh giá năng lực của Annotator. Thông tin này giúp Manager ra quyết định phân công tác vụ tối ưu cho các dự án sau.

---

## 6. Tích hợp Trí tuệ Nhân tạo (AI Integration)
Hệ thống tích hợp công nghệ AI nhằm tối ưu hóa năng suất và chất lượng gán nhãn dữ liệu:
- **Hỗ trợ Annotator**: AI có thể gợi ý nhãn tự động (Pre-labeling / Auto-labeling) cho các luồng dữ liệu, giúp Annotator giảm bớt thao tác thủ công và đẩy nhanh tốc độ xử lý.
- **Hỗ trợ Reviewer**: Cung cấp đánh giá độ tự tin của mô hình AI (Confidence score) trên các nhãn đã gán. Reviewer có thể tập trung kiểm tra rà soát các nhãn có độ tin cậy thấp hoặc có khả năng sai sót cao (AI-assisted verification), giúp đẩy nhanh quá trình duyệt.

---

> **Lưu ý**: Hãy kiểm tra xem bản cập nhật này đã sát với yêu cầu thực tế của dự án chưa. Các module này tuy có lúc đóng vai trò hỗ trợ phụ trợ, nhưng nếu quy trình chuẩn đòi hỏi thì vẫn cần phải ghi nhận rõ thành nghiệp vụ chính để thiết kế Entity tương ứng. Vui lòng cho tôi biết nếu cần điều chỉnh thêm!

# KỊCH BẢN BÁO CÁO: DEMO HỆ THỐNG GÁN NHÃN DỮ LIỆU

> **Ghi chú cho người thuyết trình:** 
> - File này kết hợp giữa lời nói (🗣️) và hành động (🖱️) trên màn hình Swagger UI.
> - Hãy chuẩn bị trước ra Notepad sẵn tất cả các ID như `PROJECT_ID`, `ANNOTATOR_ID`, `REVIEWER_ID`, `DATA_ITEM_ID`... trước khi lên bục, để lúc gọi API trên Swagger cứ copy paste cho nhanh.

---

### MỞ ĐẦU: CÀI ĐẶT BỐI CẢNH
**🗣️ Lời nói:** 
"Dạ em chào thầy/cô và các bạn. Hôm nay em xin phép demo hệ thống hỗ trợ gán nhãn dữ liệu của nhóm em. Hệ thống này giải quyết bài toán quy trình gán nhãn thực tế gồm 3 vai trò chính: Manager (Quản lý), Annotator (Người gán nhãn) và Reviewer (Người kiểm duyệt)."

"Mô hình hoạt động là: Manager sẽ tạo dự án và giao việc. Annotator làm xong sẽ nộp bài. Reviewer duyệt, nếu sai thì trả lại, nếu đúng thì hoàn tất để Manager nghiệm thu cuối cùng."

**🖱️ Hành động:** *(Chuyển màn hình qua thẻ trình duyệt đang mở Swagger UI, cuộn lướt nhẹ các danh mục API)*
**🗣️ Lời nói:** 
"Để tiết kiệm thời gian, hệ thống của em khi khởi chạy đã tự động khởi tạo sẵn dữ liệu mẫu gồm 1 dự án phân loại thú cưng, data mẫu và 4 tài khoản ứng với các vai trò."

---

### BƯỚC 1: MANAGER TẠO TASK (GIAO VIỆC)

**🗣️ Lời nói:** 
"Đầu tiên, em sẽ đóng vai Manager để vào giao việc cho nhân viên."
**🖱️ Hành động:** 
- Kéo đến `POST /api/auth/login`, điền `manager` / `managerpassword`, bấm Execute. 
- Copy Token, cuộn lên bấm nút ổ khóa Authorize, dán `Bearer <token>` vào.

**🗣️ Lời nói:** 
"Manager đã đăng nhập thành công. Để giao việc, Manager sẽ dùng API tạo Task."
**🖱️ Hành động:** 
- Kéo xuống API `POST /api/tasks`, dán JSON đã chuẩn bị sẵn (có chứa Project ID, Annotator ID, Reviewer ID, DataItem IDs), bấm Execute.

**🗣️ Lời nói:** 
"Như mọi người thấy ở kết quả trả về mã 201 Created. Một Task mới đã được tạo với trạng thái ban đầu là `OPEN`. Task này được giao đích danh cho tài khoản Annotator1 và Reviewer1, yêu cầu gán nhãn cho 2 bức ảnh."

---

### BƯỚC 2: ANNOTATOR NHẬN VIỆC VÀ THAO TÁC

**🗣️ Lời nói:**
"Bây giờ, Manager đã giao việc xong, em xin phép đổi sang góc nhìn của người nhân viên gán nhãn là Annotator1."
**🖱️ Hành động:** 
- Lên nút Authorize bấm Logout. Xuống `POST /api/auth/login`, điền `annotator1` / `123456`, lấy token mới rồi Authorize lại.

**🗣️ Lời nói:**
"Annotator sẽ lấy danh sách các ảnh mình cần làm trong Task này."
**🖱️ Hành động:** 
- Mở API `GET /api/tasks/{id}/items`, dán Task ID vào, Execute.

**🗣️ Lời nói:**
"Đây là 2 tấm ảnh Annotator cần làm. Em sẽ tiến hành gán nhãn cho ảnh đầu tiên là 'Chó'."
**🖱️ Hành động:** 
- Mở API `POST /api/annotations`, nhập ID ảnh 1, ghi content là "Chó", Execute.

**🗣️ Lời nói:**
"Task của chúng ta lúc này, do đã có action gán nhãn đầu tiên, hệ thống đã thông minh tự động chuyển trạng thái Task từ `OPEN` sang `IN_PROGRESS` (Đang thực hiện)."
*(Có thể mở nhanh API `GET /api/tasks/{id}` nhập Task ID rồi execute để thầy cô xem dòng chữ `IN_PROGRESS`)*

"Em sẽ gán nốt nhãn cho ảnh thứ 2 là 'Mèo'."
**🖱️ Hành động:** 
- Gọi API gán nhãn (`POST /api/annotations`) lần nữa cho ảnh 2.

---

### BƯỚC 3: ANNOTATOR NỘP BÀI (SUBMIT)

**🗣️ Lời nói:**
"Annotator đã làm xong toàn bộ ảnh trong Task. Bây giờ, họ cầm bấm nộp bài để chuyển vòng đời Task sang khâu kiểm duyệt. Nếu chưa làm xong mà bấm nộp, hệ thống sẽ báo lỗi chặn ngay."
**🖱️ Hành động:** 
- Mở API `PATCH /api/tasks/{id}/submit`, dán Task ID vào, Execute.

**🗣️ Lời nói:**
"Như kết quả trả về, Task đã chuyển sang trạng thái `SUBMITTED`. Nghĩa là bài đã được nộp lên chờ duyệt, Annotator kết thúc nhiệm vụ ở vòng này."

---

### BƯỚC 4: REVIEWER KIỂM DUYỆT CHẤT LƯỢNG

**🗣️ Lời nói:**
"Tiếp theo là khâu kiểm soát chất lượng, em lại đổi sang vai trò Reviewer."
**🖱️ Hành động:** 
- Logout, Login lại bằng `reviewer1` / `123456`, Authorize token mới.

**🗣️ Lời nói:**
"Reviewer sẽ xem thử các nhãn Annotator vừa gán. Giả sử nhãn đầu tiên Annotator làm đúng, Reviewer sẽ Approve (Chấp thuận)."
**🖱️ Hành động:** 
- Mở API `POST /api/review-feedbacks`, nhập ID annotation 1, chọn status `APPROVED`, ghi chú "Làm tốt", Execute.

**🗣️ Lời nói:**
"Nhưng ở ảnh số 2, Annotator làm sai. Reviewer sẽ Reject (Từ chối) và bắt buộc hệ thống yêu cầu phải ghi lý do."
**🖱️ Hành động:** 
- Đổi sang ID annotation 2, status `REJECTED`, ghi chú "Sai rồi, đây là con chó chứ không phải mèo", Execute.

**🗣️ Lời nói:**
"Reviewer đã duyệt xong toàn bộ, giờ họ sẽ bấm nút lệnh Hoàn tất Review."
**🖱️ Hành động:** 
- Mở API `PATCH /api/tasks/{id}/complete-review`, nhập Task ID, Execute.

**🗣️ Lời nói:**
"Do có 1 ảnh bị đánh rớt (Reject), nên thay vì được nghiệm thu, hệ thống rất chặt chẽ đánh tụt trạng thái Task quay ngược lại thành `IN_PROGRESS`. Mục đích là để trả task về lại cho Annotator sửa."

---

### BƯỚC 5: MÔ TẢ NHANH RẼ NHÁNH SỬA LỖI (VÀ KẾT THÚC)

**🗣️ Lời nói:**
"Ở thực tế, Annotator lúc này sẽ thấy task bị trả lại kèm feedback. Họ sẽ vào sửa lại nhãn bị sai cho đúng, sau đó nộp lại (Submit) lần 2. Y như quy trình vừa nãy, Reviewer sẽ vào duyệt thành Approve."

"Giả định là Annotator đã sửa xong và Reviewer duyệt Approve thành công toàn bộ rồi. Khi Reviewer bấm Hoàn tất Review lần cuối, Task cuối cùng sẽ được chuyển sang trạng thái `REVIEWED`."

"Khi Task ở trạng thái `REVIEWED` nghĩa là khâu gán nhãn đã qua kiểm định đạt chất lượng. Manager (Quản lý dự án) chỉ việc vào chốt lại lần cuối và cập nhật Task thành `COMPLETED`."

"Như vậy quy trình của một Task gán nhãn khép kín và phân quyền chặt chẽ đã hoàn tất! Em xin kết thúc phần demo luồng nghiệp vụ chính ạ."

---

### BƯỚC 6: XEM LẠI NHẬT KÝ HOẠT ĐỘNG (BẰNG CHỨNG BẤT KHẢ XÂM PHẠM)

**🗣️ Lời nói:**
"Và để đảm bảo tính minh bạch tuyệt đối, tránh cãi vã giữa nhân viên và quản lý về việc ai đã làm gì vào lúc nào, hệ thống của em có trang bị chức năng **Activity Log (Nhật ký hoạt động)**."

**🖱️ Hành động:** 
- Đăng nhập lại tài khoản `manager` / `managerpassword`, copy Token, Authorize.
- Kéo xuống API `GET /api/activity-logs`, bấm Execute.

**🗣️ Lời nói:**
"Thầy cô có thể thấy, mọi thao tác từ lúc em tạo Task, Annotator nộp bài, Reviewer duyệt bài... nãy giờ đều được API này ghi lại đầy đủ đến từng giây. Bảng nhật ký này được thiết kế theo dạng **Bất biến (Immutable)** - tức là chỉ có API để Đọc (GET), hoàn toàn không có API để Sửa (PUT/PATCH) hay Xóa (DELETE). Kể cả em là Admin cũng không thể vào đây xóa dấu vết tội ác của mình được ạ."

"Đến đây em xin chính thức khép lại phần demo của nhóm. Cảm ơn quý thầy cô đã lắng nghe ạ!"

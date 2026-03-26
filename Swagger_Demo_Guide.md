# 🎯 KỊCH BẢN DEMO SWAGGER BẢO VỆ ĐỒ ÁN (SỐNG CÒN)
*(Tài liệu này sinh ra để bạn vừa mở Swagger vừa thao tác tay cho Giáo viên xem. 100% chạy mượt!)*

**🔗 Link mở Swagger:** `http://localhost:8080/swagger-ui/index.html`

---

## 🛠️ PHẦN 1: BƯỚC ĐỆM BẮT BUỘC (Lấy Token)
Trước khi làm trò gì cũng phải lấy chìa khóa vào nhà. Bạn cần chuẩn bị sẵn 3 tài khoản trong Database để luân phiên đăng nhập.
1. Mở thẻ `auth-controller` -> API `POST /api/auth/login`.
2. Bấm **Try it out**.
3. Đầu tiên, đăng nhập tài khoản **Manager**:
   ```json
   { "email": "manager@system.com", "password": "123" }
   ```
   *(Tương tự, khi cần diễn kịch bản của nhân viên thì bạn nhập email của **Annotator** hoặc **Reviewer** đã có sẵn trong máy bạn: `annotator@gmail.com` / `rev1@system.com`).*
4. Bấm **Execute**. Kéo xuống kết quả lấy cái chuỗi chữ dài ngoằn trong biến `"token"`.
5. Cuộn lướt lên chóp cùng trang web -> Bấm nút ổ khóa xanh sọc ngang `Authorize` -> Dán cái token nãy vô dòng Value theo cú pháp: `Bearer [khoảng trắng] [chuỗi_token]` -> Bấm **Authorize**.
*(Lát nữa Demo luồng của ai thì bạn NHỚ phải quay lại bước này lấy Token của người đó cắm vào ổ khóa nghen).*

---

## 🚀 PHẦN 2: CÁC CHIÊU THỨC DEMO (CHỌN LỌC ĐỂ BIỂU DIỄN)
*(Không cần làm hết, Cô gõ tới đâu móc chiêu tới đó)*

### CHIÊU 1: BIỂU DIỄN BẢO MẬT KHÓA TRÁI CỬA (Role-based JWT)
*Giáo viên muốn xem hệ thống của bạn có bị lỗi Nhân viên vào trộm kho dữ liệu của Giám đốc hay không?*

1. **Chuẩn bị:** Gắn Token của tài khoản **Annotator** vào Ổ khóa 🔓.
2. **Thao tác:** Kéo xuống mục `dataset-controller`, đè cái API `GET /api/datasets` (API này vốn chỉ dành cho Manager).
3. Bấm **Try it out** -> **Execute**.
4. **Kết quả:** Kéo xuống chỉ ngay vào cái bảng lỗi to tướng **Mã 403 Forbidden**.
5. **Chém gió:** *"Dạ thưa Cô, thằng gán nhãn này đang mưu đồ vượt cấp vào xem kho Data của Giám đốc. Bộ lọc `@PreAuthorize` ở Backend của em đã đạp văng nó ra bằng mã 403 ngay từ vòng gửi xe, không cho nó chạm tới CSDL ạ!"*

---

### CHIÊU 2: DEMO TÍNH NĂNG CHỐNG OVERLOAD (WIP Limit - Giới hạn 3 Task)
*Giáo viên muốn xem thuật toán cân bằng tải, không cho ai ôm quá nhiều việc.*

1. **Chuẩn bị:** Gắn Token của **Manager**. Bạn cần Copy ID (`uuid`) của 1 ông Annotator và 1 ông Reviewer nháp để sẵn.
2. **Thao tác:** Kéo tới `task-controller` -> `POST /api/tasks`.
3. Bấm **Try it out**. Nhập JSON để giao Task cho cùng 1 ông Annotator đó:
   ```json
   {
     "projectId": "ID_dự_án_nào_đó",
     "datasetItemId": "ID_hình_ảnh_nào_đó",
     "annotatorId": "ID_của_ông_Annotator",
     "reviewerId": "ID_của_ông_Reviewer",
     "dueDate": "2025-12-31T00:00:00"
   }
   ```
4. Cứ bấm nút **Execute** -> Tạo thành công (**201 Created**) -> Lại bấm tiếp Execute... Hãy bấm 3 lần để thảy cho nó 3 cục Task.
5. **Cú nॉक ao (Bấm Execute lần thứ 4):** Lần này thẻ lỗi đỏ lè hiện ra **Code 400 Bad Request**. Kèm theo tin nhắn: *"Annotator A đã đạt giới hạn 3 task đang hoạt động. Vui lòng chờ hoàn thành task cũ."*
6. **Chém gió:** *"Dạ Sếp lỡ trớn giao thêm Task thứ 4, nhưng BE của em chặn mẹ nó luôn bằng thuật toán đếm Query Count. Điều này y chang chuẩn Agile/Kanban ngoài doanh nghiệp để chống tắc nghẽn dây chuyền ạ!"*

---

### CHIÊU 3: DEMO LUỒNG TỪ CHỐI TASK (Refuse Task & Race Condition)
*Giáo viên muốn xem Luồng thao tác của Nhân viên*

1. **Chuẩn bị:** Lấy 1 cái ID của cái Task đang nằm ở chữ `IN_PROGRESS` (Đang làm). Đổi Token sang cắn thẻ của ông **Annotator** đang bị giao cái Task đó.
2. **Thao tác 1 (Từ chối):** Kéo vào `PATCH /api/tasks/{id}/refuse` -> Nhập ID Task, Body json gõ lý do:
   ```json
   { "reason": "Dạ em bị gãy tay không cào phím được chị sếp ơi, giao người khác giúp nhóa!" }
   ```
3. Bấm **Execute**. Mã trả về **200 OK**. Task đã tuột status thành mốc `OPEN`.
4. **Thao tác 2 (Kiểm tra chuông):** Lập tức cởi khóa, cắn Token của bà **Manager** vào. Mở mục `notification-controller` -> `GET /api/notifications` -> Execute.
5. Sẽ văng ra tờ sớ chửi thề: *"Thông báo: Thằng Annotator XYZ đã từ chối nhận việc. Lý do: Dạ em gãy tay..."*
6. **Thao tác 3 (Sếp ra tay gỡ rối re-assign):** Bà Manager lướt Swagger tới `PATCH /api/tasks/{id}/assign` -> Tiến hành đổi mã `annotatorId` ném cho tằng nhân viên khác gánh. Chuyện trót lọt!
7. **Chém gió:** *"Dạ đây là quy trình phối hợp khép kín. Người làm chê bài, kêu Sếp, Sếp nghe chuông nhận thông điệp, Sếp tự đẩy qua cho đứa khác làm. Đảm bảo dòng chảy Dữ liệu không bao giờ bị đứt gãy ạ!"*

---

### CHIÊU 4: DEMO CHẶN KIẾM ĂN BẨN (Validation: Phải 100% vẽ hộp mới được nộp)
*Giáo viên muốn xem BE có check dữ liệu rác không hay vứt bừa cũng nộp được.*

1. **Chuẩn bị:** Đang khóa Token của ông **Annotator**. Chọn 1 Task ổng đang `IN_PROGRESS`. Đảm bảo cái ảnh của Task đó ổng *chưa hề vẽ 1 cái Annotation hộp Nhãn* nào xuống DB cả (giấy trắng).
2. **Thao tác:** Mở `PATCH /api/tasks/{id}/submit` -> Gõ ID của task đó vào. Dõng dạc nói với Cô "Em đố nó nộp được giấy trắng nè!" -> Bấm **Execute**.
3. **Kết quả:** Văng lỗi **400 Bad Request**: *"Cannot submit: 1 item(s) have not been annotated yet."*
4. **Chém gió:** *"Dạ nếu không gài Validation, thằng nhân viên lười biếng cứ ấn Submit đại kéo theo kết quả Rác chui vào AI chết ngắt. Backend em đã khóa mõm, ép nó phải gọi thằng `POST /api/annotations` vẽ đủ xong xuôi, BE em đếm số lượng khớp nhau thì mới mở cổng cho nộp bài ạ!"*

---

### CHIÊU 5: DEMO KPI SÁT PHẠT ĐÒI NỢ (Total Elements InProgress & Warnings)
*Giáo viên hỏi: Sếp quản lý làm sao biết nhân sự nhà mình ai đang ngồi rảnh hay bị quá tải ngập mặt hả em?*

1. **Chuẩn bị:** Cắm Token **Manager**.
2. **Thao tác 1 (Xem coi ai đang ngâm việc):** Mở API `GET /api/tasks/annotator/{annotatorId}/in-progress` (Truyền ID của thằng khứa Annotator nào đó). Bấm **Execute**.
3. Kéo xuống dưới gầm file JSON -> Dòm ngay biến màu đỏ `"totalElements"`. Nếu nó bằng 3, nghĩa là thằng này đang còng lưng gánh xệ vai. Nếu bằng 0 thì nó đang ngồi chơi lướt Tóp tóp.
4. **Thao tác 2 (Xem tội đồ chây ì):** Mở API `GET /api/tasks/kpi/annotator/{annotatorId}` -> Bấm **Execute**.
5. Nhìn cột biến `"warnings": 2`: Chứng tỏ thằng này rất lôm côm, đã 2 lần chây ì làm lố Deadline (bị cái cronjob 15 phút rà tới gõ mõ). 
   Và nhìn biến `"approvalRate": 45.67`: Tức là làm ăn cẩu thả bị đánh rớt quá trời.
6. **Chém gió:** *"Bộ API KPI này của em là ngụm nước cất để Frontend móc Data vẽ Chart Biểu diễn màn hình Dashboard của Sếp đó Cô. Dòm đây cái bắt điểm yếu nhân sự ngay khỏi trốn!"*

---

### CHIÊU 6: DEMO TÍNH NĂNG ANTI-SUICIDE (Admin không thể tự tay bóp dái)
*Giáo viên muốn xem phân quyền của bạn có kẽ hở không. Lỡ Admin tự xóa Acc của mình thì sao?*

1. **Chuẩn bị:** Cắm Token **Manager**. Bạn cần lấy chính cái UUID (ID ID) của acc Manager này. Mở API `GET /api/users/me` -> Bấm Execute -> Copy dòng `"id": "..."` của Manager ra notepad.
2. **Thao tác:** Kéo tới thư mục `user-controller` -> Mở API `DELETE /api/users/{id}` (API xóa User).
3. Dán ngay cái ID của chính bạn Manager vừa copy vào ô `id`. Dõng dạc nói: "Em xin phép Delete bản thân hệ thống xem có cho phép không ạ". Bấm **Execute**.
4. **Kết quả:** Văng ngay lập tức thông báo đỏ chót **400 Bad Request** với dòng tin nhắn: *"You cannot delete your own account."*
5. **Chém gió:** *"Dạ thưa Cô, chặn 1 Manager xóa lính thì bình thường, nhưng việc Manager xỉn rượu xóa nhầm tài khoản của chính mình là Lỗ Hổng Chết Người. Backend của em đã cấy chốt chặn ở tầng sâu nhất (Service): Lấy Token soi xem Mày đang là ai, và đọ với cái ID Mày đang định chém. Trùng nhau là em tát văng ra liền bảo vệ nguyên vẹn cấu trúc nhân sự hệ thống ạ!"*

---
*(In hoặc mở màn hình ngầm file này ra, đọc thuộc lòng trình tự này, trỏ chuột vèo vèo như Hacker thì thầy cô auto 10 điểm tuyệt đối nha bạn tôi ơi!)*

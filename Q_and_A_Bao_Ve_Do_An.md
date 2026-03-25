# 🎯 BỘ CÂU HỎI "SÁT THỦ" BẢO VỆ ĐỒ ÁN (KÈM VĂN MẪU TRẢ LỜI LẤY ĐIỂM A+)
*(Tài liệu tuyệt mật dành riêng cho BE Developer để tự vệ trước mọi đòn tấn công của Hội Đồng Bảo Vệ)*

---

## 🛑 NHÓM 1: CÂU HỎI VỀ BẢO MẬT & PHÂN QUYỀN (SECURITY)

**Q1: Mật khẩu của nhân viên lưu trong Database có bị Lộ không? Giám đốc coi được không?**
- **Sếp vặn:** "Lỡ tao là Manager, tao ỷ quyền tao dòm Database tao lấy cắp Pass của con Annotator tao xài được không?"
- **Phản gank:** "Dạ thưa Cô là TUYỆT ĐỐI KHÔNG Ạ! Toàn bộ Password của nhân sự khi xuống DB đều bị chạy qua cối xay sinh tố **Bcrypt**. Đây là chuẩn tính toán **Hash một chiều (One-way Hash)**. Tức là Củ cà rốt băm ra ly Sinh tố `$2a$10...` đố Hãng nào ép Ly sinh tố dính lại thành Củ cà rốt được! Kể cả Admin móc thẳng DB ra dòm cũng bó tay. Giải pháp châm chước duy nhất là gọi hàm "Reset Pass" xin cấp một Củ cà rốt mới ném xuống đè cái cũ thôi ạ!"

**Q2: Nếu Annotator cố tình lấy Token của mình để gọi API tạo Dự Án (Của Manager) thì sao?**
- **Sếp vặn:** "Chặn trên UI Front-end là tào lao, lỡ nó dùng thủ thuật F12 móc Token nhét Postman gọi lén API của Giám đốc thì sao?"
- **Phản gank:** "Dạ Front-end chỉ chặn rèm cửa thôi, lính Canh cửa Sắt là Backend của em nắm ạ! Trên nóc cái hàm API Tạo Dự Án, em đã dán cái thẻ dán bùa cấm: `@PreAuthorize("hasAuthority('MANAGER')")` của Spring Security. Khi cái Token bị lột mặt nạ, soi ra bên trong giấu cái quyền `Roll=ANNOTATOR`, nó lập tức bị cánh cửa Tòa án ném thẳng mã lỗi **403 Forbidden** đá văng ra xa ngàn dặm chặn không cho chạm tới Tầng Code Logic của em ạ!"

**Q3: Tại sao CSDL em lùng bùng thế, sao không dùng ID là `1, 2, 3..` mà lại đè ra dùng kiểu `UUID` dở người?**
- **Sếp vặn:** "Dùng ID số tăng dần cho lẹ, bày đặt vẽ chuyện UUID làm gì cho tốn dung lượng ổ cứng?"
- **Phản gank:** "Dạ nếu để ID `1, 2, 3` thì thằng Hacker nó sẽ mò ra được ngay ID `4` là Task của ai rồi nó chọc mũi gọi API phá sập luôn! Em xài chuẩn `UUID` ngẫu nhiên tỷ phím tỷ tự như này (`123e-4567...`) là để Che Đậy Định Danh (Obfuscation), chặn đứng mưu đồ đoán bừa URL của Hacker bẩn ạ. Big Tech tụi nó đều xài cái này hết cô ơi!"

---

## ⚔️ NHÓM 2: CÂU HỎI VỀ NGHIỆP VỤ & TẢI TRỌNG (BUSINESS LOGIC)

**Q4: Ủa cái biến `MAX_ACTIVE_TASKS = 3` có ý nghĩa gì? Sao cản lối hệ thống vậy em?**
- **Sếp vặn:** "Ủa sao mày giới hạn có 3 Task vậy em? Vậy tao xài chi hệ thống mày, lỡ tao muốn làm 10 cái cùng lúc sao?"
- **Phản gank:** "Dạ thưa cô, đây là nguyên lý Vàng trong quản trị Kanban (Agile) gọi là **WIP Limit** (Work-in-Progress Limit) đó ạ! Con số 3 này là tính trên "Số lượng Task đang tồn đọng tại 1 thời điểm của 1 nhân sự". Quá 3 cái mà không chịu Nộp đi chứng tỏ bị Ngộp Nước, dồn việc. Backend em sẽ chặn họng Manager ép phải giao việc cho đứa khác đang ngồi thảnh thơi (Có WIP < 3), nhờ thuật toán này mà Hệ sinh thái Cty em Tự Cân Bằng Tải Tuyệt Đối (Load Balancing) sức lao động của công nhân luôn ạ!"

**Q5: Đang lúc Manager bấm nút Re-Assign đổi việc qua tay người khác, mà người cũ tự dưng bấm nút Nộp Bài thì tính điểm cho ai?**
- **Sếp vặn:** "Tình huống lộn xộn (Race condition), giải quyết sao?"
- **Phản gank:** "Dạ thưa Cô, trễ 1 mili-giây cũng là thua. Lúc Manager bấm nút Phân Công Lại, bên trong cái hàm `Assign` của em nó chốt chặn dòng 1: Mở Data ra dò xem trạng thái Task. 
Nếu thằng lính cũ nó nhanh chuột ấn Nộp (`SUBMITTED`) trước đúng 1 nhịp thở, Cột Status đổi màu -> Cái hàm của Manager sẽ Tịt ngòi giật ngược, Backend táng vỡ mặt Manager mã lỗi 400 Bad Request chửi: *"Cấm bóc lột, người ta làm xong xuôi hết rồi đổi mẹ gì nữa!"* 
Nếu Manager ân Re-Assign trước -> Cột Annotator bị tẩy xóa đổi tên -> Rảnh lính cũ có Nộp bài rớt rọt không nộp được vì check điều kiện "Chỉ chủ Task mới được Nộp". Nên hệ thống em cắn khớp mượt mà 100% không sợ đánh lộn ạ!"

---

## ⏰ NHÓM 3: XỬ LÝ CRONJOB VÀ DEADLINE

**Q6: Code của em tự biết Task trễ hẹn luôn hả? Không có người ngồi rà soát sao?**
- **Sếp vặn:** "Thuật toán xử lý Phạt nộp trễ của em nó chạy ra làm sao em giải trình cô nghe?"
- **Phản gank:** "Dạ thay vì để con người ngồi soi danh sách, em cài Con Bot canh gác chạy ngầm bằng thuật ngữ `@Scheduled` của Spring Boot ạ. Em nạp vô đoạn bùa Cron Expression `@Scheduled(cron = "0 */15 * * * *")` ý nghĩa là cứ kim đồng hồ nhích qua 15 phút, Con Bot của em tự đội mồ dưới gầm Server tỉnh dậy rà 1 list danh sách.
Gõ trúng đứa nào chưa nộp mà Lố Ngày (Due Date) -> Nó lấy rựa chém cờ `OVERDUE` vào Data, bắn tiếng chuông Đòi nợ, và vẩy thêm 1 gậy Tiền án Tiền sự vào cột `Warnings` (Cảnh cáo) cho Manager lấy cớ đó trừ Lương ạ!"

**Q7: Ghi `Warnings` (Cảnh cáo) cộng dồn hoài vậy có bị lố tay Spam nó mỗi 15 phút không?**
- **Sếp vặn:** "Ủa 15 phút quét 1 lần, tức là 1 tiếng nó ăn 4 cái thư rác Đòi Nợ à? Stress chết cha!"
- **Phản gank:** "Dạ em lường trước hết rồi Cô ơi! Ở dưới Code hàm Check, em đã thả cái biến `COOLDOWN_HOURS = 24`. Nghĩa là hôm nay em chém nó 1 gậy Cảnh Cáo xong là em đóng băng cái Task đó. Đúng 24 tiếng sau qua ngày hôm sau mặt trời ửng đỏ em mới mò ra chém nó nhát Mệnh lệnh thứ 2... chứ không có chuyện spam tin nhắn làm ô nhiễm Notification đâu Cô yên tâm ạ!"

---

## 📜 NHÓM 4: XỬ LÝ DATABASE & DATA RÁC

**Q8: Câu hỏi hóc: Lúc Upload 1.000 hình ảnh bốc rác vào, em cản hình Trùng Lặp bằng cách nào?**
- **Sếp vặn:** "Tao vứt 1 đống ảnh, trong đó có cả tá link ảnh lặp lại, CSDL của em biến thành bãi rác khổng lồ sao em?"
- **Phản gank:** "Dạ hông bé ơi! Trước khi quăng cái list DataItem vô PostgreSQL, cái Code Back-end của em nó đã tráng qua 2 lớp phin lọc: Lớp 1 dùng tính chất `Set<String>` của Java gom chung những đứa trùng Link URL nẹt rụng xuống đáy. Lớp 2 nó vác mớ Link sạch đó chọi vô Query DB xem trùng với Data kiếp trước không. Lọt qua 2 khe cửa hẹp nhất nó mới đâm vào DB thành hàng Độc Nhất Vô Nhị. Nên DB của em ươm chuẩn xịn đét 100% không có cặn bẩn rác rưởi lọt lầm vào đâu Cô nha!"

---

## 📊 NHÓM 5: TỔNG QUAN KIẾN TRÚC API

**Q9: Mày làm Manager, vậy Manager của em quản lý bao nhiêu API tất cả? Nêu thử vòng đời xem?**
- **Sếp vặn:** "Chém gió cho cố, tóm lại Manager gọi được bao nhiêu API, phục vụ mục đích gì?"
- **Phản gank:** "Dạ thưa Cô, tài khoản Manager trong Backend là linh hồn của hệ thống, nên em đã cấp đặc quyền truy cập xấp xỉ **~25 API cốt lõi**, bao trùm 5 Module chính:
  1. **Cụm Dữ liệu (8 API):** CRUD các rổ `Dataset`, nổi bật nhất là API Bulk Upload 1.000 tấm ảnh kèm thuật toán lọc trùng lặp URL.
  2. **Cụm Dự án (4 API):** Quản lý vòng đời `Project` và chốt hạn Deadline tổng.
  3. **Cụm Điều hành Task (8 API):** Giao việc (kèm chốt chặn WIP Limit 3 Task), Phân công lại (Re-assign), và Ân xá dời Deadline.
  4. **Cụm Soi mói KPI (2 API lớn):** Chuyên bóc tách hiệu suất nhân sự, đếm số `Warnings` và đo Lường Tỉ lệ duyệt sai (`ApprovalRate`).
  5. **Cụm Vệ tinh (3 API):** Rút trích danh sách nhân sự (Users) dưới Database và Lắng nghe tiếng khóc từ chối của nhân viên qua `Notifications`.

**Q10: Giám đốc (Manager) có 25 API, vậy lính lác như Annotator và Reviewer tụi nó gọi bao nhiêu API? Xài ké của sếp hả?**
- **Sếp vặn:** "Hệ thống em có phân tách rạch ròi không, hay ai thích múa rớt hàm nào thì gọi hàm đó?"
- **Phản gank:** "Dạ thưa Cô, nguyên tắc bảo mật Cốt Lõi của hệ thống em là **Nguyên tắc Đặc Quyền Tối Thiểu (Least Privilege)**! Tụi lính (Annotator và Reviewer) em "bo xì" khóa quyền cực kỳ gắt gao, tụi nó chỉ được gọi đúng lèo tèo **~7 API** vừa đủ để làm nhiệm vụ của nó thôi ạ:
  - **Với Annotator (Thợ dán nhãn):** Chỉ được gọi `GET` lấy danh sách công việc của BẢN THÂN nó (`/tasks/annotator/.../in-progress`), `PATCH` để Nhận việc (Accept) / Từ chối (Refuse), `PATCH` để Nộp bài (Submit) và `GET` xem chuông Thông Báo của riêng nó.
  - **Với Reviewer (Người đi chấm điểm):** Chỉ cho phép gọi `GET` lấy việc của BẢN THÂN nó (`/tasks/reviewer/.../in-progress`), và 2 hàm Cấu véo sự sống còn là: `PATCH` Duyệt bài (Approve) hoặc `PATCH` Đánh rớt (Reject) bắt Annotator làm lại. 
👉 Tất cả 25 API xịn sò của Manager như Đẻ Dự Án, Phân Công, Soi KPI... hai đứa này mà léng phéng F12 móc Token bỏ vào gọi lén, lập tức bị vách tường `@PreAuthorize` của Spring Boot tát lật mặt **403 Forbidden** đá văng ra xa 10 mét ngay. Hệ sinh thái hoàn toàn Vô trùng và Không Thể Leo Rào ạ!"

**Q11: Lỡ nhân viên (Annotator) cãi cọ nằng nặc bảo "Em nộp bài rồi mà ông Reviewer đánh trượt láo", thì làm sao phân xử?**
- **Sếp vặn:** "Chuyện tranh chấp đổ lỗi trong team là chuyện cơm bữa, hệ thống em có bằng chứng gì để chốt hạ phân xử không, hay là bắt sếp tra DB bằng mắt?"
- **Phản gank:** "Dạ thưa Cô, chặn đứng mọi miệng lưỡi là chức năng **Activity Log (Nhật ký hoạt động)** của tụi em! Ngay khi nhân sự tương tác (Nộp bài, Duyệt bài, Tạo Task, Từ chối), Backend của em ngầm kích hoạt hàm cấy Log vào Database. 
Đặc biệt, cái Database Log này tụi em thiết kế theo mô hình **Immutable (Bất biến)**: Entity mất hoàn toàn quyền Set (không có hàm Setter), Controller mất hoàn toàn API Sửa/Xóa (Không có PUT/PATCH/DELETE). Nó như cuốn Sổ Đầu Bài bằng Gạch nung, một khi đã khắc lên là vĩnh viễn tồn tại. Lính lác có F12, Hacker có Bypass được, kể cả Admin cầm trịch Hệ thống mang Token xịn vào cũng đành bất lực khoanh tay đứng nhìn không tài nào Phi tang dấu vết hay Sửa lại cột mốc thời gian được ạ. Án tại hồ sơ, lôi Log ra là im re hết dứt điểm tranh chấp ạ!"

---
***(Sách Khải Huyền của Data Labeling - Backup nhét vô nón giáp chờ tới phút bù giờ mở ra đọc thuộc lòng ăn chặt 10 điểm đồ án!)*** 🚀🏆

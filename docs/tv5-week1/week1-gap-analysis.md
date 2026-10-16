# Báo Cáo Phân Tích Hiện Trạng & Điểm Cần Làm Rõ (Gap Analysis)
## Phân hệ Quản Trị, Kiểm Duyệt & Báo Cáo — TV5 (Tuần 1)

---

## 1. Tổng hợp hiện trạng thực tế của Repository

Sau khi rà soát toàn bộ source code, cây thư mục, commit log và danh sách nhánh trên remote GitHub:

| Thành viên / Phân hệ | Hiện trạng trong Source Code | Đánh giá kiến trúc & Rủi ro |
|---|---|---|
| **TV1 (Auth, Account, Place)** | Đã hoàn thành Tuần 1 & 2. Đã merge vào nhánh `dev` qua PR #1. Gồm migration `V1`..`V5`, Entity `User`, `Place`, `Favorite`, backend service, controller, test cases và giao diện frontend (Home, Discover, Auth). | Nền tảng vững chắc, sẵn sàng để TV5 liên kết phân quyền (`Role.CONTENT_ADMIN`, `SYSTEM_ADMIN`) và quản lý vòng đời `PlaceStatus`. |
| **TV2 (Trip & Stop)** | Đã hoàn thành Tuần 1 & 2. Đã merge vào nhánh `dev` qua PR #2. Gồm migration `V6__create_trips_and_stops_tables.sql`, Entity `Trip`, `TripStop`, backend CRUD, security test và frontend UI trip. | Hoạt động tốt. Cần lưu ý khi TV5 ẩn một `Place`, không được hard-delete để tránh vi phạm foreign key của `trip_stops`. |
| **TV3 (Trip Collaboration)** | Đã commit trên nhánh riêng `feature/trip-collaboration-tv3` (commit `ad629f6`). Chưa merge vào `dev`. | **RỦI RO CAO XUNG ĐỘT MIGRATION:** TV3 rẽ nhánh từ commit ban đầu (`fd8a541`) nên tự đặt tên migration là `V2__create_domain_schema.sql`, `V3__add_authentication.sql`, `V4__trip_collaboration_enhancements.sql`. Khi merge vào `dev` (đã có V2..V6) chắc chắn Flyway sẽ báo lỗi duplicate version. |
| **TV4 (Community & Content)** | Các package `review`, `route`, `story` ở backend và `features/community` ở frontend hiện chỉ chứa file rỗng `.gitkeep`. Chưa có code nghiệp vụ. | TV5 thiết kế sẵn mô hình tham chiếu đa hình (`subject_type` + `subject_id`) để không bị phụ thuộc vào tiến độ của TV4. |
| **TV5 (Moderation & Admin)** | Các package `com.citypulse.moderation` và `frontend/src/features/moderation` chỉ có `.gitkeep`. Toàn bộ thiết kế Tuần 1 đã được chuẩn hóa trong thư mục `docs/tv5-week1/`. | Chưa có code backend/frontend (đúng theo phạm vi công việc Tuần 1 là tập trung thiết kế và lập kế hoạch test). Sẵn sàng code ở Tuần 2. |

---

## 2. Những thành phần còn thiếu cần triển khai ở Tuần 2

1. **Database Migration:**
   * Cần tạo file migration chính thức tạo 3 bảng `reports`, `moderation_actions`, `audit_logs` cùng các chỉ mục tối ưu (dự thảo đã sẵn sàng trong [erd.md](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/erd.md)).
2. **Backend Services & Controllers:**
   * Package `com.citypulse.report`: Entity `Report`, `ReportRepository`, `ReportService`, `ReportController` (cho cả user gửi report và admin xem queue).
   * Package `com.citypulse.moderation`: Entity `ModerationAction`, `ModerationService`, `ModerationController` (duyệt/từ chối submission, ẩn/khôi phục review và place).
   * Package `com.citypulse.audit`: Entity `AuditLog`, `AuditLogService`, Spring Event Listener tự động bắt sự kiện quản trị để ghi log.
3. **Dữ liệu mẫu (Seed Data):**
   * Chuẩn bị sẵn bộ dữ liệu giả lập cho hàng đợi kiểm duyệt (ví dụ: 5 địa điểm đề xuất chờ duyệt, 10 báo cáo vi phạm với các trạng thái `OPEN`, `IN_REVIEW`, `RESOLVED`) phục vụ kiểm thử và demo.
4. **Giao diện Admin Dashboard (Frontend):**
   * Xây dựng layout Admin riêng biệt, thanh điều hướng sidebar, bảng thống kê số lượng report tồn đọng, danh sách duyệt nội dung.

---

## 3. Các giả định kiến trúc được áp dụng trong Tuần 1

* **Giả định 1:** Áp dụng mô hình khóa tham chiếu đa hình (*Polymorphic Association*) `subject_type` + `subject_id` cho Báo cáo và Kiểm duyệt, do các bảng của TV4 chưa tồn tại trong cơ sở dữ liệu hiện tại.
* **Giả định 2:** Đánh giá địa điểm (`Review`) được hiển thị công khai ngay sau khi người dùng đăng bài (hậu kiểm duyệt), chỉ đưa vào hàng đợi kiểm duyệt khi có người dùng khác gửi báo cáo vi phạm hoặc hệ thống phát hiện từ cấm.
* **Giả định 3:** Địa điểm đề xuất (`Submission`) bắt buộc phải qua tiền kiểm duyệt (phê duyệt bởi `CONTENT_ADMIN` hoặc `ADMIN`) mới được tạo thành bản ghi chính thức trong bảng `places`.
* **Giả định 4:** Toàn bộ test case soạn thảo trong Tuần 1 được đánh dấu là `Chưa thực thi (Planned)` và sẽ được chuyển sang automated test trong Tuần 2 và Tuần 3.

---

## 4. Danh sách câu hỏi cần nhóm thống nhất trước khi triển khai Tuần 2

Để quá trình lập trình ở Tuần 2 diễn ra trơn tru và không bị xung đột, TV5 đề nghị toàn đội ngũ (TV1, TV2, TV3, TV4) họp bàn và chốt 5 điểm sau:

1. **Xử lý xung đột số thứ tự Flyway Migration của TV3:**
   * *Đề xuất của TV5:* TV3 cần thực hiện `git rebase origin/dev` trên nhánh `feature/trip-collaboration-tv3`, đổi tên các file migration thành `V7__...` và `V8__...`. Khi đó, migration của TV5 sẽ là `V9__create_moderation_report_audit_tables.sql`. Hoặc nếu TV5 merge trước, TV5 sẽ lấy `V7` và TV3 đổi thành `V8`, `V9`.
2. **Quy ước tên bảng và trường dữ liệu của TV4:**
   * TV4 dự kiến đặt tên bảng đánh giá là `reviews` hay `place_reviews`? Bảng đề xuất địa điểm là `submissions` hay `place_submissions`?
   * Các giá trị trong trường `status` của TV4 có thống nhất theo chuẩn `PENDING_APPROVAL`, `APPROVED`, `REJECTED` (với submission) và `PENDING_MODERATION`, `PUBLISHED`, `HIDDEN` (với review) như thiết kế của TV5 không?
3. **Quy tắc khi tài khoản bị khóa (`UserStatus.LOCKED`):**
   * Khi System Admin khóa một tài khoản, các đánh giá cũ và địa điểm người này từng đăng có bị tự động ẩn không?
   * *Đề xuất theo SRS (BR-145):* **Không ẩn tự động**. Việc khóa tài khoản chỉ nhằm ngăn chặn user đó đăng nhập và thực hiện thao tác mới. Các nội dung cũ chỉ bị ẩn nếu từng nội dung đó vi phạm quy chế cộng đồng.
4. **Cơ chế phân quyền xem Nhật ký kiểm toán (Audit Log):**
   * Theo ma trận quyền hạn, chỉ `SYSTEM_ADMIN` và `ADMIN` được xem toàn bộ audit log hệ thống. `CONTENT_ADMIN` có được cấp quyền xem lịch sử các hành động do chính mình thực hiện không?
5. **Giao diện Admin nằm chung Repo hay tách rời:**
   * Giao diện Admin sẽ được dựng thành một route riêng trong cùng dự án frontend hiện tại (ví dụ: route `/admin/**` có layout riêng và được bảo vệ bởi guard role admin), đúng theo cấu trúc monorepo của dự án?

# Danh Mục Tiêu Chuẩn & Checklist Tích Hợp Liên Phân Hệ (Integration Checklist)
## Dự án DNVIBE / CITYPULSE — TV5 Phối hợp TV1, TV2, TV3, TV4

---

## 1. Tích hợp giữa TV5 (Moderation/Admin) và TV1 (Auth, Account, Place)

| Hạng mục tích hợp | Cơ chế kỹ thuật | Trạng thái hiện tại | Checklist hành động cần kiểm tra |
|---|---|:---:|---|
| **Xác thực Role Quản trị** | Spring Security `@PreAuthorize` kiểm tra `Role.CONTENT_ADMIN`, `Role.SYSTEM_ADMIN`, `Role.ADMIN` trong [Role.java](file:///d:/DuAn/DANANG_VISTA/backend/src/main/java/com/citypulse/user/entity/Role.java). | Đã có sẵn Enum | [x] Đảm bảo TV1 không cho phép client tự gán role admin khi đăng ký (`AuthServiceTest` đã có test case này).<br>[ ] Backend kiểm tra đúng quyền ở mọi endpoint `/api/v1/admin/**`. |
| **Khóa tài khoản người dùng** | Cập nhật `User.status = UserStatus.LOCKED` trong bảng `users`. | Đã có Enum & DB constraint | [ ] TV5 cung cấp API khóa tài khoản kèm lý do.<br>[ ] TV1 đảm bảo `JwtAuthenticationFilter` và `CustomUserDetailsService` từ chối cấp token hoặc từ chối request của tài khoản `LOCKED` (BR-06). |
| **Ẩn / Khôi phục Place** | Quản lý vòng đời `PlaceStatus` (`PUBLISHED`, `DRAFT`, `ARCHIVED`) trong [Place.java](file:///d:/DuAn/DANANG_VISTA/backend/src/main/java/com/citypulse/place/entity/Place.java). | Đã có Enum & bảng places | [ ] Khi TV5 chuyển Place sang `ARCHIVED`, truy vấn tìm kiếm/khám phá công khai của TV1 (`PlaceSpecifications`) phải tự động loại bỏ địa điểm này.<br>[ ] Khi Restore, Place trở lại kết quả tìm kiếm bình thường. |
| **Cập nhật điểm đánh giá địa điểm** | Cập nhật trường `rating_average` và `review_count` trên bảng `places`. | Đã có cột trong bảng `places` | [ ] Khi Review bị ẩn hoặc khôi phục, TV5 gọi hook service để TV1 tính lại điểm trung bình chính xác (BR-41, BR-59). |

---

## 2. Tích hợp giữa TV5 (Admin) và TV2 (Trip, Stop, Routing)

| Hạng mục tích hợp | Cơ chế kỹ thuật | Trạng thái hiện tại | Checklist hành động cần kiểm tra |
|---|---|:---:|---|
| **Xử lý khi Place trong Trip bị ẩn** | Ràng buộc `trip_stops.place_id` tham chiếu `places(id)`. | Đã có bảng `trips` và `trip_stops` (V6) | [ ] TV5 **tuyệt đối không dùng lệnh `DELETE FROM places` (hard delete)** để tránh vi phạm khóa ngoại hoặc làm mất lịch sử chuyến đi của người dùng (BR-125).<br>[ ] Khi một Place chuyển sang `ARCHIVED`, giao diện lịch trình của TV2 hiển thị nhãn cảnh báo `[Địa điểm tạm ngưng hoạt động]` thay vì báo lỗi ứng dụng (BR-18, AT-03). |
| **Xử lý điểm dừng tùy chỉnh (Custom Stop)** | Bảng `trip_stops` hỗ trợ `custom_name` khi `place_id` là null (`chk_trip_stops_identity`). | Đã có constraint V6 | [ ] Báo cáo vi phạm (Report) chỉ áp dụng cho Place chính thức hoặc Post/Review, không kiểm duyệt private trip stops nội bộ của người dùng (trừ khi chuyến đi được chia sẻ công khai). |

---

## 3. Tích hợp giữa TV5 (Admin/Report) và TV3 (Trip Collaboration)

| Hạng mục tích hợp | Cơ chế kỹ thuật | Trạng thái hiện tại | Checklist hành động cần kiểm tra |
|---|---|:---:|---|
| **Báo cáo hành vi quấy rối / Lời mời spam** | Người dùng gửi Report loại lời mời chuyến đi rác qua hệ thống Report của TV5. | TV3 đã có module invitation trên branch riêng | [ ] Thống nhất `subject_type` khi báo cáo lời mời spam: `TRIP_INVITATION`.<br>[ ] Cho phép System Admin khóa tài khoản kẻ spam lời mời nếu tái phạm nhiều lần. |
| **Xung đột phiên bản Migration Flyway** | TV3 tạo migration `V2`, `V3`, `V4` trên nhánh `feature/trip-collaboration-tv3` bị xung đột với `V2`..`V6` đã merge vào `dev`. | **CẦN ĐỒNG BỘ GẤP** | [ ] **Hành động bắt buộc:** TV3 phải rebase nhánh theo `dev` và đánh lại số migration bắt đầu từ `V7` (hoặc TV5 và TV3 ngồi lại chốt thứ tự V7, V8) trước khi merge vào nhánh chính. |

---

## 4. Tích hợp giữa TV5 (Moderation) và TV4 (Community, Review, Submission, Post)

| Hạng mục tích hợp | Cơ chế kỹ thuật | Trạng thái hiện tại | Checklist hành động cần kiểm tra |
|---|---|:---:|---|
| **Duyệt địa điểm đề xuất (Submission)** | TV4 tạo `submissions` -> TV5 duyệt -> TV1 tạo `places`. | TV4 chưa triển khai | [ ] Thống nhất trạng thái của Submission: `PENDING_APPROVAL`, `APPROVED`, `REJECTED`.<br>[ ] Khi duyệt thành công, TV5 sao chép dữ liệu cần thiết sang bảng `places` của TV1 với `status = 'PUBLISHED'` và gán `data_quality = 'COMMUNITY_VERIFIED'`. |
| **Kiểm duyệt Đánh giá (Review)** | TV4 tạo `reviews` -> Người dùng báo cáo -> TV5 xem xét ẩn hoặc giữ. | TV4 chưa triển khai | [ ] Thống nhất trạng thái Review: `PENDING_MODERATION`, `PUBLISHED`, `HIDDEN`, `REMOVED`.<br>[ ] Đảm bảo TV4 khi truy vấn hiển thị review cho người dùng luôn có mệnh đề `WHERE status = 'PUBLISHED'`. |
| **Kiểm duyệt Bài viết cộng đồng (Post)** | TV4 tạo `posts` -> TV5 ẩn bài viết vi phạm. | TV4 chưa triển khai | [ ] Thống nhất trạng thái Post: `PUBLISHED`, `HIDDEN`, `REMOVED`.<br>[ ] Khi Post bị ẩn, không hiển thị trên bảng tin công khai nhưng tác giả vẫn thấy thông báo kèm lý do bị ẩn trong trang cá nhân. |
| **Đồng bộ sự kiện (Event-driven Notification)** | Khi nội dung bị duyệt/từ chối/ẩn, hệ thống tạo thông báo in-app cho tác giả. | TV5 / Hệ thống | [ ] TV5 gửi event nội bộ (Spring Application Event) để module Notification gửi thông báo cho tác giả nội dung. |

---

## 5. Chuẩn hóa API Contract và Quy ước giao tiếp chung

Mọi endpoint do TV5 phát triển đều tuân thủ chặt chẽ quy ước đã được SRS và nhóm thống nhất:

1. **Định dạng URL Endpoint:**
   * `/api/v1/admin/moderation/**` : Dành cho Content Admin kiểm duyệt nội dung.
   * `/api/v1/admin/reports/**` : Dành cho Content Admin xử lý báo cáo.
   * `/api/v1/admin/system/**` : Dành cho System Admin quản lý tài khoản và audit log.
   * `/api/v1/reports/**` : Dành cho người dùng thông thường gửi báo cáo.
2. **Cấu trúc Response lỗi chuẩn (`ApiErrorResponse`):**
   ```json
   {
     "success": false,
     "code": "MOD_ALREADY_PROCESSED",
     "message": "Nội dung này đã được kiểm duyệt bởi quản trị viên khác",
     "details": null,
     "timestamp": "2026-10-16T21:40:00+07:00",
     "requestId": "req-9b8e-4a12"
   }
   ```
3. **Chuẩn phân trang (Pagination):**
   * Tham số query: `?page=0&size=20&sort=createdAt,desc`.
   * Size mặc định là 20, tối đa 100 bản ghi/trang để chống quá tải bộ nhớ.

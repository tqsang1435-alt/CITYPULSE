# Ma Trận Phân Quyền Vai Trò (Role-Based Access Control — RBAC)
## Phân hệ Quản Trị, Kiểm Duyệt & Báo Cáo — TV5

---

## 1. Khảo sát hệ thống vai trò thực tế trong source code

Dựa trên việc kiểm tra trực tiếp mã nguồn tại:
* [Role.java](file:///d:/DuAn/DANANG_VISTA/backend/src/main/java/com/citypulse/user/entity/Role.java): Hỗ trợ các vai trò `USER`, `ADMIN`, `CONTENT_ADMIN`, `SYSTEM_ADMIN`.
* [UserStatus.java](file:///d:/DuAn/DANANG_VISTA/backend/src/main/java/com/citypulse/user/entity/UserStatus.java): Quản lý trạng thái vòng đời `ACTIVE`, `LOCKED`.
* [V2__create_users_table.sql](file:///d:/DuAn/DANANG_VISTA/backend/src/main/resources/db/migration/V2__create_users_table.sql): Ràng buộc `CHECK (role IN ('USER', 'ADMIN', 'CONTENT_ADMIN', 'SYSTEM_ADMIN'))`.

Hệ thống định nghĩa 5 vai trò tác nhân chính (bao gồm cả khách vãng lai):

1. **GUEST (Khách chưa xác thực):** Người dùng chưa đăng nhập, chỉ có quyền xem các tài nguyên công khai (`PUBLISHED`).
2. **USER (Người dùng tiêu chuẩn):** Đã đăng ký và xác thực hợp lệ, tài khoản ở trạng thái `ACTIVE`.
3. **CONTENT_ADMIN (Kiểm duyệt viên nội dung / Moderator):** Phụ trách duyệt địa điểm đề xuất, kiểm duyệt đánh giá, bài viết cộng đồng và xử lý báo cáo vi phạm.
4. **SYSTEM_ADMIN (Quản trị viên hệ thống):** Phụ trách an ninh, khóa/mở khóa tài khoản, xem audit log, giám sát kỹ thuật.
5. **ADMIN (Quản trị viên toàn quyền / Super Admin):** Sở hữu toàn bộ quyền hạn của cả `CONTENT_ADMIN` và `SYSTEM_ADMIN`.

---

## 2. Ma trận phân quyền chi tiết (RBAC Matrix)

Ký hiệu:
* **[V] Cho phép:** Tác nhân có quyền thực hiện.
* **[X] Từ chối (403 Forbidden):** Hệ thống chặn ở tầng backend security.
* **[-] Yêu cầu đăng nhập (401 Unauthorized):** Chưa cung cấp Bearer Token.
* **[!] Có điều kiện:** Phải vượt qua các ràng buộc nghiệp vụ cụ thể.

| Nhóm chức năng | Hành động chi tiết | GUEST | USER | CONTENT_ADMIN | SYSTEM_ADMIN | ADMIN | Ràng buộc nghiệp vụ / Ghi chú |
|---|---|:---:|:---:|:---:|:---:|:---:|---|
| **Báo cáo (Report)** | Gửi báo cáo vi phạm | [-] | [V] | [V] | [V] | [V] | Phải đăng nhập, kiểm tra rate-limit (BR-49) |
| | Xem hàng đợi báo cáo | [X] | [X] | [V] | [V] | [V] | Endpoint: `GET /api/v1/admin/reports` |
| | Xem chi tiết báo cáo & bằng chứng | [X] | [X] | [V] | [V] | [V] | Kèm theo dữ liệu của đối tượng bị báo cáo |
| | Tiếp nhận báo cáo (Assign/Claim) | [X] | [X] | [V] | [X] | [V] | Khóa mềm tránh 2 mod trùng xử lý |
| | Đóng / Bác bỏ báo cáo (Reject) | [X] | [X] | [V] | [X] | [V] | Bắt buộc nhập lý do (BR-142) |
| | Giải quyết báo cáo (Resolve) | [X] | [X] | [V] | [X] | [V] | Liên kết với hành động kiểm duyệt |
| **Kiểm duyệt (Moderation)** | Xem hàng đợi kiểm duyệt (Queue) | [X] | [X] | [V] | [X] | [V] | Endpoint: `GET /api/v1/admin/moderation/queue` |
| | Duyệt địa điểm đề xuất (Submission) | [X] | [X] | [V] | [X] | [V] | Tự động tạo Place mới trạng thái PUBLISHED |
| | Từ chối địa điểm đề xuất | [X] | [X] | [V] | [X] | [V] | Bắt buộc lý do gửi cho tác giả |
| | Ẩn đánh giá vi phạm (Hide Review) | [X] | [X] | [V] | [X] | [V] | Tự động trừ rating_average của Place (BR-41) |
| | Khôi phục đánh giá (Restore Review) | [X] | [X] | [V] | [X] | [V] | Tự động cộng lại rating_average (BR-59) |
| | Ẩn bài viết cộng đồng (Hide Post) | [X] | [X] | [V] | [X] | [V] | Loại khỏi danh sách cộng đồng |
| | Duyệt / Khôi phục nội dung chính mình | [X] | [X] | [X] | [X] | [!] | **Cấm tự phê duyệt (BR-MOD-01)** |
| **Quản lý địa điểm (Place)** | Tạo mới địa điểm chính thức | [X] | [X] | [V] | [X] | [V] | Bắt buộc đạt checklist publish (BR-21, BR-123) |
| | Sửa thông tin địa điểm chính thức | [X] | [X] | [V] | [X] | [V] | Tạo phiên bản và ghi nhận audit log |
| | Ẩn địa điểm (Archive Place) | [X] | [X] | [V] | [X] | [V] | Không xóa cứng nếu đã có trip tham chiếu |
| | Khôi phục địa điểm (Restore Place) | [X] | [X] | [V] | [X] | [V] | Trạng thái chuyển lại PUBLISHED |
| **Kiểm toán & Quản trị hệ thống** | Xem danh sách Audit Log | [X] | [X] | [X] | [V] | [V] | Endpoint: `GET /api/v1/admin/audit-logs` |
| | Xuất file Audit Log | [X] | [X] | [X] | [V] | [V] | Giới hạn khoảng thời gian truy vấn (BR-169) |
| | Khóa tài khoản người dùng (Lock) | [X] | [X] | [X] | [V] | [V] | Thu hồi ngay mọi phiên JWT active (BR-164) |
| | Mở khóa tài khoản (Unlock) | [X] | [X] | [X] | [V] | [V] | Cho phép người dùng đăng nhập lại |
| | Thay đổi Role người dùng | [X] | [X] | [X] | [X] | [V] | Chỉ Super ADMIN mới có quyền gán role |
| | Truy cập Dashboard Quản trị | [X] | [X] | [V] | [V] | [V] | Giao diện hiển thị theo từng phân quyền |

---

## 3. Kiến trúc bảo mật tầng Backend (Defense in Depth)

Hệ thống tuân thủ nguyên tắc **"Zero Trust ở Frontend"**: Tuyệt đối không dựa vào việc ẩn/hiện nút bấm trên giao diện để bảo vệ quyền truy cập. Mọi yêu cầu đều phải được kiểm soát đa lớp tại Backend:

```
[HTTP Request]
       |
       v
[1. JwtAuthenticationFilter] ----> Trích xuất Bearer Token, kiểm tra chữ ký & Hạn dùng
       |
       v
[2. UserStatusCheck] -------------> Kiểm tra DB/Cache: nếu status == 'LOCKED' -> Trả về 403 Forbidden
       |
       v
[3. Method Security Interceptor] -> Đánh giá @PreAuthorize("hasAnyRole(...)")
       |
       v
[4. Domain Business Policy] ------> Kiểm tra xung đột: Cấm tự phê duyệt, cấm thao tác dữ liệu người khác
       |
       v
[Controller & Service Logic]
```

### 3.1 Cấu hình bảo vệ mẫu bằng Spring Security
```java
// Ví dụ cấu hình phân quyền tại Service/Controller
@RestController
@RequestMapping("/api/v1/admin/moderation")
@PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'ADMIN')")
public class ModerationController {

    @PostMapping("/submissions/{id}/approve")
    public ResponseEntity<Void> approveSubmission(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        // Domain Policy: Chặn tự duyệt nội dung của chính mình
        moderationService.approveSubmission(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
```

### 3.2 Quy định xử lý trường hợp đặc biệt
1. **Tài khoản bị khóa (`LOCKED`):** Khi System Admin thực hiện khóa tài khoản, token đang lưu hành của tài khoản đó phải bị vô hiệu hóa ngay lập tức thông qua blacklist token hoặc kiểm tra `user.status` trong `JwtAuthenticationFilter` (BR-06).
2. **Bảo vệ quyền riêng tư người dùng (BR-170):** Kể cả `SYSTEM_ADMIN` hay `ADMIN` cũng **không được cung cấp API để đọc lịch trình riêng tư (`private itineraries`) hoặc bộ sưu tập cá nhân (`collections`)** của người dùng, ngoại trừ trường hợp nhận được yêu cầu hỗ trợ kỹ thuật có văn bản phê duyệt.

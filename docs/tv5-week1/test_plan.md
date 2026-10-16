# Kế Hoạch Kiểm Thử Toàn Diện (Master Test Plan)
## Dự Án DNVIBE / CITYPULSE (DANANG_VISTA) — Phiên Bản 1.0

---

## 1. Mục tiêu kiểm thử (Test Objectives)

Kế hoạch kiểm thử này đóng vai trò kim chỉ nam đảm bảo chất lượng phần mềm cho toàn bộ dự án **DNVIBE (CITYPULSE)** trong suốt chu kỳ phát triển 8 tuần. Mục tiêu chính bao gồm:
1. **Xác minh tuân thủ đặc tả SRS:** Đảm bảo tất cả 34 Yêu cầu chức năng (FR-01 đến FR-34), 30 Trường hợp sử dụng (UC-01 đến UC-30) và 182 Quy tắc nghiệp vụ (BR-01 đến BR-182) được kiểm tra có hệ thống.
2. **Kiểm tra tính toàn vẹn dữ liệu (Data Integrity):** Xác thực các ràng buộc toàn vẹn cơ sở dữ liệu (`CHECK`, `UNIQUE`, `FOREIGN KEY`, `NOT NULL`) và tính chính xác của dữ liệu địa lý PostGIS.
3. **Bảo mật và phân quyền (Security & RBAC):** Đảm bảo cơ chế xác thực JWT, bảo vệ tài khoản `LOCKED`, ma trận quyền hạn cho 4 vai trò (`USER`, `CONTENT_ADMIN`, `SYSTEM_ADMIN`, `ADMIN`) và ngăn chặn triệt để lỗ hổng phân quyền ngang IDOR (AT-01).
4. **Kiểm tra tích hợp liên phân hệ (Inter-module Integration):** Kiểm chứng tính liên tục của luồng dữ liệu xuyên suốt giữa 5 thành viên (TV1 -> TV2 -> TV3 -> TV4 -> TV5).
5. **Đảm bảo kịch bản Demo hoàn chỉnh:** Thực thi smoke test và nghiệm thu thực tế theo kịch bản xuyên suốt: Người dùng khám phá -> Lập lịch trình -> Check-in/Review -> Báo cáo vi phạm -> Admin kiểm duyệt và cập nhật dữ liệu.

---

## 2. Phạm vi kiểm thử (Test Scope)

### 2.1 Các phân hệ trong phạm vi (In-Scope)
* **TV1 (Auth, Account, Place, Search, Favorite):** Đăng ký, đăng nhập, bảo vệ JWT, tìm kiếm địa điểm, lọc theo khoảng cách/giờ mở cửa, lưu yêu thích.
* **TV2 (Trip Planning & Routing):** Tạo lịch trình, thêm/xóa/đổi thứ tự `TripStop`, ràng buộc thời gian bắt đầu/kết thúc, tính toán chi phí ước tính.
* **TV3 (Trip Collaboration):** Mời thành viên tham gia chuyến đi qua email, chấp nhận/từ chối lời mời, phân quyền vai trò trong chuyến đi (`OWNER`, `CONTRIBUTOR`, `VIEWER`), đề xuất và duyệt địa điểm (`Proposal`).
* **TV4 (Community & Content):** Đăng đánh giá địa điểm (`Review`), đề xuất địa điểm mới (`Submission`), viết bài chia sẻ cộng đồng (`Post`), tuyến khám phá (`Story/Route`).
* **TV5 (Moderation, Report, Admin, Audit Log):** Hàng đợi kiểm duyệt, duyệt/từ chối địa điểm, ẩn/khôi phục đánh giá, tiếp nhận và xử lý báo cáo người dùng, khóa tài khoản, ghi nhận nhật ký kiểm toán bất biến.

### 2.2 Các phân hệ ngoài phạm vi MVP (Out-of-Scope)
* Cổng thanh toán trực tuyến, đặt phòng khách sạn, đặt vé máy bay (P2).
* Ứng dụng di động native hoàn chỉnh (MVP tập trung Web Responsive 360px+; mobile native là mở rộng P2).
* Hệ thống xử lý AI sinh tự do không giới hạn (AI chỉ đóng vai trò hỗ trợ giải thích dựa trên dữ liệu có sẵn của backend).

---

## 3. Các cấp độ và loại kiểm thử (Testing Types)

```
        / \
       /   \        E2E Tests (Luồng người dùng chính, kịch bản Demo)
      /-----\
     /       \      API & Integration Tests (@SpringBootTest, MockMvc, Testcontainers)
    /---------\
   /           \    Unit Tests (JUnit 5, Mockito, Vitest - Logic nghiệp vụ cô lập)
  /-------------\
```

1. **Unit Testing:**
   * **Backend:** JUnit 5 + Mockito. Kiểm tra tính đúng đắn của logic tính toán (tính điểm trung bình rating, kiểm tra thời gian mở cửa, thuật toán xếp thứ tự stop, validation DTO).
   * **Frontend:** Vitest + React Testing Library. Kiểm tra render component, xử lý sự kiện form, hiển thị trạng thái loading/error.
2. **Integration Testing:**
   * Sử dụng `@SpringBootTest` kết hợp cơ sở dữ liệu kiểm thử độc lập (Testcontainers PostgreSQL 16 + PostGIS) để xác minh tương tác Repository, Flyway migration và quan hệ khóa ngoại thực tế.
3. **API Contract & Security Testing:**
   * Kiểm thử cấu trúc JSON response theo chuẩn chung (`ApiResponse`, mã lỗi, status code `400`, `401`, `403`, `404`, `409`, `422`).
   * Kiểm thử phân quyền RBAC: Gọi API Admin bằng token của `USER` thường (phải nhận `403 Forbidden`).
4. **Database Constraint Testing:**
   * Thử insert dữ liệu vi phạm `CHECK constraint` (ví dụ: ngày kết thúc trước ngày bắt đầu, budget âm, rating ngoài khoảng 1–5, email trùng lặp) để đảm bảo DB bắt lỗi chính xác.
5. **Smoke & Regression Testing:**
   * Bộ kiểm thử nhanh (Smoke Test) gồm 10 ca kiểm thử then chốt chạy trước mỗi buổi demo hoặc sau khi merge mã nguồn vào nhánh `dev`.

---

## 4. Môi trường kiểm thử (Test Environment)

| Thành phần | Cấu hình kiểm thử | Ghi chú an ninh |
|---|---|---|
| **Máy chủ ứng dụng** | Java 21 LTS, Spring Boot 3.3+ | Chạy trên profile `test` (`application-test.yml`) |
| **Cơ sở dữ liệu** | PostgreSQL 16 + PostGIS 3.4 (Docker) | Database test riêng biệt, tự động reset giữa các lượt test tích hợp |
| **Giao diện Web** | Node.js v20+, React 18 / Vite | Trình duyệt Chrome / Firefox (Responsive view 375px & 1440px) |
| **Map Provider Mock** | WireMock / Adapter Mock | Tuyệt đối không gọi tốn quota map API thật trong quá trình chạy test tự động |
| **Secret Management** | Sử dụng `.env.test` với khóa giả định | **Cấm commit API key thật hoặc mật khẩu môi trường sản xuất lên Git** |

---

## 5. Chiến lược dữ liệu kiểm thử (Test Data Strategy)

Dữ liệu kiểm thử được phân định làm 3 nhóm độc lập:
1. **Bộ dữ liệu hạt giống (Seed Data Đà Nẵng):**
   * Được nạp qua migration `V4__seed_da_nang_places.sql` (hơn 30 địa điểm du lịch, ẩm thực thực tế của Đà Nẵng như Cầu Rồng, Bán đảo Sơn Trà, Cầu Vàng Bà Nà...).
   * Mục đích: Phục vụ trải nghiệm UI thực tế và kịch bản Demo nghiệm thu.
2. **Dữ liệu giả lập cô lập (Test Fixtures):**
   * Được sinh động trong từng bài test (Fixture Factory) và tự dọn dẹp (Rollback transaction).
   * Bao gồm các user mẫu: `test_user@danang.vn`, `mod@danang.vn`, `sysadmin@danang.vn`.
3. **Dữ liệu trường hợp biên (Edge Cases Data):**
   * Chuỗi văn bản cực dài (> 2000 ký tự), ký tự đặc biệt XSS (`<script>`), SQL Injection (`' OR 1=1 --`), số âm, tọa độ địa lý ngoài lãnh thổ Đà Nẵng.

---

## 6. Quy trình quản lý lỗi (Defect Management Process)

Mọi khiếm khuyết phát hiện trong quá trình kiểm thử phải được lập phiếu lỗi chuẩn hóa:

### 6.1 Mẫu báo cáo lỗi (Bug Report Template)
```markdown
- Mã lỗi (Bug ID): BUG-MOD-01
- Tiêu đề: Moderator có thể tự duyệt Submission của chính mình
- Phân hệ: TV5 - Moderation
- Môi trường: Local Dev / Branch feature/admin-moderation-tv5
- Mức độ nghiêm trọng (Severity): S1 - Blocker
- Mức độ ưu tiên (Priority): P0 - Khắc phục ngay
- Các bước tái hiện:
  1. Đăng nhập bằng tài khoản có role CONTENT_ADMIN.
  2. Tạo một địa điểm đề xuất mới qua API Submission.
  3. Lấy ID submission và gọi POST /api/v1/admin/moderation/submissions/{id}/approve.
- Kết quả thực tế: API trả về 200 OK, địa điểm được publish thành công.
- Kết quả mong đợi: API trả về 403 Forbidden hoặc 422 Unprocessable Entity kèm thông báo "Không được phép tự phê duyệt nội dung của chính mình".
- Người phát hiện: TV5 (QA)
- Người phụ trách sửa: TV5 / TV1
- Trạng thái: NEW
```

### 6.2 Phân loại mức độ nghiêm trọng (Severity) và ưu tiên (Priority)
* **S1 (Blocker):** Sập hệ thống, mất dữ liệu, hổng an ninh nghiêm trọng (IDOR, vượt quyền admin). -> **P0: Fix trong 4h**.
* **S2 (Critical):** Tính năng cốt lõi không hoạt động (không tạo được trip, không tìm kiếm được). -> **P1: Fix trong 24h**.
* **S3 (Major):** Lỗi logic nghiệp vụ có thể lách qua được, tính toán sai một số tiêu chí phụ. -> **P2: Fix trong sprint**.
* **S4 (Minor):** Lỗi giao diện, sai chính tả, khoảng cách hiển thị chưa chuẩn. -> **P3: Fix khi có thời gian**.

---

## 7. Tiêu chí bắt đầu và kết thúc (Entry & Exit Criteria)

### 7.1 Điều kiện bắt đầu kiểm thử (Entry Criteria)
* Mã nguồn compile thành công không có lỗi cú pháp.
* Toàn bộ migration Flyway hiện hành (`V1` đến `V6`) chạy thành công trên DB PostgreSQL sạch.
* Các module cần kiểm thử có API contract rõ ràng và dữ liệu mẫu cơ bản.

### 7.2 Điều kiện kết thúc kiểm thử (Exit Criteria)
* 100% các ca kiểm thử P0 và kịch bản Demo hoàn thành không còn lỗi S1 (Blocker) hoặc S2 (Critical).
* Không có lỗi rò rỉ dữ liệu nhạy cảm hoặc bỏ sót kiểm tra phân quyền.
* Báo cáo kiểm thử tổng hợp (QA Report) được cập nhật đầy đủ và các bên liên quan ký xác nhận.

---

## 8. Ma trận phân công trách nhiệm kiểm thử (RACI Matrix)

| Phân hệ / Hoạt động | TV1 (Sơn) | TV2 (Sang) | TV3 (Quy) | TV4 | TV5 (QA Lead) |
|---|:---:|:---:|:---:|:---:|:---:|
| **Unit Test phân hệ mình** | **R / A** | **R / A** | **R / A** | **R / A** | **R / A** |
| **Test tích hợp Trip & Stop** | C | **R / A** | C | I | C |
| **Test phân quyền Trip Collab** | C | C | **R / A** | I | C |
| **Test vòng đời Review & Post** | I | I | I | **R / A** | C |
| **Test Moderation, Report, Admin** | I | I | I | C | **R / A** |
| **Điều phối Test tích hợp toàn dự án** | C | C | C | C | **R / A** |
| **Tổng hợp Bug Tracker & Checklist Demo** | C | C | C | C | **R / A** |

*(Ghi chú: R = Responsible - Người thực hiện; A = Accountable - Người chịu trách nhiệm chính; C = Consulted - Người được tham vấn; I = Informed - Người nhận thông tin)*

---

## 9. Phân tích rủi ro kiểm thử và giải pháp giảm thiểu

| Rủi ro kỹ thuật | Mức độ | Kế hoạch giảm thiểu của nhóm |
|---|:---:|---|
| **Lệch cấu trúc DB giữa các nhánh Git** | Cao | Thống nhất tuyệt đối thứ tự migration Flyway; không merge nhánh khi chưa kiểm tra rebase với `dev`. |
| **Bỏ sót kiểm tra quyền hạn ở Backend** | Rất cao | Viết riêng bộ integration test bảo mật kiểm tra quét toàn bộ endpoint với role `USER` và token rác. |
| **Nội dung bị ẩn vẫn rò rỉ ra Search/Map** | Cao | Bổ sung integration test xác nhận các query Place, Review luôn có điều kiện `status = 'PUBLISHED'`. |
| **Thiếu hụt dữ liệu mẫu khi demo thực tế** | Trung bình | Chuẩn bị sẵn file SQL seed dữ liệu phong phú cho Đà Nẵng tại Tuần 2. |

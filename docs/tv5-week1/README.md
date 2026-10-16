# DNVIBE / CITYPULSE — Phân Hệ Quản Trị, Kiểm Duyệt & Báo Cáo (TV5)
## Bàn Giao Thiết Kế & Kế Hoạch Kiểm Thử — Tuần 1

---

## 1. Giới thiệu tổng quan

Tài liệu này tổng hợp toàn bộ kết quả công việc **Tuần 1** của **Thành viên thứ 5 (TV5)** trong dự án **DNVIBE (DANANG_VISTA / CITYPULSE)** theo phân công kiến trúc hệ thống và tài liệu SRS v2.3.

### Phân công trách nhiệm dự án:
* **TV1:** Tài khoản & Địa điểm (*Auth, Account, Place, Search, Favorite*) — *Đã merge Tuần 1-2 vào `dev`*.
* **TV2:** Kế hoạch chuyến đi (*Trip, Stop, Lịch trình, Routing*) — *Đã merge Tuần 1-2 vào `dev`*.
* **TV3:** Cộng tác chuyến đi (*Invitation, Membership, Proposal*) — *Đã hoàn thành trên branch `feature/trip-collaboration-tv3`*.
* **TV4:** Nội dung & Cộng đồng (*Review, Submission, Story, Route, Post*) — *Chưa triển khai*.
* **TV5 (Phân hệ hiện tại):** Quản trị, Kiểm duyệt, Báo cáo & Điều phối chất lượng (*Moderation, Report, Admin, Audit Log, Test Plan toàn dự án*).

---

## 2. Mục tiêu bàn giao Tuần 1 của TV5

Trong Tuần 1, mục tiêu cốt lõi của TV5 là **thiết kế nghiệp vụ, chuẩn hóa mô hình dữ liệu ERD, ma trận phân quyền RBAC và xây dựng Master Test Plan** cho toàn bộ dự án mà không can thiệp làm vỡ code hay migration hiện có của các thành viên khác.

Kết quả bàn giao gồm 7 tài liệu chi tiết:

| STT | Tài liệu | Nội dung chính | Trạng thái |
|:---:|:---|:---|:---:|
| 1 | [Quy trình nghiệp vụ Moderation, Report & Audit Log](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/moderation-report-workflow.md) | Đặc tả luồng xử lý kiểm duyệt nội dung, xử lý báo cáo vi phạm, quy tắc deduplicate và kiến trúc nhật ký kiểm toán bất biến (Audit Log). | Hoàn thành |
| 2 | [Thiết kế mô hình dữ liệu ERD & DDL Proposal](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/erd.md) | Sơ đồ Mermaid ERD, chi tiết bảng `reports`, `moderation_actions`, `audit_logs`, phân tích polymorphic keys và DDL dự thảo cho Tuần 2. | Hoàn thành |
| 3 | [Ma trận phân quyền RBAC](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/authorization-matrix.md) | Phân quyền chi tiết cho `GUEST`, `USER`, `CONTENT_ADMIN`, `SYSTEM_ADMIN` và cơ chế bảo vệ tầng Backend. | Hoàn thành |
| 4 | [Master Test Plan toàn dự án](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/test_plan.md) | Kế hoạch kiểm thử toàn diện 5 phân hệ (TV1–TV5), môi trường test, quy trình bug tracking, entry/exit criteria và phân bổ trách nhiệm QA. | Hoàn thành |
| 5 | [Bộ Test Case chi tiết](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/test_cases.md) | 36 test case định danh chi tiết (Moderation, Report, Audit Log, Security RBAC, Integration luồng xuyên suốt TV1–TV5). | Hoàn thành |
| 6 | [Checklist tích hợp liên phân hệ TV1–TV4](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/integration-checklist.md) | Giao ước kỹ thuật, điểm tiếp xúc (touchpoints), sự kiện kích hoạt (event hooks) và ràng buộc toàn vẹn giữa TV5 và TV1..TV4. | Hoàn thành |
| 7 | [Phân tích hiện trạng & Điểm cần làm rõ (Gap Analysis)](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/week1-gap-analysis.md) | Đánh giá hiện trạng source code, xung đột migration giữa các nhánh, danh sách câu hỏi cần nhóm thống nhất trước Tuần 2. | Hoàn thành |

---

## 3. Hướng dẫn đọc và phối hợp nhóm

* **Dành cho TV1 (Auth & Place):**
  * Đọc [Ma trận phân quyền](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/authorization-matrix.md) để đối chiếu `Role` enum và quy trình khóa tài khoản (`chk_users_status`).
  * Đọc [Quy trình nghiệp vụ](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/moderation-report-workflow.md) mục 1.4 để nắm cách Admin ẩn/khôi phục Place ảnh hưởng tới Search & Discover.
* **Dành cho TV2 & TV3 (Trip & Collaboration):**
  * Đọc [Checklist tích hợp](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/integration-checklist.md) mục 2 & 3 để hiểu cách xử lý khi một `Place` trong `trip_stops` bị ẩn/archived.
  * Xem [Gap Analysis](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/week1-gap-analysis.md) về xung đột số thứ tự Flyway migration của nhánh TV3.
* **Dành cho TV4 (Review, Submission, Post):**
  * Đọc kỹ [Quy trình nghiệp vụ Moderation](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/moderation-report-workflow.md) để đồng bộ trạng thái `Submission` (`PENDING_APPROVAL` -> `APPROVED`/`REJECTED`) và `Review` (`PUBLISHED` -> `HIDDEN`).
  * Đọc [ERD & DDL Proposal](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/erd.md) để thống nhất khóa ngoại đa hình `subject_type` + `subject_id`.
* **Dành cho Giảng viên / Project Manager:**
  * Xem [Master Test Plan](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/test_plan.md) và [Bộ Test Case](file:///d:/DuAn/DANANG_VISTA/docs/tv5-week1/test_cases.md) để kiểm tra mức độ bao phủ kiểm thử và kế hoạch nghiệm thu 8 tuần.

---

## 4. Kế hoạch Tuần 2 chuẩn bị triển khai

Dựa trên thiết kế Tuần 1 đã được chuẩn hóa, công việc Tuần 2 của TV5 sẽ gồm:
1. **Flyway Migration:** Triển khai file migration chính thức (dự kiến `V7__create_moderation_report_audit_tables.sql` sau khi chốt thứ tự với nhóm).
2. **Khung Entity & Repository Backend:** Xây dựng package `com.citypulse.moderation`, `com.citypulse.report`, `com.citypulse.audit`.
3. **Dữ liệu mẫu (Seed Data):** Tạo dữ liệu mẫu các báo cáo vi phạm, hàng đợi kiểm duyệt địa điểm/đánh giá để phục vụ kiểm thử và demo.
4. **Khung giao diện Admin cơ bản (Frontend):** Dựng layout Admin, Sidebar, trang Dashboard tổng quan chất lượng dữ liệu.

# CITYPULSE

Nền tảng khám phá Đà Nẵng và lập chuyến đi cùng bạn bè theo đặc tả CITYPULSE v2.3.

## Stack
- Frontend: React + TypeScript + Vite
- Backend: Java 21 + Spring Boot + Maven
- Database: PostgreSQL (local hoặc Neon Free)
- Persistence: Spring Data JPA / Hibernate
- Migration: Flyway
- Test: JUnit 5 / Spring Boot Test; Vitest

## Cấu trúc
- `frontend/`: giao diện web và các feature theo nghiệp vụ.
- `backend/`: REST API, bảo mật, nghiệp vụ và migration database.
- `docs/`: tài liệu đặc tả, ERD, API và nghiệm thu.

## Chạy local

Cần JDK 21, Maven 3.9+, Node.js và npm. Backend cần PostgreSQL 17; nếu đã cài Docker Desktop, có thể khởi động PostgreSQL local bằng:

```powershell
docker compose --profile local-db up -d postgres
```

Mở hai terminal PowerShell riêng:

**Terminal 1 — backend**

```powershell
Set-Location backend
mvn spring-boot:run
```

Mặc định backend kết nối PostgreSQL ở `localhost:5432` với database/user `citypulse`. Nếu dùng DB khác, đặt biến môi trường trong terminal này trước khi chạy:

```powershell
$env:DB_URL = "jdbc:postgresql://<host>:<port>/<database>"
$env:DB_USERNAME = "<username>"
$env:DB_PASSWORD = "<password>"
mvn spring-boot:run
```

`backend/.env.example` chỉ là file mẫu; Spring Boot không tự nạp file `.env`. Backend health check: `http://localhost:8080/api/v1/health`.

**Terminal 2 — frontend**

```powershell
Set-Location frontend
npm ci
npm run dev
```

Frontend dùng API mặc định tại `http://localhost:8080/api/v1`. Nếu backend dùng địa chỉ khác, cấu hình `VITE_API_BASE_URL` trong `frontend/.env.local` trước khi khởi động Vite.

Đăng ký tại `/register`, đăng nhập tại `/login`, sau đó mở `/trips` để tạo chuyến đi và gửi lời mời. API lưu phiên dạng Bearer token, mật khẩu được mã hóa BCrypt; database tự chạy các migration Flyway khi backend khởi động. Các tài khoản đã có trong database từ trước khi thêm xác thực cần đăng ký tài khoản mới hoặc được cấp lại mật khẩu.

Gửi lời mời yêu cầu SMTP hợp lệ. Đặt `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` và `MAIL_FROM` trong môi trường chạy backend; không commit thông tin này vào repository. Với Gmail, dùng App Password. Nếu SMTP chưa cấu hình hoặc không gửi được, API trả lỗi và không tạo lời mời.

Các use case ngoài UC-01, xác thực nâng cao (như xác minh email/đặt lại mật khẩu) và quản lý thành viên chuyến đi chưa được triển khai.

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

## Chạy nhanh
1. Cài JDK 21+, Node.js LTS và npm.
2. Cấu hình `backend/.env.example` thành biến môi trường thực tế hoặc chỉnh `backend/src/main/resources/application.yml`.
3. Cấu hình `frontend/.env.example` nếu backend không chạy ở `http://localhost:8080/api/v1`.
4. Chạy backend: `cd backend` rồi `mvn spring-boot:run`.
5. Chạy frontend: terminal khác, `cd frontend`, `npm install`, `npm run dev`.

Các endpoint mẫu chỉ là khung khởi tạo. Xác thực, phân quyền, 26 Use Case và các tích hợp ngoài chưa được triển khai hoàn chỉnh.

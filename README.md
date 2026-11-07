# Online Auction System
Bài tập lớn Lập trình nâng cao: hệ thống đấu giá trực tuyến (Java, JavaFX, Socket, Maven).

## Cấu trúc
- `common/`: model, enum, exception, giao thức dùng chung
- `server/`: server socket, service, DAO, database
- `client/`: ứng dụng JavaFX

## Yêu cầu
- JDK 25
- Maven 3.9+

## Build
    mvn clean verify

## Quy trình làm việc
- Không push trực tiếp lên `main`
- Mỗi việc một nhánh `feature/...`, xong thì tạo Pull Request
- Commit theo Conventional Commits (`feat:`, `fix:`, `test:`, `docs:`, `chore:`)

## Thành viên

| Họ tên | GitHub | Phụ trách |
|---|---|---|
| Nguyễn Huy Hoàng | gitlee272 |  |
| Nguyễn Văn Trường | Truongk7hehe |  |
| Nguyễn Hải Đăng | Dangk97 |  |
| Phạm Minh Khôi | khoip5786-lang |  |
=======
![CI](https://github.com/gitlee272/Online-Auction-System/actions/workflows/ci.yml/badge.svg)

Bài tập lớn môn Lập trình nâng cao: hệ thống đấu giá trực tuyến theo kiến trúc Client–Server (Java, JavaFX, Socket, Maven).

## Giới thiệu

Hệ thống cho phép nhiều người dùng cùng cạnh tranh giá để mua một sản phẩm trong một khoảng thời gian xác định. Có 3 vai trò:

- **Bidder**: xem phiên đấu giá, đặt giá.
- **Seller**: đăng và quản lý sản phẩm, tạo phiên đấu giá.
- **Admin**: quản lý người dùng và phiên đấu giá.

Trạng thái phiên đấu giá: `OPEN → RUNNING → FINISHED → PAID / CANCELED`.

## Công nghệ

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 25 |
| Build | Maven (multi-module) |
| Giao diện | JavaFX + FXML |
| Giao tiếp | Socket TCP, dữ liệu JSON (Gson) |
| Cơ sở dữ liệu | SQLite |
| Kiểm thử | JUnit 5 |
| CI/CD | GitHub Actions |

## Cấu trúc dự án

```
Online-Auction-System/
├── pom.xml            (parent POM, quản lý 3 module)
├── common/            (model, enum, exception, giao thức dùng chung)
├── server/            (socket server, service, DAO, database)
├── client/            (ứng dụng JavaFX)
├── docs/              (tài liệu, giao thức Client-Server)
└── .github/workflows/ (CI)
```

## Yêu cầu môi trường

- JDK 25
- Maven 3.9+
- Git

## Build và chạy

Luôn chạy lệnh Maven ở **thư mục gốc** của dự án.

```bash
# Build toàn bộ và chạy test
mvn clean verify

# Chỉ chạy test
mvn test

# Build riêng client (kèm module common)
mvn -pl client -am clean verify
```

Hướng dẫn chạy server và client sẽ được bổ sung khi hai phần này hoàn thiện.

## Quy trình làm việc

- Không push trực tiếp lên `main`. Mỗi việc một nhánh, xong thì tạo Pull Request.
- Pull Request cần CI xanh và ít nhất 1 người trong nhóm duyệt (Approve) mới được merge.
- Đặt tên nhánh: `feature/...`, `fix/...`, `docs/...`, `test/...`, `chore/...`.
- Commit theo [Conventional Commits](https://www.conventionalcommits.org/):

| Tiền tố | Ý nghĩa |
|---|---|
| `feat:` | Thêm tính năng |
| `fix:` | Sửa lỗi |
| `test:` | Thêm hoặc sửa test |
| `docs:` | Tài liệu |
| `refactor:` | Sửa cấu trúc code, không đổi chức năng |
| `chore:` | Việc vặt (cấu hình, build) |

- Quy ước code: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
- Review chéo: Hoàng ↔ Khôi, Trường ↔ Đăng.

## Thành viên và phân công

| Họ tên | GitHub | Phụ trách |
|---|---|---|
| Nguyễn Huy Hoàng | gitlee272 | Giao diện JavaFX (client), CI/CD, biểu đồ giá realtime |
| Phạm Minh Khôi | khoip5786-lang | Server, Socket, giao thức Client–Server, realtime Observer |
| Nguyễn Văn Trường | Truongk7hehe | Model (Entity, User, Item, Auction), exception, unit test |
| Nguyễn Hải Đăng | Dangk97 | Service, DAO, database, concurrency, auto-bid, anti-sniping |

## Tiến độ

- [x] Khởi tạo Maven multi-module
- [x] Thiết lập CI (GitHub Actions)
- [ ] Giao thức Client–Server (`docs/protocol.md`)
- [ ] Model và exception
- [ ] Service, DAO, database
- [ ] Socket server
- [ ] Giao diện JavaFX
- [ ] Đặt giá đồng thời an toàn và realtime update
- [ ] Tính năng nâng cao (auto-bid, anti-sniping, biểu đồ giá)
- [ ] Báo cáo và slide

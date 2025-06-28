<pre>
  ____             _                  _   ____                  _
 | __ )  __ _  ___| | _____ _ __   __| | / ___|  ___ _ ____   _(_) ___ ___
 |  _ \ / _` |/ __| |/ / _ \ '_ \ / _` | \___ \ / _ \ '__\ \ / / |/ __/ _ \
 | |_) | (_| | (__|   <  __/ | | | (_| |  ___) |  __/ |   \ V /| | (_|  __/
 |____/ \__,_|\___|_|\_\___|_| |_|\__,_| |____/ \___|_|    \_/ |_|\___\___|
</pre>

## 1. Giới thiệu

**Human Resource Management**: là một ứng dụng Spring Boot backend phục vụ cho mục đích quản lý nhân sự. Hệ thống cung cấp các API RESTful hỗ trợ xác thực người dùng bằng JWT, thao tác dữ liệu nhân sự với JPA, tích hợp Swagger để kiểm thử, Redis để caching, và hỗ trợ đa ngôn ngữ với i18n. Ứng dụng được đóng gói bằng Maven, sẵn sàng để triển khai bằng Docker hoặc tích hợp CI/CD.

---

## 2. Cấu trúc dự án

<pre>
src/
├── main/
│   ├── java/com/vti/lab7/
│   │   ├── config/              # Cấu hình Spring Security, CORS, Swagger
│   │   │   └── jwt/             # Xử lý JWT token
│   │   ├── constant/            # Các hằng số dùng chung
│   │   ├── controller/          # RestController cho các API
│   │   ├── dto/                 # Định nghĩa DTO cho request/response
│   │   │   ├── mapper/          # Chuyển đổi giữa DTO và entity
│   │   │   ├── request/         # Các lớp request DTO
│   │   │   └── response/        # Các lớp response DTO
│   │   ├── exception/           # Xử lý exception toàn cục
│   │   │   └── custom/          # Custom exception cụ thể
│   │   ├── model/               # Các entity ánh xạ database
│   │   ├── repository/          # JPA repository
│   │   ├── service/             # Interface logic nghiệp vụ
│   │   │   └── impl/            # Triển khai business logic
│   │   ├── specification/       # Build điều kiện truy vấn động
│   │   ├── util/                # Các hàm tiện ích dùng chung
│   │   └── Lab7Application.java # Class main để khởi chạy ứng dụng
│   └── resources/
│       ├── i18n/                      # Thư mục chứa các file đa ngôn ngữ
│       │   ├── messages.properties
│       │   ├── messages_vi.properties
│       │   └── messages_ja.properties
│       └── application.properties     # Cấu hình ứng dụng Spring Boot
</pre>

---

## 3. Chức năng chính

### 👤 Người dùng

- Đăng ký, đăng nhập, quên mật khẩu
- Cập nhật thông tin cá nhân, đổi mật khẩu
- Xem sản phẩm, chi tiết sản phẩm
- Thêm sản phẩm vào giỏ hàng
- Đặt hàng và theo dõi lịch sử mua hàng

### 🛠️ Quản trị viên

- Quản lý sản phẩm (thêm, sửa, xóa)
- Quản lý đơn hàng
- Quản lý người dùng
- Xem dashboard thống kê

---

## 4. Hướng dẫn chạy dự án

### Cách 1: Dùng Docker Compose (khuyên dùng)

#### Bước 1: Clone dự án

```
git clone https://github.com/quanglam04/human-management-system.git
```

#### Bước 2: Di chuyển vào thư mục dự án

```
cd food-store
```

#### Bước 3: Cấu hình theo file hướng dẫn sau

```
spring.application.name=human-management-system

spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=

# Hibernate properties
spring.jpa.database-platform=
spring.jpa.hibernate.ddl-auto=
spring.jpa.show-sql=

#Jwt
jwt.secret:
jwt.access.expiration_time:
jwt.refresh.expiration_time:
```

#### Bước 4: Chạy Redis sử dụng Docker

```
docker run -d --name redis-container -p 6379:6379 redis
```

#### Bước 5: Chạy ứng dụng bằng Maven

```
./mvnw spring-boot:run
```

### Kết quả sau khi chạy thành công, truy cập:

```
http://localhost:8080/swagger-ui.html
```

## 5. Công nghệ sử dụng

- Java (17)
- Spring Boot
- Spring Security (JWT)
- Spring Session
- Spring Data JPA
- SpringDOC OpenAPI
- Redis + Letture
- PostgreSQL
- Maven
- Docker & Docker Compose

### Yêu cầu về version

- **Docker:** version `20.10+`
- **Docker Compose:** version `1.29+`
- **Java:** version `17+`
- **Spring Boot:** version `3.3.4+`
- **Spring Security:** version `6.3.1+`
- **Maven:** `3.8+`
- **PostgreSQL:** version `12.0+`
- **Redis:** version `12.0+`

---

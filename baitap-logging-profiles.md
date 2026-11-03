# Bài tập: Quản lý cấp độ Log bằng Logback và Spring Profiles

**Ngày thực hiện:** 03/11/2026
**Dự án:** `order-app` (Spring Boot 3.3, Java 17, PostgreSQL)
**Môi trường chạy:** AWS EC2 (Ubuntu), PostgreSQL chạy bằng Docker

---

## 1. Cấu hình gốc và vấn đề

### Tình huống

Môi trường Dev cần log chi tiết (`DEBUG`) để tìm lỗi. Tuy nhiên trên Production, hệ thống liên tục bị tràn ổ cứng do log quá nhiều. File `application.yml` đang cố định cấp độ log cho **tất cả** các môi trường:

```yaml
logging:
  level:
    root: DEBUG
    com.example: TRACE
```

### Vấn đề

- `root: DEBUG` khiến mọi thư viện (Spring, Hibernate, Tomcat, HikariCP...) đều ghi log chi tiết.
- `com.example: TRACE` ghi thêm log mức thấp nhất của code ứng dụng.
- Cấu hình nằm trong `application.yml` chung nên Production cũng bị áp dụng, sinh ra hơn 10GB log mỗi ngày, có nguy cơ đầy ổ cứng và gây downtime.
- Khi chạy thử ứng dụng với cấu hình gốc, lượng dòng `DEBUG`/`TRACE` in ra console rất lớn, đúng như mô tả của đề bài.

---

## 2. Yêu cầu và cách thức thực hiện

### Yêu cầu

1. Tạo 2 file cấu hình riêng: `application-dev.yml` và `application-prod.yml`.
2. Xóa cấu hình log trong `application.yml` chung.
3. `application-dev.yml`: root log là `DEBUG`.
4. `application-prod.yml`: root log là `INFO`, và giới hạn `com.example` ở mức `INFO`.
5. Chạy ứng dụng với profile `prod` để kiểm chứng.

### Cách thực hiện

**Bước 1. Dựng môi trường.** Chạy PostgreSQL bằng Docker. Port 5432 trên EC2 đã bị container `quickbite-db` (project khác) chiếm, nên map port host sang **5433**:

```yaml
# docker-compose.yml
ports:
  - "5433:5432"
```

```bash
docker compose up -d
```

**Bước 2. Xóa cấu hình log khỏi `application.yml`.** Chỉ giữ lại phần dùng chung (server, datasource, JPA):

```yaml
server:
  port: 8080

spring:
  application:
    name: order-app
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:orderdb}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: update
```

**Bước 3. Tạo `application-dev.yml`:**

```yaml
logging:
  level:
    root: DEBUG
```

**Bước 4. Tạo `application-prod.yml`:**

```yaml
logging:
  level:
    root: INFO
    com.example: INFO
```

**Bước 5. Build và chạy với profile `prod`:**

```bash
mvn -DskipTests package
DB_PORT=5433 java -jar target/order-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Lưu ý khi thực hiện

- Nếu port 8080 báo bị chiếm, nguyên nhân thường là một bản app chạy trước đó chưa tắt. Tìm và tắt bằng `sudo ss -ltnp | grep 8080`, sau đó `kill <PID>`.
- Phải dùng `DB_PORT=5433`. Dùng nhầm 5432 sẽ kết nối vào database của project khác.

---

## 3. Kết quả: khi tạo đơn hàng

Chạy với profile `prod`, gọi API tạo đơn hàng:

```bash
curl -s -X POST localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customerName":"Nguyen Van A","customerPhone":"0901234567","items":[{"productId":1,"quantity":1}]}'
```

Log in ra console sau khi tạo đơn:

```text
2026-11-03T14:26:52.405Z  INFO 4877 --- [order-app] [nio-8080-exec-3] c.example.orderapp.service.OrderService  : Order created id=2 customer=Nguyen Van A total=1200000.00
```

### Nhận xét

| | Trước khi sửa | Sau khi sửa (profile `prod`) |
|---|---|---|
| Mức log root | `DEBUG` | `INFO` |
| Mức log `com.example` | `TRACE` | `INFO` |
| Log khi tạo đơn | Rất nhiều dòng `DEBUG`/`TRACE` (request, kiểm tra tồn kho, truy vấn...) | Chỉ còn dòng `INFO` xác nhận đơn hàng được tạo |

- Console chỉ hiển thị log từ mức `INFO` trở lên, không còn log `DEBUG` hay `TRACE`, đúng kết quả mong muốn của đề bài.
- Các log chi tiết trong code (`log.debug`, `log.trace` ở `OrderService`, `OrderController`) vẫn còn nguyên, nhưng không được in ra ở Production. Khi cần điều tra lỗi, chạy với profile `dev`.
- Lượng log giảm mạnh nên nguy cơ đầy ổ cứng được giải quyết ở mức cấu hình.

### Hướng mở rộng

- Dùng `logback-spring.xml` với thẻ `<springProfile>` để tách hẳn cấu hình theo môi trường.
- Dùng `RollingFileAppender` với giới hạn kích thước/số ngày lưu để file log trên Production không tăng vô hạn.

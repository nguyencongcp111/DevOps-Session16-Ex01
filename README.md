# order-app

API đặt hàng đơn giản (Spring Boot 3.3 + PostgreSQL) dùng cho bài tập Logback / Spring Profiles.

> `application.yml` đang để log `root: DEBUG` và `com.example: TRACE` cố định cho mọi môi trường.
> Đây là tình huống của bài tập, bạn sẽ sửa nó ở bước sau.

## Yêu cầu
- Java 17, Maven 3.9+
- Docker (để chạy PostgreSQL)

## Chạy

```bash
# 1. Khởi động PostgreSQL
docker compose up -d

# 2. Chạy ứng dụng
mvn spring-boot:run
```

Biến môi trường (có giá trị mặc định): `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.
Khi khởi động, app tự tạo bảng và seed 3 sản phẩm.

## Thử bằng curl

```bash
# Xem sản phẩm
curl -s localhost:8080/api/products

# Thêm sản phẩm
curl -s -X POST localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Tai nghe","price":500000,"stock":30}'

# Đặt hàng
curl -s -X POST localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{
        "customerName": "Nguyen Van A",
        "customerPhone": "0901234567",
        "items": [
          {"productId": 1, "quantity": 2},
          {"productId": 2, "quantity": 1}
        ]
      }'

# Xem đơn hàng
curl -s localhost:8080/api/orders/1
curl -s localhost:8080/api/orders
```

Lỗi thường gặp: `404` (không có sản phẩm), `409` (hết hàng), `400` (dữ liệu không hợp lệ).

## Đóng gói để chạy trên EC2

```bash
mvn -DskipTests package
java -jar target/order-app-0.0.1-SNAPSHOT.jar
```

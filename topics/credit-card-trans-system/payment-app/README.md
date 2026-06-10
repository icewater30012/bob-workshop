# Payment Processing Application

A mock credit card payment processing system built with Spring Boot and React.

## Features

### Backend (Java 11 + Spring Boot 2.7)
- **REST API Endpoints:**
  - `POST /api/payments/authorize` - Authorize a card transaction
  - `POST /api/payments/capture` - Capture an authorized transaction
  - `POST /api/payments/refund` - Refund a captured transaction
  - `GET /api/payments/{id}` - Get transaction status
  - `GET /api/payments/history` - List recent transactions
  - `POST /admin/cache/clear` - Clear local cache
  - `GET /actuator/health` - Health check endpoint
  - `GET /actuator/prometheus` - Metrics endpoint

- **Technical Features:**
  - In-memory H2 database
  - Caffeine local cache for transaction lookups
  - Realistic processing delays (200-500ms)
  - Random transaction declines (10% failure rate)
  - Realistic response codes (approved, declined, insufficient funds, expired card)

### Frontend (React)
- Payment form with card number, expiry, CVV, and amount fields
- Transaction history dashboard
- Status badges: Authorized (yellow), Captured (green), Declined (red), Refunded (gray)
- Capture and refund actions for eligible transactions

### Test Card Numbers
- **Visa:** 4263970000005262
- **MasterCard:** 5425230000004415
- **Amex:** 374101000000608

## Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Run Locally

```bash
cd payment-app
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

Open your browser and navigate to `http://localhost:8080` to access the payment interface.

## API Examples

### Authorize Payment
```bash
curl -X POST http://localhost:8080/api/payments/authorize \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4263970000005262",
    "expiry": "12/25",
    "cvv": "123",
    "amount": 100.00
  }'
```

### Capture Payment
```bash
curl -X POST http://localhost:8080/api/payments/capture \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "YOUR_TRANSACTION_ID",
    "amount": 100.00
  }'
```

### Refund Payment
```bash
curl -X POST http://localhost:8080/api/payments/refund \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "YOUR_TRANSACTION_ID",
    "amount": 100.00
  }'
```

### Get Transaction
```bash
curl http://localhost:8080/api/payments/{transactionId}
```

### Get Transaction History
```bash
curl http://localhost:8080/api/payments/history
```

### Clear Cache
```bash
curl -X POST http://localhost:8080/admin/cache/clear
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## Docker Build

Build the Docker image using the multi-stage Dockerfile:

```bash
cd payment-app
docker build -t payment-app:latest .
```

Run the container:

```bash
docker run -p 8080:8080 payment-app:latest
```

## Kubernetes Deployment

The `k8s/deployment.yaml` file contains a complete Kubernetes manifest with placeholder values.

### Deploy to OpenShift/Kubernetes

1. Set environment variables:
```bash
export NAMESPACE=your-namespace
export IMAGE_TAG=latest
```

2. Replace placeholders and apply:
```bash
envsubst < k8s/deployment.yaml | kubectl apply -f -
```

Or use `sed` for replacement:
```bash
sed -e "s/\${NAMESPACE}/your-namespace/g" \
    -e "s/\${IMAGE_TAG}/latest/g" \
    k8s/deployment.yaml | kubectl apply -f -
```

### Kubernetes Resources Included

- **Namespace:** Application namespace
- **Deployment:** 3 replicas with resource limits and health probes
- **Service:** ClusterIP service exposing port 8080
- **Route:** OpenShift route with TLS edge termination
- **PodDisruptionBudget:** Max 1 unavailable pod during disruptions
- **HorizontalPodAutoscaler:** Auto-scaling from 3 to 10 replicas based on CPU/memory
- **ServiceAccount:** Dedicated service account
- **ConfigMap:** Application configuration

## Project Structure

```
payment-app/
├── src/
│   └── main/
│       ├── java/com/demo/payment/
│       │   ├── controller/          # REST controllers
│       │   │   ├── PaymentController.java
│       │   │   └── AdminController.java
│       │   ├── service/             # Business logic
│       │   │   └── PaymentService.java
│       │   ├── model/               # Entities and DTOs
│       │   │   ├── Transaction.java
│       │   │   ├── TransactionStatus.java
│       │   │   ├── TransactionRepository.java
│       │   │   ├── TransactionResponse.java
│       │   │   ├── AuthorizeRequest.java
│       │   │   ├── CaptureRequest.java
│       │   │   └── RefundRequest.java
│       │   ├── config/              # Configuration
│       │   │   └── CacheConfig.java
│       │   └── PaymentApplication.java
│       └── resources/
│           ├── static/              # React frontend
│           │   └── index.html
│           └── application.properties
├── pom.xml
├── Dockerfile                       # Multi-stage Docker build
├── k8s/
│   └── deployment.yaml             # Kubernetes manifests
└── README.md
```

## Technical Details

### Database Schema
The application uses an in-memory H2 database with the following schema:

**transactions** table:
- `id` (VARCHAR, Primary Key) - UUID
- `card_number` (VARCHAR) - Card number
- `card_type` (VARCHAR) - VISA, MASTERCARD, AMEX
- `amount` (DECIMAL) - Transaction amount
- `status` (VARCHAR) - AUTHORIZED, CAPTURED, DECLINED, REFUNDED
- `response_code` (VARCHAR) - Response code
- `response_message` (VARCHAR) - Response message
- `created_at` (TIMESTAMP) - Creation timestamp
- `updated_at` (TIMESTAMP) - Update timestamp
- `authorized_amount` (DECIMAL) - Authorized amount
- `captured_amount` (DECIMAL) - Captured amount
- `refunded_amount` (DECIMAL) - Refunded amount

### Caching
- Uses Caffeine cache for transaction lookups
- Cache name: `transactions`
- Max size: 1000 entries
- TTL: 10 minutes
- Cache can be cleared via `/admin/cache/clear` endpoint

### Transaction Flow
1. **Authorize:** Validates card and reserves funds (10% random decline rate)
2. **Capture:** Captures the authorized amount (can be partial)
3. **Refund:** Refunds the captured amount (can be partial)

### Response Codes
- `APPROVED` - Transaction approved
- `DECLINED` - Transaction declined by issuer
- `INSUFFICIENT_FUNDS` - Insufficient funds
- `EXPIRED_CARD` - Card has expired
- `CAPTURED` - Transaction captured
- `REFUNDED` - Transaction refunded

## Lessons Learnt

### 1. GenerationType.UUID requires JPA 3.x
Spring Boot 2.7 ships with JPA 2.x (Hibernate 5), which does not support `GenerationType.UUID`. 

**Solution:** Use `UUID.randomUUID()` in a `@PrePersist` hook instead:
```java
@PrePersist
protected void onCreate() {
    if (id == null) {
        id = UUID.randomUUID().toString();
    }
    createdAt = LocalDateTime.now();
}
```

### 2. Lombok Annotation Processing
Lombok requires annotation processing to be active at compile time. When the compiler's annotation processor isn't wired correctly, all generated methods (getters, setters, builder, log) are invisible to javac.

**Solution:** For a zero-friction demo, this project uses explicit getters/setters and hand-written builders instead of Lombok, making it more portable and avoiding the dependency entirely.

## Monitoring

### Actuator Endpoints
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Prometheus: `http://localhost:8080/actuator/prometheus`

### Prometheus Metrics
The application exposes Prometheus metrics at `/actuator/prometheus` for monitoring:
- JVM metrics (memory, threads, GC)
- HTTP request metrics
- Cache statistics
- Custom business metrics

## Security Considerations

⚠️ **This is a demo application. Do NOT use in production without:**
- Proper authentication and authorization
- PCI DSS compliance measures
- Encrypted card data storage
- Secure communication (HTTPS)
- Input validation and sanitization
- Rate limiting
- Audit logging
- Proper error handling without exposing sensitive data

## License

This is a demo application for educational purposes.

# **Payment Service Platform (PSP)**

A reactive payment service platform that processes payment requests, validates card details, and routes transactions to appropriate acquirers based on routing logic. This platform simulates real-world payment processing with high scalability and fault tolerance.

---

## **Features**

1. **Payment Processing API**
   - Accepts payment details such as card number, expiry date, CVV, amount, currency, and merchant ID.
   - Initializes transactions with a `PENDING` status.

2. **Card Validation**
   - Uses the **Luhn algorithm** to validate the card number.
   - Ensures all fields are correctly formatted.

3. **Acquirer Routing**
   - Routes transactions to **Acquirer A** or **Acquirer B** based on the BIN (Bank Identification Number).
   - Logic:
      - **Even sum of BIN digits** → Route to **Acquirer A**.
      - **Odd sum of BIN digits** → Route to **Acquirer B**.

4. **Transaction Simulation**
   - Simulates transaction approval or denial based on the card number's last digit:
      - **Even last digit** → `APPROVED`.
      - **Odd last digit** → `DENIED`.

5. **Timeout Handling**
   - If the acquirer does not respond within a specified timeout, the transaction is marked as `DENIED`.

6. **Reactive Programming**
   - Built using **Spring WebFlux** for high concurrency and non-blocking operations.

7. **Containerization**
   - The application can be containerized using Docker for easy deployment.

---

## **Getting Started**

### Prerequisites

- **Java**: JDK 17 or later.
- **Maven**: 3.8.1 or later.
- **Docker**: Installed and running.

---

### **1. Clone the Repository**
```bash
git clone https://github.com/your-repo/payment-service-platform.git
cd payment-service-platform
```

---

### **2. Build and Run Without Docker**

#### **Build the Project**
```bash
mvn clean install
```

#### **Run the Application**
```bash
mvn spring-boot:run
```

The service will be available at: `http://localhost:8080/api/payments`.

---

## **Running the Application with Docker**

The application can be built and run as a Docker container.

### **1. Dockerfile**
The project includes the following `Dockerfile` to containerize the application:

```dockerfile
# Stage 1: Build the application
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### **2. Build the Docker Image**
Run the following command in the project directory (where the `Dockerfile` is located):
```bash
docker build -t payment-service-platform .
```

### **3. Run the Docker Container**
Run the container with the following command:
```bash
docker run -p 8080:8080 payment-service-platform
```

This maps the container's port `8080` to your local machine's port `8080`. The application will be accessible at:
```
http://localhost:8080
```

### **4. Verify Swagger and API**
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## **Testing the API**

### **Process Payment Endpoint**

**Endpoint**:
```
POST /api/payments
```

**Request Body**:
```json
{
   "cardNumber": "4137894711755904",
   "expiryDate": "12/25",
   "cvv": "123",
   "amount": 100.00,
   "currency": "USD",
   "merchantId": "merchant123"
}
```

**Response (Success)**:
```json
{
   "transactionId": "e2a3e8e9-1234-4567-890a-abcdef123456",
   "status": "APPROVED",
   "message": "Processed by Acquirer A"
}
```

**Response (Timeout)**:
```json
{
   "transactionId": "e2a3e8e9-1234-4567-890a-abcdef123456",
   "status": "DENIED",
   "message": "No response from Acquirer B"
}
```

---

## **Project Structure**

```
src/main/java/com/payment/gateway/
├── controller/                       # API controllers
|   |── GlobalExceptionHandler.java   # Exception handler for validation errors
│   └── PaymentController.java        # Handles incoming payment requests

├── model/                            # Data models
│   ├── PaymentRequest.java           # Payment request structure
│   ├── PaymentResponse.java          # Payment response structure
│   └── Status.java                   # Enum for transaction statuses
├── service/                          # Business logic
│   └── PaymentService.java           # Core payment processing logic
├── util/                             # Utility classes
│   └── CardUtils.java                # Luhn algorithm and routing logic
```

---

## **Testing**

### **Run Unit Tests**
```bash
mvn test
```

### **Test Coverage**

1. **Validation Tests**:
   - Valid card numbers pass the Luhn algorithm.
   - Invalid card numbers fail validation.

2. **Routing Tests**:
   - Transactions are routed correctly to Acquirer A or B based on BIN.

3. **Timeout Handling**:
   - Transactions exceeding the timeout duration are marked as `DENIED`.

4. **API Integration Tests**:
   - End-to-end tests for the `/api/payments` endpoint.

---

## **Future Enhancements**

1. **Persistent Storage**:
   - Integrate with a database (e.g., PostgreSQL or MongoDB) for transaction persistence.

2. **External Acquirer Integration**:
   - Replace mock acquirers with real payment gateways (e.g., Stripe, PayPal).

3. **Enhanced Security**:
   - Add **encryption** for sensitive card data.
   - Implement **JWT authentication** for API access.

4. **Scalability**:
   - Deploy the service using **Docker** and **Kubernetes** for production scalability.

5. **Logging**
   - Add logging in Controller and Service level, mask sensitive data
---

## **Contributors**

- **Dragan Ilievski** - Project Author

---


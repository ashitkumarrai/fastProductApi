# **FastProductAPI**

## **Overview**
**FastProductAPI** is a high-performance RESTful API designed for managing product data with seamless CRUD operations. Built with Java and Spring Boot, it leverages MySQL for relational database management and Redis for caching frequently accessed data. To achieve optimal performance, the system implements parallel processing using `ExecutorService`, ensuring scalability, low latency, and high throughput.

## **Features**
1. **Efficient CRUD Operations**
   - Create, read, update, and delete product data stored in MySQL with thread-safe operations.
2. **Caching with Redis**
   - Frequently accessed data is cached to reduce database load and improve response times.
   - Cache invalidation ensures data consistency during updates or deletions.
3. **Parallel Processing**
   - Multithreading implemented using `ExecutorService` for concurrent handling of database queries and cache updates.
   - Minimizes latency and maximizes throughput.
4. **Error Handling & Resilience**
   - Graceful handling of connection failures to MySQL or Redis with retry mechanisms and fallback strategies.
   - Exception logging using SLF4J for robust debugging.
5. **Performance Metrics**
   - Logs execution time for all operations, including database reads/writes, cache updates, and cache hit/miss rates.
   - Provides insights into system performance and scalability.

---

## **Architecture**
The system is designed to ensure modularity, scalability, and reliability:
- **Spring Boot**: Framework for creating RESTful APIs and managing the application lifecycle.
- **MySQL**: Relational database for persistent product data storage.
- **Redis**: In-memory data store for caching and quick retrieval of frequently used data.
- **ExecutorService**: Java concurrency library for parallel task execution, ensuring high performance.
- **SLF4J**: Logging framework for tracking operations and errors.

---
### **Prerequisites**
- Java 17 or later
- Maven
- MySQL (Installed and running locally or remotely)
- Redis (Installed and running locally or remotely)

## **Setup Instructions**
### MySQL and Redis Setup using Docker

This guide provides concise instructions to set up MySQL and Redis using Docker. Follow these steps for a seamless setup.

---

### MySQL Setup

### Step 1: Start MySQL Container
```bash
docker run --env=MYSQL_ROOT_PASSWORD=123456789 --name mysqldb -p 3306:3306 -d mysql
```
- **`MYSQL_ROOT_PASSWORD`**: Sets root password (`123456789`).
- **`mysqldb`**: Container name.
- **`3306:3306`**: Maps MySQL's default port.

### Step 2: Connect to MySQL CLI
```bash
docker exec -it mysqldb mysql -u root -p
```
- Enter the root password when prompted.

---

### Redis Setup

### Step 1: Start Redis Container
```bash
docker run -p 6379:6379 --name redis-cache -d redis
```
- **`redis-cache`**: Container name.
- **`6379:6379`**: Maps Redis's default port.

### Step 2: Access Redis CLI
```bash
docker exec -it redis-cache redis-cli
```

---

### Verification

### MySQL
1. Run:
   ```sql
   SHOW DATABASES;
   ```
2. Verify default databases are listed.

### Redis
1. Run:
   ```bash
   PING
   ```
2. Verify response:
   ```
   PONG
   ```

---

### Notes
- Stop containers:
  ```bash
  docker stop mysqldb redis-cache
  ```
- Remove containers:
  ```bash
  docker rm mysqldb redis-cache
  ```

By following these steps, MySQL and Redis should be successfully running on your system.




### **Steps to Run**
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd FastProductAPI


Run the application:
```bash
mvn clean install
mvn spring-boot:run

```


Access the API at: http://localhost:8080/api/products

## Performance Highlights
### **Parallel Processing**
- Bulk operations (read/write) are processed in parallel using `ExecutorService`.
- Logs execution time for each task, enabling performance analysis.

### **Cache Efficiency**
- Cache hit/miss rates are logged for better understanding of caching performance.
- Redis reduces MySQL query load significantly.

### **Scalability**
- Designed to handle concurrent requests with minimal contention and resource blocking.

---

## **Contributors**
- **Ashit Kumar Rai** - Developer






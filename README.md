# **Fast Product API Documentation**

## **Overview**
**FastProductAPI** is a high-performance RESTful API designed for managing product data with seamless CRUD operations. Built with Java and Spring Boot, it leverages MySQL for relational database management and Redis for caching frequently accessed data. To achieve optimal performance, the system implements parallel processing using `ExecutorService`, ensuring scalability, low latency, and high throughput.



This API provides endpoints to manage products, including fetching, uploading, updating, and deleting products. It supports bulk operations in parallel or sequential modes. The API is optimized for performance, utilizing **Redis** cache for faster retrieval of data and **MySQL** as the database for persistent storage.

## **Features**

1. **Efficient CRUD Operations**
   - Perform Create, Read, Update, and Delete (CRUD) operations on product data stored in MySQL. All database interactions are optimized for thread-safety, ensuring seamless performance in concurrent environments.

2. **Caching with Redis**
   - Frequently accessed product data is cached using Redis, significantly reducing the load on the MySQL database and improving response times.
   - Cache hits and misses are logged for performance tracking, and cache invalidation is managed during updates or deletions to ensure data consistency.

3. **Parallel Processing**
   - Bulk operations (e.g., upload, update, delete) support both parallel and sequential processing, enabled via `ExecutorService` to handle multiple tasks concurrently.
   - This approach minimizes latency, maximizes throughput, and is ideal for processing large datasets efficiently.

4. **Retry Mechanism with Backoff**
   - Transient failures in external systems (such as MySQL or Redis) are handled with a retry mechanism employing exponential backoff.
   - This ensures system resilience in the event of temporary issues and prevents overwhelming the external services.

5. **Graceful Shutdown**
   - The API supports graceful shutdown of both the embedded Tomcat server and `ExecutorService`, ensuring ongoing tasks are completed before termination.
   - This feature prevents abrupt failures, maintaining system stability during shutdown and restarts.

6. **Error Handling & Resilience**
   - Robust error handling for scenarios like database failures, missing products, and Redis cache issues.
   - The API employs fallback strategies, retries, and centralized exception handling to ensure meaningful error messages and status codes, making it easier to debug and recover from failures.

7. **Performance Metrics & Logging**
   - Performance is continuously monitored by logging execution times for all key operations, such as database reads/writes, cache updates, and cache hit/miss rates.
   - This data is invaluable for tracking system performance, identifying bottlenecks, and ensuring the API is scalable and responsive under load.
   - Additionally, all REST method executions are intercepted using Spring AOP, enabling detailed logging of execution times for monitoring and optimization.

8. **Enhanced System Monitoring**
   - Cache hits and misses are tracked and logged to evaluate the effectiveness of the caching strategy.
   - Performance metrics for each API endpoint are logged, providing insights into system scalability and helping in tuning the application for optimal performance.

9. **Advanced Logging Configuration with Logback**
   - **Log Rotation**: Logs are rolled over daily, with an index appended to the file name (e.g., `fastProductApplication.2025-01-14.1.gz`). This allows better management and archiving of log files.
   - **Max File Size**: A single log file is capped at 10MB, ensuring that log files do not grow too large and are manageable.
   - **Total Size Cap**: The total size of all log files combined (compressed or uncompressed) is capped at 1GB, preventing log files from consuming excessive disk space.
   - **Log Retention**: Logs are kept for a maximum of 7 days, after which older logs are cleaned up to save storage space. This ensures that only recent logs are stored.
   - **Cleanup on Startup**: When the application starts, old log files are cleaned up automatically, ensuring no unnecessary files remain from previous runs.
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




## API Endpoints
### API BASEPATH : http://localhost:8080/api/v1

---

### 1. **Fetch Products by IDs**
- **Endpoint**: `POST /products/getProductByIds`
- **Description**: Fetch products based on their IDs, utilizing Redis cache for faster data retrieval. If the products are not found in the cache, it falls back to MySQL. Cache hits and misses are logged for performance tracking.
- **Request Body**:
    ```json
   {
    "ids": [3,4]
   }
    ```
- **Query Parameter**:
    - `isParallel`: `true` for parallel processing (default), `false` for sequential.
- **Response**:
    - **200 OK**: Products fetched successfully.
        - Example:
        ```json
       { "product": [
            {
            "id": 3,
            "name": "prd_2",
            "description": "this is product 2",
            "price": 950.75,
            "stock": 95,
            "createdAt": "2025-01-16 00:20:31",
            "lastUpdatedAt": "2025-01-16 00:20:31"
            },
           {
            "id": 4,
            "name": "prd_1",
            "description": "this is product 1",
            "price": 900.25,
            "stock": 90,
            "createdAt": "2025-01-16 00:20:31",
            "lastUpdatedAt": "2025-01-16 00:20:31"
           }
                ],
          "responseStatus": {
              "message": "SUCCESS",
              "status": "200 OK"
                }
        }

        ```

---

### 2. **Upload Products**
- **Endpoint**: `POST /products/uploadProducts`
- **Description**: Upload a list of products into MySQL. If Redis cache is utilized, it is updated as well. This operation supports retry with backoff in case of failure.
- **Request Body**:
    ```json
    {
    "products": [
        {
            "name": "prd_1",
            "description": "this is product 1",
            "price": "900.25",
            "stock": "90"
        },
        {
            "name": "prd_2",
            "description": "this is product 2",
            "price": "950.75",
            "stock": "95"
        }
    ]
      }

    ```
- **Query Parameter**:
    - `isParallel`: `true` for parallel processing (default), `false` for sequential.
- **Response**:
    - **201 Created**: Products uploaded successfully.
        - Example:
        ```json
           { "products": [ {
        
            "id": 2,
            "name": "prd_2",
            "description": "this is product 2",
            "price": 950.75,
            "stock": 95,
            "createdAt": "2025-01-16 00:07:40",
            "lastUpdatedAt": "2025-01-16 00:07:40"
           },
           {
            "id": 1,
            "name": "prd_1",
            "description": "this is product 1",
            "price": 900.25,
            "stock": 90,
            "createdAt": "2025-01-16 00:07:40",
            "lastUpdatedAt": "2025-01-16 00:07:40"
           }
       ],
          "responseStatus": {
        "message": "SUCCESS",
        "status": "201 CREATED"
             }
         }
        
---

### 3. **Update Products**
- **Endpoint**: `PUT /products/updateProductsByIds`
- **Description**: Update a list of products. Changes are propagated to both MySQL and Redis cache. The operation supports retry mechanisms for resilience.
- **Request Body**:
    ```json
    {
    "products": [
        {   
            "id": 1,
            "name": "prd_1",
            "description": "this is updated product 1",
            "price": "101.99",
            "stock": "40"
        },
        {
            "id": 2,
            "name": "prd_2",
            "description": "this is updated product 2",
            "price": "101.99",
            "stock": "20"
        }
    ]
      }


    ```
- **Query Parameter**:
    - `isParallel`: `true` for parallel processing (default), `false` for sequential.
- **Response**:
    - **201 Created**: Products updated successfully.
        - Example:
        ```json

        { "products":  [{
        
            "id": 1,
            "name": "prd_1",
            "description": "this is updated product 1",
            "price": 101.99,
            "stock": 40,
            "createdAt": "2025-01-16 00:07:40",
            "lastUpdatedAt": "2025-01-16 00:16:04"
        },
        {
            "id": 2,
            "name": "prd_2",
            "description": "this is updated product 2",
            "price": 101.99,
            "stock": 20,
            "createdAt": "2025-01-16 00:07:40",
            "lastUpdatedAt": "2025-01-16 00:16:04"
        }],
       "responseStatus": {
           "message": "SUCCESS",
        "status": "201 CREATED"
       }
      }

---

### 4. **Delete Products by IDs**
- **Endpoint**: `DELETE /products/deleteProductsByIds`
- **Description**: Delete products by their IDs from both MySQL and Redis cache. This process supports retry with backoff in case of temporary failures.
- **Request Body**:
    ```json
    {
      "ids": [1, 2]
    }
    ```
- **Query Parameter**:
    - `isParallel`: `true` for parallel processing (default), `false` for sequential.
- **Response**:
    - **200 OK**: Products deleted successfully.
        - Example:
        ```json
       {"deletedProductIds": [1,2],
          "responseStatus": {
           "message": "SUCCESS",
           "status": "200 OK"
          }
      }
---

### **Important Notes**:
- **Parallel Processing**: By default, bulk operations (e.g., upload, update, delete) are performed in parallel. To process them sequentially, use the `isParallel=false` query parameter.
- **Retry with Backoff**: Operations like uploading and updating products are backed by a retry mechanism in case of transient failures, with exponential backoff.
- **Caching**: Redis cache is utilized for fetching and updating products. If the product data is not found in the cache, it falls back to fetching data from MySQL.


---


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






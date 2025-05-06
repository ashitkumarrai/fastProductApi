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
### Postman collection json file: [fastProductApis.postman_collection.json](https://github.com/ashitkumarrai/fastProductApi/blob/master/fastProductApis.postman_collection.json)
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
    { 
        "product": [
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

- **206 PARTIAL_CONTENT**: Some products were not found.
    - Example:
    ```json
    {
        "product": [
            {
                "id": 1,
                "name": "prd_1",
                "description": "this is product 1",
                "price": 101.99,
                "stock": 40,
                "createdAt": "2025-01-16 12:40:25",
                "lastUpdatedAt": "2025-01-16 12:40:31"
            },
            {
                "id": 2,
                "name": "prd_2",
                "description": "this is product 2",
                "price": 101.99,
                "stock": 20,
                "createdAt": "2025-01-16 12:40:25",
                "lastUpdatedAt": "2025-01-16 12:40:31"
            }
        ],
        "responseStatus": {
            "message": "Products not found for the following IDs: 3",
            "status": "206 PARTIAL_CONTENT"
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
        {
            "deletedProductIds": [1, 2],
            "responseStatus": {
                "message": "SUCCESS",
                "status": "200 OK"
            }
        }
        ```
- **206 PARTIAL_CONTENT**: Some products were not found for deletion.
        - Example:
  ```json
        {
            "deletedProductIds": [
                1,
                2
            ],
            "responseStatus": {
                "message": "Products not found for the following IDs to delete: 3",
                "status": "206 PARTIAL_CONTENT"
            }
        }
  ```

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

 

# **FastProductAPI Performance Report**

## **1. Product Creation**

### **Sequential Upload**
- **Time Taken**: 2.28 seconds
- **Description**: 
  - Uploaded 20 products sequentially to MySQL.
  - Each product was also added to the Redis cache sequentially.
- **Outcome**: All 20 products successfully stored in both MySQL and Redis.
![image](https://github.com/user-attachments/assets/a388d3ab-4a54-47ad-9d36-0ad442c6af72)
![image](https://github.com/user-attachments/assets/0eca7080-80ac-45de-8134-286a8ce1eaca)



### **Parallel Upload**
- **Time Taken**: 531 ms
- **Description**: 
  - Uploaded 20 products in parallel using `ExecutorService`.
  - The products were stored in MySQL and added to Redis in parallel.
- **Outcome**: All 20 products successfully stored in both MySQL and Redis.
- **Comparison**: Parallel upload was significantly faster (531 ms vs. 2.28 seconds).
![image](https://github.com/user-attachments/assets/1c71a68c-dd0a-403b-b37e-8f98caeb3b17)
![image](https://github.com/user-attachments/assets/32fc68ef-718d-48e6-b0fb-2f845f7918f6)



---

## **2. Fetching Products by IDs**

### **Sequential Fetch**
- **Time Taken**: 466 ms
- **Description**: 
  - Fetched 20 product details sequentially by their IDs.
  - All requests were cache hits (data directly fetched from Redis, no MySQL calls).
- **Outcome**: Successfully fetched all 20 products from Redis cache.
![image](https://github.com/user-attachments/assets/a301ca79-6531-42f3-8795-5860cf50174a)


### **Parallel Fetch**
- **Time Taken**: 22 ms
- **Description**: 
  - Fetched 20 product details in parallel using `ExecutorService`.
  - All requests were cache hits, leveraging Redis for faster data retrieval.
- **Outcome**: Successfully fetched all 20 products from Redis cache.
- **Comparison**: Parallel fetch was drastically faster (22 ms vs. 466 ms).
![image](https://github.com/user-attachments/assets/780d1c37-6110-4703-be55-fe6d3fdff2ef)

---

## **3. Updating Products by IDs**

### **Sequential Update**
- **Time Taken**: 796 ms
- **Description**: 
  - Updated 20 products sequentially by their IDs.
  - Updated data was propagated to both MySQL and Redis cache during the API call.
- **Outcome**: All 20 products successfully updated and stored in Redis cache.
![image](https://github.com/user-attachments/assets/552af277-50c3-4b3a-9328-7848ab6eaad2)

### **Parallel Update**
- **Time Taken**: 180 ms
- **Description**: 
  - Updated 20 products in parallel using `ExecutorService`.
  - Changes were propagated to both MySQL and Redis cache.
- **Outcome**: All 20 products successfully updated and stored in Redis cache.
- **Comparison**: Parallel update was significantly faster (180 ms vs. 796 ms).
![image](https://github.com/user-attachments/assets/735da3d0-fad2-47df-a52e-a003c699d193)


---

## **4. Deleting Products by IDs**

### **Sequential Deletion**
- **Time Taken**: 570 ms
- **Description**: 
  - Deleted 20 products sequentially by their IDs.
  - The products were removed from both MySQL and Redis cache.
- **Outcome**: All 20 products successfully deleted.
![image](https://github.com/user-attachments/assets/c1cd2af7-08e5-453c-b9bf-6d1270a1c12d)
![image](https://github.com/user-attachments/assets/4ef70bdc-8da1-4566-a186-cf499609c240)


### **Parallel Deletion**
- **Time Taken**: 118 ms
- **Description**: 
  - Deleted 20 products in parallel using `ExecutorService`.
  - The products were removed from both MySQL and Redis cache.
- **Outcome**: All 20 products successfully deleted.
- **Comparison**: Parallel deletion was significantly faster (118 ms vs. 570 ms).
![image](https://github.com/user-attachments/assets/2f7801f9-e124-417a-92e2-2563d2a2a01f)

---

## **Summary of Observations**

| **Operation**        | **Sequential Time** | **Parallel Time** | **Performance Improvement (Parallel)** |
|-----------------------|---------------------|-------------------|-----------------------------------------|
| **Create (20 products)** | 2.28 seconds       | 531 ms            | ~4.3x faster                           |
| **Fetch (20 IDs)**      | 466 ms             | 22 ms             | ~21.2x faster                          |
| **Update (20 products)**| 796 ms             | 180 ms            | ~4.4x faster                           |
| **Delete (20 products)**| 570 ms             | 118 ms            | ~4.8x faster                           |

---

## **Conclusion**
- Parallel processing using `ExecutorService` drastically reduces the time taken for bulk operations, showcasing the API's scalability and efficiency under concurrent workloads.
- Redis caching ensures quick data retrieval, as evident in the significantly reduced time for fetch operations.
- Updating and deleting operations also demonstrate the efficient propagation of changes to both MySQL and Redis cache in parallel mode.

---

### cache logs:
1) during create/update cache creating entry logs
  
![image](https://github.com/user-attachments/assets/a9b996f4-bc55-4cef-a02a-bd8d80c651a1)

2)during cache hits: 
 
![image](https://github.com/user-attachments/assets/92351029-7b9e-4475-9315-68d47428ae7b)

3)during cache miss: 
![image](https://github.com/user-attachments/assets/b404cc94-9dca-4913-99f1-84c75d92a087)


### Retry Fallback Error Mechanism on MySQL DB & Its Error Handling

As per configuration:

- **Maximum Pool Size (`maximum-pool-size: 10`)**: Limits active DB connections to 10. If all are in use, further requests must wait.
- **Connection Timeout (`connection-timeout: 10000`)**: If a DB connection can't be established within 10 seconds, it times out.
- **Retry Settings**:
    - **Max Retries (`max-retries: 3`)**: Tries 3 times if a DB connection fails.
    - **Initial Interval (`initial-interval: 2000`)**: Waits 2 seconds before retrying the connection.

#### `@Retryable` Annotation:

- **Max Attempts (`maxAttempts = 3`)**: Will retry the operation up to 3 times on failure.
- **Backoff Delay (`@Backoff(delay = 2000)`)**: Waits 2 seconds between retries.

### What Happens:
1. If DB connection fails:
    1. Waits up to 10 seconds to connect.
    2. Retries 3 times, each with a 2-second delay.
    3. This can lead to almost **35 seconds** of total delay due to retries.

#### Screenshot of the tested working scenario: 
![image](https://github.com/user-attachments/assets/b875f966-c54d-439f-a8e2-77130050e30e)
![image](https://github.com/user-attachments/assets/048e542b-1942-41b5-8e0e-3929ed63eff1)





### Retry Fallback Error Mechanism on Redis Cache and Its Error Handling:

- The method is annotated with `@Retryable`, which retries the operation up to 3 times (`maxAttempts = 3`) if a `CustomException` or `RedisException` occurs.
- There is a 2-second delay (`@Backoff(delay = 2000)`) between each retry attempt.
- The method computes the cache key `'89'` using `@Cacheable` and tries to retrieve or store data in Redis.
- When Redis is unavailable, the method throws a `RedisException`, triggering the retry mechanism.
- The first attempt fails, followed by 3 retries, each delayed by 2 seconds.
- The retries (total of 6 seconds delay) contribute to the total API response time of near **6 seconds**.
- If all retries fail, the method either throws an exception or returns a fallback value, depending on the implementation.
- The **5.38-second** response time results from retry delays, time to process the request, and Redis connection failure.

#### Screenshot of the tested working scenario: 
![image](https://github.com/user-attachments/assets/b667ec6e-5f8a-430c-b2cf-75161a01b43f)

![image](https://github.com/user-attachments/assets/63b583c7-8bb2-4d52-aee5-c196cdc3d93a)

 
### Cache Hits and Misses Logging and Performance of Each REST Method

- **Cache Logging**: Tracks cache hits and misses to monitor cache effectiveness.
- **Performance Logging**: Logs the execution time for each REST method in `@RestController`-annotated classes using Spring AOP.
- **AOP Interception**: Intercepts all methods in classes annotated with `@RestController` and logs the time taken for execution.
- **Graceful Shutdown**:
  - Ensures that the **Executor Service** completes tasks before shutdown and bydefault Gracefully shuts down the **Tomcat Embedded Spring Boot Server**, allowing in-progress HTTP requests to finish.
**screenshot for working sceanrio:** 

![image](https://github.com/user-attachments/assets/85f1c1e9-3d0a-4b67-b373-811b5cd93201)





 


### Test cases output screenshot for all components:
 ![image](https://github.com/user-attachments/assets/6d15d901-8136-4707-8f10-dc0a89466dca)

 



## **Author & Developer**
- **Ashit Kumar Rai** 

import pandas as pd
import requests
import json
from time import sleep

def process_excel_to_api(excel_file, api_url, headers=None, batch_size=1, delay=0):
    """
    Read an Excel file and send each row as JSON payload to an API endpoint.
    
    Parameters:
        excel_file (str): Path to the Excel file
        api_url (str): API endpoint URL
        headers (dict): Optional headers for the API request
        batch_size (int): Number of rows to process at once (default 1)
        delay (float): Delay in seconds between API calls (default 0)
    """
    try:
        # Read the Excel file
        df = pd.read_excel(excel_file)
        
        # Convert NaN values to None for proper JSON serialization
        df = df.where(pd.notnull(df), None)
        
        # Process rows in batches
        for i in range(0, len(df), batch_size):
            batch = df.iloc[i:i+batch_size]
            
            # Process each row in the batch
            for _, row in batch.iterrows():
                # Convert row to dictionary
                payload = row.to_dict()
                
                try:
                    # Make API request
                    response = requests.post(
                        api_url,
                        json=payload,
                        headers=headers or {}
                    )
                    
                    # Print results
                    print(f"Row {_ + 1}: Status Code: {response.status_code}")
                    print(f"Payload: {json.dumps(payload, indent=2)}")
                    if response.text:
                        print(f"Response: {response.text}")
                    print("-" * 50)
                    
                    # Respect API rate limits
                    if delay > 0:
                        sleep(delay)
                        
                except requests.exceptions.RequestException as e:
                    print(f"Error processing row {_ + 1}: {str(e)}")
                    continue
                    
    except Exception as e:
        print(f"Error reading Excel file: {str(e)}")

# Example usage
if __name__ == "__main__":
    # Configuration
    EXCEL_FILE = "data.xlsx"  # Path to your Excel file
    API_URL = "https://api.example.com/endpoint"  # Your API endpoint
    HEADERS = {
        "Content-Type": "application/json",
        "Authorization": "Bearer your_token_here"  # If authentication is needed
    }
    
    # Process the Excel file
    process_excel_to_api(
        excel_file=EXCEL_FILE,
        api_url=API_URL,
        headers=HEADERS,
        batch_size=1,  # Process one row at a time
        delay=0.5  # Half-second delay between API calls
    )






service: questionnaire-api

frameworkVersion: '3'

provider:
  name: aws
  runtime: nodejs18.x
  region: us-east-1
  environment:
    QUESTIONS_TABLE: ${self:custom.questionsTable}
    ANSWERS_TABLE: ${self:custom.answersTable}

custom:
  questionsTable: questionnaires-${sls:stage}
  answersTable: questionnaire-answers-${sls:stage}

functions:
  storeQuestionnaires:
    handler: src/handlers/storeQuestionnaires.handler
    events:
      - http:
          path: /questionnaires
          method: post
          cors: true

resources:
  Resources:
    QuestionsTable:
      Type: AWS::DynamoDB::Table
      Properties:
        TableName: ${self:custom.questionsTable}
        AttributeDefinitions:
          - AttributeName: classification
            AttributeType: S
          - AttributeName: questionId
            AttributeType: S
        KeySchema:
          - AttributeName: classification
            KeyType: HASH
          - AttributeName: questionId
            KeyType: RANGE
        BillingMode: PAY_PER_REQUEST

    AnswersTable:
      Type: AWS::DynamoDB::Table
      Properties:
        TableName: ${self:custom.answersTable}
        AttributeDefinitions:
          - AttributeName: userId
            AttributeType: S
          - AttributeName: answerId
            AttributeType: S
        KeySchema:
          - AttributeName: userId
            KeyType: HASH
          - AttributeName: answerId
            KeyType: RANGE
        BillingMode: PAY_PER_REQUEST


const AWS = require('aws-sdk');
const { v4: uuidv4 } = require('uuid');
const documentClient = new AWS.DynamoDB.DocumentClient();

module.exports.handler = async (event) => {
  try {
    // Validate input
    if (!event.body) {
      return {
        statusCode: 400,
        body: JSON.stringify({ error: 'Request body is missing' })
      };
    }

    const body = JSON.parse(event.body);
    const { classification, questions } = body;

    if (!classification || !questions || !Array.isArray(questions)) {
      return {
        statusCode: 400,
        body: JSON.stringify({ error: 'classification and questions array are required' })
      };
    }

    // Prepare questions for storage
    const putRequests = questions.map(question => ({
      PutRequest: {
        Item: {
          classification,
          questionId: question.questionId || `q_${uuidv4()}`,
          questionText: question.questionText,
          createdAt: new Date().toISOString(),
          modifiedAt: new Date().toISOString()
        }
      }
    }));

    // Batch write in chunks of 25 (DynamoDB limit)
    const batchSize = 25;
    for (let i = 0; i < putRequests.length; i += batchSize) {
      const batch = putRequests.slice(i, i + batchSize);
      await documentClient.batchWrite({
        RequestItems: {
          [process.env.QUESTIONS_TABLE]: batch
        }
      }).promise();
    }

    return {
      statusCode: 201,
      body: JSON.stringify({
        message: 'Questionnaires stored successfully',
        classification,
        count: questions.length
      }),
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
      }
    };
  } catch (error) {
    console.error('Error storing questionnaires:', error);
    return {
      statusCode: 500,
      body: JSON.stringify({ 
        error: 'Internal server error',
        details: error.message 
      })
    };
  }
};



{
  "classification": "customer_feedback",
  "questions": [
    {
      "questionText": "How would you rate our service?"
    },
    {
      "questionId": "feedback_comments",
      "questionText": "Any additional comments?"
    }
  ]
}



const { DynamoDBClient } = require('@aws-sdk/client-dynamodb');
const { DynamoDBDocumentClient, BatchWriteCommand } = require('@aws-sdk/lib-dynamodb');
const { v4: uuidv4 } = require('uuid');

// Create vanilla DynamoDB client
const client = new DynamoDBClient();

// Create DocumentClient wrapper
const docClient = DynamoDBDocumentClient.from(client);

module.exports.handler = async (event) => {
  try {
    // Validate input
    if (!event.body) {
      return formatResponse(400, { error: 'Request body is missing' });
    }

    const body = JSON.parse(event.body);
    const { classification, questions } = body;

    if (!classification || !questions || !Array.isArray(questions)) {
      return formatResponse(400, { error: 'classification and questions array are required' });
    }

    // Prepare batch write items (no manual marshalling needed)
    const putRequests = questions.map(question => ({
      PutRequest: {
        Item: {
          classification,
          questionId: question.questionId || `q_${uuidv4()}`,
          questionText: question.questionText,
          createdAt: new Date().toISOString(),
          modifiedAt: new Date().toISOString()
        }
      }
    }));

    // Process in batches of 25 (DynamoDB limit)
    const batchSize = 25;
    for (let i = 0; i < putRequests.length; i += batchSize) {
      const batch = putRequests.slice(i, i + batchSize);
      const command = new BatchWriteCommand({
        RequestItems: {
          [process.env.QUESTIONS_TABLE]: batch
        }
      });
      await docClient.send(command);
    }

    return formatResponse(201, {
      message: 'Questionnaires stored successfully',
      classification,
      count: questions.length
    });
  } catch (error) {
    console.error('Error storing questionnaires:', error);
    return formatResponse(500, { 
      error: 'Internal server error',
      details: error.message 
    });
  }
};

function formatResponse(statusCode, body) {
  return {
    statusCode,
    body: JSON.stringify(body),
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*'
    }
  };
}




@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private DataSource dataSource; // Your existing MS SQL datasource

    @Bean
    public Job printDocumentsJob() {
        return jobBuilderFactory.get("printDocumentsJob")
                .incrementer(new RunIdIncrementer())
                .start(printStep())
                .next(postProcessStep())
                .build();
    }

    @Bean
    public Step printStep() {
        return stepBuilderFactory.get("printStep")
                .<String, String>chunk(10)
                .reader(documentReader(null))
                .processor(documentProcessor())
                .writer(documentWriter())
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Step postProcessStep() {
        return stepBuilderFactory.get("postProcessStep")
                .tasklet(postProcessTasklet())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> documentReader(
            @Value("#{jobParameters['filePaths']}") List<String> filePaths) {
        return new IteratorItemReader<>(filePaths);
    }

    @Bean
    public ItemProcessor<String, String> documentProcessor() {
        return filePath -> {
            // Any preprocessing logic
            return filePath;
        };
    }

    @Bean
    public ItemWriter<String> documentWriter() {
        return items -> {
            for (String filePath : items) {
                // Your print logic
            }
        };
    }

    @Bean
    public Tasklet postProcessTasklet() {
        return (contribution, chunkContext) -> {
            // Access job context data
            int printedCount = (int) chunkContext.getStepContext()
                .getJobExecutionContext()
                .get("printedCount");
            
            // Your post-processing logic
            System.out.println("Total printed: " + printedCount);
            return RepeatStatus.FINISHED;
        };
    }
}

@Service
public class PrintJobLauncher {

    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job printDocumentsJob;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long launchPrintJob(List<String> filePaths) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addParameter("filePaths", 
                    new JobParameter<>(filePaths, List.class))
                .toJobParameters();
        
        JobExecution execution = jobLauncher.run(printDocumentsJob, jobParameters);
        
        // You can store additional metadata in your existing tables
        jdbcTemplate.update(
            "INSERT INTO print_jobs (job_id, start_time, document_count) VALUES (?, ?, ?)",
            execution.getJobId(),
            new Timestamp(System.currentTimeMillis()),
            filePaths.size()
        );
        
        return execution.getJobId();
    }
}






..
@Configuration
@EnableBatchProcessing
public class PrintJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private PrinterService printerService;

    @Bean
    public Job documentPrintingJob() {
        return jobBuilderFactory.get("documentPrintingJob")
                .start(printDocumentsStep())
                .next(postProcessingStep())  // Simple tasklet for post-job work
                .build();
    }

    @Bean
    public Step printDocumentsStep() {
        return stepBuilderFactory.get("printDocumentsStep")
                .<String, String>chunk(10)  // Process 10 documents at a time
                .reader(documentReader(null))
                .writer(documentWriter())
                .faultTolerant()
                .retryLimit(3)  // Retry up to 3 times
                .retry(Exception.class)  // Retry on any exception
                .build();
    }

    @Bean
    public Step postProcessingStep() {
        return stepBuilderFactory.get("postProcessingStep")
                .tasklet((contribution, chunkContext) -> {
                    // Your one-time post-job operations here
                    System.out.println("Executing post-print cleanup");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> documentReader(
            @Value("#{jobParameters['filePaths']}") List<String> filePaths) {
        return new IteratorItemReader<>(filePaths);
    }

    @Bean
    public ItemWriter<String> documentWriter() {
        return items -> {
            for (String filePath : items) {
                printerService.printDocument(filePath);
            }
        };
    }
}



@Service
public class PrinterService {
    
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void printDocument(String filePath) throws Exception {
        PrintService printer = PrintServiceLookup.lookupDefaultPrintService();
        if (printer == null) {
            throw new IllegalStateException("No printer available");
        }
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Copies(1));
            
            printer.createPrintJob().print(doc, attributes);
        }
    }
    
    @Recover
    public void recoverPrint(Exception e, String filePath) {
        System.err.println("Failed to print after 3 attempts: " + filePath);
        // Optionally log to database or error tracking system
    }
}







@RestController
@RequestMapping("/api/print")
public class PrintController {

    @Autowired
    private PrintJobLauncher printJobLauncher;

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> startPrintJob(
            @RequestBody List<String> filePaths) {
        try {
            Long jobId = printJobLauncher.launchPrintJob(filePaths);
            return ResponseEntity.ok(Map.of(
                "jobId", jobId,
                "message", "Print job started",
                "documentCount", filePaths.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to start job",
                "details", e.getMessage()
            ));
        }
    }
}






@Configuration
@EnableBatchProcessing
public class SimpleBatchConfig {

    @Bean
    public Job printJob(JobRepository jobRepository, Step printStep) {
        return new JobBuilder("simplePrintJob", jobRepository)
                .start(printStep)
                .build();
    }

    @Bean
    public Step printStep(JobRepository jobRepository, 
                        PlatformTransactionManager transactionManager,
                        ItemReader<String> reader,
                        ItemWriter<String> writer) {
        return new StepBuilder("printStep", jobRepository)
                .<String, String>chunk(10, transactionManager) // Process 10 at a time
                .reader(reader)
                .writer(writer)
                .faultTolerant()
                .skipLimit(100) // Max 100 failed documents
                .skip(Exception.class) // Skip on any error
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> documentReader(
            @Value("#{jobParameters['filePaths']}") List<String> filePaths) {
        return new IteratorItemReader<>(filePaths);
    }

    @Bean
    public ItemWriter<String> documentWriter(PrinterService printerService) {
        return items -> {
            for (String filePath : items) {
                printerService.printWithRetry(filePath);
            }
            // Any post-processing for this chunk can go here
            System.out.println("Processed chunk of " + items.size() + " documents");
        };
    }
}


@Service
public class SimpleJobLauncher {
    private final JobLauncher jobLauncher;
    private final Job printJob;

    public SimpleJobLauncher(JobLauncher jobLauncher, 
                           @Qualifier("printJob") Job printJob) {
        this.jobLauncher = jobLauncher;
        this.printJob = printJob;
    }

    public void launchJob(List<String> filePaths) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startTime", System.currentTimeMillis())
                .addParameter("filePaths", new JobParameter<>(filePaths, List.class))
                .toJobParameters();

        jobLauncher.run(printJob, jobParameters);
    }
}

import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchTableForceInitializer {

    @Bean
    public DataSourceInitializer batchTablesInitializer(DataSource dataSource, 
                                                     ResourceLoader resourceLoader) {
        // 1. Load the SQL Server specific schema script
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(resourceLoader.getResource(
            "classpath:org/springframework/batch/core/schema-sqlserver.sql"));
        
        // 2. Critical for SQL Server - handle batch separators
        populator.setSeparator("GO");
        populator.setContinueOnError(false);
        
        // 3. Force initialization
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        initializer.setEnabled(true);
        
        return initializer;
    }
}





USE [ETL]
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_JOB_INSTANCE' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.BATCH_JOB_INSTANCE (
        JOB_INSTANCE_ID BIGINT NOT NULL PRIMARY KEY,
        VERSION BIGINT NULL,
        JOB_NAME VARCHAR(100) NOT NULL,
        JOB_KEY VARCHAR(32) NOT NULL,
        CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
    )
    PRINT 'Created table BATCH_JOB_INSTANCE'
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_JOB_EXECUTION' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.BATCH_JOB_EXECUTION (
        JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
        VERSION BIGINT NULL,
        JOB_INSTANCE_ID BIGINT NOT NULL,
        CREATE_TIME DATETIME NOT NULL,
        START_TIME DATETIME DEFAULT NULL,
        END_TIME DATETIME DEFAULT NULL,
        STATUS VARCHAR(10) NULL,
        EXIT_CODE VARCHAR(2500) NULL,
        EXIT_MESSAGE VARCHAR(2500) NULL,
        LAST_UPDATED DATETIME NULL,
        CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID)
        REFERENCES dbo.BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
    )
    PRINT 'Created table BATCH_JOB_EXECUTION'
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_JOB_EXECUTION_PARAMS' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.BATCH_JOB_EXECUTION_PARAMS (
        JOB_EXECUTION_ID BIGINT NOT NULL,
        PARAMETER_NAME VARCHAR(100) NOT NULL,
        PARAMETER_TYPE VARCHAR(100) NOT NULL,
        PARAMETER_VALUE VARCHAR(2500) NULL,
        IDENTIFYING CHAR(1) NOT NULL,
        CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID)
        REFERENCES dbo.BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    )
    PRINT 'Created table BATCH_JOB_EXECUTION_PARAMS'
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_STEP_EXECUTION' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.BATCH_STEP_EXECUTION (
        STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
        VERSION BIGINT NOT NULL,
        STEP_NAME VARCHAR(100) NOT NULL,
        JOB_EXECUTION_ID BIGINT NOT NULL,
        CREATE_TIME DATETIME NOT NULL,
        START_TIME DATETIME DEFAULT NULL,
        END_TIME DATETIME DEFAULT NULL,
        STATUS VARCHAR(10) NULL,
        COMMIT_COUNT BIGINT NULL,
        READ_COUNT BIGINT NULL,
        FILTER_COUNT BIGINT NULL,
        WRITE_COUNT BIGINT NULL,
        READ_SKIP_COUNT BIGINT NULL,
        WRITE_SKIP_COUNT BIGINT NULL,
        PROCESS_SKIP_COUNT BIGINT NULL,
        ROLLBACK_COUNT BIGINT NULL,
        EXIT_CODE VARCHAR(2500) NULL,
        EXIT_MESSAGE VARCHAR(2500) NULL,
        LAST_UPDATED DATETIME NULL,
        CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID)
        REFERENCES dbo.BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    )
    PRINT 'Created table BATCH_STEP_EXECUTION'
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_STEP_EXECUTION_CONTEXT' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.BATCH_STEP_EXECUTION_CONTEXT (
        STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
        SHORT_CONTEXT VARCHAR(2500) NOT NULL,
        SERIALIZED_CONTEXT VARCHAR(MAX) NULL,
        CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID)
        REFERENCES dbo.BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
    )
    PRINT 'Created table BATCH_STEP_EXECUTION_CONTEXT'
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_JOB_EXECUTION_CONTEXT' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.BATCH_JOB_EXECUTION_CONTEXT (
        JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
        SHORT_CONTEXT VARCHAR(2500) NOT NULL,
        SERIALIZED_CONTEXT VARCHAR(MAX) NULL,
        CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID)
        REFERENCES dbo.BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
    )
    PRINT 'Created table BATCH_JOB_EXECUTION_CONTEXT'
END
GO

-- Create sequences if they don't exist
IF NOT EXISTS (SELECT * FROM sys.sequences WHERE name = 'BATCH_STEP_EXECUTION_SEQ')
BEGIN
    CREATE SEQUENCE dbo.BATCH_STEP_EXECUTION_SEQ 
    START WITH 0 MINVALUE 0 MAXVALUE 9223372036854775807 NO CACHE NO CYCLE
    PRINT 'Created sequence BATCH_STEP_EXECUTION_SEQ'
END
GO

IF NOT EXISTS (SELECT * FROM sys.sequences WHERE name = 'BATCH_JOB_EXECUTION_SEQ')
BEGIN
    CREATE SEQUENCE dbo.BATCH_JOB_EXECUTION_SEQ 
    START WITH 0 MINVALUE 0 MAXVALUE 9223372036854775807 NO CACHE NO CYCLE
    PRINT 'Created sequence BATCH_JOB_EXECUTION_SEQ'
END
GO

IF NOT EXISTS (SELECT * FROM sys.sequences WHERE name = 'BATCH_JOB_SEQ')
BEGIN
    CREATE SEQUENCE dbo.BATCH_JOB_SEQ 
    START WITH 0 MINVALUE 0 MAXVALUE 9223372036854775807 NO CACHE NO CYCLE
    PRINT 'Created sequence BATCH_JOB_SEQ'
END
GO








USE [ETL]
GO

-- Drop tables only if they exist
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_STEP_EXECUTION_CONTEXT' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    DROP TABLE dbo.BATCH_STEP_EXECUTION_CONTEXT
    PRINT 'Dropped table BATCH_STEP_EXECUTION_CONTEXT'
END
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_JOB_EXECUTION_CONTEXT' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    DROP TABLE dbo.BATCH_JOB_EXECUTION_CONTEXT
    PRINT 'Dropped table BATCH_JOB_EXECUTION_CONTEXT'
END
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_STEP_EXECUTION' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    DROP TABLE dbo.BATCH_STEP_EXECUTION
    PRINT 'Dropped table BATCH_STEP_EXECUTION'
END
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_JOB_EXECUTION_PARAMS' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    DROP TABLE dbo.BATCH_JOB_EXECUTION_PARAMS
    PRINT 'Dropped table BATCH_JOB_EXECUTION_PARAMS'
END
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_JOB_EXECUTION' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    DROP TABLE dbo.BATCH_JOB_EXECUTION
    PRINT 'Dropped table BATCH_JOB_EXECUTION'
END
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'BATCH_JOB_INSTANCE' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    DROP TABLE dbo.BATCH_JOB_INSTANCE
    PRINT 'Dropped table BATCH_JOB_INSTANCE'
END
GO

-- Drop sequences only if they exist
IF EXISTS (SELECT * FROM sys.sequences WHERE name = 'BATCH_STEP_EXECUTION_SEQ')
BEGIN
    DROP SEQUENCE dbo.BATCH_STEP_EXECUTION_SEQ
    PRINT 'Dropped sequence BATCH_STEP_EXECUTION_SEQ'
END
GO

IF EXISTS (SELECT * FROM sys.sequences WHERE name = 'BATCH_JOB_EXECUTION_SEQ')
BEGIN
    DROP SEQUENCE dbo.BATCH_JOB_EXECUTION_SEQ
    PRINT 'Dropped sequence BATCH_JOB_EXECUTION_SEQ'
END
GO

IF EXISTS (SELECT * FROM sys.sequences WHERE name = 'BATCH_JOB_SEQ')
BEGIN
    DROP SEQUENCE dbo.BATCH_JOB_SEQ
    PRINT 'Dropped sequence BATCH_JOB_SEQ'
END
GO

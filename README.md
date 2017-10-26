# UPass Renew Service

The background REST Service for renewing your UPass.

## Build
 ```shell
 gradle build
 ```
 
## Run the server
 ```shell
 gradle bootRun
 ```
 You will be able to find the server running at [https://localhost:8080]
 
## About the service

Here are the endpoints you can call:

### Get information about jobs, create jobs and shutdown the queue.

```
http://localhost:8080/api
http://localhost:8080/api/get
http://localhost:8080/api/renew
http://localhost:8080/api/shutdown
```

### Get API version

```
GET /api/

RESPONSE: HTTP 202 (Accepted)
{
    "version" : <current-api-version>
}
```

### Create a renew job

```
POST /api/renew
username: Username encrypted with public key
password: Password encrypted with public key

RESPONSE: HTTP 202 (Accepted)
{
    "jobId" : <UUID>,
    "status" : "RUNNING"
}
```

### Get Job Status

```
GET /api/get
id: jobId

RESPONSE: HTTP 202 (Accepted)
{
    "jobId" : <UUID>,
    "status" : "RUNNING"
}
```

### Shutdown

```
GET /api/shutdown

RESPONSE: HTTP 202 (Accepted)
```
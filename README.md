# UPass Renew Service

The background REST Service for renewing your UPass.
It is currently hosted [here](https://upass-renew.herokuapp.com/)

## How it works
Your credentials are not logged and are encrypted using a public key and decrypted by the server using the accompanying private key.


## Prerequisites
- Java & JDK
- [Process Variables](https://www.schrodinger.com/kb/1842):
    - `PRIVATE_KEY`: The private key for your application (keep this safe)
    - `SECRET`: The secret key used to shutdown the job pool 
 
## Build
 ```shell
 gradle build
 ``` 
 
## Run the server as is
 ```shell
 gradle bootRun
 ```
 You will be able to find the server running at [https://localhost:8080]
 
## API Specifications

**Get API Version**
----
  Get the API version number

* **URL**

  /api

* **Method:**

  `GET`

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{ version : 2 }`
 
**Submit a renew job**
----
  Create a new renew job

* **URL**

  /api/renew

* **Method:**

  `POST`
  
*  **URL Header Params**

   **Required:**
 
   `username=[string encrypted with public key]`
   `password=[string encrypted with public key]`
   `school=[string]`


* **Data Params**

  None

* **Success Response:**

  * **Code:** 202 ACCEPTED <br />
    **Content:** 
    `{"status": "RUNNNING", "jobId": "7045f3a0-dbc4-40cc-bef8-88523afeedc9"}`
    
  OR
  
  * **Code:** 208 ALREADY_REPORTED <br />
      **Content:** `{ response : "Job already submitted" }`

 
* **Error Response:**

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:** `{ error : "Username is encrypted incorrectly" }`

  OR

  * **Code:** 401 UNAUTHORIZED <br />
      **Content:** `{ error : "Password is encrypted incorrectly" }`

* **Notes:**
    
   The jobId returned can be used to retrieve the status of the job later

**Get job status**
----
  Get the job status

* **URL**

  /api/get

* **Method:**

  `GET`
  
*  **URL Header Params**

   **Required:**
 
   `id=[string]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 OK <br />
    **Content:** 
    `{"status": "NOTHING_TO_RENEW", "jobId": "7045f3a0-dbc4-40cc-bef8-88523afeedc9"}`
  
* **Error Response:**

  * **Code:** 400 BAD REQUEST <br />
    **Content:** `{ error : "No Job found" }`


**Shutdown job pool**
----
  Get the job status

* **URL**

  /api/shutdown

* **Method:**

  `GET`
  
*  **URL Header Params**

   **Required:**
 
   `key=[string]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 OK <br />
    **Content:** 
    `{"result": "Job pool shutdown"}`
  
* **Error Response:**

  * **Code:** 403 FORBIDDEN <br />
    **Content:** `{ error : "That is the incorrect key" }`
    
* **Notes:**
    
   This is just in case the server goes out of memory

Your school credentials are stored locally on your phone, and your password is encrypted before being saved, so only RenewPass can see it.



### Currently supported schools:
- UBC

More schools are coming soon!

## Contributing
Pull requests and issues are welcome.

## Shoutout
Huge shoutout to [Coffeeboys](https://github.com/Coffeeboys) for their [Android App](https://github.com/Coffeeboys/RenewPass)

## License
```
The MIT License (MIT)

Copyright (c) 2016 Coffeeboys

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
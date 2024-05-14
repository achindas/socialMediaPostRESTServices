# Social Media Posting Application - Spring Boot Microservices

## Table of Contents
* [Objectives](#objectives)
* [App Description](#app-description)
* [Technologies Used](#technologies-used)
* [Microservices Practices Built](#microservices-practices-built)
* [Acknowledgements](#acknowledgements)

## Objectives

**Microservices** provide a software development style that segregates large applications into smaller, stand-alobe units which communicate with each other through APIs. There are a set of practices which should be embedded while building microservices that make them secured, robust, scalable, user-friendly, flexible and easily deployable.

This simple application has been built using many of these best practices with the objective of creating a **ready reference and reminders to designers and developers** who would use this style in their organisations to build enterprise solutions.


## App Description

This application provides a simple set of APIs that allows to perform CRUD operations to manage Users and user-specific Social Media posts. The posts only have a single text-based description.


## Technologies Used

This is back-end application has been developed using Java Spring Boot and JPA framework at the backend along with H2 in-memory database. It has the option to switch over to other databases like mySQL through change of application property file.

## Microservices Practices Built

The following microservices development practices have been used in this application. Any designer, aiming to deploy end-to-end microservices based applications in their organisation, should consider these parctices as part of their design criteria.

The application has been deployed on local machine though it can be deployed on any cloud easily. The application functionalities including the various design practices can be demonstrated on request.

### Posting Service

This is the only and main restful microservice that manages the CRUD operations related to Users and their Posts management. It uses JPA to connect to H2 in-memory DB to store and retrieve data. The APIs can be accessed through API testing tools like Postman or Talend API Tester.

#### Usage

    Deploy: java -jar socialmediapost-web-services-0.0.1-SNAPSHOT.jar --spring.profiles.active=default

    API Prefix: http://<IP Address/Domain Name>:8080

    Get Users (GET Method): "/jpa/users"

    Get One User (GET Method): "/jpa/users/{id}"

    Create User (POST Method): "/jpa/users"

    Delete a User (DELETE Method): "/jpa/users/{id}"

    Create a Post (POST Method): "v1/jpa/users/{id}/posts"

### Return of Right Response Code

It's important to return appropriate response code from a API call to let the caller know the status of the API execution. Some of the standard response codes are: 200 - Success, 201 - Created, 401 - Unauthorised, 404 - Resource Not Found, 500 - Server Error etc.

#### Usage

    Create User (POST Method): "/users"

    Response Code: 201 - Created (if successful)

### Exception Handling

In case any API operation is unsuccesful, there should be proper structure to communicate the exception to caller. Override the ResponseEntityExceptionHandler class approrpiately with your own custom data structure.

#### Usage

    Get a non-existent user (GET Method): "/users/101"
    (UserId 101 is non-existent)

    Response Code: 404
    Response Data: 
        <ErrorDetails>
            <timestamp>2024-05-11T18:50:59.800706</timestamp>
            <message>id:101</message>
            <details>uri=/users/101</details>
        </ErrorDetails> 


### API Arguments Validation

Enable validation of the API arguments and return appropriate messages if proper arguments are not used.

#### Usage

    Create User with blank name and past birthdate: "/jpa/users"

    JSON Request Body:
    {
		"name": "",
		"birthDate": "2030-09-02"
    }

    Response Code: 400
    Response Data:
        <ErrorDetails>
            <timestamp>2024-05-14T08:06:42.218701</timestamp>
            <message>Total Errors:2 First Error:Name should have atleast 2 characters</message>
            <details>uri=/jpa/users</details>
        </ErrorDetails>

### Static & Dynamic Filtering

Filtering mechanism ensure that APIs don't return all fields, particularly confidential fields, as part of response.

Static filtering masks fields of a bean acorss all APIs, while Dynamic filtering allows filtering a bean for a specific API.

#### Usage

    Test filtering: "/filtering"

    API Response:

    {
        "field1": "value1",
        "field3": "value3"
    }

    "field2" has been masked in response body


### Circuit Breaking

Safeguard your microservices from getting overburdened with calls when the service is down or not responding. Enabling circuit-breaker functionalities help in responding back to callers with fallback messages without actually calling the API which is unstable.

#### Usage

    A) Enable Retry after an interval: 
    
    API: "/broken-api"

    Log extract:

    2024-05-14T13:54:10.951+05:30  INFO 84800 --- [posts-service] [nio-8080-exec-1] c.s.s.user.UserJpaResource               : Broken API call received
    2024-05-14T13:54:12.031+05:30  INFO 84800 --- [posts-service] [nio-8080-exec-1] c.s.s.user.UserJpaResource               : Broken API call received
    2024-05-14T13:54:13.564+05:30  INFO 84800 --- [posts-service] [nio-8080-exec-1] c.s.s.user.UserJpaResource               : Broken API call received
    2024-05-14T13:54:15.871+05:30  INFO 84800 --- [posts-service] [nio-8080-exec-1] c.s.s.user.UserJpaResource               : Broken API call received
    2024-05-14T13:54:19.287+05:30  INFO 84800 --- [posts-service] [nio-8080-exec-1] c.s.s.user.UserJpaResource               : Broken API call received

    Response Body:

    "System is down! Circuit Breaker activated. Please try after sometime"

    Note: 5 retries are executed before responding with exception.

    B) Enable Rate Limiter allowing 2 calls per 10 sec: 
    
    API: "/jpa/users/10002"

    Response Code: 500
    Response Body:
        <ErrorDetails>
            <timestamp>2024-05-14T13:07:00.534543</timestamp>
            <message>RateLimiter 'get-one-user-api' does not permit further calls</message>
            <details>uri=/jpa/users/10002</details>
        </ErrorDetails>

    C) Enable Circuit Breaker to disallow any calls after certain buildups: 

    Shutdown the Dtatabase to simulate API down condition
    
    Simulate simultaneous calls using below command:
        watch -n 0.1 curl http://localhost:8080/jpa/users/10002/posts


    Response Code: 500
    Response Body:
    {
        "timestamp": "2024-05-14T15:04:38.569085",
        "message": "CircuitBreaker 'get-user-posts-api' is OPEN and does not permit further calls",
        "details": "uri=/jpa/users/10002/posts"
    }

### API Versioning

APIs evolve over time. As new versions come up, the older versions should continue to work to cater to earlier user base. Plan for API versioning strategy from the beginning.

#### Usage

    A) Create Post through API version-1: "v1/jpa/users/10002/posts"

    Response as part of Response Body:
    {
        "description": "This is a new social media post"
    }

    B) Create Post through API version-2: "v2/jpa/users/10002/posts"

    Response through Location Header

        http://<IP Address>:8080/v2/jpa/users/10002/posts/2

### Internationalisation - i18n

Customise your messages for users from across the world through internationalisation of API responses.

#### Usage

    Get Good Morning Message in French (GET Method): "/hello-world-internationalized"
    Request Header:
        Accept: application/fr

    Response Code: 200
    Response Data:
        Bonjour

### Use Hateos for Action Links

Enrich your API responses to add information on further actions. Use JSON Hypertext Application Language (HAL) standard to add action links easily.

#### Usage

    Get a user (GET Method): "/jpa/users/1"

    API Response Body with Action Link:

    {
        "name": "Arnab",
        "birthDate": "1972-09-02",
        "_links":{
            "all-users":{
                "href": "http://localhost:8080/jpa/users"
                }
        }
    }   

### Content Negotiation

Make provision to return API response in either JSON or XML format.

#### Usage

    Get Users List (GET Method): "/users"
    Request Header:
        Accept: application/xml

    API Response (in XML format):
        <ArrayList>
            <item>
                <name>Adam</name>
                <birthDate>1994-04-15</birthDate>
            </item>
            <item>
                <name>Eve</name>
                <birthDate>1999-04-15</birthDate>
            </item>
        </ArrayList>

### API Documentation

Enable Open API-based Swagger documentation for your REST APIs to enable users to view and explore your API usage.

#### Usage

    View API documentation: "http://<IP Address>>:8080/api-docs"

    View details visually: "http:/<IP Address>:8080/swagger-ui/index.html#/"

## Acknowledgements

This project is based on an online training developed by Ranga Karanam, as part of his company in28Minutes Official, which is as follows:

Master Microservices with Spring Boot and Spring Cloud (https://www.udemy.com/course/microservices-with-spring-boot-and-spring-cloud/)

# reverse-proxy-mock-mesh
An example for mock-reverse-proxy scenario

![](docs/catcher.png)

# Run screnario
````shell
$ cd backend
$ ./mvnw clean package
$ cd..
$ docker-compose up
````

# Open Proxymanager
[open "Login – Nginx Proxy Manager" after startup](http://localhost:81/login)

|      | Value           |
|------|-----------------|
| User | admin@admin.com |
| Pass | password123     |

![](docs/Proxmanager_Configuration_For_Wiremock_and_Java_Backend.png)

## Test java backend
```shell
$ curl localhost/backend/datetime
```

```shell
$ curl localhost/backend/data
```
[http://localhost/backend/](http://localhost/backend/)
![](docs/Static_Content_from_Java-Backend.jpg)

## Test Wiremock
````shell
$ curl localhost/api/example-1/test
````

# Request-Sequence-Diagramm

## Clients calls local backend
The local backend has been started in the Java-IDE.
````mermaid
sequenceDiagram
    actor client
    participant local-backend
    participant reverse-proxy
    participant wiremock
    participant container-backend

    rect rgb(96, 96, 96)
        client->>local-backend: GET http://localhost:8080/datetime
        Note over client,local-backend: (1) direct call
        local-backend->>client: 18.04.23 14:26:42
    end
    
    rect rgb(96, 96, 96)
        client->>local-backend: http://localhost:8080/data
        activate local-backend
        local-backend-->local-backend: GET http://localhost:8080/datetime
        local-backend->>client: Response from http://localhost:8080/datetime: 18.04.23 14:39:22
        Note over client,local-backend: (2) indirect call to configured target
    end
````

## Clients calls local backend with TARGETUURL
The local backend has been started in the Java-IDE with environment-variable ``TARGETURL=http://localhost/api/some/thing``.
````mermaid
sequenceDiagram
    actor client
    participant local-backend
    participant reverse-proxy
    participant wiremock
    participant container-backend

    rect rgb(96, 96, 96)
        client->>local-backend: GET http://localhost:8080/datetime
        Note over client,local-backend: (1) direct call
        local-backend->>client: 18.04.23 14:26:42
    end
    
    rect rgb(96, 96, 96)
        client->>local-backend: http://localhost:8080/data
        activate local-backend
        local-backend->>reverse-proxy: http://localhost/api/some/thing
        reverse-proxy->>wiremock: GET http://wiremock:8080/some/thing
        wiremock->>reverse-proxy: Hello World!
        reverse-proxy->>local-backend: Hello World!
        local-backend->>client: Response from http://localhost/api/some/thing: Hello World!
    end
````


## Clients calls container-backend with TARGETUURL
The backend in the docker-compose-network (configured with environment-variable ``TARGETURL=http://reverse-proxy/api/some/thing``) will be call indirect by reverse-proxy.
````mermaid
sequenceDiagram
    actor client
    participant local-backend
    participant reverse-proxy
    participant wiremock
    participant container-backend

    rect rgb(96, 96, 96)
        client->>reverse-proxy: GET http://localhost/backend/data
        reverse-proxy->>container-backend: GET http://backend:8080/data
        container-backend->>reverse-proxy: GET http://reverse-proxy/api/some/thing
        reverse-proxy->>wiremock: GET http://wiremock:8080/some/thing
        wiremock-->>reverse-proxy: Hello World!
        reverse-proxy-->>container-backend: Hello World!
        container-backend-->>reverse-proxy: Hello World!
        reverse-proxy-->>client: Hello World!
    end
````


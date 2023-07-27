# Reducurl

Reducurl is a very simplified URL shortener application.

### Cassandra as datastore
Since this kind of service is expected to store large amounts of data, there is no relationships between records,
and service is expected to be read-heavy, Cassandra was chosen as datastore. What is demonstrated in repository
is the ability to control whether system is AP or CP through consistency level. In this case CP was chosen. Since
number of writes is expected to be much smaller than number of reads and we want fast reads, consistency is 
achieved through ALL consistency level while writing URLs to database. Ttl feature was also utilized to expiry URLs
after a certain period of time.

### Running example

##### Preconditions
- Installed Docker (with Compose)
- Installed Java 17 JDK

##### Running
Application is utilizing [docker compose support](https://spring.io/blog/2023/06/21/docker-compose-support-in-spring-boot-3-1)
so you just need to run:

```sh
./mvnw spring-boot:run
```

which will use compose.yml to start Cassandra service defined in yml and once Cassandra is ready (because there is
custom healthcheck defined) Spring application will start successfully.

After the application is up execute following to create reduced URL:
```sh
curl --location --request POST 'http://localhost:8080/' \
--header 'Content-Type: application/json' \
--data-raw 'http://www.google.com'
```

Response will contain shortened URL which you can then open in your browser or execute:
```sh
curl --location --request GET 'http://localhost:8080/yGosUrrU'
```

### Tests
There is one integration test covering both creation and fetching of shortened URL which is utilizing 
[improved Testcontainers support](https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1).
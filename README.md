# stunning-fiesta

An Application based on Spring-Boot

## Configuration

### Configure PostgreSQL

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spring_db
    username: postgres
    password: postgres
```

### Configure JPA

```yaml
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```
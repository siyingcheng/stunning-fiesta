# Annotions

[TOC]

## @Transient (javax.persistence.Transient)

> Ref: https://javabydeveloper.com/using-transient-in-spring-boot-examples/

`@Transient` annotation is used to mark a field to be transient for the mapping framework, which means the field marked
with `@Transient` is ignored by mapping framework and the field not mapped to any database column (in RDBMS) or Document
property (in NOSQL). Thus, the property will not be persisted to data store. `@Transient` exists in
`org.springframework.data.annotation` package. The mapping framework will be different for each Spring Data module (
Spring
Data Jdbc, Spring Data MongoDB, Spring Data JPA .. etc).


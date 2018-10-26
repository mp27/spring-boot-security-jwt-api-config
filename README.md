# Spring Boot with security configuration and JWT authentification
#### Dependencies
* Spring Boot 2
* Data JPA
* Dev Tools
* Starter Web
* Mysql
* Spring Security
* io.jsonwebtoken
* Jackson
* Project Lombok

#### SQL
```sql
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

#### API endpoints
```$xslt
POST /api/auth/signin
POST /api/auth/signup
```
#### TODO
- [x] create user and role entity
- [x] add security configuration
- [x] create auth controller endpoints
- [ ] add service layers
- [ ] create user profile
- [ ] add documentation (with swagger and readme file)
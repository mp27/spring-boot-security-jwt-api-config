## Spring Boot with security configuration and JWT authentification
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
GET /api/users/me
GET /api/user/checkUsernameAvailability?username=
GET /api/user/checkEmailAvailability?email=
GET /api/users/{username}
```
#### TODO
- [x] create user and role entity
- [x] add security configuration
- [x] create auth controller endpoints
- [x] create user profile
- [x] add service layers
- [x] user verification with email confirmation
- [x] add yaml app configuration
- [ ] change user password
- [ ] admin list users
- [ ] create user notifications
- [ ] update user roles
- [ ] database schema migration with Flyway or liquibase
- [ ] add documentation (with swagger and readme file)
- [ ] create tests for all controllers
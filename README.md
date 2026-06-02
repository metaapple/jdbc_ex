# JDBC Ex

Java JDBC 연습 프로젝트입니다. MySQL 데이터베이스에 연결하고, `PreparedStatement`를 사용해 CRUD 작업을 수행합니다.  
초기에는 JDBC를 직접 사용하고, 이후 **DAO(Data Access Object) 패턴**으로 DB 접근 로직을 분리합니다. JUnit 5로 단계별 테스트를 진행합니다.

---

## 프로젝트 구조

```mermaid
graph TD
    subgraph "테스트 계층"
        CT[ConnectionTest]
        CR[CrudTest]
        UDT[UserDaoTest]
    end

    subgraph "DAO 계층"
        UD[UserDao<br/>interface]
        UDI[UserDaoImpl]
    end

    subgraph "도메인 계층"
        UVO[UserVO]
    end

    subgraph "공통 계층"
        JU[JDBCUtil]
    end

    subgraph "설정 / DB"
        AP[application.properties]
        SQL[users.sql]
        DB[(MySQL jdbc_ex)]
    end

    CT -->|직접 연결| DB
    CT -->|getConnection / close| JU
    CR -->|getConnection / close| JU
    UDT --> UDI
    UDI -->|implements| UD
    UDI --> UVO
    UDI --> JU
    JU -->|설정 로드| AP
    JU -->|Connection| DB
    SQL -->|스키마 생성| DB
```

---

## 계층 구조 (DAO 패턴)

```mermaid
flowchart TB
    subgraph Test["테스트 (UserDaoTest)"]
        T1[create]
        T2[getList]
        T3[get]
        T4[update]
        T5[delete]
    end

    subgraph DAO["DAO 계층"]
        IF[UserDao<br/>인터페이스]
        IMPL[UserDaoImpl<br/>구현체]
    end

    subgraph Domain["도메인"]
        VO[UserVO]
    end

    subgraph Common["공통"]
        JDBC[JDBCUtil]
    end

    DB[(USERS 테이블)]

    Test --> IF
    IF --> IMPL
    IMPL --> VO
    IMPL --> JDBC
    JDBC --> DB
```

---

## JDBC 연결 흐름

JDBC는 DB와 통신하기 위해 **4단계**를 거칩니다.

```mermaid
sequenceDiagram
    participant App as 애플리케이션
    participant Driver as JDBC Driver
    participant DM as DriverManager
    participant DB as MySQL

    App->>Driver: 1. Class.forName(driver)
    Note over App,Driver: 드라이버 클래스 로딩

    App->>DM: 2. getConnection(url, id, password)
    DM->>DB: 3. TCP 연결 요청
    DB-->>DM: Connection 객체 반환
    DM-->>App: Connection

    App->>DB: 4. SQL 실행 (PreparedStatement)
    App->>DB: 5. con.close() 자원 해제
```

---

## 파일 구성

| 경로 | 역할 | 설명 |
|------|------|------|
| `src/main/java/org/scoula/jdbc_ex/common/JDBCUtil.java` | DB 연결 유틸 | static 블록으로 드라이버 로딩 및 Connection 생성 |
| `src/main/java/org/scoula/jdbc_ex/domain/UserVO.java` | 도메인 객체 | `users` 테이블과 매핑되는 Value Object |
| `src/main/java/org/scoula/jdbc_ex/dao/UserDao.java` | DAO 인터페이스 | CRUD 기능을 추상 메서드로 정의 |
| `src/main/java/org/scoula/jdbc_ex/dao/UserDaoImpl.java` | DAO 구현체 | JDBC + PreparedStatement로 CRUD 구현 |
| `src/main/resources/application.properties` | DB 설정 | 드라이버, URL, 계정 정보 |
| `src/test/java/org/scoula/jdbc_ex/ConnectionTest.java` | 연결 테스트 | JDBC 직접 연결 / JDBCUtil 연결 검증 |
| `src/test/java/org/scoula/jdbc_ex/CrudTest.java` | CRUD 테스트 | INSERT 등 SQL 직접 실행 테스트 |
| `src/test/java/org/scoula/jdbc_ex/dao/UserDaoTest.java` | DAO 테스트 | UserDaoImpl CRUD 단위 테스트 |
| `users.sql` | DB 초기화 | DB·테이블 생성 및 샘플 데이터 |
| `build.gradle` | 빌드 설정 | MySQL Connector, Lombok, JUnit 5 의존성 |

---

## 클래스 / 메서드 정리

### UserVO (도메인)

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | `String` | 사용자 아이디 |
| `password` | `String` | 비밀번호 |
| `name` | `String` | 이름 |
| `role` | `String` | 권한 (`USER`, `ADMIN`) |

> Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` 적용

### UserDao (인터페이스)

| 메서드 | 반환 타입 | 설명 |
|--------|-----------|------|
| `create(UserVO user)` | `int` | 회원 등록 (INSERT) |
| `getList()` | `List<UserVO>` | 회원 목록 조회 (SELECT) |
| `get(String id)` | `Optional<UserVO>` | 회원 단건 조회 (SELECT) |
| `update(UserVO user)` | `int` | 회원 수정 (UPDATE) |
| `delete(String id)` | `int` | 회원 삭제 (DELETE) |

### UserDaoImpl (구현체)

| SQL 상수 | SQL |
|----------|-----|
| `USER_LIST` | `SELECT * FROM users` |
| `USER_GET` | `SELECT * FROM users WHERE id = ?` |
| `USER_INSERT` | `INSERT INTO users VALUES(?, ?, ?, ?)` |
| `USER_UPDATE` | `UPDATE users SET name = ?, role = ? WHERE id = ?` |
| `USER_DELETE` | `DELETE FROM users WHERE id = ?` |

| 메서드 | 구현 상태 | 설명 |
|--------|-----------|------|
| `create()` | ✅ 완료 | try-with-resources로 INSERT 실행 |
| `getList()` | 🔲 미구현 | `List.of()` 반환 |
| `get()` | 🔲 미구현 | `Optional.empty()` 반환 |
| `update()` | 🔲 미구현 | `0` 반환 |
| `delete()` | 🔲 미구현 | `0` 반환 |

### JDBCUtil

| 메서드 | 반환 타입 | 설명 |
|--------|-----------|------|
| `getConnection()` | `Connection` | static 블록에서 생성된 Connection 반환 |
| `close()` | `void` | Connection 종료 후 `null` 처리 |

### ConnectionTest

| 메서드 | 테스트 내용 |
|--------|-------------|
| `testConnection()` | `Class.forName` + `DriverManager`로 직접 DB 연결 |
| `testConnection2()` | `JDBCUtil`을 통한 DB 연결 및 종료 |

### CrudTest

| 메서드 | `@Order` | 테스트 내용 |
|--------|----------|-------------|
| `insertUser()` | 1 | `users` 테이블에 회원 INSERT |
| `close()` | `@AfterAll` | 모든 테스트 후 Connection 종료 |

### UserDaoTest

| 메서드 | 테스트 내용 | 구현 상태 |
|--------|-------------|-----------|
| `create()` | `UserDaoImpl.create()` — 회원 등록 | ✅ 완료 |
| `getList()` | 회원 목록 조회 | 🔲 미구현 |
| `get()` | 회원 단건 조회 | 🔲 미구현 |
| `update()` | 회원 수정 | 🔲 미구현 |
| `delete()` | 회원 삭제 | 🔲 미구현 |
| `tearDown()` | `@AfterAll` — Connection 종료 | ✅ 완료 |

---

## DB 스키마

### USERS 테이블

| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| `ID` | `VARCHAR(12)` | PK, NOT NULL | 사용자 아이디 |
| `PASSWORD` | `VARCHAR(12)` | NOT NULL | 비밀번호 |
| `NAME` | `VARCHAR(30)` | NOT NULL | 이름 |
| `ROLE` | `VARCHAR(6)` | NOT NULL | 권한 (`USER`, `ADMIN`) |

---

## 사전 준비

### 1. MySQL 데이터베이스 생성

`users.sql`을 실행하여 DB, 사용자, 테이블을 생성합니다.

```sql
CREATE DATABASE jdbc_ex;

CREATE USER 'scoula'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON jdbc_ex.* TO 'scoula'@'%';
FLUSH PRIVILEGES;

USE jdbc_ex;

CREATE TABLE USERS (
    ID       VARCHAR(12) NOT NULL PRIMARY KEY,
    PASSWORD VARCHAR(12) NOT NULL,
    NAME     VARCHAR(30) NOT NULL,
    ROLE     VARCHAR(6)  NOT NULL
);
```

### 2. application.properties 설정

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://127.0.0.1:3306/jdbc_ex
id=scoula
password=1234
```

---

## 핵심 코드 설명

### 1. JDBC 직접 연결 (ConnectionTest)

드라이버 로딩 → Connection 획득 → 사용 후 종료 순서로 동작합니다.

```java
// 1단계: JDBC 드라이버 로딩
Class.forName("com.mysql.cj.jdbc.Driver");

// 2단계: DB 연결 (url, id, password)
String url = "jdbc:mysql://127.0.0.1:3306/jdbc_ex";
Connection con = DriverManager.getConnection(url, "scoula", "1234");

// 3단계: 자원 해제
con.close();
```

### 2. JDBCUtil — 연결 공통화

CRUD 작업마다 1~2단계(드라이버 로딩, 연결)를 반복하지 않도록 static 블록에서 한 번만 초기화합니다.

```java
static {
    Properties properties = new Properties();
    properties.load(JDBCUtil.class.getResourceAsStream("/application.properties"));

    String driver   = properties.getProperty("driver");
    String url      = properties.getProperty("url");
    String id       = properties.getProperty("id");
    String password = properties.getProperty("password");

    Class.forName(driver);
    con = DriverManager.getConnection(url, id, password);
}

public static Connection getConnection() {
    return con;
}
```

> **static 블록이란?**  
> 객체 생성(`new`) 없이 클래스 로딩 시점에 자동 실행되는 초기화 블록입니다.  
> `static` 필드(`con`)는 생성자로 초기화할 수 없으므로 static 블록을 사용합니다.

### 3. INSERT — PreparedStatement (CrudTest)

`?` 플레이스홀더로 SQL Injection을 방지하고, 값을 순서대로 바인딩합니다.

```java
String sql = "INSERT INTO users(id, password, name, role) VALUES(?, ?, ?, ?)";
PreparedStatement pstmt = con.prepareStatement(sql);

pstmt.setString(1, "winner2");  // id
pstmt.setString(2, "1234");     // password
pstmt.setString(3, "win");      // name
pstmt.setString(4, "admin");    // role

int row = pstmt.executeUpdate(); // 영향받은 행 수 반환
pstmt.close();
```

### 4. UserVO — 도메인 객체 (UserVO)

DB 테이블의 한 행(row)을 Java 객체로 표현합니다. Lombok으로 getter/setter/생성자를 자동 생성합니다.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private String id;
    private String password;
    private String name;
    private String role;
}
```

### 5. UserDao — 인터페이스 (UserDao)

구현 클래스가 반드시 제공해야 할 CRUD 기능을 추상 메서드로 정의합니다.

```java
public interface UserDao {
    int create(UserVO user) throws SQLException;
    List<UserVO> getList() throws SQLException;
    Optional<UserVO> get(String id) throws SQLException;
    int update(UserVO user) throws SQLException;
    int delete(String id) throws SQLException;
}
```

### 6. UserDaoImpl — DAO 구현 (UserDaoImpl)

인터페이스를 구현하고, SQL을 클래스 내부 상수로 관리합니다.  
`try-with-resources`로 `PreparedStatement`를 자동 close합니다.

```java
public class UserDaoImpl implements UserDao {
    Connection conn = JDBCUtil.getConnection();

    private String USER_INSERT = "insert into users values(?, ?, ?, ?)";

    @Override
    public int create(UserVO user) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(USER_INSERT)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getRole());
            return stmt.executeUpdate();
        }
    }
}
```

### 7. UserDaoTest — TDD 방식 테스트

테스트를 먼저 작성하고, DAO 구현체를 단계적으로 완성합니다.

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {
    UserDao dao = new UserDaoImpl();

    @AfterAll
    static void tearDown() {
        JDBCUtil.close();
    }

    @Test
    void create() throws SQLException {
        UserVO user = new UserVO("app", "1234", "app", "admin");
        int count = dao.create(user);
        Assertions.assertEquals(1, count);
    }
}
```

---

## 테스트 실행 흐름

```mermaid
flowchart LR
    A[JUnit 실행] --> B[ConnectionTest]
    A --> C[CrudTest]
    A --> D[UserDaoTest]

    B --> B1[testConnection<br/>직접 JDBC 연결]
    B --> B2[testConnection2<br/>JDBCUtil 연결]

    C --> C1["@Order(1) insertUser<br/>회원 INSERT"]
    C --> C2["@AfterAll close<br/>Connection 종료"]

    D --> D1[create<br/>DAO INSERT 테스트]
    D --> D2[getList / get / update / delete<br/>추후 구현]
    D --> D3["@AfterAll tearDown<br/>Connection 종료"]
```

### 테스트 실행 명령

```bash
# Windows
gradlew.bat test

# macOS / Linux
./gradlew test
```

특정 테스트만 실행:

```bash
gradlew.bat test --tests "org.scoula.jdbc_ex.ConnectionTest"
gradlew.bat test --tests "org.scoula.jdbc_ex.CrudTest"
gradlew.bat test --tests "org.scoula.jdbc_ex.dao.UserDaoTest"
```

---

## 의존성

| 라이브러리 | 버전 | 용도 |
|------------|------|------|
| `mysql-connector-j` | 8.3.0 | MySQL JDBC 드라이버 |
| `lombok` | 1.18.30 | 보일러플레이트 코드 제거 |
| `junit-jupiter` | 5.10.0 | 단위 테스트 |

---

## JDBC 단계 요약

| 단계 | 작업 | 사용 클래스 / 메서드 |
|------|------|----------------------|
| 1 | 드라이버 로딩 | `Class.forName(driver)` |
| 2 | DB 연결 | `DriverManager.getConnection(url, id, pw)` |
| 3 | SQL 실행 | `Connection.prepareStatement(sql)` |
| 4 | 자원 해제 | `Connection.close()`, `PreparedStatement.close()` |

---

## 학습 진행 순서

```mermaid
flowchart TD
    S1["1단계: JDBC 직접 연결<br/>(ConnectionTest)"]
    S2["2단계: JDBCUtil 공통화<br/>(static 블록)"]
    S3["3단계: PreparedStatement CRUD<br/>(CrudTest)"]
    S4["4단계: DAO 패턴 도입<br/>(UserVO + UserDao + UserDaoImpl)"]
    S5["5단계: TDD로 CRUD 완성<br/>(UserDaoTest)"]

    S1 --> S2 --> S3 --> S4 --> S5
```

<hr>
<img width="2705" height="1837" alt="image" src="https://github.com/user-attachments/assets/db616ab7-1ff5-4942-84b5-65797afe9c54" />
<img width="965" height="1484" alt="image" src="https://github.com/user-attachments/assets/6cf9c1f1-61e6-4602-9862-6f1ee113907c" />
<img width="2365" height="1850" alt="image" src="https://github.com/user-attachments/assets/1b0ae863-c91f-4662-8f8d-5ee245c4949f" />

<img width="1455" height="363" alt="image" src="https://github.com/user-attachments/assets/4e62b740-c637-42f8-b0a1-7163f2cdcf99" />
<img width="1317" height="874" alt="image" src="https://github.com/user-attachments/assets/722e00de-299f-4abf-90d1-a60fff96dcbf" />
<img width="951" height="1477" alt="image" src="https://github.com/user-attachments/assets/eb290082-815f-4b4b-828a-60b65cf218c7" />

<img width="1813" height="1192" alt="image" src="https://github.com/user-attachments/assets/794856d2-68d4-46c2-a02e-76d6acd5a40b" />
<img width="1478" height="1175" alt="image" src="https://github.com/user-attachments/assets/472a2384-f396-43ec-ab78-93b614de978f" />

<br>
<hr>
<br>
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/3bf2ad14-bde7-4cd1-9ff0-97a7c590bc9f" />

<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/674e605b-4a18-49fc-a9c9-299adb076e40" />
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/ad6d35fe-7556-46d5-b9b6-06a6b6d0cd95" />
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/f024fcf0-4939-4dc0-a521-e02cef243cd7" />
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/7b36b654-2569-4e96-bf3b-869f22312fbd" />

<img width="1672" height="941" alt="image" src="https://github.com/user-attachments/assets/011df47e-5120-437f-a4a9-cadee2824193" />
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/c77bf119-d4cd-4de5-a8ee-60a6cc300e3b" />
<img width="1672" height="941" alt="image" src="https://github.com/user-attachments/assets/99fe26f1-ba14-4d57-9060-7424050ff9bc" />

<hr>
<br>
- 인터페이스/클래스 <br>
<img width="2008" height="686" alt="image" src="https://github.com/user-attachments/assets/c80f76ce-5f0d-4f99-a617-3782cb7c4aba" />
<img width="2927" height="1818" alt="image" src="https://github.com/user-attachments/assets/3f6c92cc-e4f1-48a3-b3e1-b9628399e8f8" />




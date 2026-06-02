package org.scoula.jdbc_ex.dao;

import org.junit.jupiter.api.*;
import org.scoula.jdbc_ex.common.JDBCUtil;
import org.scoula.jdbc_ex.domain.UserVO;

import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {

    UserDao dao = new UserDaoImpl();

    @AfterAll
    static void tearDown() {
        JDBCUtil.close();
    }

    @Test
    void create() throws SQLException {
        UserVO user = new UserVO("app", "1234",
                                "app", "admin");
        int count = dao.create(user);
        Assertions.assertEquals(1, count);
    }

    @Test
    void getList() {
    }

    @Test
    void get() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}
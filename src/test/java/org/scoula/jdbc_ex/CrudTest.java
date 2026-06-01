package org.scoula.jdbc_ex;

//테스트 클래스에는 단위별로 메서드를 만들어서 테스트
//메서드 전체를 내가 원하는 순서에 따라 실행하게 할 수 있음.

import org.junit.jupiter.api.*;
import org.scoula.jdbc_ex.common.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudTest {
    Connection con = JDBCUtil.getConnection();

    @AfterAll
    //다른 메서드 다 실행하고 나서 무조건 실행할 메서드를 넣어줌.
    static void close(){
        JDBCUtil.close();
    }


    @Test
    @Order(1)
    @DisplayName("회원가입테스트함.")
    public void insertUser() throws SQLException {
        String sql = "insert into users(id, password, name, role) values(?, ?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, "winner2");
        pstmt.setString(2, "1234");
        pstmt.setString(3, "win");
        pstmt.setString(4, "admin");
        int row = pstmt.executeUpdate(); //mysql로 sql문 전송
        //실행된 sql문 결과 row수
        System.out.println(row);
        Assertions.assertEquals(1, row);
        pstmt.close();
    }
}

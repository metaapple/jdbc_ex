package org.scoula.jdbc_ex;

//테스트 클래스에는 단위별로 메서드를 만들어서 테스트
//메서드 전체를 내가 원하는 순서에 따라 실행하게 할 수 있음.

import org.junit.jupiter.api.*;
import org.scoula.jdbc_ex.common.JDBCUtil;

import java.sql.*;

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
        pstmt.setString(1, "winner3");
        pstmt.setString(2, "1234");
        pstmt.setString(3, "win");
        pstmt.setString(4, "admin");
        int row = pstmt.executeUpdate(); //mysql로 sql문 전송
        //실행된 sql문 결과 row수
        System.out.println(row);
        Assertions.assertEquals(1, row);
        pstmt.close();
    }

    @Test
    @DisplayName("user 목록 조회 테스트")
    @Order(2)
    public void selectUser() throws SQLException {
        //JDBC 단계 1,2
        //JDBC 단계 3 - sql문 정함.
        String sql = "select * from users";
        //JDBC 단계 4 - sql객체 생성
        try (Statement stmt = con.createStatement();
             //JDBC 단계 5 - sql 전송(mysql서버로)
             ResultSet rs = stmt.executeQuery(sql);
        ) {
            //JDBC 단계 6 - sql 결과를 받아와서 처리
            while (rs.next()) {
                String name = rs.getString("name");
                System.out.println(name);
            }
        }
    }

        @Test
        @DisplayName("user 상세 조회 테스트")
        @Order(3)
        public void selectUserById() throws SQLException {
            //JDBC 단계 1,2
            //JDBC 단계 3 - sql문 정함.
            String sql = "select * from users where id = ?";
            //JDBC 단계 4 - sql객체 생성
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, "admin");

                //JDBC 단계 5 - sql 전송(mysql서버로))
                try (ResultSet rs = stmt.executeQuery()) {
                    //JDBC 단계 6 - sql 결과를 받아와서 처리
                    if (rs.next()) {
                        String name = rs.getString("name");
                        System.out.println(name);
                        String role = rs.getString("role");
                        System.out.println(role);
                    }
                }
            }
        }

        //JDBC 단계 7 - close
//        stmt.close();
//        rs.close();

}

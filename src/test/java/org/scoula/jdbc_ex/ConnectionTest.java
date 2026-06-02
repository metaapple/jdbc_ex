package org.scoula.jdbc_ex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.scoula.jdbc_ex.common.JDBCUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConnectionTest {

    //db연결 테스트 메서드 정의해주면 됨.!!!
    //메서드 단위로 기능을 테스트해볼 수 있음.(JUnit라이브러리 사용함.)
    @Test
    @DisplayName("jdbc 1-2단계 테스트")
    public void testConnection() throws ClassNotFoundException, SQLException {
        //JDBC 단계별 테스트해보자.!!!
        //1. jdbc드라이버 세팅(로딩), jdbc connector
        Class.forName("com.mysql.cj.jdbc.Driver"); //외부파일 연결
        System.out.println("1. jdbc드라이버 세팅(로딩) 성공");

        //2. db연결해보자.
        //db연결시 필요한 데이터 3가지 : url(ip + port + db명), username, pw


        String url = "jdbc:mysql://127.0.0.1:3306/jdbc_ex";
        //        String id = JOptionPane.showInputDialog("id입력");
        String id = "scoula";
        String password = "1234";
        Connection con = DriverManager.getConnection(url, id, password);
        System.out.println("2. db연결 성공>> " + con);

        //자원해제 해주어야함.
        con.close();
    }

    @Test
    public void testConnection2() {
        Connection con = JDBCUtil.getConnection();
        JDBCUtil.close();
    }
}
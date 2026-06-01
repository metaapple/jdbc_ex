package org.scoula.jdbc_ex.common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtil {
    //tdd로 먼저 테스트하고 나중에 정확한 코드로 구현하자.!!!

    //crud할 때 jdbc1-2단계는 항상 필요함.
    //1-2단계 전용 응집도 높게 클래스를 하나 만들예정.
    static Connection con = null;

    //Car car = new Car();
    //public class Car{
    //  public Car() {
    //      System.out.println("초기화 담당 메서드가 호출됨");
    //  }
    //}
    //static 초기화 블록 ==> 객체생성시 클래스이름과 동일한 메서드(생성자 메서드)로 초기화해주는데,
    //static인 경우 생성자를 호출하지 않아 초기화해줄 수 없음. 이때 static블록을 넣어 초기화.
    static {
            Properties properties = new Properties();
        try {
            properties.load(JDBCUtil.class.getResourceAsStream("/application.properties"));
            String driver = properties.getProperty("driver");
            String url = properties.getProperty("url");
            String id = properties.getProperty("id");
            String password = properties.getProperty("password");
            Class.forName(driver);
            con = DriverManager.getConnection(url, id, password);
        } catch (IOException | SQLException e) {
            System.out.println("예외처리함.");
        } catch (ClassNotFoundException e) {
            System.out.println("예외처리함.");
        }
    }

//    @driver
//    String driver;

    //메서드를 new로 객체생성하지않고
    //바로 호출해서 사용하고 싶으면
    //메서드 리턴앞에 static을 붙여라!!
    //아무때나 호출해서 사용 가능.
    //Integer.parseInt()
    //String.valueOf()
    //Math.max(1, 2);
    public static Connection getConnection(){
        //static메서드 안에서 사용하는 필드는 반드시 static이여야함.
        return con;
    }

    public static void close(){
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("예외처리함.");
            }
            con = null;
        }
    }
}

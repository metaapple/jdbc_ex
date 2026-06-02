package org.scoula.jdbc_ex.dao;

import org.scoula.jdbc_ex.domain.UserVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    //구현해야하는 기능을 추상메서드(구현이 빠져있는 메스드)로 정의해두자.
    //CRUD기능을 메서드로 정의.

    // 회원 등록
    int create(UserVO user) throws SQLException;

    // 회원 목록 조회
    List<UserVO> getList() throws SQLException;


    // 회원 정보 조회
    Optional<UserVO> get(String id) throws SQLException;


    // 회원 수정
    int update(UserVO user) throws SQLException;


    // 회원 삭제
    int delete(String id) throws SQLException;
}

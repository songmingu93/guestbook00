package com.javaex.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javaex.vo.PersonVo;

public class GuestDao {

	// 0. import java.sql.*;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String id = "guestbook";
	private String pw = "guestbook";

	private void getConnection() {
		try {
			// 1. JDBC 드라이버 (Oracle) 로딩
			Class.forName(driver);

			// 2. Connection 얻어오기
			conn = DriverManager.getConnection(url, id, pw);
			// System.out.println("접속성공");

		} catch (ClassNotFoundException e) {
			System.out.println("error: 드라이버 로딩 실패 - " + e);
		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
	}

	public void close() {
		// 5. 자원정리
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
	}

	// 사람 추가
	public int personInsert(PersonVo personVo) {
		int count = 0;
		getConnection();

		try {

			// 3. SQL문 준비 / 바인딩 / 실행
			String query = ""; // 쿼리문 문자열만들기, ? 주의
			query += " INSERT INTO person ";
			query += " VALUES (seq_person_id.nextval, ?, ?, ?) ";
			// System.out.println(query);

			pstmt = conn.prepareStatement(query); // 쿼리로 만들기

			pstmt.int(1, personVo.getno()); // ?(물음표) 중 1번째, 순서중요
			pstmt.setString(2, personVo.getname()); 
			pstmt.setString(3, personVo.getpessword());
			pstmt.setString(4, personVo.getcontent());
			pstmt.setString(5, personVo.getreg_date());

			count = pstmt.executeUpdate(); // 쿼리문 실행

			// 4.결과처리
			// System.out.println("[" + count + "건 추가되었습니다.]");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		close();
		return count;
	}

	// 사람 리스트(검색안할때)
	public List<PersonVo> getPersonList() {
		return getPersonList("");
	}

	// 사람 리스트(검색할때)
	public List<PersonVo> getPersonList(String keword) {
		List<PersonVo> personList = new ArrayList<PersonVo>();

		getConnection();

		try {

			// 3. SQL문 준비 / 바인딩 / 실행 --> 완성된 sql문을 가져와서 작성할것
			String query = "";
			query += " select  person_id, ";
			query += "         name, ";
			query += "         hp, ";
			query += "         company ";
			query += " from person";

			if (keword != "" || keword == null) {
				query += " where name like ? ";
				query += " or hp like  ? ";
				query += " or company like ? ";
				pstmt = conn.prepareStatement(query);

				pstmt.setString(1, '%' + keword + '%');
				pstmt.setString(2, '%' + keword + '%');
				pstmt.setString(3, '%' + keword + '%');
			} else {
				pstmt = conn.prepareStatement(query);
			}

			rs = pstmt.executeQuery();

			// 4.결과처리
			while (rs.next()) {
				int no = rs.getInt("no");
				String name = rs.getString("name");
				String password = rs.getString("password");
				String content = rs.getString("content");
				String reg_date = rs.getString("reg_date");
				
				PersonVo personVo = new PersonVo(no, name, password, content, reg_date);
				personList.add(personVo);
			}

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		close();

		return personList;

	}

	// 사람 수정
	public int personUpdate(PersonVo personVo) {
		int count = 0;
		getConnection();

		try {

			// 3. SQL문 준비 / 바인딩 / 실행
			String query = ""; // 쿼리문 문자열만들기, ? 주의
			query += " update person ";
			query += " set name = ? , ";
			query += "     password = ? , ";
			query += "     content = ? ";
			query += "     reg_date = ? ";
			query += " where no = ? ";

			pstmt = conn.prepareStatement(query); // 쿼리로 만들기

			pstmt.setInt(1, personVo.getNo());
			pstmt.setString(2, personVo.getName());
			pstmt.setString(3, personVo.getPassword());
			pstmt.setString(4, personVo.getContent());
			pstmt.setString(5, personVo.getReg_date());


			count = pstmt.executeUpdate(); // 쿼리문 실행

			// 4.결과처리
			// System.out.println(count + "건 수정되었습니다.");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		close();
		return count;
	}

	// 사람 삭제
	public int personDelete(int no) {
		int count = 0;
		getConnection();

		try {
			// 3. SQL문 준비 / 바인딩 / 실행
			String query = ""; // 쿼리문 문자열만들기, ? 주의
			query += " delete from person ";
			query += " where person_id = ? ";
			pstmt = conn.prepareStatement(query); // 쿼리로 만들기

			pstmt.setInt(1, no);// ?(물음표) 중 1번째, 순서중요

			count = pstmt.executeUpdate(); // 쿼리문 실행

			// 4.결과처리
			// System.out.println(count + "건 삭제되었습니다.");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		close();
		return count;
	}

	// 사람 1명 가져오기
	public PersonVo getPerson(int personId) {
		PersonVo personVo = null;
		
		getConnection();

		try {
			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "";
			query += " select  no, ";
			query += "         name, ";
			query += "         password, ";
			query += "         content ";
			query += "     reg_date = ? ";
			query += " from person";
			query += " where no = ?";

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, personId);
			
			rs = pstmt.executeQuery();
		
			// 4.결과처리
			while(rs.next()) {
				int no = rs.getInt("no");
				String name = rs.getString("name");
				String password = rs.getString("password");
				String content = rs.getString("content");
				String reg_date = rs.getString("reg_date");
				
				personVo = new PersonVo(no, name, password, content, reg_date);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 4.결과처리

		close();

		return personVo;
	}

}
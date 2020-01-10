package com.bigdata2019.mysite.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bigdata2019.mysite.vo.BoardVo;

public class BoardDao {
	
	public Boolean delete(String delBoardNo) {
		Boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
	
			//SQL 준비
			String sql = 
				" delete" + 
				"   from board" + 
				"  where no = ?";
					
			pstmt = conn.prepareStatement(sql);			
		
			//값 바인딩
			pstmt.setString(1, delBoardNo);
			
			//쿼리 실행
			int count = pstmt.executeUpdate();
			result = (count == 1);
			
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패:" + e);
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public Boolean insert(BoardVo vo) {
		Boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
	
			//todo- orderNo Depth 처리
			
			//SQL 준비
			String sql = 
				" insert" + 
				"  into board" + 
				" values (null, ?, ?, now(), 0,  (select IF(max(g_no) is null, 0, max(g_no)) from board a)+1 , 1, 0, ?)";
			
			pstmt = conn.prepareStatement(sql);			
			
			//값 바인딩
			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getContent());
			pstmt.setLong(3, vo.getUserNo());

			//쿼리 실행
			int count = pstmt.executeUpdate();
			result = (count == 1);
			
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패:" + e);
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public List<BoardVo> findList(int pageNo){
		List<BoardVo> result = new ArrayList<BoardVo>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			
			String sql =
					"SELECT * FROM " + 
					"	( " + 
					"		SELECT " + 
					"			b.no, b.title, b.contents, b.Reg_date, b.hit, b.g_no, b.o_no, b.depth, b.user_no, u.name , @ROWNUM:=@ROWNUM+1, FLOOR((@ROWNUM-1)/10+1) AS PAGE " + 
					"		FROM " + 
					"			board b LEFT JOIN user AS u ON b.user_no = u.no " + 
					"			, (SELECT @ROWNUM:=0) R " + 
					"		ORDER BY no DESC " + 
					"	) A " + 
					"WHERE PAGE = ? ";
				
			pstmt = conn.prepareStatement(sql);		
			pstmt.setInt(1, pageNo);

			rs = pstmt.executeQuery();
			while(rs.next()) {
				BoardVo vo = new BoardVo();
				vo.setNo(rs.getLong(1));
				vo.setTitle(rs.getString(2));
				vo.setContent(rs.getString(3));
				vo.setRegDate(rs.getString(4));
				vo.setHit(rs.getInt(5));
				vo.setGroupNo(rs.getInt(6));
				vo.setOrderNo(rs.getInt(7));
				vo.setDepth(rs.getInt(8));
				vo.setUserNo(rs.getLong(9));
				vo.setUserName(rs.getString(10));
				result.add(vo);
			}
			
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 클래스 로딩 실패:" + e);
		} catch (SQLException e) {
			System.out.println("에러:" + e);
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
		return result;
	}
	
	public List<BoardVo> search(int searchType, String searchWord, int pageNo){
		List<BoardVo> result = new ArrayList<BoardVo>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = getConnection();

			switch(searchType) {
				case 1:
					sql =
						"SELECT * FROM " + 
						"	( " + 
						"		SELECT " + 
						"			b.no, b.title, b.contents, b.Reg_date, b.hit, b.g_no, b.o_no, b.depth, b.user_no, u.name , @ROWNUM:=@ROWNUM+1, FLOOR((@ROWNUM-1)/10+1) AS PAGE " + 
						"		FROM " + 
						"			board b LEFT JOIN user AS u ON b.user_no = u.no " + 
						"			, (SELECT @ROWNUM:=0) R " +
						"		WHERE " +
						"			title like '%"+ searchWord + "%' " +
						"		ORDER BY no DESC " + 
						"	) A " + 
						"WHERE PAGE = ? ";										
					break;
					
				case 2:
					sql =
						"SELECT * FROM " + 
						"	( " + 
						"		SELECT " + 
						"			b.no, b.title, b.contents, b.Reg_date, b.hit, b.g_no, b.o_no, b.depth, b.user_no, u.name , @ROWNUM:=@ROWNUM+1, FLOOR((@ROWNUM-1)/10+1) AS PAGE " + 
						"		FROM " + 
						"			board b LEFT JOIN user AS u ON b.user_no = u.no " + 
						"			, (SELECT @ROWNUM:=0) R " +
						"		WHERE " +
						"			contents like '%"+ searchWord + "%' " +
						"		ORDER BY no DESC " + 
						"	) A " + 
						"WHERE PAGE = ? ";					
					break;
				
				default:
					break;
			}	
			pstmt = conn.prepareStatement(sql);		
			pstmt.setInt(1, pageNo);

			rs = pstmt.executeQuery();
			while(rs.next()) {
				BoardVo vo = new BoardVo();
				vo.setNo(rs.getLong(1));
				vo.setTitle(rs.getString(2));
				vo.setContent(rs.getString(3));
				vo.setRegDate(rs.getString(4));
				vo.setHit(rs.getInt(5));
				vo.setGroupNo(rs.getInt(6));
				vo.setOrderNo(rs.getInt(7));
				vo.setDepth(rs.getInt(8));
				vo.setUserNo(rs.getLong(9));
				vo.setUserName(rs.getString(10));
				result.add(vo);
			}

		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 클래스 로딩 실패:" + e);
		} catch (SQLException e) {
			System.out.println("에러:" + e);
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
		return result;
	}	
	
	public BoardVo findOne(Long no){
//		List<BoardVo> result = new ArrayList<BoardVo>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardVo vo = new BoardVo();
		
		try {
			conn = getConnection();
			
			String sql = 
					" select " +
					"	 b.no, b.title, b.contents, b.Reg_date, b.hit, b.g_no, b.o_no, b.depth, b.user_no, u.name" +
					" from " +
					"	 board as b left join user as u on b.user_no = u.no " +
					" where b.no = ? ";			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, Long.toString(no));

			rs = pstmt.executeQuery();
			while(rs.next()) {				
				vo.setNo(rs.getLong(1));
				vo.setTitle(rs.getString(2));
				vo.setContent(rs.getString(3));
				vo.setRegDate(rs.getString(4));
				vo.setHit(rs.getInt(5));
				vo.setGroupNo(rs.getInt(6));
				vo.setOrderNo(rs.getInt(7));
				vo.setDepth(rs.getInt(8));
				vo.setUserNo(rs.getLong(9));
				vo.setUserName(rs.getString(10));
			}
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 클래스 로딩 실패:" + e);
			return null;
		} catch (SQLException e) {
			System.out.println("에러:" + e);
			return null;
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return vo;
	}
	
	public int pageCount(){
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();		
			String sql = " SELECT COUNT(no) FROM board ";			
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();		
			if(rs.next()) {
				result = rs.getInt(1);
			}			
			
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 클래스 로딩 실패:" + e);
			return 0;
		} catch (SQLException e) {
			System.out.println("에러:" + e);
			return 0;
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public int pageCount_Search(int searchType, String searchWord){
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		
		try {
			conn = getConnection();

			switch(searchType) {
				case 1:
					sql = " SELECT COUNT(no) FROM board WHERE title like '%"+searchWord+"%' ";
					break;
				case 2:
					sql = " SELECT COUNT(no) FROM board WHERE contents like '%"+searchWord+"%' ";
					break;
				default:
					break;
			}
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();		
			if(rs.next()) {
				result = rs.getInt(1);
			}			
			
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 클래스 로딩 실패:" + e);
			return 0;
		} catch (SQLException e) {
			System.out.println("에러:" + e);
			return 0;
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}		
	
	
	public Boolean update(BoardVo vo) {
		Boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
			
			//todo- orderNo Depth 처리
			//SQL 준비
			String sql = " update board set title = ?, contents = ? where no = ?";
			
			pstmt = conn.prepareStatement(sql);			
			//값 바인딩
			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getContent());
			pstmt.setLong(3, vo.getNo());
			
			//쿼리 실행
			int count = pstmt.executeUpdate();
			result = (count == 1);

		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패:" + e);
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public Boolean hitCount(Long no) {
		Boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = getConnection();
			
			//todo- orderNo Depth 처리
			//SQL 준비
			String sql = " update board set hit = hit+1 where no = ?";
			
			pstmt = conn.prepareStatement(sql);			
			//값 바인딩
			pstmt.setLong(1, no);
			
			//쿼리 실행
			int count = pstmt.executeUpdate();
			result = (count == 1);

		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패:" + e);
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}	
		
	
	private Connection getConnection() throws ClassNotFoundException, SQLException {
		//1. JDBC Driver(Mysql) 로딩
		Class.forName("com.mysql.jdbc.Driver");
		
		//2. 연결하기
		String url = "jdbc:mysql://localhost:3306/webdb";
		Connection conn = DriverManager.getConnection(url, "webdb", "webdb");
		
		return conn;
	}	
}

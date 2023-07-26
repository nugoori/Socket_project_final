package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import config.DBConnectionMgr;

public class Main2 {

	public static void main(String[] args) {
		
		System.out.println(getProductByProductCode(20230701));
		insertProduct(20230706, "상품6");
		
		System.out.println(getProductByProductCode(20230706));

	}
	
	// 
	public static void insertProduct(int productCode, String productName) {
		
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = pool.getConnection();
			String sql = "insert into product_tb values(?, ?)";
			
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, productCode);
			pstmt.setString(2, productName);
			
			pstmt.executeUpdate(); // inset를 하면 성공횟수를 나타내줌
			System.out.println("Successfully inserted!!");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
	}
	
	public static Map<String, Object> getProductByProductCode(int productCode) {
		Map<String, Object> resultMap = new HashMap<>();
		
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = pool.getConnection();
			String sql = "select product_code, product_name from product_tb where product_code = ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, productCode);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				resultMap.put("productCode", rs.getInt(1));
				resultMap.put("productName", rs.getString(2));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return resultMap;
	}
	
	
	
}

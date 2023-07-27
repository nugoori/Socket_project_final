package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.protocol.Resultset;

import config.DBConnectionMgr;
import entity.ProductColor;

public class ProductColorRepository {

	private DBConnectionMgr pool;
	private static ProductColorRepository instance;
	
	private ProductColorRepository() {
		pool = DBConnectionMgr.getInstance();
	}
	
	public static ProductColorRepository getInstance() {
		if(instance == null) {
			instance = new ProductColorRepository();
		}
		return instance;
	}
	
	public List<ProductColor> getProductColorListAll() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ProductColor> productColorList = null;
		
		try {
			con = pool.getConnection();
			String sql = "select "
					+ "product_color_id, "
					+ "product_color_name "
					+ "from "
					+ "product_color_tb "
					+ "order by "
					+ "product_color_name";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			productColorList = new ArrayList<>();
			while(rs.next()) {
				ProductColor productColor = ProductColor.builder()
						.productColorId(rs.getInt(1))
						.productColorName(rs.getString(2))
						.build();
				productColorList.add(productColor);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return productColorList;
	}
	
	public ProductColor findProductColorByProductColorName(String productColorName) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProductColor productColor = null;
		
		try {
			con = pool.getConnection();
			// sql문에 줄바꿈을 할 경우 띄어쓰기를 유의 다 적어놓고 줄바꿈 하는게 편함
			String sql = "select "
					+ "product_color_id,"
					+ "product_color_name "
					+ "from "
					+ "product_color_tb "
					+ "where "
					+ "product_color_name = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, productColorName);
			rs = pstmt.executeQuery();
			
			// sql select문의 결과가 있을경우 DB에서 가져온 데이터를 ProductColor 객체로 생성해서 return
			if(rs.next()) {
				productColor = ProductColor.builder()
						.productColorId(rs.getInt(1))
						.productColorName(rs.getString(2))
						.build();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return productColor;
	}
	// 어떤것을 등록하려고 할 때 객체형태로 받으려고 해야함
	public int saveProductColor(ProductColor productColor) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int successCount = 0;
		
		try {
			con = pool.getConnection();
			String sql = "insert into product_color_tb values(0, ?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, productColor.getProductColorName());
			successCount = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return successCount;
	}
	
}

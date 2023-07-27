package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.protocol.Resultset;

import config.DBConnectionMgr;
import entity.ProductCategory;

public class ProductCategoryRepository {

	private DBConnectionMgr pool;
	private static ProductCategoryRepository instance;
	
	private ProductCategoryRepository() {
		pool = DBConnectionMgr.getInstance();
	}
	
	public static ProductCategoryRepository getInstance() {
		if(instance == null) {
			instance = new ProductCategoryRepository();
		}
		return instance;
	}
	
	public List<ProductCategory> getProductCategoryNameList() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ProductCategory> productCategoryList = null;
		
		try {
			con = pool.getConnection();
			String sql = "select "
					+ "product_category_id, "
					+ "product_category_name "
					+ "from "
					+ "product_category_tb "
					+ "group by "
					+ "product_category_name";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			productCategoryList = new ArrayList<>();
			while (rs.next()) {
				ProductCategory productCategory = ProductCategory.builder()
						.productCategoryId(rs.getInt(1))
						.productCategoryName(rs.getString(2))
						.build();
				productCategoryList.add(productCategory);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return productCategoryList;
	}
	
	public ProductCategory findProductCategoryByProductCategoryName(String productCategoryName) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProductCategory productCategory = null;
		
		try {
			con = pool.getConnection();
			String sql = "select "
					+ "product_category_id, "
					+ "product_category_name "
					+ "from "
					+ "product_category_tb "
					+ "where "
					+ "product_category_name = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, productCategoryName);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				productCategory = ProductCategory.builder()
				.productCategoryId(rs.getInt(1))
				.productCategoryName(rs.getString(2))
				.build();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return productCategory;
	}
	
	public int saveProductCategory(ProductCategory productCategory) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int success = 0;
		
		try {
			con = pool.getConnection();
			String sql = "insert into product_category_tb values(0, ?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, productCategory.getProductCategoryName());
			success = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
}

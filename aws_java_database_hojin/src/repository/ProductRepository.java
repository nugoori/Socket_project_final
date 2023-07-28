package repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DBConnectionMgr;
import entity.Product;
import entity.ProductCategory;
import entity.ProductColor;

public class ProductRepository {

	private DBConnectionMgr pool;
	private static ProductRepository instance;
	
	private ProductRepository() {
		// repository에선 DB와 연결할 JDBC객체를 사용해야 함
		pool = DBConnectionMgr.getInstance();
	}
	
	public static ProductRepository getInstance() {
		if(instance == null) { 
			instance = new ProductRepository();
		}
		return instance;
	}
	
	public Product findProductByProductId(int productId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Product product = null;
		
		try {
			con = pool.getConnection();
			String sql = "select \r\n"
					+ "		pt.product_id,\r\n"
					+ "		pt.product_name,\r\n"
					+ "    	pt.product_price,\r\n"
					+ "    	pt.product_color_id,\r\n"
					+ "    	pcot.product_color_name,\r\n"
					+ "    	pt.product_category_id,\r\n"
					+ "    	pcgt.product_category_name\r\n"
					+ "from \r\n"
					+ "		product_tb pt\r\n"
					+ "    left outer join product_color_tb pcot \r\n"
					+ "		on(pcot.product_color_id = pt.product_color_id)\r\n"
					+ "    left outer join product_category_tb pcgt \r\n"
					+ "		on(pcgt.product_category_id = pt.product_category_id)\r\n"
					+ "where\r\n"
					+ "		pt.product_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, productId);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				product = Product.builder()
					.productId(rs.getInt(1))
					.productName(rs.getString(2))
					.productPrice(rs.getInt(3))
					.productColorId(rs.getInt(4))
					.productColor(ProductColor.builder()
							.productColorId(rs.getInt(4))
							.productColorName(rs.getString(5))
							.build())
					.productCategoryId(rs.getInt(6))
					.productCategory(ProductCategory.builder()
							.productCategoryId(rs.getInt(6))
							.productCategoryName(rs.getString(7))
							.build())
					.build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return product;
	}

	// 
	public Product findProductByProductName(String productName) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Product product = null;
		
		try {
			con = pool.getConnection();
			String sql = "select \r\n"
					+ "		pt.product_id,\r\n"
					+ "		pt.product_name,\r\n"
					+ "    	pt.product_price,\r\n"
					+ "    	pt.product_color_id,\r\n"
					+ "    	pcot.product_color_name,\r\n"
					+ "    	pt.product_category_id,\r\n"
					+ "    	pcgt.product_category_name\r\n"
					+ "from \r\n"
					+ "		product_tb pt\r\n"
					+ "    left outer join product_color_tb pcot \r\n"
					+ "		on(pcot.product_color_id = pt.product_color_id)\r\n"
					+ "    left outer join product_category_tb pcgt \r\n"
					+ "		on(pcgt.product_category_id = pt.product_category_id)\r\n"
					+ "where\r\n"
					+ "		pt.product_name = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, productName);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				product = Product.builder()
					.productId(rs.getInt(1))
					.productName(rs.getString(2))
					.productPrice(rs.getInt(3))
					.productColorId(rs.getInt(4))
					// Product안에 P.Co , P.Ct 객체있음!!
					.productColor(ProductColor.builder()
							.productColorId(rs.getInt(4))
							.productColorName(rs.getString(5))
							.build())
					.productCategoryId(rs.getInt(6))
					.productCategory(ProductCategory.builder()
							.productCategoryId(rs.getInt(6))
							.productCategoryName(rs.getString(7))
							.build())
					.build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return product;
	}
	
	public int saveProduct(Product product) {
		Connection con = null;
		// procedure calld
		CallableStatement cstmt = null;
		int successCount = 0;
		
		try {
			con = pool.getConnection();
			String sql = "{ call p_insert_product(?, ?, ?, ?) }";
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, product.getProductName());
			cstmt.setInt(2, product.getProductPrice());
			cstmt.setString(3, product.getProductColor().getProductColorName());
			cstmt.setString(4, product.getProductCategory().getProductCategoryName());
			successCount = cstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cstmt != null) {
				try {
					cstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return successCount;
	}
	
	public List<Product> getSearchProductList(String searchOption, String searchValue) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Product> productList = null;
		
		try {
			// sql문을 procedure에 옮겨보기
			con = pool.getConnection();
			String sql = "select \r\n"
					+ "	pt.product_id,\r\n"
					+ "    pt.product_name,\r\n"
					+ "    pt.product_price,\r\n"
					+ "    pt.product_color_id,\r\n"
					+ "    pcot.product_color_name,\r\n"
					+ "    pt.product_category_id,\r\n"
					+ "    pcgt.product_category_name\r\n"
					+ "from \r\n"
					+ "	product_tb pt\r\n"
					+ "    left outer join product_color_tb pcot \r\n"
					+ "		on(pcot.product_color_id = pt.product_color_id)\r\n"
					+ "    left outer join product_category_tb pcgt \r\n"
					+ "		on(pcgt.product_category_id = pt.product_category_id)\r\n"
					+ "where\r\n"
					+ "	1 = 1 ";
			
			if(searchValue != null) { 
				if(!searchValue.isBlank()) {
					String whereSql = null;
					
					switch (searchOption) {
					case "전체":
						whereSql = "and (pt.product_name like concat('%', ?, '%') "
								+ "or pcot.product_color_name like concat('%', ?, '%') "
								+ "or pcgt.product_category_name like concat('%', ?, '%')";
						break;
					case "상품명":
						whereSql = "and pt.product_name like concat('%', ?, '%')";
						break;
					case "색상":
						whereSql = "and pcot.product_color_name like concat('%', ?, '%')";
						break;
					case "카테고리":
						whereSql = "and pcgt.product_category_name like concat('%', ?, '%')";
						break;
					}
					sql += whereSql;
				}
			}
			
			pstmt = con.prepareStatement(sql);
			// null blank 따로 검사해줘야함 &&를 넣어서할 경우 뒤의 black조건이 검사가 안되기 때문
			if(searchValue != null) {
				if(!searchValue.isBlank()) {
					if(searchOption.equals("전체")) {
						pstmt.setString(1, searchValue);
						pstmt.setString(2, searchValue);
						pstmt.setString(3, searchValue);
					} else {
						pstmt.setString(1, searchValue);						
					}				
				}				
			}
			
			rs = pstmt.executeQuery();
			productList = new ArrayList<>();
			while (rs.next()) {
				Product product = Product.builder()
						.productId(rs.getInt(1))
						.productName(rs.getString(2))
						.productPrice(rs.getInt(3))
						.productColorId(rs.getInt(4))
						// Product안에 P.Co , P.Ct 객체있음!!
						.productColor(ProductColor.builder()
								.productColorId(rs.getInt(4))
								.productColorName(rs.getString(5))
								.build())
						.productCategoryId(rs.getInt(6))
						.productCategory(ProductCategory.builder()
								.productCategoryId(rs.getInt(6))
								.productCategoryName(rs.getString(7))
								.build())
						.build();
				
				productList.add(product);
 			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return productList;
	}
	
	public int deleteProduct(int productId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int successCount = 0;
		
		try {
			con = pool.getConnection();
			String sql = "delete from product_tb where product_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, productId);
			successCount = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			pool.freeConnection(con, pstmt);
		}
		return successCount;
		
	}
	
	public int updateProduct(Product product) {
		Connection con = null;
		CallableStatement cstmt = null;
		int successCount = 0;
		
		try {
			con = pool.getConnection();
			String sql = "{ call p_update_product(?, ?, ?, ?, ?) }";
			cstmt = con.prepareCall(sql);
			cstmt.setInt(1, product.getProductId());
			cstmt.setString(2, product.getProductName());
			cstmt.setInt(3, product.getProductPrice());
			cstmt.setInt(4, product.getProductColor().getProductColorId());
			cstmt.setInt(5, product.getProductCategory().getProductCategoryId());
			successCount = cstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con);
		}
		return successCount;
	}
}






package entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Product {

	private int productId;
	private String productName;
	private int productPrice;
	private int productColorId;
	private int productCategoryId;
	// 정규화 전
	private ProductColor productColor;
	private ProductCategory productCategory;
}

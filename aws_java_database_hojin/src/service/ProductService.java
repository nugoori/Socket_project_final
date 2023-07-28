package service;

import java.util.List;

import entity.Product;
import repository.ProductRepository;

public class ProductService {

	private ProductRepository productRepository;
	private static ProductService instance;
	
	private ProductService() {
		productRepository = ProductRepository.getInstance();
	}
	
	public static ProductService getInstance() {
		if(instance == null) {
			instance = new ProductService();
		}
		return instance;
	}

	public boolean isProductNameDuplicated(String productName) {
		boolean result = false;
		result =  ProductRepository.getInstance().findProductByProductName(productName) != null;
		return result;
	}
	
	public boolean registerProduct(Product product) {
		return productRepository.saveProduct(product) > 0;
	}
	
	public List<Product> searchProduct(String searchOption, String searchValue) {
		
		return ProductRepository.getInstance().getSearchProductList(searchOption, searchValue);
	}
	
	
	
}

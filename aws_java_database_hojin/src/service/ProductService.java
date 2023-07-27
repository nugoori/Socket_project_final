package service;

import entity.Product;
import repository.ProductRepository;

public class ProductService {

	private static ProductService instance;
	
	private ProductService() {
		
	}
	
	public static ProductService getInstance() {
		if(instance == null) {
			instance = new ProductService();
		}
		return instance;
	}
	
	public boolean registerProduct(Product product) {
		return ProductRepository.getInstance().saveProduct(product) > 0;
	}
}

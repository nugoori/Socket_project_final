package service;

import java.util.ArrayList;
import java.util.List;

import entity.ProductColor;
import repository.ProductColorRepository;

public class ProductColorService {

	private ProductColorRepository productColorRepository;
	private static ProductColorService instance;
	
	private ProductColorService() {
		// service에서 Repository객체를 사용하기 위해
		productColorRepository = ProductColorRepository.getInstance();
	}
	// 왜 객체를 만들어서 데이터를 넣고 거기서 필요한 부분만 꺼내오는가? : 재상용성 때문(ProductColorListAll()을 사용하면 됨)
	public List<String> getProductColorLNameList() {
		List<String> prductColorNameList = new ArrayList<>();
		
		productColorRepository.getProductColorListAll().forEach(productColor -> {
			prductColorNameList.add(productColor.getProductColorName());
		});
		return prductColorNameList;
	}
	
	public static ProductColorService getInstance() {
		if(instance == null) {
			instance = new ProductColorService();
		}
		return instance;
	}
	// 중복되면 true 반환하도록
	public boolean isProductColorNameDuplicated(String productColorName) {
		boolean result = false;
		// 객체가 생성되어 나오면 중복이 있다는 의미이므로 중복체크를 할 때는 null값을 반환해줘야함?
		result = productColorRepository.findProductColorByProductColorName(productColorName) != null;
		return result;
	}
	
	public boolean registerProductColor(ProductColor productColor) {
		boolean result = false;
		result = productColorRepository.saveProductColor(productColor) > 0;
		return result;
	}
	
}

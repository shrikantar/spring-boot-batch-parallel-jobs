package com.ctcs.paralleljobs.processors;

import org.springframework.batch.item.ItemProcessor;

import com.ctcs.paralleljobs.model.Product;
import com.ctcs.paralleljobs.model.ProductRating;

public class ProductProcessor implements ItemProcessor<Product, ProductRating>{
	
	private final String MOSTLY_PREFERED = "A";
	private final String PREFERED = "B";

	@Override
	public ProductRating process(Product product) throws Exception {
		ProductRating ratedProduct = new ProductRating();
		ratedProduct.setId(product.getId());
		
		//Lets say we have some business logic to rate product
		if(product.getOrderCount() > 5000) {
			ratedProduct.setRating(MOSTLY_PREFERED);
		}else {
			ratedProduct.setRating(PREFERED);
		}
		
		return ratedProduct;
	}
}

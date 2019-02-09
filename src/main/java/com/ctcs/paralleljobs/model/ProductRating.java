package com.ctcs.paralleljobs.model;

import java.io.Serializable;

public class ProductRating implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id; 
	private String rating;
	
	public ProductRating() {
		//Default Constructor 
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

}

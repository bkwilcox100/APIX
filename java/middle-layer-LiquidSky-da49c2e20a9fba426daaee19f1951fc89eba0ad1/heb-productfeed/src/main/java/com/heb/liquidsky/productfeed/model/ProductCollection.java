package com.heb.liquidsky.productfeed.model;

import java.util.List;

public class ProductCollection {
	private String nextPage;
	private Long totalProductsPages;
	private Long totalProductsRecords;
	private Long currentProductsPage;
	private List<Product> products = null;

	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	public Long getTotalProductsPages() {
		return totalProductsPages;
	}

	public void setTotalProductsPages(Long totalProductsPages) {
		this.totalProductsPages = totalProductsPages;
	}

	public Long getTotalProductsRecords() {
		return totalProductsRecords;
	}

	public void setTotalProductsRecords(Long totalProductsRecords) {
		this.totalProductsRecords = totalProductsRecords;
	}

	public Long getCurrentProductsPage() {
		return currentProductsPage;
	}

	public void setCurrentProductsPage(Long currentProductsPage) {
		this.currentProductsPage = currentProductsPage;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

}

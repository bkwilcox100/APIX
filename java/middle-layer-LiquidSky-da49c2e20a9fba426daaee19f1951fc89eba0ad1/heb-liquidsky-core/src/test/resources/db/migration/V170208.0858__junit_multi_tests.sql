USE middle_layer;

DROP TABLE test_product_sku;
ALTER TABLE test_sku ADD COLUMN required_prod_id VARCHAR(40) NOT NULL;
ALTER TABLE test_sku ADD FOREIGN KEY (required_prod_id) REFERENCES test_product(prod_id);
ALTER TABLE test_sku ADD COLUMN optional_prod_id VARCHAR(40);
ALTER TABLE test_sku ADD FOREIGN KEY (optional_prod_id) REFERENCES test_product(prod_id);
CREATE TABLE test_related_products (
	prod_id VARCHAR(40) NOT NULL,
	related_prod_id VARCHAR(40) NOT NULL,
	PRIMARY KEY (prod_id, related_prod_id),
	FOREIGN KEY (prod_id) REFERENCES test_product(prod_id),
	FOREIGN KEY (related_prod_id) REFERENCES test_product(prod_id)
);
